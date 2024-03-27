package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: EtlJobGroupParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 17:21
 */
@Data
public class EtlJobGroupParam {

    @ApiModelProperty("主键")
    private Long etlGroupId;

    @ApiModelProperty("Etl作业分组名称")
    private String etlGroupName;

    @ApiModelProperty("Etl作业分组编码")
    private String etlGroupCode;

    @ApiModelProperty("集成任务数据域编码")
    private String dataRegionCode;

    @ApiModelProperty("Etl作业分组父编码")
    private String parentEtlGroupCode;

    @ApiModelProperty("分组层级")
    private Integer groupLevel;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

}
