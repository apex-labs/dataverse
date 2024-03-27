package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DvsParentParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/17 14:29
 */
@Data
public class DvsParentParam {

    @ApiModelProperty("数据空间父ID")
    private Long parentId;

    @ApiModelProperty("Dvs父编码，同parent下Dvs在多个环境(dev/prod)下Dvs_code相同。")
    private String dvsCode;

    @ApiModelProperty("数据空间名称，英文名称")
    private String parentName;

    @ApiModelProperty("数据空间别名，中文/显示名称")
    private String parentAlias;

    @ApiModelProperty("数据空间缩写，英文简称")
    private String parentAbbr;

    @ApiModelProperty("环境模式，1：BASIC、2：DEV-PROD")
    private Integer envMode;

    @ApiModelProperty("不同模式下的数据空间")
    private List<DvsParam> dvsParamList;

    @ApiModelProperty("数据空间存储桶,对应前端存储区选择:单存储区/多存储区(ADS/非ADS区)")
    private List<StorageBoxParam> storageBoxParamList;

    @ApiModelProperty("是否多存储区，0：单存储区，1：多存储区")
    private Integer multStorage;
}
