package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: BdmOutTableVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@Data
public class BdmOutTableVO {

    @ApiModelProperty("主键")
    private Long bdmOutTableId;

    @ApiModelProperty("数据建模编码，同一作业在不同环境下bdmJobCode相同")
    private String bdmJobCode;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

//    @ApiModelProperty("所属数据域")
//    private Long dataRegionId;

    @ApiModelProperty("所属数据域Code")
    private String dataRegionCode;

//    @ApiModelProperty("数据建模结果集至Dvs中的表ID")
//    private Long tableId;

    @ApiModelProperty("表输出类型，1：临时表，2：正式表-逻辑，3：正式表-物理")
    private Integer bdmOutType;

    @ApiModelProperty("表名，英文名，帕斯卡命名法则")
    private String tableName;

    @ApiModelProperty("表别名")
    private String tableAlias;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("来自所选择的region中，所属数据仓库的分层，1:ODS，2:CDM，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,")
    private String dwLayerDetail;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
