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
package org.apex.dataverse.core.netty.worker;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;

/**
 * @author Danny.Huo
 * @date 2023/12/1 18:48
 * @since 0.1.0
 */
@Slf4j
public class ServerWorker implements Runnable{

    private final ServerContext context;

    public static ServerWorker newWorker(ServerContext context) {
        return new ServerWorker(context);
    }

    private ServerWorker(ServerContext context) {
        this.context = context;
    }

    private boolean isRunning = true;


    @Override
    public void run() {
        while (isRunning) {
            try {
                Response<Packet> response = context.takeResponse();

                Channel channel = context.getChannel(response.getChannelId());

                // 收集Response回写给客户端
                channel.writeAndFlush(response);
            } catch (InterruptedException e) {
                log.error("Server port push response to driver found error ", e);
            }
        }
    }

    /**
     * Kill worker
     * @return boolean
     */
    public boolean kill() {
        this.isRunning = false;
        return true;
    }
}
