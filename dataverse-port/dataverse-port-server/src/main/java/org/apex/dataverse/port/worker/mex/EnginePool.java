/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apex.dataverse.port.worker.mex;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.NoEngineException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Danny.Huo
 * @date 2024/1/13 15:54
 * @since 0.1.0
 */
@Slf4j
@Data
public class EnginePool implements Serializable {

    /**
     * Engine pool for port
     */
    private StoragePort storagePort;

    /**
     * 正在启动中的引擎
     */
    private final Map<Long, Object> starting = new ConcurrentHashMap<>();

    /**
     * 在线引擎列表
     * key : engineId
     * val : Engine
     */
    private final Map<Long, EngineContext> online = new ConcurrentHashMap<>();

    /**
     * 已失效引擎ID集合
     */
    private final List<Long> offlineIds = new ArrayList<>();

    /**
     * 已失效或断开链接的引擎
     * TODO 可用专门任务从此队列中消费数据，将更新引擎表
     */
    private final LinkedBlockingQueue<EngineContext> offline = new LinkedBlockingQueue<>();

    /**
     * 查找引擎
     *
     * @return EngineContext
     * @throws NoEngineException NoEngineException
     */
    public synchronized EngineContext findEngine() throws NoEngineException {
        Iterator<Map.Entry<Long, EngineContext>> iterator = online.entrySet().iterator();
        EngineContext engine = null;
        while (iterator.hasNext()) {
            Map.Entry<Long, EngineContext> next = iterator.next();
            EngineContext e = next.getValue();
            if (!e.getEngineClient().isActive()) {
                e.close();
                offline.add(e);
                offlineIds.add(e.getEngineNode().getEngineId());
                continue;
            }

            // 寻找空闲数最大的引擎
            if (null == engine ||
                    engine.getFree() < e.getFree()) {
                engine = e;
            }
        }

        if (null == engine || !engine.isAvailable()) {
            throw new NoEngineException("No engine available at the moment");
        }

        // 清理过期引擎
        this.clearInvalidEngine();

        return engine;
    }

    /**
     * 清除已失效的Engine客户端
     */
    private void clearInvalidEngine() {
        if (offlineIds.isEmpty()) {
            return;
        }

        for (Long id : offlineIds) {
            EngineContext e = online.remove(id);
            log.info("The engine[engineId => {},engineName => {}] is closed, will remove it", e.getEngineNode().getEngineId(),
                    e.getEngineNode().getEngineName());
        }

        offlineIds.clear();
    }

    /**
     * 启动中，预备注册
     * @param engine DataEngine
     */
    public synchronized void preregister(Engine engine) {
        this.starting.put(engine.getEngineId(), engine);
    }

    /**
     * 注册引擎
     * @param engine EngineContext
     */
    public synchronized void register(EngineContext engine) {
        // Remove starting engine
        this.starting.remove(engine.getEngineNode().getEngineId());

        // Register EngineContext
        online.put(engine.getEngineNode().getEngineId(), engine);
    }

    /**
     * 注销引擎
     * @param engineId Engine ID
     */
    public EngineContext unregister(Long engineId) throws InterruptedException {

        EngineContext e = online.remove(engineId);

        if (null != e) {
            log.info("The engine[engineId => {},engineName => {}] is closed, have being removed", e.getEngineNode().getEngineId(),
                    e.getEngineNode().getEngineName());

            this.offline.put(e);

            this.offlineIds.add(e.getEngineNode().getEngineId());

        } else {
            log.error("The engine [engineId => {}] not exists, Unregister it failed", engineId);
        }

        return e;
    }

    /**
     * 查找引擎
     * @param engineId Long
     * @return EngineContext
     */
    public EngineContext findEngine(Long engineId) {
        return online.get(engineId);
    }

    /**
     * 引擎心跳
     * @param engineId Long
     * @param lastHeatBeatTime Long
     * @return boolean
     */
    public boolean heartbeat(Long engineId, long lastHeatBeatTime) {
        if (null == engineId) {
            return false;
        }

        EngineContext engineContext = online.get(engineId);
        if (null != engineContext) {
            engineContext.setLastHeatBeatTime(lastHeatBeatTime);
            return true;
        } else {
            if(starting.containsKey(engineId)) {
                log.warn("Engine[{}] is starting, heat beat failed", engineId);
            }
        }

        return false;
    }

    /**
     * Engine online quantity
     * @return int
     */
    public int onlineQuantity() {
        return online.size();
    }

    /**
     * Engine online and starting quantity
     * @return int
     */
    public int quantity() {
        return online.size() + starting.size();
    }
}
