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

import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.NoEngineException;
import org.apex.dataverse.port.exception.StorageConnException;
import org.apex.dataverse.port.worker.mex.EngineContext;
import org.apex.dataverse.port.worker.mex.StorageConnPool;
import org.apex.dataverse.port.worker.mex.StorageConnection;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Danny.Huo
 * @date 2024/1/4 17:04
 * @since 0.1.0
 */
public interface IStorageConnectionService {

    /**
     * Create storage connection pool
     * @param storageId storage id
     * @return StorageConnPool
     */
    StorageConnPool createConnPool(Long storageId) throws StorageConnException;

    /**
     * 根据Storage id 创建 StorageConnection
     * @param storagePort storagePort
     * @return StorageConnection
     */
    StorageConnection createConnection(StoragePort storagePort)
            throws StorageConnException, ClassNotFoundException,
            SQLException, NoEngineException, AppLaunchException, IOException;

    /**
     * 根据Storage id 创建 StorageConnection
     * @param storageId storageId
     * @return StorageConnection
     */
    StorageConnection createConnection(Long storageId)
            throws StorageConnException, ClassNotFoundException,
            SQLException, NoEngineException, AppLaunchException, IOException;

    /**
     * Create spark engine
     * @param storagePort storage port
     * @return Engine
     * @throws IOException IOException
     * @throws AppLaunchException AppLaunchException
     */
    Engine createSparkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException;


    /**
     * Collect to engine
     * @param engine Engine
     * @return EngineContext
     */
    EngineContext connectEngine(Engine engine);

    /**
     * Get storage by storage id
     * @param storageId storage id
     * @return StorageConnection
     */
    StorageConnection getConnection(Long storageId) throws AppLaunchException, SQLException, NoEngineException, IOException, ClassNotFoundException, StorageConnException;

}
