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
package org.apex.dataverse.port.worker;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.enums.ConnType;
import org.apex.dataverse.core.enums.EngineType;
import org.apex.dataverse.port.config.SparkEngineConfig;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.service.IStorageConnectionService;
import org.apex.dataverse.port.service.IPortDaemonService;
import org.apex.dataverse.port.service.IStoragePortService;
import org.apex.dataverse.port.worker.abs.AbsWorker;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;
import org.apex.dataverse.storage.service.IJdbcStorageService;
import org.apex.dataverse.storage.service.IOdpcStorageService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Danny.Huo
 * @date 2023/11/28 18:54
 * @since 0.1.0
 */
@Slf4j
public class PortDaemonWorker extends AbsWorker {

    private final DvsPort dvsPort;

    private final SparkEngineConfig sparkEngineConfig;

    private final IStoragePortService storagePortService;

    private final IJdbcStorageService jdbcStorageService;

    private final IOdpcStorageService odpcStorageService;

    private final IStorageConnectionService connectionService;

    private final IPortDaemonService portDaemonService;

    public PortDaemonWorker(DvsPort dvsPort,
                            SparkEngineConfig sparkEngineConfig,
                            IStoragePortService storagePortService,
                            IJdbcStorageService jdbcStorageService,
                            IOdpcStorageService odpcStorageService,
                            IStorageConnectionService connectionService,
                            IPortDaemonService portDaemonService) {
        this.dvsPort = dvsPort;
        this.sparkEngineConfig = sparkEngineConfig;
        this.storagePortService = storagePortService;
        this.jdbcStorageService = jdbcStorageService;
        this.odpcStorageService = odpcStorageService;
        this.connectionService = connectionService;
        this.portDaemonService = portDaemonService;
    }

    @Override
    public void run() {
        while (this.isRunning()) {
            try {
                this.portStorageGard();

                this.portDaemonService.portHeartbeat(dvsPort);

                this.sleep();

            } catch (InterruptedException | IOException | AppLaunchException
                     | SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Port storage gard
     *
     * @throws IOException        IOException
     * @throws AppLaunchException AppLaunchException
     */
    private void portStorageGard() throws IOException, AppLaunchException,
            SQLException, ClassNotFoundException, InterruptedException {

        List<StoragePort> portStorages = storagePortService.getByPortCode(dvsPort.getPortCode());
        if (null == portStorages || portStorages.isEmpty()) {
            return;
        }

        for (StoragePort storagePort : portStorages) {
            if (ConnType.JDBC.getConnType().equalsIgnoreCase(storagePort.getConnType())) {
                JdbcStorage jdbcStorage = this.jdbcStorageService.getByStorageId(storagePort.getStorageId());
                handleJdbcStorage(storagePort, jdbcStorage);
            } else if (ConnType.ODPC.getConnType().equalsIgnoreCase(storagePort.getConnType())) {
                OdpcStorage odpcStorage = this.odpcStorageService.getByStorageId(storagePort.getStorageId());
                handleOdpcStorage(storagePort, odpcStorage);
            }
        }
    }

    /**
     * Handle jdbc storage
     * @param storagePort storage port
     * @param jdbcStorage jdbc storage
     * @throws ClassNotFoundException  ClassNotFoundException
     * @throws SQLException SQLException
     */
    private void handleJdbcStorage(StoragePort storagePort, JdbcStorage jdbcStorage)
            throws ClassNotFoundException, SQLException, JsonProcessingException, InterruptedException {
        for (int i = 0; i < storagePort.getMinJdbcConns(); i++) {
            // Create storage connection
            //StorageConnection jdbcStorageConn = this.connectionService.createJdbcStorageConn(storagePort, jdbcStorage);

            // Register storage connection
            //this.connectionService.registryStorageConnection(dvsPort, jdbcStorageConn);
        }
    }

    /**
     * Handle odpc storage
     * @param odpcStorage Odpc storage
     * @param storagePort storagePort
     */
    private void handleOdpcStorage(StoragePort storagePort, OdpcStorage odpcStorage)
            throws AppLaunchException, IOException {
        int i = sparkEngineConfig.getEngineExchanger().quantity(storagePort.getStorageId());
        for (; i < storagePort.getMinOdpcEngines(); i++) {
            // TODO check engine type,  spark or flink
            Engine engine = null;
            // Create odpc connection by engine type, spark/flink
            if(EngineType.SPARK_ENGINE.getName().equalsIgnoreCase(storagePort.getEngineType())) {
                //engine = this.portDaemonService.createSparkEngine(storagePort);
            } else if (EngineType.FLINK_ENGINE.getName().equalsIgnoreCase(storagePort.getEngineType())) {
                //engine = this.portDaemonService.createFlinkEngine(storagePort);
            } else {
                log.error("Engine type not recognized， engine type => {}", storagePort.getEngineType() );
            }

            // Prepare registry engine
            //this.portDaemonService.preRegistryEngine(engine);
        }
    }
}
