package org.apex.dataverse.core;

import org.apex.dataverse.core.context.env.ClientEnv;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.exception.ProcessorException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlJobReqPacket;
import org.apex.dataverse.core.msg.packet.info.SqlInfo;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;
import org.apex.dataverse.core.msg.packet.info.output.HdfsOutput;
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
public class TestDiAndDDSparkApp {

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
            long start = System.currentTimeMillis();
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
        // 抽取user表
        Request<Packet> request = new Request();
        Header header = Header.newHeader(CmdSet.DATA_INTEGRATION.getReq());
        request.setHeader(header);
        DiReqPacket diPacket = new DiReqPacket();
        diPacket.setJobId(UCodeUtil.produce());
        diPacket.setCommandId(UCodeUtil.produce());
        diPacket.setId(UCodeUtil.produce());
        diPacket.setName("Danny 抽数");
        diPacket.setDriver("com.mysql.jdbc.Driver");
        diPacket.setQuery("select * from a_user limit 20000");
        diPacket.setUrl("jdbc:mysql://?:3306/ecommerce_min?useUnicode=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8&allowMultiQueries=true&useCursorFetch=true&serverTimezone=Asia/Shanghai");
        diPacket.setUser("dev");
        diPacket.setPassword("?");
        diPacket.setOutput(HdfsOutput.newOutput("/tmp/datavs/test/a_user/20230425"));

        request.setPacket(diPacket);
        try {
            context.pushRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 抽取订单表
        request = new Request();
        header = Header.newHeader(CmdSet.DATA_INTEGRATION.getReq());
        request.setHeader(header);
        diPacket = new DiReqPacket();
        diPacket.setJobId(UCodeUtil.produce());
        diPacket.setCommandId(UCodeUtil.produce());
        diPacket.setId(UCodeUtil.produce());
        diPacket.setName("Danny 抽数");
        diPacket.setDriver("com.mysql.jdbc.Driver");
        diPacket.setQuery("select * from a_order limit 20000");
        diPacket.setUrl("jdbc:mysql://?:3306/ecommerce_min?useUnicode=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8&allowMultiQueries=true&useCursorFetch=true&serverTimezone=Asia/Shanghai");
        diPacket.setUser("dev");
        diPacket.setPassword("?");
        diPacket.setOutput(HdfsOutput.newOutput("/tmp/Dvs/test/a_order/20230425"));

        request.setPacket(diPacket);
        try {
            context.pushRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // 执行ETL JOB
        request = new Request();
        header = Header.newHeader(CmdSet.EXE_SQL_JOB.getReq());
        request.setHeader(header);

        ExeSqlJobReqPacket exeSqlJobReqPacket = new ExeSqlJobReqPacket();
        exeSqlJobReqPacket.setJobId(UCodeUtil.produce());
        exeSqlJobReqPacket.setCommandId(UCodeUtil.produce());
        exeSqlJobReqPacket.setId(UCodeUtil.produce());
        request.setPacket(exeSqlJobReqPacket);
        exeSqlJobReqPacket.setExeEnv((byte) 1);
        //  store info
        StoreInfo userStoreInfo = new StoreInfo("a_user", "/tmp/Dvs/test/a_user/20230425");
        StoreInfo orderStoreInfo = new StoreInfo("a_order", "/tmp/Dvs/test/a_order/20230425");
        List<StoreInfo> storeInfos = new ArrayList<>();
        storeInfos.add(userStoreInfo);
        storeInfos.add(orderStoreInfo);
        exeSqlJobReqPacket.setStoreInfos(storeInfos);

        //  sql info
        String sql = "select a.user_id, a.user_name, a.real_name, a.gender, a.phone, a.open_id, a.card_id, a.city, o.order_id, o.total_amount, o.discount_amount, o.due_amount, o.actual_pay_amount, o.contact_name, o.contact_mobile, o.create_time, o.update_time from a_user a left join a_order o on a.user_id = o.user_id";
        SqlInfo sqlInfo = new SqlInfo(1, sql, "user_order", "/tmp/Dvs/test/a_user_order/20230425");
        List<SqlInfo> sqlInfos = new ArrayList<>();
        sqlInfos.add(sqlInfo);
        exeSqlJobReqPacket.setSqlInfos(sqlInfos);

        try {
            context.pushRequest(request);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
