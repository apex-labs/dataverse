package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Data
public class DvsVO {

    @ApiModelProperty("数据空间ID")
    private Long dvsId;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据空间名称，英文名称")
    private String dvsName;

    @ApiModelProperty("数据空间别名，中文/显示名称")
    private String dvsAlias;

    @ApiModelProperty("数据空间缩写，英文简称")
    private String dvsAbbr;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("数据仓库状态，1：规划中（草稿），2：开发中，3：已上线")
    private Integer dvsStatus;

    @ApiModelProperty("0：未完成，1：层之间无隔离（三层同一存储区），2：ODS-CDM无隔离，3：全隔离")
    private Integer layerAtaIsolation;

    @ApiModelProperty("数据空间描述")
    private String description;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

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

    @ApiModelProperty("是否多存储区，0：单存储区，1：多存储区")
    private Integer multStorage;

}
