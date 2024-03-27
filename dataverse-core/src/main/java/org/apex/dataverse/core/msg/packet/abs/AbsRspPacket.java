package org.apex.dataverse.core.msg.packet.abs;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.engine.TrackingPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/25 19:15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbsRspPacket extends Packet {

    /**
     * Command execution log
     */
    private List<TrackingPacket> exeLogs;

    /**
     * Add a new tracking object based on the current request object
     *
     */
    public void addTracking(String logText) {
        if(null == this.exeLogs) {
           this.exeLogs = new ArrayList<>();
        }
        this.exeLogs.add(TrackingPacket.newTracking(logText));
    }
}
