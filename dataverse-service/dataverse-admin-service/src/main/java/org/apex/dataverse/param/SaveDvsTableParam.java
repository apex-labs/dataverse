package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: SaveDvsTableParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 11:01
 */
@Data
public class SaveDvsTableParam {

    @ApiModelProperty("表ID")
    private Long tableId;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域")
    private String dataRegionCode;

    @ApiModelProperty("表名，英文名称")
    private String tableName;

    @ApiModelProperty("表别名，显示名称")
    private String tableAlias;

    @ApiModelProperty("环境 0：BASIC，1：DEV，2：PROD")
    private Integer env;

    @ApiModelProperty("数仓分层，1：ODS，2：CDM，3：ADS")
    private Integer dwLayer;

    @ApiModelProperty("数仓分层明细，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,")
    private Integer dwLayerDetail;

    @ApiModelProperty("是否流式表，1：是，0：否")
    private Integer isStream;

    @ApiModelProperty("建表模式，1：系统自动创建，2：手动创建")
    private Integer createMode;

    @ApiModelProperty("表的生命周期/状态，开发中（草稿）：11，开发完成：12，测试中：21，测试完成(通过）：22，调度编排中：31，调度编排完成：32，调度测试中：41，调度测试完成(通过）：42，已发布生产：51，生产测试中：61，生产测试完成(通过）：62，已上线：71，已下线：72")
    private Integer tableLifecycle;

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

    @ApiModelProperty("保存数据集成表对应的列信息")
    private List<SaveDvsTableColumnParam> saveDvsTableColumnParamList;

}
