package org.apex.dataverse.port.config;

import jodd.io.FileUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.core.context.ServerContext;
import org.apex.dataverse.core.context.env.ServerEnv;
import org.apex.dataverse.core.netty.server.PortServer;
import org.apex.dataverse.core.util.AddressUtil;
import org.apex.dataverse.core.util.StringUtil;
import org.apex.dataverse.core.util.UCodeUtil;
import org.apex.dataverse.port.core.enums.RegistryKeyEnum;
import org.apex.dataverse.port.core.exception.RegistryException;
import org.apex.dataverse.port.core.node.PortNode;
import org.apex.dataverse.port.core.redis.RedisRegistryService;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.exception.PortRunningException;
import org.apex.dataverse.port.service.*;
import org.apex.dataverse.port.worker.PortDaemonWorker;
import org.apex.dataverse.storage.service.IJdbcStorageService;
import org.apex.dataverse.storage.service.IOdpcStorageService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 1、Create bean of port server context
 * 2、Create bean of port server
 * 3、Init dvs port info
 *
 * @author Danny.Huo
 * @date 2023/2/21 18:55
 * @since 0.1.0
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "nexus.odpc.server")
public class PortServerConfig {

    /**
     * When port code is not specified,
     * the system automatically generates port code and stores it in this file
     */
    private final static String PORT_CODE_FILE = ".DVS_PORT_CODE";

    /**
     * Registry offset
     * The registry offset is port's heartbeatHz plus it
     */
    private final static Long EXPIRE_OFFSET = 2000L;

    /**
     * Port Node for registry
     */
    private PortNode portNode;

    /**
     * Port server ss started or not
     */
    private boolean started = true;

    /**
     * PortServer unique code, each port unique,
     * does not configure the system generated
     */
    private String code;

    /**
     * The port server's worker threads for netty server
     */
    @Value("${nexus.odpc.server.worker-thread}")
    private Integer workerThread;

    /**
     * The port server's boss threads for netty server
     */
    @Value("${nexus.odpc.server.boss-thread}")
    private Integer bossThread;

    /**
     * The port server's server port
     */
    private Integer serverPort;

    /**
     * Max connections for driver
     */
    private Integer maxDriverConns;

    /**
     * Port server's heartbeat hz
     */
    private Integer heartbeatHz;


    private ExecutorService executorService;

    private RedisRegistryService redisRegistryService;

    private SparkEngineConfig sparkEngineConfig;

    private IHeartbeatService heartbeatService;


    private IDvsPortService dvsPortService;

    private IStoragePortService storagePortService;

    private IJdbcStorageService jdbcStorageService;

    private IOdpcStorageService odpcStorageService;

    private IStorageConnectionService connectionService;

    private IPortDaemonService portDaemonService;

    /**
     * Init dataverse port
     * @return DvsPort
     * @throws IOException IOException
     * @throws PortRunningException PortRunningException
     */
    @Bean
    public DvsPort dvsPort() throws IOException, PortRunningException, RegistryException {
        // Check port code
        if (StringUtil.isBlank(this.code)) {
            String userDir = System.getProperty("user.dir");
            File file = new File(userDir + "/" + PORT_CODE_FILE);
            if (file.exists()) {
                String[] strings = FileUtil.readLines(file);
                this.code = strings[0].trim();
            } else {
                this.code = "port_" + UCodeUtil.produce();
                FileUtil.writeString(file, this.code);
            }
        }

        // Port registry code
        List<StoragePort> storagePorts = storagePortService.getByPortCode(this.code);
        String registryCode = RegistryKeyEnum.buildPortKey(null, this.code);
        int size;
        if(null != storagePorts && (size = storagePorts.size()) > 0) {
            String[] storageIds = new String[size];
            for (int i = 0; i < size; i++) {
                storageIds[i] = storagePorts.get(i).getStorageId().toString();
            }
            registryCode = RegistryKeyEnum.buildPortKey(storageIds, this.code);
            log.info("Port[{}] have being bind storage, registry code is {}", this.code, registryCode);
        } else {
            log.warn("Port[{}] does not bind storage, please bind storage first", this.code);
        }

        if (null != redisRegistryService.get(registryCode)) {
            // A port with the same code is already running
            throw new PortRunningException("A port with the same code [" + this.code + "] is already running!");
        }

        DvsPort port = dvsPortService.getByCode(this.code);

        if (null == port) {
            // First start,  init dataverse port
            port = this.initDvsPort(registryCode);
        } else {
            // update registry code and save to database
            port.setRegistryCode(registryCode);
            dvsPortService.saveOrUpdate(port);
        }

        // Register with Redis
        portNode = new PortNode();
        BeanUtils.copyProperties(port, portNode);
        heartbeatService.portHeartbeat(portNode);

        return port;
    }

