package org.apex.dataverse.port.worker.mex;

import org.apex.dataverse.core.netty.client.EngineClient;
import org.apex.dataverse.port.core.node.EngineNode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.entity.StoragePort;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * @author Danny.Huo
 * @date 2023/5/10 11:33
 * @since 0.1.0
 */
@Slf4j
@Data
public class EngineContext implements Serializable {

    /**
     * StoragePort for this engine
     */
    private StoragePort storagePort;

    /**
     * The engine client sends commands to the engine server
     */
    private EngineClient engineClient;

    /**
     * 引擎节点信息
     */
    private EngineNode engineNode;

    /**
     * 空闲数量, 默认0
     */
    private Integer free = 0;

    /**
     * 使用数量，默认0
     */
    private Integer used = 0;

    private Future<?> reqFuture;

    private Future<?> rspFuture;

    /**
     * 最后汇报心跳时间
     */
    private long lastHeatBeatTime;

    /**
     * 心跳间隔
     */
    private Integer heartbeatInterval = 1000;

    /**
     * 主动下线标识
     */
    private boolean offline = false;

    /**
     * 是否被动下线
     *
     * @return boolean
     */
    public boolean isPassiveOffline() {
        if (lastHeatBeatTime <= 0) {
            return false;
        }
        return System.currentTimeMillis() - lastHeatBeatTime
                > heartbeatInterval + 1000;
    }

    /**
     * 是否有效
     *
     * @return boolean
     */
    public boolean isAvailable() {
        return !isPassiveOffline() && !offline && free > 0;
    }

    /**
     * 使用
     */
    public synchronized void use() {
        this.free--;
        this.used++;
    }

    /**
     * 释放
     */
    public synchronized void release() {
        this.free++;
        this.used--;
    }

    public void close() {
        try {
            this.getEngineClient().closeChannel();
            this.reqFuture.cancel(true);
        } catch (InterruptedException e) {
            log.info("The engine closed", e);
        }
    }
}
