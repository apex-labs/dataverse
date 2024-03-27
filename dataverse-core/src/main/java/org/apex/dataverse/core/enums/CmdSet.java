package org.apex.dataverse.core.enums;

import lombok.Getter;
import org.apex.dataverse.core.msg.packet.*;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.di.DiRspPacket;
import org.apex.dataverse.core.msg.packet.engine.EngineEventPacket;
import org.apex.dataverse.core.msg.packet.engine.TrackingPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlJobReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlJobRspPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlRspPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * A set of all defined commands, each of which must appear in pairs,
 * containing both request and response message bodies.
 * Defined commands must be declared in this enumeration class.
 * Each type of command contains a request packet and a response packet
 *
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @since : 0.1.0
 */
@Getter
public enum CmdSet {

    /**
     * 引擎状态命令
     */
    ENGINE_EVENT((byte) 1, EngineEventPacket.class, (byte) 2, EngineEventPacket.class),

    /**
     * 命令处理过程指令
     * 引擎端汇报运行状态
     */
    CMD_TRACKING((byte) 3, TrackingPacket.class, (byte) 4, TrackingPacket.class),

    /**
     * 数据集成命令
     */
    DATA_INTEGRATION((byte) 11, DiReqPacket.class, (byte) 12, DiRspPacket.class),

    /**
     * 运行SQL作业
     */
    EXE_SQL_JOB((byte) 13, ExeSqlJobReqPacket.class, (byte) 14, ExeSqlJobRspPacket.class),

    /**
     * 运行SQL
     */
    EXE_SQL((byte) 15, ExeSqlReqPacket.class, (byte) 16, ExeSqlRspPacket.class),

    ;

    /**
     * Packet's class
     */
    private final static Map<Byte, Class<? extends Packet>> PACKET_CLASS = new HashMap<>();

    /**
     * Command type, flag is request or response
     */
    private final static Map<Byte, Boolean> CMD_TYPE = new HashMap<>();

    /**
     * Request command and response command pair
     */
    private final static Map<Byte, Byte> CMD_PAIR = new HashMap<>();

    // definition command
    static {
        definition(CmdSet.DATA_INTEGRATION);
        definition(CmdSet.CMD_TRACKING);
        definition(CmdSet.ENGINE_EVENT);
        definition(CmdSet.EXE_SQL_JOB);
        definition(CmdSet.EXE_SQL);
    }

    /**
     * request command
     */
    private final Byte req;

    /**
     * Request command's class
     */
    private final Class<? extends Packet> reqClass;

    /**
     * response command
     */
    private final Byte rsp;

    /**
     * Response command's class
     */
    private final Class<? extends Packet> rspClass;

    /**
     * Constructor with parameter
     *
     * @param req Byte, Request
     * @param reqClass Class, Request class
     * @param rsp Byte, Response
     * @param rspClass Class, Response class
     */
    CmdSet(Byte req, Class<? extends Packet> reqClass, Byte rsp, Class<? extends Packet> rspClass) {
        this.req = req;
        this.reqClass = reqClass;
        this.rsp = rsp;
        this.rspClass = rspClass;
    }

    /**
     * Command definition
     * @param cmd CmdSet enum
     */
    public static void definition(CmdSet cmd) {
        // set request class
        PACKET_CLASS.put(cmd.getReq(), cmd.getReqClass());
        CMD_TYPE.put(cmd.getReq(), true);

        // set response class
        PACKET_CLASS.put(cmd.getRsp(), cmd.getRspClass());
        CMD_TYPE.put(cmd.getRsp(), false);

        // set cmd pair
        CMD_PAIR.put(cmd.req, cmd.rsp);
    }

    /**
     * Get packet's class by command id
     *
     * @param command Byte, Command
     * @return Class<? extends Packet>
     */
    public static Class<? extends Packet> getClass(Byte command) {
        return PACKET_CLASS.get(command);
    }

    /**
     * Is request or not
     *
     * @param command Byte, Command
     * @return Class<? extends Packet>
     */
    public static Boolean isRequest(Byte command) {
        return CMD_TYPE.get(command);
    }

    /**
     * Is response or not
     *
     * @param command Byte, Command
     * @return Boolean
     */
    public static Boolean isResponse(Byte command) {
        return !CMD_TYPE.get(command);
    }

    /**
     *  Get response command by request
     * @param req Request
     * @return Byte
     */
    public static Byte getRspByReq(Byte req) {
        return CMD_PAIR.get(req);
    }

}
