package org.apex.dataverse.port.exception;

import org.apex.dataverse.core.exception.SerializeException;

/**
 * @author Danny.Huo
 * @date 2023/1/15 18:26
 * @since 0.1.0
 */
public class StorageConnException extends SerializeException {

    public StorageConnException() {
        super();
    }

    public StorageConnException(String msg) {
        super(msg);
    }

    public StorageConnException(Throwable e) {
        super(e);
    }

    public StorageConnException(String msg, Throwable e) {
        super(msg, e);
    }
}
