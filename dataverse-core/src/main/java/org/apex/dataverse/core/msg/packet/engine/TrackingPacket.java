package org.apex.dataverse.core.msg.packet.engine;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.util.AddressUtil;
import org.apex.dataverse.core.util.DateUtil;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;

/**
 * 命令处理过程数据包
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/27 17:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TrackingPacket extends Packet {

    /**
     * log format
     */
    private final static String TRACKING_LOG = "%s [TRACKING] --- [%s %s] %s";

    public static TrackingPacket newTracking() {
        return new TrackingPacket();
    }

    public static TrackingPacket newTracking(String text) {
        return new TrackingPacket(text);
    }

    public TrackingPacket() {
        this.timestamp = System.currentTimeMillis();
    }

    public TrackingPacket(String text) {
        this.logText = text;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * log timestamp
     */
    private Long timestamp;

    /**
     * log text
     */
    private String logText;

    @Override
    public String toString() {
        return String.format(TRACKING_LOG, DateUtil.defaultFormat(timestamp), AddressUtil.getLocalAddress(), Thread.currentThread().getName(), logText);
    }

    public static void main(String[] args) throws JsonProcessingException {
        TrackingPacket log = TrackingPacket.newTracking("Hello tracking log");
        System.out.println(log);
        System.out.println(ObjectMapperUtil.toJson(log));
    }
}
