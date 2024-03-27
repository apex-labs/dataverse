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
package org.apex.dataverse.port.service;

import org.apex.dataverse.port.core.exception.RegistryException;
import org.apex.dataverse.port.core.node.EngineNode;
import org.apex.dataverse.port.core.node.PortConnNode;
import org.apex.dataverse.port.core.node.PortNode;
import org.apex.dataverse.port.core.node.StorageConnNode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2024/1/20 15:13
 * @since 0.1.0
 */
public interface IHeartbeatService {

    long HEARTBEAT_HZ = 5000L;

    long EXPIRE_TIME = HEARTBEAT_HZ + 3000L;

    /**
     * Port nodes
     */
    Map<Long, PortNode> PORT_NODES = new HashMap<>();

    /**
     * Engine nodes
     */
    Map<Long, EngineNode> ENGINE_NODES = new HashMap<>();

    /**
     * Port connection nodes
     */
    Map<Long, PortConnNode> PORT_CONN_NODES = new HashMap<>();

    /**
     * Storage connection nodes
     */
    Map<Long, StorageConnNode> STORAGE_CONN_NODES = new HashMap<>();

    /**
     * Port heartbeat
     *
     * @param portNode PortNode
     */
    void portHeartbeat(PortNode portNode) throws RegistryException;

    /**
     * Engine heartbeat
     * @param engineNode EngineNode
     */
    void engineHeartbeat(EngineNode engineNode) throws RegistryException;

    /**
     * Port connection heartbeat
     * @param portConnNode PortConnNode
     */
    void portConnHeartbeat(PortConnNode portConnNode) throws RegistryException;

    /**
     * Storage connection heartbeat
     * @param storageConnNode StorageConnNode
     */
    void storageConnHeartbeat(StorageConnNode storageConnNode) throws RegistryException;
}
