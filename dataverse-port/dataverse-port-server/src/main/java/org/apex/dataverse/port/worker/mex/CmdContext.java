package org.apex.dataverse.port.worker.mex;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Danny.Huo
 * @date 2023/5/11 14:33
 * @since 0.1.0
 */
@Data
public class CmdContext implements Serializable {

    /**
     * Default timeout
     */
    private final static Integer DEFAULT_TIMEOUT = 1000 * 60 * 60;

    /**
     *
     * @param channelId Netty channel id
     * @return CmdContext
     */
    public static CmdContext newContext(String channelId) {
        return new CmdContext(channelId);
    }

    private CmdContext() {

    }

    private CmdContext(String channelId) {
        this.channelId = channelId;
        this.registerTime = System.currentTimeMillis();
    }

    /**
     * Netty Channel ID
     */
    private String channelId;

    /**
     * Command execution timeout time
     */
    private Integer timeout;

    /**
     * Command register time
     */
    private long registerTime;

    /**
     * Determines whether a message has not been returned after a timeout
     *
     * @return boolean
     */
    public boolean isTimeout() {
        long now = System.currentTimeMillis();
        if (null == timeout) {
            return now - registerTime > DEFAULT_TIMEOUT;
        }
        return now - registerTime > timeout;
    }
}
