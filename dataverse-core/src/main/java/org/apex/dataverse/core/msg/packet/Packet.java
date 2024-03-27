package org.apex.dataverse.core.msg.packet;

import lombok.Data;

import java.io.Serializable;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/1/15 17:41
 */
@Data
public abstract class Packet implements Serializable {

    /**
     * 数据包ID
     */
    private String id;

    /**
     * 作业ID
     * 关联数据集成作业或者数据开发ETL作业的ID
     */
    private String jobId;

    /**
     * 命令ID
     * 同一request和response相同
     */
    private String commandId;

    /**
     * Source storage id
     */
    private Long sourceStorageId;

    /**
     * Target storage id
     */
    private Long targetStorageId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 基于当前对象，实例化一新的Response对象
     *
     * @return Packet
     */
    public Packet newRspPacket() {
        return null;
    }

    /**
     * 继承ID信息
     * @param rsp Packet
     */
    public void extend(Packet rsp) {
        rsp.setId(this.getId());
        rsp.setJobId(this.getJobId());
        rsp.setCommandId(this.getCommandId());
    }
}
