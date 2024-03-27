package org.apex.dataverse.core;

import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.core.netty.client.PortClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @version : v1.0
 * @projectName : nexus-msg
 * @package : com.apex.jms.test
 * @className : TestClientShutdown
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2023/5/11 17:49
 * @updateUser :
 * @updateDate :
 * @updateRemark :
 */
public class TestClientShutdown {

    public static void main(String[] args) {
        ClientContext clientContext = ClientContext.newContext();
        PortClient odpcClient = new PortClient(clientContext);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.submit(odpcClient);

        PortClient odpcClient2 = new PortClient(clientContext);
        executorService.submit(odpcClient2);

        executorService.shutdown();
    }
}
