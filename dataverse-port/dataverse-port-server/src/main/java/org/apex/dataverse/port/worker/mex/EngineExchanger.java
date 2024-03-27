package org.apex.dataverse.port.worker.mex;

import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.exception.NoEngineException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Danny.Huo
 * @date 2023/5/10 11:32
 * @since 0.1.0
 */
@Slf4j
@Component
public class EngineExchanger {
    /**
     * 引擎池，分存储区ID
     * key : storageId
     * val : EnginePool
     */
    private final Map<Long, EnginePool> pools = new ConcurrentHashMap<>();

    /**
     * 查找引擎
     *
     * @return EngineContext
     * @throws NoEngineException NoEngineException
     */
    public synchronized EngineContext findEngine(Long storageId) throws NoEngineException {
        EnginePool enginePool = pools.get(storageId);
        if (null == enginePool) {
            throw new NoEngineException("Storage[" + storageId + "] no suitable engine");
        }
        return enginePool.findEngine();
    }

    /**
     * 启动中，预备注册
     * @param engine DataEngine
     */
    public synchronized void preregister(Long storageId, Engine engine) {
        EnginePool enginePool = this.pools.get(storageId);
        if (null == enginePool) {
            enginePool = new EnginePool();
            this.pools.put(storageId, enginePool);
        }
        enginePool.getStarting().put(engine.getEngineId(), engine);
    }

    /**
     * 注册引擎
     * @param engine EngineContext
     */
    public synchronized void register(Long storageId, EngineContext engine) {
        EnginePool enginePool = this.pools.get(storageId);
        if (null == enginePool) {
            enginePool = new EnginePool();
            this.pools.put(storageId, enginePool);
        }

        // Remove starting engine
        enginePool.getStarting().remove(engine.getEngineNode().getEngineId());

        // Register EngineContext
        enginePool.getOnline().put(engine.getEngineNode().getEngineId(), engine);
    }

    /**
     * 注销引擎
     * @param engineId Engine ID
     */
    public EngineContext unregister(Long storageId, Long engineId) throws InterruptedException {

        EnginePool enginePool = this.pools.get(storageId);
        if (null == enginePool) {
            return null;
        }

        EngineContext e = enginePool.getOnline().remove(engineId);

        if (null != e) {
            log.info("The engine[engineId => {},engineName => {}] is closed, have being removed", e.getEngineNode().getEngineId(),
                    e.getEngineNode().getEngineName());

            enginePool.getOffline().put(e);

            enginePool.getOfflineIds().add(e.getEngineNode().getEngineId());

        } else {
            log.error("The engine [engineId => {}] not exists, Unregister it failed", engineId);
        }

        return e;
    }

    /**
     * 引擎心跳
     * @param engineId Long
     * @param lastHeatBeatTime Long
     * @return boolean
     */
    public boolean heartbeat(Long storageId, Long engineId, long lastHeatBeatTime) {
        if (null == engineId) {
            return false;
        }

        EnginePool enginePool = this.pools.get(storageId);

        if (null == enginePool) {
            return false;
        }

        EngineContext engineContext = enginePool.getOnline().get(engineId);
        if (null != engineContext) {
            engineContext.setLastHeatBeatTime(lastHeatBeatTime);
            return true;
        } else {
            if(enginePool.getStarting().containsKey(engineId)) {
                log.warn("Engine[{}] is starting, heat beat failed", engineId);
            }
        }

        return false;
    }

    /**
     * Engine online quantity
     *
     * @return int
     */
    public int onlineQuantity(Long storageId) {

        EnginePool enginePool = pools.get(storageId);

        if (null == enginePool) {
            return 0;
        }

        return enginePool.onlineQuantity();
    }

    /**
     * Engine online and starting quantity
     *
     * @return int
     */
    public int quantity(Long storageId) {

        EnginePool enginePool = pools.get(storageId);

        if (null == enginePool) {
            return 0;
        }

        return enginePool.quantity();
    }

}
