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

import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.engine.StartEngineReqPacket;

/**
 * @author Danny.Huo
 * @date 2023/11/6 17:16
 * @since 0.1.0
 */
public class TestPipeline {

    public static void main(String[] args) throws InterruptedException, InvalidCmdException {
        Pipeline pipeline = Pipeline.newDefaultPipeline();

        Request<Packet> request = new Request<>();
        request.setRequest(true);
        request.setHeader(Header.newHeader((byte) 2));
        request.setPacket(new StartEngineReqPacket());
        request.getPacket().setCommandId("1001");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000 * 10);
                    Response<Packet> response = new Response<>();
                    response.setHeader(Header.newHeader((byte) 2));
                    response.setPacket(new StartEngineReqPacket());
                    response.getPacket().setCommandId("1001");
                    System.out.println("返回结果：" + System.currentTimeMillis());
                    pipeline.pushResponse(response);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        t.start();

        Response<Packet> response = pipeline.exeCmd(request);
        System.out.println("取到结果：" + System.currentTimeMillis() + " => "  + response);
        System.out.println("End");
    }
}
