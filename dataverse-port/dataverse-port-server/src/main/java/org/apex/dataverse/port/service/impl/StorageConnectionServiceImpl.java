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

import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.context.env.ClientEnv;
import org.apex.dataverse.core.enums.ConnType;
import org.apex.dataverse.core.enums.EngineState;
import org.apex.dataverse.core.enums.EngineType;
import org.apex.dataverse.core.netty.client.EngineClient;
import org.apex.dataverse.core.util.AddressUtil;
import org.apex.dataverse.core.util.ExceptionUtil;
import org.apex.dataverse.core.util.StringUtil;
import org.apex.dataverse.core.util.UCodeUtil;
import org.apex.dataverse.port.config.SparkEngineConfig;
import org.apex.dataverse.port.core.enums.RegistryKeyEnum;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherContext;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherUtil;
import org.apex.dataverse.port.core.node.EngineNode;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StorageConn;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.NoEngineException;
import org.apex.dataverse.port.exception.StorageConnException;
import org.apex.dataverse.port.service.IStorageConnectionService;
import org.apex.dataverse.port.service.IEngineService;
import org.apex.dataverse.port.service.IStorageConnService;
import org.apex.dataverse.port.service.IStoragePortService;
import org.apex.dataverse.port.worker.PortRspWorker;
import org.apex.dataverse.port.worker.mex.*;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;
import org.apex.dataverse.storage.entity.Storage;
import org.apex.dataverse.storage.service.IJdbcStorageService;
import org.apex.dataverse.storage.service.IOdpcStorageService;
import org.apex.dataverse.storage.service.IStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * @author Danny.Huo
 * @date 2024/1/6 17:05
 * @since 0.1.0
 */
@Slf4j
@Service
public class StorageConnectionServiceImpl implements IStorageConnectionService {

    private DvsPort dvsPort;

    private RedisRegistryService redisRegistryService;

    private IStoragePortService storagePortService;

    private IEngineService engineService;

    private IStorageService storageService;

    private IJdbcStorageService jdbcStorageService;

    private IOdpcStorageService odpcStorageService;

    private IStorageConnService storageConnService;

    private SparkEngineConfig enginePoolConfig;

    private EngineExchanger engineExchanger;

    private ExecutorService executorService;

    private CmdExchanger cmdExchanger;

    private ServerContext portServerContext;

    private StorageExchanger storageExchanger;


    @Override
    public StorageConnPool createConnPool(Long storageId) throws StorageConnException {
        StoragePort storagePort = storagePortService.getByStorageAndPortId(storageId, dvsPort.getPortId());
        if (null == storagePort) {
            throw new StorageConnException("port[" + dvsPort.getPortId() + "] cannot manipulate storage[" + storageId + "]");
        }
        return StorageConnPool.newPool(storagePort, this);
    }

    @Override
    public StorageConnection createConnection(StoragePort storagePort) throws StorageConnException,
            ClassNotFoundException, SQLException, AppLaunchException, IOException {

        Storage storage = storageService.getById(storagePort.getStorageId());

        if (ConnType.JDBC.getConnType().equalsIgnoreCase(storage.getConnType())) {
            return createJdbcStorageConnection(storagePort);
        } else if (ConnType.ODPC.getConnType().equalsIgnoreCase(storage.getConnType())) {
            return createOdpcStorageConnection(storagePort);
        }

        throw new StorageConnException("The conn type [" + storage.getConnType() + "] is mismatch in (JDBC, ODPC)");
    }

    @Override
    public StorageConnection createConnection(Long storageId) throws StorageConnException,
            ClassNotFoundException, SQLException, AppLaunchException, IOException {

        Storage storage = storageService.getById(storageId);

        List<StoragePort> storagePorts = storagePortService.getByPortId(dvsPort.getPortId());

        StoragePort[] suitableStoragePort = (StoragePort[]) storagePorts.stream().filter(storagePort -> storagePort.getStorageId().equals(storageId)).toArray();
        if (suitableStoragePort.length < 1) {
            throw new StorageConnException("Current port[" + dvsPort.getPortId() + "] no suitable storage port mapping for storage " + storageId);
        }

        if (ConnType.JDBC.getConnType().equalsIgnoreCase(storage.getConnType())) {
            return createJdbcStorageConnection(suitableStoragePort[0]);
        } else if (ConnType.ODPC.getConnType().equalsIgnoreCase(storage.getConnType())) {
            return createOdpcStorageConnection(suitableStoragePort[0]);
        }

        throw new StorageConnException("The conn type [" + storage.getConnType() + "] is mismatch in (JDBC, ODPC)");
    }

