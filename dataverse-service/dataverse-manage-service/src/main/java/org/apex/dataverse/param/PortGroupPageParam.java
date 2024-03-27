package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PortGroupPageParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 16:50
 */
@Data
@ApiModel
public class PortGroupPageParam {

    @ApiModelProperty("分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("连接器分组名称")
    private String groupName;

    @ApiModelProperty("连接器分组编码")
    private String groupCode;
}
