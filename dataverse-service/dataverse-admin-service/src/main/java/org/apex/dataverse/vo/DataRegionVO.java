package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据域
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Data
public class DataRegionVO {

    @ApiModelProperty("数据域ID")
    private Long dataRegionId;

    @ApiModelProperty("数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域")
    private String dataRegionCode;

    @ApiModelProperty("数据域名称，英文名称")
    private String regionName;

    @ApiModelProperty("数据域别名，显示名称")
    private String regionAlias;

    @ApiModelProperty("数据域简称，英文缩写")
    private String regionAbbr;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("所属数据仓库的分层，1:ODS，2:DW，3:ADS")
    private Integer dwLayer;

    @ApiModelProperty("所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,")
    private String dwLayerDetail;

    @ApiModelProperty("数据域描述")
    private String description;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同；Dvs_code+env唯一确认一个Dvs")
    private String dvsCode;

    @ApiModelProperty("数据空间名称")
    private String dvsName;

    @ApiModelProperty("环境模式, 1:BASIC,2:DEV-PROD")
    private Integer envMode;

}