    /**
     * Create jdbc storage connection
     *
     * @param storagePort StoragePort
     * @return StorageConnection
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    private StorageConnection createJdbcStorageConnection(StoragePort storagePort) throws ClassNotFoundException, SQLException {
        JdbcStorage jdbcStorage = jdbcStorageService.getByStorageId(storagePort.getStorageId());

        // Establish a JDBC connection to the storage
        Class.forName(jdbcStorage.getDriverClass());
        Connection connection = DriverManager.getConnection(jdbcStorage.getJdbcUrl(),
                jdbcStorage.getUserName(), jdbcStorage.getPassword());

        // Save storage connection
        StorageConn storageConn = new StorageConn();
        storageConn.setStorageId(storagePort.getStorageId());
        storageConn.setRegistryCode("sc_conn" + UCodeUtil.produce());
        storageConn.setConnTime(LocalDateTime.now());
        storageConn.setPortId(dvsPort.getPortId());
        storageConn.setPortCode(dvsPort.getPortCode());
        storageConn.setIp(AddressUtil.getLocalAddress());
        storageConn.setHostname(AddressUtil.getLocalHostname());
        storageConn.setState("");
        storageConn.setDescription("");
        storageConnService.save(storageConn);

        // New jdbc connection and return it
        return StorageConnection.newJdbcConn(storagePort, connection, portServerContext);
    }

    /**
     * Create odpc storage connection
     *
     * @param storagePort StoragePort
     * @return StorageConnection
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException           SQLException
     */
    private StorageConnection createOdpcStorageConnection(StoragePort storagePort) throws ClassNotFoundException, SQLException, AppLaunchException, IOException {
        OdpcStorage odpcStorage = odpcStorageService.getByStorageId(storagePort.getStorageId());

        EngineContext engine;
        try {
            engine = engineExchanger.findEngine(storagePort.getStorageId());
        } catch (NoEngineException e) {
            Engine sparkEngine = this.createSparkEngine(storagePort);

            // Pre register engine
            engineExchanger.preregister(storagePort.getStorageId(), sparkEngine);

            EngineContext engineContext = new EngineContext();
            engineContext.setStoragePort(storagePort);
            BeanUtils.copyProperties(sparkEngine, engineContext);
            engineContext.setFree(10);
            engineContext.setUsed(0);

            EngineNode engineNode = new EngineNode();
            BeanUtils.copyProperties(sparkEngine, engineNode);

            // connected to engine after 2 s
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
            engine = connectEngine(sparkEngine);
            engineContext.setEngineClient(engine.getEngineClient());
            engineContext.setEngineNode(engineNode);

            // Register engine
            engineExchanger.register(storagePort.getStorageId(), engineContext);
        }

        // New jdbc connection and return it
        return StorageConnection.newOdpcConn(storagePort, engine, portServerContext);
    }


    @Override
    public Engine createSparkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException {
        Engine engine = new Engine();
        engine.setStorageId(storagePort.getStorageId());
        engine.setEngineJar(enginePoolConfig.getJarLocation());
        engine.setEngineName(AddressUtil.getLocalHostname());
        engine.setPort(AddressUtil.getFreePort());
        // TODO set default
        engine.setDriverCup(4);
        engine.setExecutorCup(4);
        engine.setDriverMemory(2);
        engine.setExecutorMemory(8);
        engine.setExecutors(2);
        engine.setHostname(AddressUtil.getLocalHostname());
        engine.setIp(AddressUtil.getLocalAddress());
        engine.setEngineState(EngineState.STARTING.name());
        engine.setEngineType(EngineType.SPARK_ENGINE.name());
        engine.setCreateTime(LocalDateTime.now());
        engine.setPortId(storagePort.getPortId());
        engine.setPortCode(storagePort.getPortCode());
        engine.setPortName(storagePort.getPortName());
        String engineRegistryCode = RegistryKeyEnum.buildEngineKey(storagePort.getPortCode(), UCodeUtil.produce());
        engine.setRegistryCode(engineRegistryCode);
        // Save engine to database
        engineService.save(engine);

        SparkLauncherContext context = new SparkLauncherContext();
        context.setMainClass(enginePoolConfig.getMainClass());
        context.setEngineJar(enginePoolConfig.getJarLocation());
        context.setMaster(enginePoolConfig.getMaster());
        context.setDeployMode(enginePoolConfig.getDeployMode());
        context.setAppName("spark-engine" + "_" + engine.getEngineName() + "_" + engine.getEngineId());
        context.setRedirectError(enginePoolConfig.getRedirectPath() + "/" + engine.getEngineName() + "_" + engine.getEngineId() + "spark-error.log");
        context.setRedirectOut(enginePoolConfig.getRedirectPath() + "/" + engine.getEngineName() + "_" + engine.getEngineId() + "spark-out.log");
        if (StringUtil.isNotBlank(enginePoolConfig.getDependenceJars())) {
            context.setDependenceJars(enginePoolConfig.getDependenceJars().split(","));
        }

        context.setAppArgs(enginePoolConfig.buildAppArgs(engine));

        context.setSparkArgs(enginePoolConfig.getSparkArgs());

        try {
            // start spark engine
            SparkLauncherUtil.launcherSpark(context);

        } catch (AppLaunchException e) {
            engine.setDescription(ExceptionUtil.getStackTrace(e));
            throw e;
        } finally {
            if (null != context.getSparkHandleListener()) {
                engine.setApplicationId(context.getSparkHandleListener().getApplicationId());
            }
            engine.setApplicationName(context.getAppName());

            engineService.saveOrUpdate(engine);
        }

        EngineNode engineNode = new EngineNode();
        BeanUtils.copyProperties(engine, engineNode);
        redisRegistryService.registryEngine(engineNode);

        return engine;
    }

