package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: DatasourceParentParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/17 9:51
 */
@Data
public class DatasourceParentParam {

    @ApiModelProperty("根数据源Id")
    private Long parentId;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty(value = "数据源类型ID", required = true)
    private Integer datasourceTypeId;

    @ApiModelProperty(value = "数据源类型名称", required = true)
    private String datasourceTypeName;

    @ApiModelProperty(value = "数据源名称", required = true)
    private String datasourceName;

    @ApiModelProperty(value = "数据源简称，缩写名称，字母数字下划线，非数字开头", required = true)
    private String datasourceAbbr;

    @ApiModelProperty(value = "数据源读写权限，1：只读，2：读写", required = true)
    private Integer datasourceReadWrite;

    @ApiModelProperty(value = "环境模式, 1:BASIC,2:DEV-PROD", required = true)
    private Integer envMode;

    @ApiModelProperty(value = "数据源实例请求参数", required = true)
    private List<DatasourceParam> datasourceParamList;
}
