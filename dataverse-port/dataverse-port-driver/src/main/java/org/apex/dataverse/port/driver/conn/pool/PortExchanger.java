package org.apex.dataverse.port.driver.conn.pool;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.config.ConnectionPoolConfig;
import org.apex.dataverse.port.driver.conn.PortConnection;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * @author Danny.Huo
 * @date 2023/2/22 11:49
 * @since 0.1.0
 */
@Data
@Slf4j
public class PortExchanger {

    /**
     * Default group code
     */
    private final static String DEFAULT_GROUP_CODE = "default";

    /**
     * Connection pool config
     */
    private ConnectionPoolConfig poolConfig;

    /**
     * Thread executor service
     */
    private ExecutorService executorService;

    /**
     * key : storageId
     * value : ConnectionPool
     */
    private Map<String, PortConnPool> storagePool;


    public PortExchanger(ConnectionPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        storagePool = new HashMap<>();
        if(null != poolConfig.getExecutorService()) {
            this.executorService = poolConfig.getExecutorService();
        }
    }

    /**
     * Get connection by storage id
     *
     * @param storageId storage id
     *
     * @return OdpcConnection
     * @throws InterruptedException InterruptedException
     * @throws PortConnectionException OdpcConnectionException
     */
    public PortConnection getConn(String storageId, String groupCode) throws InterruptedException,
            PortConnectionException, NoPortNodeException, JsonProcessingException {
        PortConnPool connectionPool = storagePool.get(storageId);
        if(null == connectionPool) {
            connectionPool = new PortConnPool(poolConfig, storageId, groupCode);
            storagePool.put(storageId, connectionPool);
        }
        return connectionPool.getConn();
    }

    /**
     * Get connection by storage id
     *
     * @param storageId storage id
     *
     * @return OdpcConnection
     * @throws InterruptedException InterruptedException
     * @throws PortConnectionException OdpcConnectionException
     */
    public PortConnection getConn(String storageId) throws InterruptedException,
            PortConnectionException, NoPortNodeException, JsonProcessingException {
        return getConn(storageId, DEFAULT_GROUP_CODE);
    }

    /**
     * 关闭链接
     *
     * @param connection OdpcConnection
     * @throws InterruptedException  InterruptedException
     */
    public void close(PortConnection connection) throws InterruptedException {
        PortConnPool connectionPool = storagePool.get(connection.getStorageId());
        if(null != connectionPool) {
            connectionPool.back(connection);
        }  else {
            log.error("Connection is not exists, storageId => {}, groupCode => {}",
                    connection.getStorageId(), connection.getGroupCode());
        }
    }

    @Autowired
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
