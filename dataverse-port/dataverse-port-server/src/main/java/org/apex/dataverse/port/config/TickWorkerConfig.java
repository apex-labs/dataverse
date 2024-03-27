package org.apex.dataverse.port.config;

import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.port.worker.mex.EngineExchanger;
import org.apex.dataverse.port.worker.PortTickWorker;
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
public class TickWorkerConfig {

    private ExecutorService executorService;

    private ServerContext stateServerContext;

    private EngineExchanger engineExchanger;

    /**
     * 状态处理worker
     * @return PortTickWorker
     */
    @Bean
    public PortTickWorker stateCmdWorker() {
        PortTickWorker portTickWorker = new PortTickWorker(stateServerContext,
                engineExchanger);
        executorService.execute(portTickWorker);
        return portTickWorker;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Autowired
    public void setStateServerContext(ServerContext stateServerContext) {
        this.stateServerContext = stateServerContext;
    }

    @Autowired
    public void setEngineExchanger(EngineExchanger engineExchanger) {
        this.engineExchanger = engineExchanger;
    }
}
