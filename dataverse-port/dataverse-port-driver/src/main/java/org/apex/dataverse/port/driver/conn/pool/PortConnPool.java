package org.apex.dataverse.port.driver.conn.pool;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.core.node.PortNode;
import org.apex.dataverse.port.driver.config.ConnectionPoolConfig;
import org.apex.dataverse.port.driver.conn.PortConnection;
import org.apex.dataverse.port.driver.conn.impl.PortConnectionImpl;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.core.context.env.ClientEnv;
import org.apex.dataverse.core.context.ClientContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Danny.Huo
 * @date 2023/2/16 13:47
 * @since 0.1.0
 */
@Slf4j
public class PortConnPool {

    /**
     * storageId
     */
    private final String storageId;

    /**
     * group code
     */
    private final String groupCode;

    /**
     * Connection total count
     */
    private int connCount;

    /**
     * The number of connections in use
     */
    private int usedCount;

    /**
     * Connection pool config
     */
    private final ConnectionPoolConfig config;

    /**
     * Store in-use connections
     */
    public final Map<Integer, PortConnection> busyConnPool;

    /**
     * Store free connections
     */
    public final LinkedBlockingQueue<PortConnection> freeConnPool;


    /**
     * Construct with config, storage infos
     * @param config connection pool config
     * @param storageId storage id
     * @param groupCode port's group code
     */
    public PortConnPool(ConnectionPoolConfig config, String storageId, String groupCode) {
        this.config = config;
        this.storageId = storageId;
        this.groupCode = groupCode;

        busyConnPool = new ConcurrentHashMap<>();
        freeConnPool = new LinkedBlockingQueue<>(config.getConnPoolCapacity());
    }

    /**
     * Add to busy connection pool
     *
     * @param conn PortConnection
     * @throws InterruptedException InterruptedException
     */
    private synchronized void addToBusy(PortConnection conn) throws InterruptedException {
        busyConnPool.put(conn.hashCode(), conn);
        conn.setBusy(true);
        this.connAutoIncr();
        this.connedAutoIncr();
        log.info("Connection is add to busy and it's total count is " + this.connCount);
    }

    /**
     * 连接加入连接池
     *
     * @param conn PortConnection
     * @param use  标记使用状态
     * @throws InterruptedException InterruptedException
     */
    private synchronized void add(PortConnection conn, boolean use) throws InterruptedException {
        if (use) {
            PortConnection putConn = busyConnPool.put(conn.hashCode(), conn);
            if (null != putConn) {
                putConn.setBusy(true);
                this.connAutoIncr();
                this.connedAutoIncr();
            }
        } else {
            if (null != conn) {
                freeConnPool.put(conn);
                conn.setBusy(false);
                this.connAutoIncr();
            }
        }
    }

    /**
     * 归还链接
     *
     * @param conn PortConnection
     * @throws InterruptedException InterruptedException
     */
    public synchronized void back(PortConnection conn) throws InterruptedException {
        PortConnection removedConn = busyConnPool.remove(conn.hashCode());
        if (null != removedConn) {
            freeConnPool.put(removedConn);
            conn.setBusy(false);
            this.connedAutoDecr();
        }
    }

    /**
     * 销毁/释放连接
     *
     * @param conn PortConnection
     */
    public synchronized void destroy(PortConnection conn) throws PortConnectionException {
        if (null == conn) {
            return;
        }

        log.info("destroy conn ...");
        PortConnection removed = this.busyConnPool.remove(conn.hashCode());
        try {
            if (null == removed) {
                removed = conn;
            } else {
                this.connedAutoDecr();
            }

            this.connAutoDecr();

            log.info("destroy conn ..., conn count is " + this.connCount);

            removed.close();
        } catch (Exception e) {
            log.error("Destroy port connection found error", e);
            throw new PortConnectionException("Destroy port connection found error : " + e.getMessage());
        }
    }

    /**
     * 获取连接
     *
     * @return PortConnection
     */
    public synchronized PortConnection getConn() throws PortConnectionException,
            NoPortNodeException, InterruptedException, JsonProcessingException {
        // 从空闲连接池获取一个空闲链接
        PortConnection conn = freeConnPool.poll();

        if (null == conn) {
            if (this.isFull()) {
                log.error("Connection pool is full, free => {}, busy => {}, max => {}",
                        freeConnPool.size(), busyConnPool.size(), this.config.getMax());
                throw new PortConnectionException("Connection is full, busy size is " +
                        busyConnPool.size() + ", max size is " + this.config.getMax());
            }

            PortNode portNode = config.routePort(storageId, groupCode);
            log.info("Free connection => {}, busy connection => {}, New connection will be established soon : {}",
                    freeConnPool.size(), busyConnPool.size(), portNode);
            ClientEnv clientConfig = new ClientEnv();
            clientConfig.setServerAddress(portNode.getIp());
            clientConfig.setServerPort(portNode.getPort());
            clientConfig.setThreads(config.getClientThreads());
            ClientContext clientContext = ClientContext.newContext(clientConfig);
            conn = new PortConnectionImpl(clientContext, storageId, groupCode);
            Future<?> future = config.getExecutorService().submit(conn);

            // 等待连接成功建立
            if (conn.isActive()) {
                conn.setFuture(future);
                this.addToBusy(conn);

                // TODO 更新注册中心的链接数

                return conn;
            } else {
                future.cancel(true);
                throw new PortConnectionException("Cannot be connected the server in "
                        + clientContext.getEnv().getConnTimeOutMs()
                        + " milliseconds => " + portNode.getIp()
                        + ":" + portNode.getPort());
            }
        } else if (!conn.isActive()) {
            // 链接无效，销毁掉
            destroy(conn);

            // 重新获取链接
            this.getConn();
        }

        this.connedAutoIncr();

        // 放入使用中的链接池
        this.busyConnPool.put(conn.hashCode(), conn);

        // 设置状态
        conn.setBusy(true);

        if (log.isDebugEnabled()) {
            log.debug("Connection pool info : free => {}, busy => {}, max => {}",
                    freeConnPool.size(), busyConnPool.size(), this.config.getMax());
        }

        // 返回链接
        return conn;
    }

    /**
     * 连接池已满
     *
     * @return boolean
     */
    public boolean isFull() {
        return this.connCount >= this.config.getMax();
    }

    /**
     * The number of conn increases
     */
    private synchronized void connAutoIncr() {
        this.connCount++;
    }

    /**
     * The number of conn decreases
     */
    private synchronized void connAutoDecr() {
        this.connCount--;
    }

    /**
     * Conned increases
     */
    private synchronized void connedAutoIncr() {
        this.usedCount++;
    }

    /**
     * Conned decreases
     */
    private synchronized void connedAutoDecr() {
        this.usedCount--;
    }
}
