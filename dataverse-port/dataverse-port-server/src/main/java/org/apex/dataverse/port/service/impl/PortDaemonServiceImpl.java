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
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.enums.EngineState;
import org.apex.dataverse.core.enums.EngineType;
import org.apex.dataverse.core.util.*;
import org.apex.dataverse.port.config.PortServerConfig;
import org.apex.dataverse.port.config.SparkEngineConfig;
import org.apex.dataverse.port.core.enums.RegistryKeyEnum;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherContext;
import org.apex.dataverse.port.core.launcher.spark.SparkLauncherUtil;
import org.apex.dataverse.port.core.node.PortNode;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StorageConn;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.service.*;
import org.apex.dataverse.port.worker.mex.StorageConnection;
import org.apex.dataverse.port.worker.mex.StorageExchanger;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * @author Danny.Huo
 * @date 2024/1/2 17:57
 * @since 0.1.0
 */
@Service
public class PortDaemonServiceImpl implements IPortDaemonService {

    /**
     * Spark engine config
     */
    private SparkEngineConfig sparkEngineConfig;

    /**
     * Storage connection exchanger
     */
    private StorageExchanger scExchanger;

    private RedisRegistryService redisRegistryService;

    private PortServerConfig portServerConfig;

    private IEngineService engineService;

    private IDvsPortService dvsPortService;

    private IStorageConnService storageConnService;

    private ServerContext portServerContext;


    @Override
    public void registry(String key, Object v, Long expire) {
        this.redisRegistryService.set(key, v, expire);
    }

    @Override
    public DvsPort initDvsPort(String registryCode) throws IOException {
        String portCode = portServerConfig.getCode();
        DvsPort dvsPort = new DvsPort();
        dvsPort.setPortCode(portCode);
        String localHostname = AddressUtil.getLocalHostname();
        dvsPort.setPortName("port_" + localHostname);
        dvsPort.setPort(AddressUtil.getFreePort());
        String ip = AddressUtil.getLocalAddress();
        dvsPort.setIp(ip);
        dvsPort.setCreatorId(0L);
        dvsPort.setCreatorName("System");
        dvsPort.setDescription("Auto create by system  when deploy port server at " + localHostname + "(" + ip + ")");
        dvsPort.setHostname(localHostname);
        dvsPort.setMaxDriverConns(portServerConfig.getMaxDriverConns());
        dvsPort.setHeartbeatHz(portServerConfig.getHeartbeatHz());
        dvsPort.setRegistryCode(registryCode);
        dvsPort.setState("BUILD");
        dvsPort.setCreateTime(LocalDateTime.now());
        dvsPortService.save(dvsPort);

        return dvsPort;
    }


    @Override
    public StorageConnection createJdbcStorageConn(StoragePort storagePort, JdbcStorage jdbcStorage)
            throws ClassNotFoundException, SQLException {
        // Establish a JDBC connection to the storage
        Class.forName(jdbcStorage.getDriverClass());
        Connection connection = DriverManager.getConnection(jdbcStorage.getJdbcUrl(),
                jdbcStorage.getUserName(), jdbcStorage.getPassword());

        // Save storage connection
        StorageConn storageConn = new StorageConn();
        storageConn.setStorageId(storagePort.getStorageId());
        storageConn.setRegistryCode("sc_conn" + UCodeUtil.produce());
        storageConn.setConnTime(LocalDateTime.now());
        storageConn.setPortId(storagePort.getPortId());
        storageConn.setPortCode(storagePort.getPortCode());
        storageConn.setIp(AddressUtil.getLocalAddress());
        storageConn.setHostname(AddressUtil.getLocalHostname());
        storageConn.setState("");
        storageConn.setDescription("");
        storageConnService.save(storageConn);

        // New jdbc connection and return it
        return StorageConnection.newJdbcConn(storagePort, connection, portServerContext);
    }

    @Override
    public StorageConnection createOdpcStorageConn(StoragePort storagePort, OdpcStorage odpcStorage) throws ClassNotFoundException, SQLException {
        return null;
    }

