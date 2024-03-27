package org.apex.dataverse.port.worker;

import org.apex.dataverse.port.worker.abs.AbsWorker;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.port.worker.mex.CmdExchanger;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

/**
 * It is a resident thread
 * <p>
 * 1、Receives the response message returned by the engine server
 * </p>
 * <p>
 * 2、The response message is sent to the Driver by the Driver of the command sender
 * </p>
 * @author Danny.Huo
 * @date 2023/5/11 14:04
 * @since 0.1.0
 */
@Slf4j
public class PortRspWorker extends AbsWorker {

    /**
     * Engine client context
     */
    private final ClientContext engineClientContext;

    /**
     * Port Server context
     */
    private final ServerContext portServerContext;

    /**
     * Channel exchanger
     */
    private final CmdExchanger cmdExchanger;

    /**
     * 构造函数
     * @param clientContext ClientContext
     * @param portServerContext ServerContext
     */
    public PortRspWorker(ClientContext clientContext,
                         ServerContext portServerContext,
                         CmdExchanger cmdExchanger) {
        this.engineClientContext = clientContext;
        this.portServerContext = portServerContext;
        this.cmdExchanger = cmdExchanger;
    }

    @Override
    public void run() {
        while (this.isRunning()) {
            try {
                this.exchangeResponse();
            } catch (Exception e) {
                log.error("Do response found error", e);
            }
        }
    }

    /**
     * Exchange/process Response
     * The port server forwards the message returned by the engine to the application end
     *
     * @throws InterruptedException InterruptedException
     * @throws JsonProcessingException JsonProcessingException
     */
    private void exchangeResponse() throws InterruptedException, JsonProcessingException {

        // Gets a response message, if no response blocks
        Response<Packet> response = this.engineClientContext.takeResponse();

        // Debug response message
        if(log.isDebugEnabled()) {
            log.debug("Response => {}", ObjectMapperUtil.toJson(response));
        }

        // Unregister response message
        this.cmdExchanger.unRegister(response);

        // Push response message
        this.portServerContext.pushResponse(response);
    }
}
