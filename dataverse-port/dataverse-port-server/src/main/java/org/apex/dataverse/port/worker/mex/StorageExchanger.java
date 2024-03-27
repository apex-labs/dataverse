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
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.NoEngineException;
import org.apex.dataverse.port.exception.StorageConnException;
import org.apex.dataverse.port.service.IStorageConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Danny.Huo
 * @date 2023/12/29 17:15
 * @since 0.1.0
 */
@Slf4j
@Data
@Component
public class StorageExchanger implements Serializable {

    /**
     * Redis registry service
     */
    private RedisRegistryService redisRegistryService;

    /**
     * Online storage connection
     * key : storageId
     * val : StorageConnection
     */
    private final Map<Long, StorageConnPool> pools = new ConcurrentHashMap<>();

    /**
     * Connection service
     */
    private IStorageConnectionService storageConnectionService;

    /**
     * Add online storage connection
     *
     * @param conn storage connection
     */
    public void addOnline(StorageConnection conn) throws InterruptedException {

        StorageConnPool storageConnPool = this.pools.get(conn.getStorageId());

        if (null == storageConnPool) {
            storageConnPool = StorageConnPool.newPool(conn.getStoragePort(), storageConnectionService);
            this.pools.put(conn.getStorageId(), storageConnPool);
        }

        storageConnPool.back(conn);
    }

    /**
     * Get connection by storage id
     * @param storagePort storage id
     * @return StorageConnection
     * @throws SQLException SQLException
     * @throws NoEngineException NoEngineException
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws StorageConnException StorageConnException
     */
    public StorageConnection getConnection(StoragePort storagePort)
            throws SQLException, NoEngineException, ClassNotFoundException, StorageConnException, AppLaunchException, IOException {
        StorageConnPool storageConnPool = this.pools.get(storagePort.getStorageId());
        if(null == storageConnPool) {
            // if storage connection pool is not exist, initialize.
            storageConnPool = StorageConnPool.newPool(storagePort, this.storageConnectionService);
        }
        return storageConnPool.getConnection();
    }

    /**
     * Get connection by storage id
     * @param storageId storage id
     * @return StorageConnection
     * @throws SQLException SQLException
     * @throws NoEngineException NoEngineException
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws StorageConnException StorageConnException
     */
    public synchronized StorageConnection getConnection(Long storageId)
            throws SQLException, NoEngineException, ClassNotFoundException, StorageConnException, AppLaunchException, IOException {
        if(null == storageId) {
            throw new StorageConnException("StorageId cannot be empty when obtaining a StorageConnection");
        }

        StorageConnPool storageConnPool = this.pools.get(storageId);
        if(null == storageConnPool) {
            // if storage connection pool is null, will create a storage connection pool
            storageConnPool = this.storageConnectionService.createConnPool(storageId);
            this.pools.put(storageId, storageConnPool);
        }
        return storageConnPool.getConnection();
    }

    /**
     * Back storage connection to storage connection pool
     * @return StorageConnection
     * @throws InterruptedException SQLException
     */
    public synchronized StorageConnection back(StorageConnection conn)
            throws InterruptedException {
        StorageConnPool storageConnPool = this.pools.get(conn.getStorageId());
        if(null != storageConnPool) {
            storageConnPool.back(conn);
        }
        return conn;
    }

    /**
     * Add online storage connection
     *
     * @param conn storage connection
     */
    public void addOffline(StorageConnection conn) {
        // get storage pool
        StorageConnPool storageConnPool = this.pools.get(conn.getStorageId());

        if (null == storageConnPool) {
            return;
        }

        // remove from online
        StorageConnection removed = storageConnPool.busyPool.remove(conn.hashCode());

        // Add  to offline
        storageConnPool.getOffline().add(conn);
    }

    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }

    @Autowired
    public void setStorageConnectionService(IStorageConnectionService storageConnectionService) {
        this.storageConnectionService = storageConnectionService;
    }
}
