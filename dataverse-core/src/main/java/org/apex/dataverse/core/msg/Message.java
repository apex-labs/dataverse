package org.apex.dataverse.core.msg;

import org.apex.dataverse.core.msg.packet.Packet;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/1/12 14:05
 */
@Data
public class Message<T extends Packet> implements Serializable {

    /**
     * Is request or not
     * true : request
     * false : response
     */
    private boolean isRequest;

    /**
     * netty channel id
     */
    private String channelId;

    /**
     * Message header for netty codec
     */
    private Header header;

    /**
     * Message packet
     */
    private T packet;

}
