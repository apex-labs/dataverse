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
public class PortConnNode extends Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long portConnId;

    /**
     * Port的ID
     */
    private Long portId;

    /**
     * Port的编码
     */
    private String portCode;

    /**
     * driver端的IP
     */
    private String ip;

    /**
     * driver端的hostname
     */
    private String hostname;

    /**
     * 链接状态
     */
    private String state;

    /**
     * 链接时间
     */
    private LocalDateTime connTime;

    /**
     * 断开链接时间
     */
    private LocalDateTime disConnTime;

    /**
     * 描述
     */
    private String description;

    public PortConnNode() {
        super.setNodeId(UCodeUtil.produce());
    }
}
