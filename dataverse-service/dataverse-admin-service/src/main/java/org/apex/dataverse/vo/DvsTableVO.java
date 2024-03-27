package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 表
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Data
public class DvsTableVO {

    @ApiModelProperty("表ID")
    private Long tableId;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty(value = "数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同", required = true)
    private String dvsCode;

    @ApiModelProperty(value = "数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域", required = true)
    private String dataRegionCode;

    @ApiModelProperty(value = "表名，英文名称", required = true)
    private String tableName;

    @ApiModelProperty(value = "表别名，显示名称", required = true)
    private String tableAlias;

    @ApiModelProperty(value = "环境 0：BASIC，1：DEV，2：PROD", required = true)
    private Integer env;

    @ApiModelProperty(value = "数仓分层，1：ODS，2：CDM，3：ADS", required = true)
    private Integer dwLayer;

    @ApiModelProperty(value = "数仓分层明细，ODS，DWD，DWS，DIM，MASTER，MODEL", required = true)
    private Integer dwLayerDetail;

    @ApiModelProperty(value = "是否流式表，1：是，0：否", required = true)
    private Integer isStream;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty(value = "建表模式，1：系统自动创建，2：手动创建", required = true)
    private Integer createMode;

    @ApiModelProperty("表的生命周期/状态 \n开发中（草稿）：11\r\n开发完成：12\r\n测试中：21\r\n测试完成(通过）：22\r\n调度编排中：31\r\n调度编排完成：32\r\n调度测试中：41\r\n调度测试完成(通过）：42\r\n已发布生产：51\r\n生产测试中：61\r\n生产测试完成(通过）：62\r\n已上线：71\r\n已下线：72")
    private Integer tableLifecycle;

    @ApiModelProperty("是否删除 1:是, 0:否")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("部门ID")
    private Long deptId;

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("数据表对应的列信息")
    private List<DvsTableColumnVO> dvsTableColumnVOList;

}
