package org.apex.dataverse.core.msg.packet.dd;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsRspPacket;
import lombok.Data;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/28 14:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExeSqlRspPacket extends AbsRspPacket {

    /**
     * SQL
     */
    private String sql;

    /**
     * 总记录数
     */
    private Integer totalCount;

    /**
     * 获取记录数
     */
    private Integer fetchedCount;

    /**
     * 结果
     */
    private Object result;

    /**
     * 持续时间
     */
    private Long duration;
}
