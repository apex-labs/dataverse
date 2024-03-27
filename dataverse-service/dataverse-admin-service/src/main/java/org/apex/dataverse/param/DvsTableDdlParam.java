package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 表的建表语句
 * </p>
 *
 * @author danny
 * @since 2023-06-16
 */
@Data
public class DvsTableDdlParam {

    @ApiModelProperty("建表DDLID")
    private Long tableDdlId;

    @ApiModelProperty("表编码。同一Evn下，表编码唯一。table_code+env唯一")
    private String tableCode;

    @ApiModelProperty("建表语句")
    private String ddlText;


}
