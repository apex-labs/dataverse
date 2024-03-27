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
package org.apex.dataverse.port.example.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Danny.Huo
 * @date 2024/3/13 19:54
 * @since 0.1.0
 */
@Data
public class BitmapAudienceVo {

    public BitmapAudienceVo() {

    }

    public BitmapAudienceVo(List<String> audiences, Long timeConsuming) {
        this.audiences = audiences;
        this.timeConsuming = timeConsuming;
    }

    private List<String> audiences;

    private Long timeConsuming;
}
