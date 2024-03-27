package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SaveScheduleNode
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/7 11:50
 */
@Data
@ApiModel
public class RemoveNodeDependenceParam {
    @ApiModelProperty("调度作业编码")
    private String scheduleCode;

    @ApiModelProperty("当前作业编码")
    private String jobCode;

    @ApiModelProperty("依赖作业编码")
    private List<String> fromCode;

    @ApiModelProperty("当前环境编码")
    private Integer env;

}
