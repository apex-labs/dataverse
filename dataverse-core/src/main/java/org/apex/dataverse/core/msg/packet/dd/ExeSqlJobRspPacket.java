package org.apex.dataverse.core.msg.packet.dd;

import lombok.EqualsAndHashCode;
import org.apex.dataverse.core.msg.packet.abs.AbsRspPacket;
import lombok.Data;
import org.apex.dataverse.core.msg.packet.info.ResultInfo;

import java.util.List;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/24 20:56
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class ExeSqlJobRspPacket extends AbsRspPacket {

    private List<ResultInfo> results;
}

