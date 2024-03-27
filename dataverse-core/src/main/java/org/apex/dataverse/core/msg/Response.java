package org.apex.dataverse.core.msg;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.Packet;
import lombok.Data;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/1/12 14:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Response<T extends Packet> extends Message<T> {

    /**
     * Success flag
     */
    public final static boolean SUCCESS = true;

    /**
     * Failed flag
     */
    public final static boolean FAILED = false;

    /**
     * Response is false
     */
    public final static boolean RESPONSE = false;

    /**
     * Is success flag
     */
    private boolean success = true;

    /**
     * Exception message
     */
    private String exception;

    /**
     *
     * Constructor
     */
    public Response () {
        this.setRequest(RESPONSE);
    }
}
