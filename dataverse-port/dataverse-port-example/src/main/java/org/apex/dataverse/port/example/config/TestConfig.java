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
package org.apex.dataverse.port.example.config;

import lombok.Getter;
import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @author Danny.Huo
 * @date 2023/12/7 13:22
 * @since 0.1.0
 */
@Getter
@Configuration
public class TestConfig {

    private PortExchanger connExchanger;

    @Autowired
    public void setConnExchanger(PortExchanger connExchanger) {
        this.connExchanger = connExchanger;
    }
}
