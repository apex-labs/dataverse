package org.apex.dataverse.port.config;

import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.port.service.IStorageConnectionService;
import org.apex.dataverse.port.worker.mex.CmdExchanger;
import org.apex.dataverse.port.worker.mex.EngineExchanger;
import org.apex.dataverse.port.service.IJobHistoryService;
import org.apex.dataverse.port.worker.PortReqWorker;
import org.apex.dataverse.port.worker.mex.StorageExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;

/**
 * @author Danny.Huo
 * @date 2023/5/4 18:50
 * @since 0.1.0
 */
@Configuration
public class PortWorkerConfig {

    private ExecutorService executorService;

    private ServerContext serverContext;

    private CmdExchanger cmdExchanger;

    private StorageExchanger storageExchanger;

    /**
     * Odpc命令处理器
     *
     * @return AdaptorReqWorker
     */
    @Bean
    public PortReqWorker portWorker() {
        PortReqWorker portWorker = new PortReqWorker(this.serverContext,
                this.cmdExchanger, storageExchanger);
        executorService.execute(portWorker);
        return portWorker;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Autowired
    public void setServerContext(ServerContext portServerContext) {
        this.serverContext = portServerContext;
    }

    @Autowired
    public void setChannelExchanger(CmdExchanger cmdExchanger) {
        this.cmdExchanger = cmdExchanger;
    }

    @Autowired
    public void setStorageExchanger(StorageExchanger storageExchanger) {
        this.storageExchanger = storageExchanger;
    }
}
