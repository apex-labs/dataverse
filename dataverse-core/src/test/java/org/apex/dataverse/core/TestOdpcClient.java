package org.apex.dataverse.core;

import org.apex.dataverse.core.context.env.ClientEnv;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.enums.CmdState;
import org.apex.dataverse.core.exception.ProcessorException;
import org.apex.dataverse.core.msg.*;
import org.apex.dataverse.core.msg.packet.engine.EngineEventPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.netty.client.PortClient;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/2/14 14:57
 */
public class TestOdpcClient {

    private static ClientContext context;

    private static final Integer max = 1;

    private static NioEventLoopGroup executor = new NioEventLoopGroup(4);

    public static void main(String[] args) throws ProcessorException, InterruptedException {
        ClientEnv config = new ClientEnv();
        config.setServerAddress("127.0.0.1");
        config.setServerPort(19999);
        config.setThreads(3);
        config.setConnTimeOutMs(2000L);

        context = ClientContext.newContext(config);
        PortClient client = new PortClient(context);

        Thread thread = new Thread(client);
        thread.start();

        // 发送消息
        executor.execute(() -> {
            for (int i = 0; i < max; i++) {
                Request<Packet> request = new Request();
                Header header = new Header();
                header.setMagic(10);
                header.setSerialNo(100000L + i);
                header.setSerialize(ISerialize.PROTOBUF_SERIALIZE);
                header.setVersion((byte) 1);
                header.setCommand(CmdSet.ENGINE_EVENT.getReq());

                request.setHeader(header);
                EngineEventPacket statePacket = new EngineEventPacket();
                statePacket.setState(CmdState.SUCCESS.getCode());
                statePacket.setIp("127.0.0.1");
                statePacket.setPort(1000);
                statePacket.setRequestId("10001_" + i);
                request.setPacket(statePacket);
                try {
                    context.pushRequest(request);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 接收返回消息
        executor.execute(() -> {
            long start = System.currentTimeMillis();
            int count = 0;
            while (true) {
                Response<Packet> packetResponse;
                try {
                    packetResponse = context.takeResponse();
                    count ++;
                    if (count >= max) {
                        System.out.println("发送并返回" + count + "条数据区耗时" + (System.currentTimeMillis() - start) + "毫秒");
                        System.out.println("消息体长度为：" + packetResponse.getHeader().getBodyLength());
                        System.out.println("request size = " + context.requestSize() + " response size = " + context.responseSize());
                        System.out.println("response:  " + ObjectMapperUtil.toJson(packetResponse));
                        client.closeChannel();
                    }
                    if (count % 100000 == 0) {
                        System.out.println("request size = " + context.requestSize() + " response size = " + context.responseSize());
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