    /**
     * Initialize dataverse port, save to database
     * @param registryCode registryCode
     * @return DvsPort
     * @throws IOException IOException
     */
    private DvsPort initDvsPort(String registryCode) throws IOException {
        String portCode = this.code;
        DvsPort dvsPort = new DvsPort();
        dvsPort.setPortCode(portCode);
        String localHostname = AddressUtil.getLocalHostname();
        dvsPort.setPortName("port_" + localHostname);
        dvsPort.setPort(AddressUtil.getFreePort());
        String ip = AddressUtil.getLocalAddress();
        dvsPort.setIp(ip);
        dvsPort.setCreatorId(0L);
        dvsPort.setCreatorName("System");
        dvsPort.setDescription("Auto created by system when deploy port server at " + localHostname + "(" + ip + ")");
        dvsPort.setHostname(localHostname);
        dvsPort.setMaxDriverConns(this.maxDriverConns);
        dvsPort.setHeartbeatHz(this.heartbeatHz);
        dvsPort.setRegistryCode(registryCode);
        dvsPort.setState("BUILD");
        dvsPort.setCreateTime(LocalDateTime.now());
        dvsPortService.save(dvsPort);

        return dvsPort;
    }

    /**
     * Sleep
     */
    private void sleep() {
        try {
            Thread.sleep(this.heartbeatHz);
        } catch (InterruptedException e) {
            log.error("Port server sleep found error", e);
        }
    }

    @Bean
    @Qualifier("portServerContext")
    public ServerContext portServerContext(DvsPort dvsPort) throws IOException {
        this.serverPort = dvsPort.getPort();
        if (null == this.serverPort) {
            // If no port is specified, a random free port is specified
            this.serverPort = AddressUtil.getFreePort();
        }
        ServerEnv config = new ServerEnv();
        config.setServerPort(this.serverPort);
        config.setBossThreads(this.bossThread);
        config.setWorkerThreads(this.workerThread);
        return ServerContext.newContext(config);
    }

    @Bean
    public PortServer portServer(ServerContext portServerContext) {
        // Start PortServer
        PortServer server = new PortServer(portServerContext);
        executorService.execute(server);

        // Register PortServer
        //PortNode odpcNode = s.registerPort(portServerContext.getEnv());

        // Start heartbeat thread
        executorService.execute(()->{
            while (this.started) {
                try {
                    this.sleep();

                    heartbeatService.portHeartbeat(portNode);
                } catch (Exception e) {
                    log.error("Port server heartbeat thead found error", e);
                }
            }
        });

        return server;
    }

    @Bean
    public PortDaemonWorker portDaemonWorker(DvsPort dvsPort) {

        PortDaemonWorker portDaemonWorker = new PortDaemonWorker(dvsPort, sparkEngineConfig,
                storagePortService, jdbcStorageService,  odpcStorageService,
                connectionService, portDaemonService);

        // start task
        executorService.execute(portDaemonWorker);

        return portDaemonWorker;
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Autowired
    public void setRedisRegistryService(RedisRegistryService redisRegistryService) {
        this.redisRegistryService = redisRegistryService;
    }

    @Autowired
    public void setSparkEngineConfig(SparkEngineConfig sparkEngineConfig) {
        this.sparkEngineConfig = sparkEngineConfig;
    }

    @Autowired
    public void setDvsPortService(IDvsPortService dvsPortService) {
        this.dvsPortService = dvsPortService;
    }

    @Autowired
    public void setHeartbeatService(IHeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @Autowired
    public void setStoragePortService(IStoragePortService storagePortService) {
        this.storagePortService = storagePortService;
    }

    @Autowired
    public void setJdbcStorageService(IJdbcStorageService jdbcStorageService) {
        this.jdbcStorageService = jdbcStorageService;
    }

    @Autowired
    public void setOdpcStorageService(IOdpcStorageService odpcStorageService) {
        this.odpcStorageService = odpcStorageService;
    }

    @Autowired
    public void setConnectionService(IStorageConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @Autowired
    public void setPortDaemonService(IPortDaemonService portDaemonService) {
        this.portDaemonService = portDaemonService;
    }
}
