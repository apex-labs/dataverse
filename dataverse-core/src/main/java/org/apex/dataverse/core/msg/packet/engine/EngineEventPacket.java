package org.apex.dataverse.core.msg.packet.engine;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.Packet;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/24 11:09
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EngineEventPacket extends Packet {

    public EngineEventPacket() {
        this.reportTime = System.currentTimeMillis();
    }

    /**
     * Engine ID
     */
    private Long engineId;

    /**
     * Engine name
     */
    private String engineName;

    /**
     * Engine type：
     * 1：spark
     * 2：flink
     */
    private String engineType;

    /**
     * The host name of the node on which the engine driver is running
     */
    private String hostname;

    /**
     * The ip of the node on which the engine driver is running
     */
    private String ip;

    /**
     * EngineServer's port on the driver
     */
    private Integer port;

    /**
     * Event message
     */
    private String message;

    /**
     * Event state
     */
    private Byte state;

    /**
     * Request id
     */
    private String requestId;

    /**
     * Request command
     */
    private String request;

    /**
     * Report time
     */
    private Long reportTime;

    /**
     * heartbeat interval
     */
    private Integer heartbeatInterval;
}
