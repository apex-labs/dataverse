package org.apex.dataverse.core.exception;

/**
 * @author : Danny.Huo
 * @date : 2023/1/15 18:26
 * @since 0.1.0
 */
public class SerializeException extends Exception{

    public SerializeException () {
        super();
    }

    public SerializeException (String msg) {
        super(msg);
    }

    public SerializeException (Throwable e) {
        super(e);
    }

    public SerializeException (String msg, Throwable e) {
        super(msg, e);
    }
}
