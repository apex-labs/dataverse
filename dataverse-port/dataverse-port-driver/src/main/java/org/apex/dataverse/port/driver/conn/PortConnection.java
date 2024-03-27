package org.apex.dataverse.port.driver.conn;

import org.apex.dataverse.core.session.ISession;
import org.apex.dataverse.port.core.enums.ConnTypeEnum;

import java.sql.Connection;
import java.util.concurrent.Future;

/**
 * @author Danny.Huo
 * @date 2023/2/22 10:30
 * @since 0.1.0
 */
public interface PortConnection extends Connection, ISession, Runnable {

    /**
     * 设置连接状态为使用中
     * @param busy boolean
     */
    void setBusy (boolean busy);

    /**
     * 连接是否在使用中
     * @return boolean
     */
    boolean isBusy ();

    /**
     * 是否activity
     * @return boolean
     */
    boolean isActive();

    /**
     * 获取连接类型
     * @return ConnTypeEnum
     */
    ConnTypeEnum getConnType();

    /**
     * 设置连接类型
     * @param connType ConnTypeEnum
     */
    void setConnType(ConnTypeEnum connType);

    /**
     * Set connection thread  submit future
     * @param future Future<?>
     */
    void setFuture(Future<?> future);

    /**
     * Get storageId
     * @return String
     */
    String getStorageId();

    /**
     * Get group code
     * @return String
     */
    String getGroupCode();
}