    private Integer heartbeatInterval = 5000;

    @Override
    public EngineContext connectEngine(Engine engine) {
        EngineContext engineContext = new EngineContext();

        ClientEnv clientConfig = new ClientEnv();
        clientConfig.setServerAddress(engine.getIp());
        clientConfig.setServerPort(engine.getPort());
        ClientContext clientContext = ClientContext.newContext(clientConfig);
        EngineClient engineClient = new EngineClient(clientContext);

        engineContext.setEngineClient(engineClient);
        engineContext.setHeartbeatInterval(heartbeatInterval);

        // TODO 测试代码，默认给5个空闲数
        engineContext.setFree(5);

        // 设置EngineNode
        EngineNode engineNode = new EngineNode();
        BeanUtils.copyProperties(engine, engineNode);
        engineContext.setEngineNode(engineNode);

        // 启动客户端，连接Engine
        Future<?> future = executorService.submit(engineClient);

        engineContext.setReqFuture(future);

        // 启动ResponseWorker
        engineContext.setRspFuture(startRspWorker(clientContext));

        if (engineClient.isActive()) {
            engineExchanger.register(engine.getStorageId(), engineContext);
        } else {
            log.error("Connect to server failed, Cancel it {}", future.cancel(true));
        }
        return engineContext;
    }

    @Override
    public StorageConnection getConnection(Long storageId)
            throws AppLaunchException, SQLException, NoEngineException,
            IOException, ClassNotFoundException, StorageConnException {
        StorageConnection connection = this.storageExchanger.getConnection(storageId);
        if (null == connection) {
            throw new StorageConnException("Storage[" + storageId + "] get null connection from storage exchanger");
        }

        return connection;
    }

    /**
     * Start response worker
     *
     * @param clientContext ClientContext
     * @return Future<?>
     */
    private Future<?> startRspWorker(ClientContext clientContext) {
        PortRspWorker odpcRspWorker = new PortRspWorker(clientContext,
                portServerContext, cmdExchanger);

        return executorService.submit(odpcRspWorker);
    }


    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }

    @Autowired
    public void setOdpcStorageService(IOdpcStorageService odpcStorageService) {
        this.odpcStorageService = odpcStorageService;
    }

    @Autowired
    public void setJdbcStorageService(IJdbcStorageService jdbcStorageService) {
        this.jdbcStorageService = jdbcStorageService;
    }

    @Autowired
    public void setStoragePortService(IStoragePortService storagePortService) {
        this.storagePortService = storagePortService;
    }

    @Autowired
    public void setStorageService(IStorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    public void setStorageConnService(IStorageConnService storageConnService) {
        this.storageConnService = storageConnService;
    }

    @Autowired
    public void setDvsPort(DvsPort dvsPort) {
        this.dvsPort = dvsPort;
    }

    @Autowired
    public void setEnginePoolConfig(SparkEngineConfig enginePoolConfig) {
        this.enginePoolConfig = enginePoolConfig;
    }

    @Autowired
    public void setEngineExchanger(EngineExchanger engineExchanger) {
        this.engineExchanger = engineExchanger;
    }

    @Autowired
    public void setEngineService(IEngineService engineService) {
        this.engineService = engineService;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Autowired
    public void setCmdExchanger(CmdExchanger cmdExchanger) {
        this.cmdExchanger = cmdExchanger;
    }

    @Autowired
    public void setPortServerContext(ServerContext portServerContext) {
        this.portServerContext = portServerContext;
    }

    @Autowired
    public void setStorageExchanger(StorageExchanger storageExchanger) {
        this.storageExchanger = storageExchanger;
    }
}
