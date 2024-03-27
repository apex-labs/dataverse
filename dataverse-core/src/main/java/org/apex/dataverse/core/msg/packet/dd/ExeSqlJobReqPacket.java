package org.apex.dataverse.core.msg.packet.dd;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsReqPacket;
import org.apex.dataverse.core.msg.packet.Packet;
import lombok.Data;
import org.apex.dataverse.core.msg.packet.info.SqlInfo;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;

import java.util.List;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/24 20:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ExeSqlJobReqPacket extends AbsReqPacket {

    /**
     * SQL命令
     */
    private List<SqlInfo> sqlInfos;

    /**
     * 表存储信息
     */
    private List<StoreInfo> storeInfos;

    /**
     * 运行环境
     */
    private Byte exeEnv;


    @Override
    public Packet newRspPacket() {
        ExeSqlJobRspPacket packet = new ExeSqlJobRspPacket();
        this.extend(packet);
        return packet;
    }
}

