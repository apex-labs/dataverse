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
package org.dvs.port.example;

import com.clickhouse.client.internal.google.common.io.ByteArrayDataOutput;
import org.roaringbitmap.RoaringBitmap;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author Danny.Huo
 * @date 2024/3/13 15:45
 * @since 0.1.0
 */
public class TestBitMapSpace {

    private final static Map<String, RoaringBitmap> audienceBitMaps = new HashMap<>();

    public static void main(String[] args) throws IOException {
        initAudience();

        printMemoryUsage();

    }

    private static void initAudience() throws IOException {
        Random random = new Random(900000000L);
        for (int i = 0; i < 10; i++) {
            RoaringBitmap bitmap = new RoaringBitmap();
            audienceBitMaps.put("a_"+i, bitmap);

            System.out.println("init audience a_" + i);
            for (int j = 0; j < 10000000; j++) {
                bitmap.add(random.nextInt());
            }

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                DataOutput dataOutput = new DataOutputStream(baos);
                bitmap.serialize(dataOutput);
                byte[] byteArray = baos.toByteArray();
                System.out.println("bitmap size is : " + byteArray.length);
            }
        }
    }

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

        System.out.println("Max memory: " + maxMemory / 1024 / 1024 + " MB");
        System.out.println("Total memory: " + totalMemory / 1024 / 1024 + " MB");
        System.out.println("Free memory: " + freeMemory / 1024 / 1024 + " MB");
        System.out.println("Used memory: " + usedMemory / 1024 / 1024 + " MB");
    }
}
