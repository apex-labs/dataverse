package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("表统计参数")
@Data
public class TableStatisticsParam {

    @ApiModelProperty("数据表统计ID")
    private Long tableStatisticsId;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("表中记录数，行数")
    private Integer count;

    @ApiModelProperty("表所占空间大小")
    private Double size;

    @ApiModelProperty("文件个数")
    private String fileCount;
}
