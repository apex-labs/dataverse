package org.apex.dataverse.port.config;

import org.apex.dataverse.core.context.env.ServerEnv;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.netty.server.TickServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * @author Danny.Huo
 * @date 2023/4/24 10:46
 * @since 0.1.0
 */
@Configuration
public class TickServerConfig {

    @Value("${nexus.odpc.server.tick-server-port}")
    private Integer stateServerPort;

    @Value("${nexus.odpc.server.tick-boss-thread}")
    private Integer stateBossThread;

    @Value("${nexus.odpc.server.tick-worker-thread}")
    private Integer stateWorkerThread;

    private ExecutorService executorService;

    @Bean
    @Qualifier("stateServerContext")
    public ServerContext stateServerContext() {
        ServerEnv config = new ServerEnv();
        config.setServerPort(this.stateServerPort);
        config.setBossThreads(this.stateBossThread);
        config.setWorkerThreads(this.stateWorkerThread);
        return ServerContext.newContext(config);
    }

    @Bean
    public TickServer stateServer(ServerContext stateServerContext) {
        TickServer tickServer = new TickServer(stateServerContext);
        executorService.execute(tickServer);
//        executorService.execute(() -> {
//            while (true) {
//                Request<Packet> request;
//                try {
//                    request = stateServerContext.takeRequest();
//                    if(request.getPacket() instanceof TrackingPacket) {
//                        System.out.println(request.getPacket().toString());
//                    }
//                    Response<Packet> response = request.newRsp();
//                    response.setChannelId(request.getChannelId());
//                    response.setPacket(request.getPacket());
//                    stateServerContext.pushResponse(response);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        });

        return tickServer;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
