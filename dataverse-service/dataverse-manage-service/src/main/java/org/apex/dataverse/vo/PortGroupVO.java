package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: PortGroupVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 16:50
 */
@ApiModel
@Data
public class PortGroupVO {

    @ApiModelProperty("组ID")
    private Long groupId;

    @ApiModelProperty("组编码，唯一")
    private String groupCode;

    @ApiModelProperty("组名")
    private String groupName;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

}
