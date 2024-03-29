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
package org.apex.dataverse.port.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.port.core.exception.RegistryException;
import org.apex.dataverse.port.core.node.EngineNode;
import org.apex.dataverse.port.core.node.PortConnNode;
import org.apex.dataverse.port.core.node.PortNode;
import org.apex.dataverse.port.core.node.StorageConnNode;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.service.IHeartbeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Danny.Huo
 * @date 2023/12/20 15:13
 * @since 0.1.0
 */
@Slf4j
@Service
public class HeartbeatServiceImpl implements IHeartbeatService {

    /**
     * Redis registry service
     */
    private RedisRegistryService redisRegistryService;

    @Override
    public void portHeartbeat(PortNode portNode) throws RegistryException {
        portNode.setHeartbeatTime(System.currentTimeMillis());
        String json = toJson(portNode);
        this.redisRegistryService.set(portNode.getRegistryCode(), json, EXPIRE_TIME);
        PORT_NODES.put(portNode.getPortId(), portNode);
    }

    @Override
    public void engineHeartbeat(EngineNode engineNode) throws RegistryException {
        engineNode.setHeartbeatTime(System.currentTimeMillis());
        String json = toJson(engineNode);
        this.redisRegistryService.set(engineNode.getRegistryCode(), json, EXPIRE_TIME);
        ENGINE_NODES.put(engineNode.getEngineId(), engineNode);
    }

    @Override
    public void portConnHeartbeat(PortConnNode portConnNode) throws RegistryException {
        portConnNode.setHeartbeatTime(System.currentTimeMillis());
        String json = toJson(portConnNode);
        this.redisRegistryService.set(portConnNode.getRegistryCode(), json, EXPIRE_TIME);
        PORT_CONN_NODES.put(portConnNode.getPortConnId(), portConnNode);
    }

    @Override
    public void storageConnHeartbeat(StorageConnNode storageConnNode) throws RegistryException {
        storageConnNode.setHeartbeatTime(System.currentTimeMillis());
        String json = toJson(storageConnNode);
        this.redisRegistryService.set(storageConnNode.getRegistryCode(), json, EXPIRE_TIME);
        STORAGE_CONN_NODES.put(storageConnNode.getStorageConnId(), storageConnNode);
    }

    /**
     * To json
     * @param o node
     * @return String
     * @throws RegistryException RegistryException
     */
    private String toJson(Object o) throws RegistryException {
        try {
            return ObjectMapperUtil.toJson(o);
        } catch (JsonProcessingException e) {
            String msg = "The node json serialize found exception" + o.toString();
            log.error(msg, e);
            throw new RegistryException(msg);
        }
    }

    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }
}
