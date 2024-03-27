package org.apex.dataverse.port.driver;

import org.apex.dataverse.core.context.Pipeline;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.netty.server.PortServer;

/**
 * @author Danny.Huo
 * @date 2023/2/22 10:26
 * @since 0.1.0
 */
public class App {

    /**
     * Main
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        ServerContext context = ServerContext.newContext();
        PortServer portServer = new PortServer(context);
        new Thread(portServer).start();
        Pipeline pipeline = context.getPipeline();
        Request<Packet> msg;
        while (null != (msg = pipeline.takeRequest())) {
            System.out.println(msg);
        }
    }

}
