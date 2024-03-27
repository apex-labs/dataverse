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

import lombok.extern.slf4j.Slf4j;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.longlong.Roaring64NavigableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Danny.Huo
 * @date 2024/3/13 19:27
 * @since 0.1.0
 */
@Slf4j
@Configuration
public class BitmapConfig {

    /**
     * 人群包Bitmap
     */
    private HashMap<String, Roaring64NavigableMap> bitmaps;

    /**
     * 人群包数量
     */
    private final int audienceCount = 10;

    /**
     * 人群包中用户数量
     */
    private final int audienceMaxUser = 10000000;

    /**
     * Bitmap对象
     * @return Map<String, RoaringBitmap>
     */
    @Bean
    public Map<String, Roaring64NavigableMap> bitmaps () {

        bitmaps = new HashMap<>();

        log.info("Start to init audience bit map ...");
        //initAudienceBitMap();

        log.info("print memory usage ...");
        printMemoryUsage();

        return bitmaps;
    }

    /**
     * 模拟初始化人群包数量
     */
    private void initAudienceBitMap() {
        Random random = new Random(9999999999L);
        for (int i = 0; i < audienceCount; i++) {
            Roaring64NavigableMap bitmap = new Roaring64NavigableMap();
            String audienceId = "A_" + i;
            bitmaps.put(audienceId, bitmap);

            log.info("Init audience {} ...", audienceId);

            for (int j = 0; j < audienceMaxUser; j++) {
                bitmap.add(random.nextInt());
            }

            if(i < 10) {
                bitmap.add(6666);
            } else if (i > 10 && i < 20) {
                bitmap.add(777);
            } else {
                bitmap.add(888);
            }
        }
    }

    /**
     * 打印内存使用情况
     */
    private static void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        // JVM尝试使用的最大内存量
        long maxMemory = runtime.maxMemory();

        // JVM已分配的内存总量
        long totalMemory = runtime.totalMemory();

        // JVM中空闲的内存量
        long freeMemory = runtime.freeMemory();

        // JVM已使用的内存量
        long usedMemory = totalMemory - freeMemory;

        log.info("Max memory: " + maxMemory / 1024 / 1024 + " MB");
        log.info("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        log.info("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        log.info("Used memory: " + usedMemory / 1024 / 1024 + " MB");
    }
}
