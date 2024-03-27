package org.apex.dataverse.port.worker.mex;

import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Danny.Huo
 * @date 2023/5/11 14:25
 * @since 0.1.0
 */
@Component
public class CmdExchanger {

    /**
     * 命令和Channel映射字典
     * key : commandId
     * val : channelId
     */
    private final Map<String, CmdContext> cmdChannelMap = new ConcurrentHashMap<>();

    /**
     * Command counter
     */
    private int cmdCounter = 0;

    /**
     * 当映射超过此值时，检查无效消息
     */
    private final static int CMD_MAX = 5000;

    /**
     * Register commands and channels
     *
     * @param req Request<Packet>
     */
    public synchronized void register(Request<Packet> req) {
        if (this.cmdCounter >= CMD_MAX) {
            this.clearTimeout();
        }
        cmdChannelMap.put(req.getPacket().getCommandId(),
                CmdContext.newContext(req.getChannelId()));
        this.cmdCounter++;
    }

    /**
     * After responding, uninstall the registered command
     *
     * @param rsp Response<Packet>
     */
    public synchronized void unRegister(Response<Packet> rsp) {
        CmdContext chInfo = cmdChannelMap.remove(rsp.getPacket().getCommandId());
        if (null == chInfo) {
            return;
        }
        this.cmdCounter--;
        rsp.setChannelId(chInfo.getChannelId());
    }

    /**
     * Clean up commands and channel mappings that timeout without returning messages
     */
    private void clearTimeout() {
        for (Map.Entry<String, CmdContext> next : cmdChannelMap.entrySet()) {
            if (next.getValue().isTimeout()) {
                cmdChannelMap.remove(next.getKey());
                this.cmdCounter--;
            }
        }
    }
}
