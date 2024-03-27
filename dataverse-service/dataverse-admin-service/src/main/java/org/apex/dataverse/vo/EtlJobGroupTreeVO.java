package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: EtlJobGroupTreeVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/1 10:33
 */
@Data
@ApiModel
public class EtlJobGroupTreeVO {

    @ApiModelProperty("主键")
    private Long etlGroupId;

    @ApiModelProperty("Etl作业分组名称")
    private String etlGroupName;

    @ApiModelProperty("Etl作业分组编码")
    private String etlGroupCode;

    @ApiModelProperty("Etl作业分组父编码")
    private String parentEtlGroupCode;

    @ApiModelProperty("分组层级")
    private Integer groupLevel;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

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

    @ApiModelProperty("根节点,数据域编码")
    private String rootDataRegionCode;

    @ApiModelProperty("etl数据分组任务列表")
    private List<EtlJobVO> etlJobVOList;

    @ApiModelProperty("etl数据分组子节点树")
    private List<EtlJobGroupTreeVO> children;

}
