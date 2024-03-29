package org.apex.dataverse.core;

import org.apex.dataverse.core.context.env.ServerEnv;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.exception.ProcessorException;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.netty.server.PortServer;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/2/14 13:51
 */
public class TestOdpcServer {

    public static void main(String[] args) throws InterruptedException, ProcessorException {
        ServerEnv config = new ServerEnv();
        config.setWorkerThreads(6);
        config.setServerPort(19999);
        config.setBossThreads(3);

        ServerContext context = ServerContext.newContext(config);
        PortServer server = new PortServer(context);

        Thread thread = new Thread(server);
        thread.start();

        while (true) {
            Request<Packet> request = context.takeRequest();
            try {
                System.out.println(ObjectMapperUtil.toJson(request));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
//            Response<Packet> response = new Response<>();
//            response.setChannelId(request.getChannelId());
//            Header header = request.getHeader();
//            header.setCommand(CmdSet.ENGINE_STAGE.getRsp());
//            response.setHeader(header);
//            response.setPacket(request.getPacket());

            Response<Packet> response = request.newRsp(Response.SUCCESS);
            context.pushResponse(response);
        }
    }
}
