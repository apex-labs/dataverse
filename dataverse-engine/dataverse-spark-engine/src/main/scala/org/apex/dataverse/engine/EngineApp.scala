package org.apex.dataverse.engine

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apex.dataverse.core.context.ServerContext
import org.apex.dataverse.core.enums.EngineEvent
import org.apex.dataverse.core.msg.{Request, Response}
import org.apex.dataverse.core.msg.packet.Packet
import org.apex.dataverse.core.netty.server.EngineServer
import org.apex.dataverse.core.util.{AddressUtil, ExceptionUtil, ObjectMapperUtil}
import org.apache.spark.sql.SparkSession
import org.apex.dataverse.engine.arg.{ArgsParser, EngineContext}
import org.apex.dataverse.engine.cmd.{CmdDistributor, CmdTracker}
import org.apex.dataverse.engine.tick.EventTicker
import org.apex.dataverse.engine.util.SparkUtil

/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/23 19:57
 * @version : v1.0
 */
object EngineApp {

    /**
     * DataIntegration input job context
     */
    var engineContext: EngineContext = _

    /**
     * Engine server for netty
     * <p>
     * Port connect to this
     */
    private var engineServer: EngineServer = _

    /**
     * SparkEngine Arguments
     *
     *  --engine-id 0
     *  --tick-server-address 127.0.0.1
     *  --tick-server-port 20001
     *  --tick-client-threads 1
     *  --tick-interval-ms 3000
     *  --engine-server-port
     *  --engine-server-boss-threads
     *  --engine-server-worker-threads
     *  --engine-storage-code default
     *
     * @param args Array[String]
     */
    def main(args: Array[String]): Unit = {
        // parse parameter
        println(s"Analytic spark engine start parameter :  ${args.mkString(",")}")
        engineContext = ArgsParser.parseArgs(args)

        // Build spark session
        println("Build the SparkSession object")
        //var spark : SparkSession = null
        val spark = buildSpark(engineContext.master, engineContext.enableHive);

        // connect to state server
        println(s"Connect to the port tick server[${engineContext.clientContext.getServerAddress}]")
        val eventTick = EventTicker.connectTickServer(engineContext)
        var connOk = false
        var times = 0
        while (!connOk && times <= 3) {
            times += 1
            if (!eventTick.isActive) {
                eventTick.closeChannel()
            } else {
                // 连接成功
                connOk = true
            }
        }

        if (!connOk) {
            // 未启动成功
            println("The engine failed to connect to the port and failed to start")

            System.exit(0)
        }

        CmdTracker.initTracker(engineContext.clientContext)
        EventTicker.tickEvent(s"The spark engine connected to port tick server[${engineContext.clientContext.getServerAddress}]", EngineEvent.HEART_BEAT)

        // start engine server
        val context = startEngineServer()
        EventTicker.tickEvent(s"The engine server[${AddressUtil.getLocalAddress}:${engineContext.serverContext.getEnv.getServerPort}] is started", EngineEvent.ENGINE_REGISTER)

        // Take command
        while (true) {
            var request: Request[Packet] = null
            try {
                // 获取请求
                request = context.takeRequest()
                CmdTracker.tracking(s"The engine server receives a command[${request.getPacket.getCommandId}] that will be distributed for execution", request)

                // 执行命令
                val response = CmdDistributor.doCmd(spark, request)

                CmdTracker.tracking(s"The command has been executed. The response[${ObjectMapperUtil.toJson(response)}] is displayed", request)

                // 返回执行结果
                context.pushResponse(response)

                // 汇报状态
                EventTicker.tickEvent(s"The command[${request.getPacket.getCommandId}] execute finished ：request => "
                        + ObjectMapperUtil.toJson(request) + ",response => "
                        + ObjectMapperUtil.toJson(response), EngineEvent.ENGINE_STOP_CMD)
            } catch {
                case ex: Throwable => {
                    ex.printStackTrace()
                    val exStr = ExceptionUtil.getStackTrace(ex)
                    val msg = s"Command[${request.getPacket.getCommandId}] execution exception : \n${exStr}"
                    EventTicker.tickEvent(msg, EngineEvent.ENGINE_EXE_CMD_ERROR)
                    if (null != request) {
                        val response : Response[Packet] = request.newSelfRsp(Response.FAILED)
                        response.getPacket.setMessage(s"Command[${request.getPacket.getCommandId}] execute failed. exception : \n ${exStr}")
                        context.pushResponse(response)
                        CmdTracker.tracking(msg, request)
                    }
                }
            }
        }
    }

    /**
     * 构建Spark Session
     *
     * @return
     */
    private def buildSpark(master : String, enableHive : Boolean): SparkSession = {
        if(enableHive) {
            SparkUtil.spark = SparkSession.builder()
                    .master(master)
                    .enableHiveSupport()
                    .getOrCreate()
        } else{
            SparkUtil.spark = SparkSession.builder()
                    .master(master)
                    .getOrCreate()
        }

        SparkUtil.fs = FileSystem.get(new Configuration())

        SparkUtil.spark
    }

    /**
     * 启动EnginServer
     *
     * @return
     */
    private def startEngineServer(): ServerContext = {
        engineServer = EngineServer.newEngineServer(engineContext.serverContext)
        new Thread(engineServer).start()
        engineContext.serverContext
    }

}
