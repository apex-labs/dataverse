package org.apex.dataverse.port.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.port.core.exception.AppLaunchException;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.worker.mex.StorageConnection;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @author Danny.Huo
 * @date 2024/1/2 17:56
 * @since 0.1.0
 */
public interface IPortDaemonService {

    /**
     * Set key and value with expire time
     *
     * @param key    String , key
     * @param v      Object, value
     * @param expire long, expire time
     */
    void registry(String key, Object v, Long expire);

    /**
     * Initialize port, save to database
     *
     * @param registryCode registryCode
     * @return DvsPort
     * @throws IOException IOException
     */
    DvsPort initDvsPort(String registryCode) throws IOException;


    /**
     * Create JDBC Connection for storage
     * @param storagePort storagePort
     * @param jdbcStorage jdbcStorage
     * @return Connection
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException SQLException
     */
    StorageConnection createJdbcStorageConn(StoragePort storagePort, JdbcStorage jdbcStorage)
            throws ClassNotFoundException, SQLException;

    /**
     * Create JDBC Connection for storage
     * @param storagePort storagePort
     * @param odpcStorage OdpcStorage, odpcStorage
     * @return Connection
     * @throws ClassNotFoundException ClassNotFoundException
     * @throws SQLException SQLException
     */
    StorageConnection createOdpcStorageConn(StoragePort storagePort, OdpcStorage odpcStorage)
            throws ClassNotFoundException, SQLException;

    /**
     * Create Spark engine
     * @param storagePort storagePort
     * @return Connection
     * @throws AppLaunchException ClassNotFoundException
     * @throws IOException SQLException
     */
    Engine createSparkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException;

    /**
     * Create Flink engine
     * @param storagePort storagePort
     * @return Connection
     * @throws AppLaunchException ClassNotFoundException
     * @throws IOException SQLException
     */
    Engine createFlinkEngine(StoragePort storagePort)
            throws IOException, AppLaunchException;

    void registryStorageConnection(DvsPort dvsPort, StorageConnection conn) throws JsonProcessingException, InterruptedException;

    void unRegistryStorageConnection(StorageConnection conn);

    void storageConnectionHeartbeat(StorageConnection conn);


    void registryPortConnection();

    void unRegistryPortConnection();

    void portConnectionHeartbeat();


    void registryPort();

    void unRegistryPort();

    void portHeartbeat(DvsPort dvsPort) throws JsonProcessingException;


    void preRegistryEngine(Engine engine) throws JsonProcessingException;

    void unRegistryEngine();

    void engineHeartbeat();
}
