package org.apex.dataverse.engine.di

import org.apex.dataverse.core.enums.{CmdState, EngineEvent}
import org.apex.dataverse.core.msg.{Request, Response}
import org.apex.dataverse.core.msg.packet.Packet
import org.apex.dataverse.core.util.{ExceptionUtil, ObjectMapperUtil}
import org.apache.spark.sql.SparkSession
import org.apex.dataverse.core.msg.packet.di.{DiReqPacket, DiRspPacket}
import org.apex.dataverse.engine.arg.JdbcOption
import org.apex.dataverse.engine.cmd.CmdTracker
import org.apex.dataverse.engine.tick.EventTicker
import org.apex.dataverse.engine.util.DfOutUtil


/**
 *
 * @author : Danny.Huo
 * @date : 2023/4/25 11:53
 * @version : v1.0
 */
object DataIntegrationJob {

    /**
     * Data integration job
     *
     * @param spark   SparkSession
     * @param request Request[Packet]
     * @return
     */
    def doDataIntegration(spark: SparkSession, request: Request[Packet]): Response[Packet] = {
        val response = request.newRsp[Packet](Response.SUCCESS)
        val rspPacket = response.getPacket.asInstanceOf[DiRspPacket]
        val start = System.currentTimeMillis()
        try {
            val reqPacket = request.getPacket.asInstanceOf[DiReqPacket]

            val reader = spark.read.format(JdbcOption.JDBC_FORMAT)

            EventTicker.tickEvent("Start execute command ： " + ObjectMapperUtil.toJson(request), EngineEvent.ENGINE_ACC_CMD)

            CmdTracker.tracking("Jdbc reader read the data", request)

            val df = reader.option(JdbcOption.URL.key, reqPacket.getUrl)
                    .option(JdbcOption.USER.key, reqPacket.getUser)
                    .option(JdbcOption.PASSWORD.key, reqPacket.getPassword)
                    .option(JdbcOption.DRIVER.key, reqPacket.getDriver)
                    .option(JdbcOption.QUERY.key, reqPacket.getQuery)
                    .option(JdbcOption.FETCH_SIZE.key, JdbcOption.FETCH_SIZE.default)
                    .load()

            CmdTracker.tracking(s"Dataframe will output[${reqPacket.getOutput}]", request)
            DfOutUtil.output(df, reqPacket.getOutput)

            // 构造响应消息响应
            CmdTracker.tracking(s"Write to hdfs finished. Build response packet", request)
            rspPacket.setState(CmdState.SUCCESS.getCode)
            rspPacket.setMessage("Successfully execute command.")
            rspPacket.setDiCount(df.count().intValue())
            rspPacket.setDuration((System.currentTimeMillis() - start).intValue())
            rspPacket.setOutput(reqPacket.getOutput)
            EventTicker.tickEvent(s"Successfully execute command[${request.getPacket.getCommandId}], response is ${ObjectMapperUtil.toJson(rspPacket)}", EngineEvent.ENGINE_ACC_CMD)
        } catch {
            case ex: Throwable => {
                ex.printStackTrace()
                val msg = s"The data integration command detects an exception : \n${ExceptionUtil.getStackTrace(ex)}"
                rspPacket.setState(CmdState.FAILED.getCode)
                response.setSuccess(Response.FAILED)
                rspPacket.setMessage(msg)
                rspPacket.setDuration((System.currentTimeMillis() - start).intValue())
                EventTicker.tickEvent(msg, EngineEvent.ENGINE_ACC_CMD)
                CmdTracker.tracking(msg, request)
            }
        }

        response
    }
}
