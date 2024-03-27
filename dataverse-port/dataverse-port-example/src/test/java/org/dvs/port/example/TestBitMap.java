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

import org.roaringbitmap.RoaringBitmap;

/**
 * @author Danny.Huo
 * @date 2024/3/13 15:31
 * @since 0.1.0
 */
public class TestBitMap {

    private static RoaringBitmap groupA;
    private static RoaringBitmap groupB;

    private static void init() {
        groupA = new RoaringBitmap();
        groupB = new RoaringBitmap();
        for (int i = 0; i < 99999999; i++) {
            groupA.add(i);
        }
        for (int i = 456789; i < 104567890; i++) {
            groupB.add(i);
        }
    }

    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();
        init();
        long t2 = System.currentTimeMillis();
        RoaringBitmap andnot = RoaringBitmap.andNot(groupA,groupB);
        long t3 = System.currentTimeMillis();
        RoaringBitmap and = RoaringBitmap.and(groupA,groupB);
        long t4 = System.currentTimeMillis();
        RoaringBitmap or = RoaringBitmap.or(groupA,groupB);
        long t5 = System.currentTimeMillis();
        RoaringBitmap xor = RoaringBitmap.xor(groupA,groupB);
        long t6 = System.currentTimeMillis();

        System.out.println(String.valueOf(andnot.toArray().length));
        System.out.println(String.valueOf(and.toArray().length));
        System.out.println(String.valueOf(or.toArray().length));
        System.out.println(String.valueOf(xor.toArray().length));

        System.out.println(t6-t5);
        System.out.println(t5-t4);
        System.out.println(t4-t3);
        System.out.println(t3-t2);
        System.out.println(t2-t1);
    }
}
