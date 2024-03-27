package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @version : v1.0
 * @author : augus.wang
 * @date : 2024/01/29 10:55
 */
@Data
public class ExeSqlRspTableStructureVo {

    @ApiModelProperty("字段名称")
    private String col_name;

    @ApiModelProperty("字段类型")
    private String data_type;

    @ApiModelProperty("字段描述")
    private String comment;


}
