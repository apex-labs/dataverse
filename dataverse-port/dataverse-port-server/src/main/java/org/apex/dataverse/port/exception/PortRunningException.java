package org.apex.dataverse.port.exception;

import org.apex.dataverse.core.exception.SerializeException;

/**
 * @author Danny.Huo
 * @date 2023/1/15 18:26
 * @since 0.1.0
 */
public class PortRunningException extends SerializeException {

    public PortRunningException() {
        super();
    }

    public PortRunningException(String msg) {
        super(msg);
    }

    public PortRunningException(Throwable e) {
        super(e);
    }

    public PortRunningException(String msg, Throwable e) {
        super(msg, e);
    }
}
