package org.apex.dataverse.core.exception;

/**
 * @author : Danny.Huo
 * @date : 2023/1/15 18:26
 * @since 0.1.0
 */
public class ProcessorException extends SerializeException{

    public ProcessorException() {
        super();
    }

    public ProcessorException(String msg) {
        super(msg);
    }

    public ProcessorException(Throwable e) {
        super(e);
    }

    public ProcessorException(String msg, Throwable e) {
        super(msg, e);
    }
}
