package org.apex.dataverse.core.msg.packet.dd;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsReqPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import lombok.Data;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;

import java.util.List;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/28 14:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ExeSqlReqPacket extends AbsReqPacket {

    /**
     * 将要执行的SQL
     */
    private String sql;

    /**
     * 获取最大条数
     * 默认：200
     */
    private Integer fetchMaxRows = 200;

    /**
     * 表存储信息
     * 针对于hdfs中的表，需要在spark中读取并创建临时view才可执行SQL
     */
    private List<StoreInfo> storeInfos;

    /**
     * 运行环境
     */
    private Byte exeEnv;

    @Override
    public Packet newRspPacket() {
        ExeSqlRspPacket packet = new ExeSqlRspPacket();
        this.extend(packet);
        return packet;
    }
}
