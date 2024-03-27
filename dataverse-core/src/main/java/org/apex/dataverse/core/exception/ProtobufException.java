package org.apex.dataverse.core.exception;

/**
 * @author : Danny.Huo
 * @date : 2023/1/15 18:26
 * @since 0.1.0
 */
public class ProtobufException extends SerializeException{

    public ProtobufException() {
        super();
    }

    public ProtobufException(String msg) {
        super(msg);
    }

    public ProtobufException(Throwable e) {
        super(e);
    }

    public ProtobufException(String msg, Throwable e) {
        super(msg, e);
    }
}
