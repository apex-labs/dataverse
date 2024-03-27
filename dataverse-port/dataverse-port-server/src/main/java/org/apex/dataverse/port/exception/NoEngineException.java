package org.apex.dataverse.port.exception;

import org.apex.dataverse.core.exception.SerializeException;

/**
 * @author Danny.Huo
 * @date 2023/1/15 18:26
 * @since 0.1.0
 */
public class NoEngineException extends SerializeException {

    public NoEngineException() {
        super();
    }

    public NoEngineException(String msg) {
        super(msg);
    }

    public NoEngineException(Throwable e) {
        super(e);
    }

    public NoEngineException(String msg, Throwable e) {
        super(msg, e);
    }
}
