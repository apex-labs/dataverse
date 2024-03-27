package org.apex.dataverse.port.driver.conn.impl;

import org.apex.dataverse.port.driver.conn.AbstractConnection;
import org.apex.dataverse.core.context.ClientContext;
import org.apex.dataverse.port.core.enums.ConnTypeEnum;

import java.sql.SQLException;
import java.util.concurrent.Future;

/**
 * @author Danny.Huo
 * @date 2023/2/22 10:33
 * @since 0.1.0
 */
public class PortConnectionImpl extends AbstractConnection {

    /**
     * Construct
     * @param storageId storage id
     * @param groupCode group code
     * @param context ClientContext
     */
    public PortConnectionImpl(ClientContext context, String storageId, String groupCode) {
        super(context);
        super.setConnType(ConnTypeEnum.SPARK);
        this.storageId = storageId;
        this.groupCode = groupCode;
    }

    @Override
    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    @Override
    public void close() throws SQLException {
        try {
            this.closeChannel();

            this.future.cancel(true);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setFuture(Future<?> future) {
        super.setFuture(future);
    }
}
