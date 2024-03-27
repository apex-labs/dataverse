package org.apex.dataverse.port.worker;

import org.apex.dataverse.port.worker.abs.AbsWorker;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.port.worker.mex.EngineExchanger;
import lombok.extern.slf4j.Slf4j;

/**
 * It is a resident thread
 * <p>
 * 1、Process the request sent by the Engine's event message
 * </p>
 * <p>
 * 2、Print the engine's event log
 * </p>
 * <p>
 * 3、Save the engine's event log to database
 * </p>
 *
 * @author Danny.Huo
 * @date 2023/4/28 18:01
 * @since 0.1.0
 */
@Slf4j
public class PortTickWorker extends AbsWorker {

    private final ServerContext stateServerContext;

    private final EngineExchanger engineExchanger;

    public PortTickWorker(ServerContext stateServerContext,
                          EngineExchanger engineExchanger) {
        this.stateServerContext = stateServerContext;
        this.engineExchanger = engineExchanger;
    }

    @Override
    public void run() {
        log.info("Port command worker started ... ");
        while (this.isRunning()) {
            try {
                Request<Packet> request = stateServerContext.takeRequest();
                if (log.isDebugEnabled()) {
                    log.debug("Packet => {}", ObjectMapperUtil.toJson(request.getPacket()));
                }
                Byte command = request.getHeader().getCommand();

                // 日志追踪
                if (command.equals(CmdSet.CMD_TRACKING.getReq())) {
                    //this.dataEngineService.tracking(request);
                }

                // 引擎状态汇报
                else if (command.equals(CmdSet.ENGINE_EVENT.getReq())) {
                    //this.dataEngineService.doEvent(request);
                }

            } catch (Exception e) {
                log.error("State command worker found error", e);
            }
        }
        log.error("The PortTickWorker exits abnormally, and the Port may not work properly");
    }
}
