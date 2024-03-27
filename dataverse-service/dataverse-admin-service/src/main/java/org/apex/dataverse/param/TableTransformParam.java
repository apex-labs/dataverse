package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
@ApiModel("数据集成源表转换信息")
public class TableTransformParam {

    @ApiModelProperty("数据集成源表转换表ID")
    private Long tableTransformId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty(value = "环境，0：BASIC、1：DEV、2：PROD", required = true)
    private Integer env;

    @ApiModelProperty(value = "输入列名", required = true)
    private String inColumn;

    @ApiModelProperty("转换")
    private String transform;

    @ApiModelProperty(value = "输出列名", required = true)
    private String outColumn;

    @ApiModelProperty(value = "源字段类型ID", required = true)
    private Integer inDataTypeId;

    @ApiModelProperty(value = "源字段类型名称", required = true)
    private String inDataTypeName;

    @ApiModelProperty(value = "输出字段类型ID", required = true)
    private Integer outDataTypeId;

    @ApiModelProperty(value = "输出字段类型名称", required = true)
    private String outDataTypeName;

}
