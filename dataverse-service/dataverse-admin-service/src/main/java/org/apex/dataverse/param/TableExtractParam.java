package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
@ApiModel("数据抽取源表信息")
public class TableExtractParam{

    @ApiModelProperty("数据抽取源表ID")
    private Long tableExtractId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty(value = "环境，1：BASIC、2：DEV、3：TEST、4：PROD", required = true)
    private Integer env;

    @ApiModelProperty(value = "源表名，数据源中的表", required = true)
    private String originTableName;

    @ApiModelProperty("源表备注，数据源表中的备注")
    private String originTableComment;

    @ApiModelProperty(value = "增量类型。0：非增量抽取，1：日期增量，2：数值增量", required = true)
    private Integer incrType;

    @ApiModelProperty("增量标记字段，日期增量可多个字段，多个字段之间逗号分隔，数值增量支持单个字段。")
    private String incrFields;

}
