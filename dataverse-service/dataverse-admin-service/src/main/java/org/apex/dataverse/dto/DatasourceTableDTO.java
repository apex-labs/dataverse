package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName DatasourceTableDTO
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/28 11:30
 **/
@Data
public class DatasourceTableDTO {

    @ApiModelProperty("表名称")
    private String tableName;

    @ApiModelProperty("表注释")
    private String tableRemarks;

}
