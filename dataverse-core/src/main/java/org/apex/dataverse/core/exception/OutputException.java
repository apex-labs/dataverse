package org.apex.dataverse.core.exception;

/**
 * @author : Danny.Huo
 * @date : 2023/1/15 18:26
 * @since 0.1.0
 */
public class OutputException extends Exception{

    public static OutputException defaultException(String path) {
        return new OutputException("Output to " + path + " error");
    }

    public static OutputException pathExistException(String path) {
        return new OutputException("Output path [" + path + "] exists, you should set overwrite is true");
    }

    public OutputException() {
        super();
    }

    public OutputException(String msg) {
        super(msg);
    }

    public OutputException(Throwable e) {
        super(e);
    }

    public OutputException(String msg, Throwable e) {
        super(msg, e);
    }
}
