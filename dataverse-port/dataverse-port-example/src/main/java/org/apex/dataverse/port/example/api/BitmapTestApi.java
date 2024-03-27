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
package org.apex.dataverse.port.example.api;

import org.apex.dataverse.port.example.vo.BitmapAudienceVo;
import org.roaringbitmap.RoaringBitmap;
import org.roaringbitmap.longlong.Roaring64NavigableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Danny.Huo
 * @date 2024/3/13 19:34
 * @since 0.1.0
 */
@RestController
@RequestMapping("/bitmap")
public class BitmapTestApi {

    private Map<String, Roaring64NavigableMap> bitmaps;

    @GetMapping("/getAudience/{userId}")
    public Object getAudiences(@PathVariable("userId") Integer userId) {
        long start = System.currentTimeMillis();
        Iterator<Map.Entry<String, Roaring64NavigableMap>> iterator = bitmaps.entrySet().iterator();
        List<String> audiences = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<String, Roaring64NavigableMap> next = iterator.next();
            if(next.getValue().contains(userId)){
                audiences.add(next.getKey());
            }
        }
        long timeConsuming = System.currentTimeMillis() - start;

        return new BitmapAudienceVo(audiences, timeConsuming);
    }

    @Autowired
    public void setBitmaps(Map<String, Roaring64NavigableMap> bitmaps) {
        this.bitmaps = bitmaps;
    }
}