    @Override
    public Engine createSparkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException {
        Engine engine = new Engine();
        engine.setStorageId(storagePort.getStorageId());
        engine.setEngineJar(sparkEngineConfig.getJarLocation());
        engine.setEngineName(AddressUtil.getLocalHostname());
        engine.setPort(AddressUtil.getFreePort());
        engine.setDriverCup(4);
        engine.setExecutorCup(8);
        engine.setDriverMemory(2);
        engine.setExecutorMemory(4);
        engine.setExecutors(4);
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
        context.setMainClass(sparkEngineConfig.getMainClass());
        context.setEngineJar(sparkEngineConfig.getJarLocation());
        context.setMaster(sparkEngineConfig.getMaster());
        context.setDeployMode(sparkEngineConfig.getDeployMode());
        context.setAppName("spark-engine" + "_" + engine.getEngineName() + "_" + engine.getEngineId());
        context.setRedirectError(sparkEngineConfig.getRedirectPath() + "/" + engine.getEngineName() + "_" + engine.getEngineId() + "spark-error.log");
        context.setRedirectOut(sparkEngineConfig.getRedirectPath() + "/" + engine.getEngineName() + "_" + engine.getEngineId() + "spark-out.log");
        if (StringUtil.isNotBlank(sparkEngineConfig.getDependenceJars())) {
            context.setDependenceJars(sparkEngineConfig.getDependenceJars().split(","));
        }

        context.setAppArgs(sparkEngineConfig.buildAppArgs(engine));

        context.setSparkArgs(sparkEngineConfig.getSparkArgs());

        try {
            // start engine
            SparkLauncherUtil.launcherSpark(context);

        } catch (AppLaunchException e) {
            engine.setDescription(ExceptionUtil.getStackTrace(e));
            throw e;
        } finally {
            if(null != context.getSparkHandleListener()) {
                engine.setApplicationId(context.getSparkHandleListener().getApplicationId());
            }
            engine.setApplicationName(context.getAppName());

            engineService.saveOrUpdate(engine);
        }

        return engine;
    }

    @Override
    public Engine createFlinkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException {
        return null;
    }

    @Override
    public void registryStorageConnection(DvsPort dvsPort, StorageConnection conn) throws JsonProcessingException, InterruptedException {
        StorageConn storageConn = new StorageConn();
        storageConn.setStorageId(conn.getStorageId());
        storageConn.setPortId(dvsPort.getPortId());
        storageConn.setPortCode(dvsPort.getPortCode());
        storageConn.setIp(dvsPort.getIp());
        storageConn.setHostname(dvsPort.getHostname());
        storageConn.setConnTime(LocalDateTime.now());
        String registryCode = RegistryKeyEnum.buildStorageConnKey(conn.getStorageId().toString(), UCodeUtil.produce());
        storageConn.setRegistryCode(registryCode);

        // Sava to database
        this.storageConnService.save(storageConn);

        // add to storage conn exchange
        scExchanger.addOnline(conn);

        //  registry to redis
        this.registry(storageConn.getRegistryCode(), ObjectMapperUtil.toJson(storageConn),  20000L);
    }


    @Override
    public void unRegistryStorageConnection(StorageConnection conn) {

    }

    @Override
    public void storageConnectionHeartbeat(StorageConnection conn) {

    }



    @Override
    public void registryPortConnection() {

    }

    @Override
    public void unRegistryPortConnection() {

    }

    @Override
    public void portConnectionHeartbeat() {

    }

    @Override
    public void registryPort() {

    }

    @Override
    public void unRegistryPort() {

    }

    @Override
    public void portHeartbeat(DvsPort dvsPort) throws JsonProcessingException {
        Object o = this.redisRegistryService.get(dvsPort.getPortCode());
        PortNode portNode;
        if(null != o) {
            portNode = ObjectMapperUtil.toObject(o.toString(), PortNode.class);
        } else {
            portNode = new PortNode();
            BeanUtils.copyProperties(dvsPort, portNode);
        }
        portNode.setHeartbeatTime(System.currentTimeMillis());
        this.redisRegistryService.portHeartbeat(portNode, dvsPort.getHeartbeatHz() + 2000L );
    }

    @Override
    public void preRegistryEngine(Engine engine) throws JsonProcessingException {
        this.redisRegistryService.set(engine.getRegistryCode(),
                ObjectMapperUtil.toJson(engine), 2000L);
    }

    @Override
    public void unRegistryEngine() {

    }

    @Override
    public void engineHeartbeat() {

    }


    @Autowired
    public void setSparkEngineConfig(SparkEngineConfig sparkEngineConfig) {
        this.sparkEngineConfig = sparkEngineConfig;
    }

    @Autowired
    public void setScExchanger(StorageExchanger scExchanger) {
        this.scExchanger = scExchanger;
    }

    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }

    @Autowired
    public void setPortServerConfig(PortServerConfig portServerConfig) {
        this.portServerConfig = portServerConfig;
    }

    @Autowired
    public void setDvsPortService(IDvsPortService dvsPortService) {
        this.dvsPortService = dvsPortService;
    }

    @Autowired
    public void setEngineService(IEngineService engineService) {
        this.engineService = engineService;
    }

    @Autowired
    public void setStorageConnService(IStorageConnService storageConnService) {
        this.storageConnService = storageConnService;
    }

    @Autowired
    public void setPortServerContext(ServerContext portServerContext) {
        this.portServerContext = portServerContext;
    }
}
