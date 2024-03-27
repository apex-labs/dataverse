package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 大数据建模作业分组
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@Data
public class BdmJobGroupVO {

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

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人所属租户ID")
    private Long tenantId;

    @ApiModelProperty("创建人所属租户名称【冗余】")
    private String tenantName;

    @ApiModelProperty("创建人所属部门ID")
    private Long deptId;

    @ApiModelProperty("创建人所属部门名称【冗余】")
    private String deptName;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

    @ApiModelProperty("数据开发分组子节点")
    private List<BdmJobGroupVO> children;

    @ApiModelProperty("数据开发列表")
    private List<BdmJobVO> bdmJobVOList;
}
