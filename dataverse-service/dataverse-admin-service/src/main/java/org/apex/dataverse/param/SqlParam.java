package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;

import java.io.Serializable;
import java.util.List;

/**
 * @author augus,wang
 * @date 2024/01/15 15:21
 * @since 0.1.0
 */
@Data
public class SqlParam implements Serializable {

    @ApiModelProperty("需要执行的sql语句")
    private String sql;

    @ApiModelProperty("数据开发任务ID")
    private Long bdmJobId;

    @ApiModelProperty("存储信息")
    private List<StoreInfo> storeInfos;

    @ApiModelProperty("环境")
    private Integer env;

    @ApiModelProperty("引擎类型，1:spark引擎，2：flink引擎")
    private Integer engineType;

    @ApiModelProperty("所属存储区ID")
    private String storageId;

    @ApiModelProperty("数据开发表数据处理，表数据处理输入")
    private List<BdmInTableParam> bdmInTableParamList;

}
