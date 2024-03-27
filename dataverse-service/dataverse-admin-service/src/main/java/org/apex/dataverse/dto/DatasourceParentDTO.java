package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DatasourceParentDTO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/6/1 17:36
 */
@Data
public class DatasourceParentDTO {

    @ApiModelProperty("数据源ID")
    private Long parentId;

    @ApiModelProperty("数据源父编码，同一编码在不同环境（DEV/PROD)中相同")
    private String datasourceCode;

    @ApiModelProperty("数据源类型ID")
    private Integer datasourceTypeId;

    @ApiModelProperty("数据源类型名称")
    private String datasourceTypeName;

    @ApiModelProperty("数据源名称")
    private String datasourceName;

    @ApiModelProperty("数据源简称，缩写名称，字母数字下划线，非数字开头")
    private String datasourceAbbr;

    @ApiModelProperty("数据源读写权限，1：只读，2：读写")
    private Integer datasourceReadWrite;

    @ApiModelProperty("环境模式, 1:BASIC,2:DEV-PROD")
    private Integer envMode;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

}
