package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: DvsPortPageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 15:23
 */
@Data
@ApiModel
public class DvsPortPageParam {

    @ApiModelProperty("分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("连接器名称")
    private String portName;

    @ApiModelProperty("连接器分组名称")
    private String groupName;

    @ApiModelProperty("连接器分组编码")
    private String groupCode;
}
