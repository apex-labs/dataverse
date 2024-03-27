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
package org.apex.dataverse.port.core.node;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.util.UCodeUtil;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Danny.Huo
 * @date 2024/1/4 14:46
 * @since 0.1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StorageConnNode extends Node implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 引擎客户端链接历史记录ID
     */
    private Long storageConnId;

    /**
     * 数据计算引擎ID，当为hdfs存储区时，引擎不为空
     */
    private Long engineId;

    /**
     * 引擎名称
     */
    private String engineName;

    /**
     * Port的ID
     */
    private Long portId;

    /**
     * Port的编码
     */
    private String portCode;

    /**
     * 连接的Channel ID
     */
    private String channelId;

    /**
     * 连接客户端的IP地址, port链接egine时port的ip
     */
    private String ip;

    /**
     * 连接客户端的host name, port链接egine时port的host
     */
    private String hostname;

    /**
     * 连接时间
     */
    private LocalDateTime connTime;

    /**
     * 断开连接时间
     */
    private LocalDateTime disConnTime;

    /**
     * 链接状态
     */
    private String state;

    /**
     * 描述
     */
    private String description;

    public StorageConnNode() {
        super.setNodeId(UCodeUtil.produce());
    }

}
