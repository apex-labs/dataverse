package org.apex.dataverse.port.core.exception;

/**
 * The Port node or engine node detects an exception
 * when registering with the registry. Procedure
 *
 * @author Danny.Huo
 * @date 2023/6/2 13:59
 * @since 0.1.0
 */
public class RegistryException extends Exception {

    public RegistryException() {

    }

    public RegistryException(String msg) {
        super(msg);
    }
}
