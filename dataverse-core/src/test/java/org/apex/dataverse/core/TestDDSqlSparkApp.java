package org.apex.dataverse.core;

import org.apex.dataverse.core.context.env.ClientEnv;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.exception.ProcessorException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;
import org.apex.dataverse.core.netty.client.PortClient;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.core.util.UCodeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.netty.channel.nio.NioEventLoopGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @version : v0.1.0
 * @author : Danny.Huo
 * @date : 2023/4/23 20:18
 */
public class TestDDSqlSparkApp {

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

        // 发送指令
        sendCmd();

        // 接收返回消息
        executor.execute(() -> {
            while (true) {
                Response<Packet> packetResponse;
                try {
                    packetResponse = context.takeResponse();
                    System.out.println("消息体长度为：" + packetResponse.getHeader().getBodyLength());
                    System.out.println("request size = " + context.requestSize() + "\n response size = " + context.responseSize());
                    System.out.println("response is = " + ObjectMapperUtil.toJson(packetResponse));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private static void sendCmd() {

        // 执行ETL JOB
        Request<Packet> request = new Request();
        Header header = Header.newHeader(CmdSet.EXE_SQL.getReq());
        request.setHeader(header);

        ExeSqlReqPacket exeSqlReqPacket = new ExeSqlReqPacket();
        exeSqlReqPacket.setJobId(UCodeUtil.produce());
        exeSqlReqPacket.setCommandId(UCodeUtil.produce());
        exeSqlReqPacket.setId(UCodeUtil.produce());
        request.setPacket(exeSqlReqPacket);
        //exeSqlJobReqPacket.setId(UCodeUtil.produce());
        exeSqlReqPacket.setExeEnv((byte) 1);
        exeSqlReqPacket.setFetchMaxRows(1000);

        //  store info
        StoreInfo userStoreInfo = new StoreInfo("a_user_order", "/tmp/datavs/test/a_user_order/20230425");
        List<StoreInfo> storeInfos = new ArrayList<>();
        storeInfos.add(userStoreInfo);
        exeSqlReqPacket.setStoreInfos(storeInfos);

        //  sql
        String sql = "select * from a_user_order";
        exeSqlReqPacket.setSql(sql);
        exeSqlReqPacket.setFetchMaxRows(100);

        try {
            context.pushRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
