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
package org.apex.dataverse.port.core.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.core.exception.RegistryException;

import java.io.Serializable;

/**
 * @author Danny.Huo
 * @date 2023/8/10 13:59
 * @since 0.1.0
 */
@Data
@Slf4j
public class RegistryInfo implements Serializable {

    private final static String EMPTY_CHAR = "";
    private final static String PROTOCOL = "redis:";
    private final static String PARAM_START_CHAR = "\\?";
    private final static String PRAM_SPLIT_CHAR = "&";
    private final static String NODE_SPLIT_CHAR = ",";
    private final static String NODE_DB_SPLIT = "/";
    private final static String PARAM_PAIR_SPLIT = "=";
    private final static String PROTOCOL_SPLIT_CHAR = "//";
    private final static String PORT_SPLIT_CHAR = ":";
    private final static String SPLIT_CHAR = "/";

    private final static String MASTER = "master";
    private final static String MIN_IDLE = "minIdle";
    private final static String MAX_IDLE = "maxIdle";
    private final static String MAX_ACTIVE = "maxActive";
    private final static String MAX_REDIRECTS = "maxRedirects";


    /**
     * url
     */
    private String url;

    /**
     * 密码
     */
    private String password;

    /**
     * redis的模式
     */
    private String mode;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 节点
     * [ip:port,...]
     */
    private String[] nodes;

    /**
     * master节点名称
     */
    private String masterName;

    private Integer minIdle;

    private Integer maxIdle;

    private Integer maxActive;

    private Integer maxRedirects;

    /**
     * 库名
     */
    private Integer database;

    /**
     * 有参构造
     *
     * @param url url
     * @param password password
     */
    public RegistryInfo(String url, String password) throws RegistryException {
        parseParam(url);
        this.password = password;
    }

    /**
     * redis:cluster//${node}:${port},.../${db}?master=mymaster&maxRedirects=3&minIdle=2&maxIdle=2&maxActive=2
     * redis:sentinel//${node}:${port},.../${db}?master=mymaster&minIdle=2&maxIdle=2&maxActive=2
     * redis:single//${node}:${port}/${db}?minIdle=2&maxIdle=2&maxActive=2
     *
     * @param url url
     */
    private void parseParam(String url) throws RegistryException {
        if (null == url) {
            throw new RegistryException("Odpc registry url is null");
        }

        String redisInfo = url.replace(PROTOCOL, EMPTY_CHAR);
        String[] split = redisInfo.split(PROTOCOL_SPLIT_CHAR);
        this.mode = split[0];

        String[] split1 = split[1].split(PARAM_START_CHAR);
        String paramsStr = split1[1];

        String[] split2 = split1[0].split(NODE_DB_SPLIT);
        this.database = Integer.parseInt(split2[1]);
        this.nodes = split2[0].split(NODE_SPLIT_CHAR);

        String[] params = paramsStr.split(PRAM_SPLIT_CHAR);
        for (String param : params) {
            String[] paramPair = param.split(PARAM_PAIR_SPLIT);
            switch (paramPair[0]) {
                case MASTER:
                    this.masterName = paramPair[1];
                    break;
                case MIN_IDLE:
                    this.minIdle = Integer.parseInt(paramPair[1]);
                    break;
                case MAX_IDLE:
                    this.maxIdle = Integer.parseInt(paramPair[1]);
                    break;
                case MAX_ACTIVE:
                    this.maxActive = Integer.parseInt(paramPair[1]);
                    break;
                case MAX_REDIRECTS:
                    this.maxRedirects = Integer.parseInt(paramPair[1]);
                    break;
                default:
                    log.info("Unknown url param {}", paramsStr);
                    break;
            }
        }


    }
}
