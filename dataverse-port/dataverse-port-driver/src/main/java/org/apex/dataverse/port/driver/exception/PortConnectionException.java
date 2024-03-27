package org.apex.dataverse.port.driver.exception;

/**
 * @author Danny.Huo
 * @date 2023/3/14 18:34
 * @since 0.1.0
 */
public class PortConnectionException extends Exception {

    /**
     *
     */
    public PortConnectionException() {

    }

    /**
     *
     * @param msg message
     */
    public PortConnectionException(String msg) {
        super(msg);
    }

}
