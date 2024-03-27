package org.apex.dataverse.core.msg.packet.di;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsRspPacket;
import org.apex.dataverse.core.msg.packet.info.output.Output;

/**
 * 数据集成成响应消息体
 *
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/24 14:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DiRspPacket extends AbsRspPacket {

    /**
     * 结果Message
     */
    private String message;

    /**
     * 数据集成执行状态
     */
    private Byte state;

    /**
     * 集成数据条数
     */
    private Integer diCount;

    /**
     * 持续时间，运行时长
     */
    private Integer duration;

    /**
     * 输出路径
     */
    private Output output;

}
