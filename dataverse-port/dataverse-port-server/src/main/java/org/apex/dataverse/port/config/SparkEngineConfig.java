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
package org.apex.dataverse.port.config;

import lombok.Data;
import org.apex.dataverse.core.util.AddressUtil;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.worker.mex.EngineExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2023/11/28 19:17
 * @since 0.1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "nexus.odpc.engine")
public class SparkEngineConfig {

    @Value("${nexus.odpc.engine.jar-location}")
    private String jarLocation;

    @Value("${nexus.odpc.engine.main-class}")
    private String mainClass;

    @Value("${nexus.odpc.engine.master}")
    private String master;

    @Value("${nexus.odpc.engine.deploy-mode}")
    private String deployMode;

    @Value("${nexus.odpc.engine.redirect-path}")
    private String redirectPath;

    @Value("${nexus.odpc.engine.tick-interval-ms}")
    private Integer heartbeatInterval = 5000;

    @Value("${nexus.odpc.engine.boss-thread}")
    private Integer engineBossThreads = 2;

    @Value("${nexus.odpc.engine.worker-thread}")
    private Integer engineWorkerThreads = 4;

    @Value("${nexus.odpc.engine.dependence-jars}")
    private String dependenceJars;

    /**
     * Spark args
     */
    private Map<String, String> sparkArgs;

    /**
     * Spark conf
     */
    private Map<String, String> sparkConf;


    private EngineExchanger engineExchanger;


    /**
     * 构建Spark App args
     * @param engine DataEngine
     * @return Map<String, String>
     */
    public Map<String, String> buildAppArgs(Engine engine) {
        Map<String, String> appArgs = new HashMap<>();
        appArgs.put("--master", this.getMaster());
        appArgs.put("--enable-hive", "true");
        appArgs.put("--tick-server-address", AddressUtil.getLocalAddress());
        appArgs.put("--tick-server-port", "20001");
        appArgs.put("--tick-client-threads", "2");
        appArgs.put("--tick-interval-ms", this.getHeartbeatInterval().toString());
        appArgs.put("--engine-server-port", engine.getPort().toString());
        appArgs.put("--engine-server-boss-threads", "2");
        appArgs.put("--engine-server-worker-threads", "4");
        appArgs.put("--engine-id", engine.getEngineId().toString());
        return appArgs;
    }

    @Autowired
    public void setEngineExchanger(EngineExchanger engineExchanger) {
        this.engineExchanger = engineExchanger;
    }
}
