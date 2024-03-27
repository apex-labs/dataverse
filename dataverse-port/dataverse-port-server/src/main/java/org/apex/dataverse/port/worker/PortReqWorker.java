package org.apex.dataverse.port.worker;

import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.util.ExceptionUtil;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.exception.StorageConnException;
import org.apex.dataverse.port.service.IStorageConnectionService;
import org.apex.dataverse.port.worker.mex.CmdExchanger;
import org.apex.dataverse.port.worker.mex.EngineExchanger;
import org.apex.dataverse.port.exception.NoEngineException;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherContext;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherUtil;
import org.apex.dataverse.port.service.IJobHistoryService;
import org.apex.dataverse.port.worker.abs.AbsWorker;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.engine.StartEngineReqPacket;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.worker.mex.StorageConnection;
import org.apex.dataverse.port.worker.mex.StorageExchanger;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.sql.SQLException;

/**
 * It is a resident thread
 * <p>
 * 1、Process the request sent by the Driver
 * </p>
 * <p>
 * 2、Push the request to the engine server through EngineClient to process the command
 * </p>
 *
 * @author Danny.Huo
 *
 * @date 2023/2/21 20:15
 *
 * @since 0.0.1
 */
@Slf4j
public class PortReqWorker extends AbsWorker {

    /**
     * Port server context
     * Receives and responds to messages from the Driver side
     */
    private final ServerContext portServerContext;

    /**
     * Command exchanger
     */
    private final CmdExchanger cmdExchanger;

    private final StorageExchanger storageExchanger;


    public PortReqWorker(ServerContext serverContext,
                         CmdExchanger cmdExchanger,
                         StorageExchanger storageExchanger) {
        this.portServerContext = serverContext;
        this.cmdExchanger = cmdExchanger;
        this.storageExchanger = storageExchanger;
    }

    @Override
    public void run() {
        while (this.isRunning()) {
            try {
                Request<Packet> cmd = portServerContext.takeRequest();
                log.info("port req worker cmd info:{}", ObjectMapperUtil.toJson(cmd));

                if (cmd.getPacket() instanceof StartEngineReqPacket) {
                    // 启动引擎命令
                    startEngine(cmd);
                } else {

                    // 其它引擎交互命令
                    handleCommand(cmd);
                }
            } catch (Exception e) {
                log.error("Handle port command found error.", e);
            }
        }
    }

    /**
     * 启动Spark引擎
     * @param request Request<Packet>
     * @throws IOException IOException
     */
    private void startEngine(Request<Packet> request) throws IOException, AppLaunchException {
        // Engine request packet
        StartEngineReqPacket packet = (StartEngineReqPacket) request.getPacket();

        // build launcher context
        SparkLauncherContext context = new SparkLauncherContext();
        context.setEngineJar(packet.getEngineJar());

        // Start spark engine
        SparkLauncherUtil.launcherSpark(context);

        // update and prepare register
        Engine engine = new Engine();
        BeanUtils.copyProperties(packet, engine);
        if(null != context.getSparkHandleListener()) {
            engine.setApplicationId(context.getSparkHandleListener().getApplicationId());
        }

        engine.setApplicationName(context.getAppName());
        // 注册引擎
        //this.dataEngineService.preregister(packet.getSourceStorageId(), engine);
    }

    /**
     * 处理Driver端的命令
     * 发送给引擎端处理
     */
    private void handleCommand(Request<Packet> cmd)
            throws InterruptedException, NoEngineException, AppLaunchException, SQLException, IOException, ClassNotFoundException, StorageConnException {

        // 保存命令
        StorageConnection conn = null;
        try {
            conn = storageExchanger.getConnection(cmd.getPacket().getSourceStorageId());

            // 保存job
            //this.jobHistoryService.saveJob(cmd, command, engine.getEngineNode());

            // 向命令交换中心注册命令
            this.cmdExchanger.register(cmd);

            // 发送命令给引擎端
            conn.exe(cmd);
        } catch (NoEngineException | InvalidCmdException | InvalidConnException e) {
            Response<Packet> response = cmd.newSelfRsp(Response.FAILED);
            log.error("Handle command[{}] found error", cmd, e);
            String stackTrace = ExceptionUtil.getStackTrace(e);
            response.getPacket().setMessage(stackTrace);
            portServerContext.pushResponse(response);
        } finally {
            if(null != conn) {
                storageExchanger.back(conn);
            }
        }
    }
}
