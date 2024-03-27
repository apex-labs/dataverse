package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: BdmJobGroupParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 17:15
 */
@Data
public class BdmJobGroupParam {

    @ApiModelProperty("主键")
    private Integer bdmJobGroupId;

    @ApiModelProperty("数据域编码")
    private String dataRegionCode;

    @ApiModelProperty("大数据建模作业分组")
    private String bdmGroupName;

    @ApiModelProperty("大数据建模作业分组编码")
    private String bdmGroupCode;

    @ApiModelProperty("大数据建模作业分组父编码")
    private String parentBdmGroupCode;

    @ApiModelProperty("大数据建模作业分组层级")
    private Integer groupLevel;

}
