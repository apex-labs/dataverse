package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@Data
public class EtlJobGroupVO {

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

    @ApiModelProperty("etl数据分组子节点")
    private List<EtlJobGroupVO> children;

}
