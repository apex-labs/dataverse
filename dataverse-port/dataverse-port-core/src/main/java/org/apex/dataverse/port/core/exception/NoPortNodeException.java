package org.apex.dataverse.port.core.exception;

/**
 * Look for the port node from the registry,
 * and throw this exception if no available port node is found
 *
 * @author Danny.Huo
 * @date 2023/6/2 13:59
 * @since 0.1.0
 */
public class NoPortNodeException extends Exception {

    /**
     * Default message
     */
    private final static String DEFAULT_MESSAGE = "No available port node was found";


    /**
     * Default construct
     */
    public NoPortNodeException() {

    }

    /**
     * Construct with message
     *
     * @param msg String , message
     */
    public NoPortNodeException(String msg) {
        super(msg);
    }

    /**
     * New an exception for default
     * @return NoAdaptorException
     */
    public static NoPortNodeException newException() {
        return new NoPortNodeException(DEFAULT_MESSAGE);
    }
}
