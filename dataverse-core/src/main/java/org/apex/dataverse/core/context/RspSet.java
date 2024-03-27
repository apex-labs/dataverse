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
package org.apex.dataverse.core.context;

import lombok.Data;
import lombok.Getter;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Danny.Huo
 * @date 2023/10/27 20:09
 * @since 0.1.0
 */
@Data
public class RspSet implements Serializable {

    /**
     * 集合池的容量
     */
    public final static int RSP_SET_POOL_CAPACITY = 500;

    /**
     * Rsp数据的的容量
     */
    public final static int RSP_DATA_CAPACITY = 1000;

    /**
     * 默认超时时间
     */
    private final static long DEFAULT_TIMEOUT_MS = 1000 * 60 * 60L;

    /**
     * RspSet集合池
     */
    private final static LinkedBlockingQueue<RspSet> SET_POOL =
            new LinkedBlockingQueue<>(RSP_SET_POOL_CAPACITY);

    /**
     * 存放响应数据的队列
     */
    @Getter
    private LinkedBlockingQueue<Response<Packet>> rsps;

    /**
     * 是否存在结果数据
     */
    private boolean hasRsp;

    /**
     * 是否还有新结果数据
     */
    private boolean hasNext;

    /**
     * 获取RspSet对象
     *
     * @return RspSet对象
     */
    public static RspSet getRspSet() {
        RspSet set = SET_POOL.poll();
        if (null == set) {
            set = new RspSet();
        }
        return set;
    }

    /**
     * 无参构造
     */
    private RspSet() {
        rsps = new LinkedBlockingQueue<>(RSP_DATA_CAPACITY);
    }

    /**
     * Response in queue
     *
     * @param rsp 响应消息体
     *
     * @throws InterruptedException 阻塞队列异常
     */
    public void pushRsp(Response<Packet> rsp) throws InterruptedException {
        this.rsps.put(rsp);
        this.hasRsp = true;
    }

    /**
     * 获取结果
     * @return 消息体对象
     * @throws InterruptedException  阻塞队列异常
     */
    public Response<Packet> takeRsp() throws InterruptedException {
        return this.rsps.poll(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取结果
     * @param timeOutMs 超时时间
     * @return 消息体对象
     * @throws InterruptedException  阻塞队列异常
     */
    public Response<Packet> takeRsp(Long timeOutMs) throws InterruptedException {
        return this.rsps.poll(timeOutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 关闭回收RspSet
     */
    public void close() {
        this.clear();
        SET_POOL.add(this);
    }

    /**
     * Clear
     */
    public void clear() {
        this.rsps.clear();
    }
}
