package org.apex.dataverse.core.msg.packet.abs;

import org.apex.dataverse.core.msg.packet.Packet;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/25 19:15
 */
public abstract class AbsReqPacket extends Packet {
    /**
     * 基于Request实例化Response
     *
     * @return Packet
     */
    public abstract Packet newRspPacket();
}
