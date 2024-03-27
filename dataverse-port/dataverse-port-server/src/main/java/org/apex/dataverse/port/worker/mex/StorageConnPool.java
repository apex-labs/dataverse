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
import org.apex.dataverse.core.enums.ConnType;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.NoEngineException;
import org.apex.dataverse.port.exception.StorageConnException;
import org.apex.dataverse.port.service.IStorageConnectionService;

import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Danny.Huo
 * @date 2024/1/3 13:30
 * @since 0.1.0
 */
@Data
@Slf4j
public class StorageConnPool implements Serializable {

    /**
     * Storage port mapping info
     */
    private StoragePort storagePort;

    /**
     * Connection total count
     */
    private int connCount;

    /**
     * The number of connections in use
     */
    private int usedCount;

    /**
     * Store free connections
     */
    public final LinkedBlockingQueue<StorageConnection> freePool;

    /**
     * Store in-use connections
     */
    public final Map<Integer, StorageConnection> busyPool;

    /**
     * Offline connection
     */
    private final LinkedBlockingQueue<StorageConnection> offline;

    /**
     * Connection service
     */
    private final IStorageConnectionService storageConnectionService;

    /**
     * New pool
     *
     * @param storagePort       Storage port
     * @param connectionService connection service
     * @return StorageConnPool
     */
    public static StorageConnPool newPool(StoragePort storagePort,
                                          IStorageConnectionService connectionService) {
        StorageConnPool scPool = new StorageConnPool(connectionService);
        scPool.setStoragePort(storagePort);
        return scPool;
    }

    /**
     * Constructor
     */
    private StorageConnPool(IStorageConnectionService storageConnectionService) {
        offline = new LinkedBlockingQueue<>();
        busyPool = new ConcurrentHashMap<>();
        freePool = new LinkedBlockingQueue<>();
        this.storageConnectionService = storageConnectionService;
    }

    /**
     * Add to busy connection pool
     *
     * @param conn PortConnection
     * @throws InterruptedException InterruptedException
     */
    private synchronized void addToBusy(StorageConnection conn) throws InterruptedException {
        busyPool.put(conn.hashCode(), conn);
        conn.setBusy(true);
        this.connAutoIncr();
        this.connedAutoIncr();
        log.info("Connection is add to busy and it's total count is " + this.connCount);
    }

    /**
     * Connection add to this pool
     *
     * @param conn PortConnection
     * @param use  Use flag, true used
     * @throws InterruptedException InterruptedException
     */
    public synchronized void add(StorageConnection conn, boolean use) throws InterruptedException {
        if (use) {
            addToBusy(conn);
        } else {
            conn.setBusy(false);
            this.freePool.add(conn);
        }
    }

    /**
     * Back connection to this pool
     *
     * @param conn PortConnection
     */
    public synchronized StorageConnection use(StorageConnection conn) {
        if(null == conn) {
            return null;
        }

        this.busyPool.put(conn.hashCode(), conn);
        conn.setBusy(true);
        return conn;
    }

    /**
     * Back connection to this pool
     *
     * @param conn PortConnection
     * @throws InterruptedException InterruptedException
     */
    public synchronized void back(StorageConnection conn) throws InterruptedException {
        StorageConnection toFree = this.busyPool.remove(conn.hashCode());
        if (null != toFree) {
            this.freePool.add(toFree);
        } else {
            log.warn("The connection[{}] is not busy, will add to free", conn.toString());
            this.freePool.add(conn);
        }
    }

    /**
     * Destroy connection
     *
     * @param conn PortConnection
     */
    public synchronized void destroy(StorageConnection conn) throws SQLException, InterruptedException {
        if (null == conn) {
            return;
        }
        if (ConnType.ODPC.getConnType().equalsIgnoreCase(conn.getConnType())) {
            conn.getEngineContext().getEngineClient().closeChannel();
        } else if (ConnType.JDBC.getConnType().equalsIgnoreCase(conn.getConnType())) {
            conn.getConnection().close();
        }
    }

    /**
     * Get connection
     *
     * @return PortConnection
     */
    public synchronized StorageConnection getConnection() throws
            StorageConnException, SQLException, ClassNotFoundException, NoEngineException, AppLaunchException, IOException {
        StorageConnection conn = this.freePool.poll();
        if (null != conn) {
            return conn;
        }

        // todo
        if (this.isFull()) {
            String msg = "Storage connection is full, "
                    + ", connType  => " + this.storagePort.getConnType()
                    + ", busySize => " + busyPool.size()
                    + ", freeSize  => " + this.freePool.size();

            throw new StorageConnException(msg);
        }

        // Create storage connection and return it
        conn = this.storageConnectionService.createConnection(storagePort);

        // set to use and return
        return this.use(conn);
    }

    /**
     * 判断连接池是否已满
     *
     * @return boolean
     */
    public boolean isFull() {
        if (ConnType.JDBC.getConnType().equalsIgnoreCase(this.storagePort.getConnType())) {
            return this.connCount >= this.storagePort.getMaxJdbcConns();
        } else {
            return this.connCount >= this.storagePort.getMaxOdpcEngines();
        }
    }

    /**
     * The number of conn increases
     */
    private synchronized void connAutoIncr() {
        this.connCount++;
    }

    /**
     * The number of conn decreases
     */
    private synchronized void connAutoDecr() {
        this.connCount--;
    }

    /**
     * Conned increases
     */
    private synchronized void connedAutoIncr() {
        this.usedCount++;
    }

    /**
     * Conned decreases
     */
    private synchronized void connedAutoDecr() {
        this.usedCount--;
    }
}
