package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 数据域
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DataRegion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据域ID
     */
    @TableId(value = "data_region_id", type = IdType.AUTO)
    private Long dataRegionId;

    /**
     * 数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域
     */
    private String dataRegionCode;

    /**
     * 数据域名称，英文名称
     */
    private String regionName;

    /**
     * 数据域别名，显示名称
     */
    private String regionAlias;

    /**
     * 数据域简称，英文缩写
     */
    private String regionAbbr;

    /**
     * 数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同；Dvs_code+env唯一确认一个Dvs
     */
    private String dvsCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 所属数据仓库的分层，1:ODS，2:DW，3:ADS
     */
    private Integer dwLayer;

    /**
     * 所属数据仓库的分层，11:ODS,21:DWD,22:DWS,23:DIM,31:MASTER,32:MODEL,33:LABEL,34:DM,
     */
    private String dwLayerDetail;

    /**
     * 数据域描述
     */
    private String description;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;


}
