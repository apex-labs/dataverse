package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: TableTransformVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@ApiModel(description = "")
@Data
public class TableTransformVO {

    @ApiModelProperty("tableTransformId")
    private Long tableTransformId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("输入列名")
    private String inColumn;

    @ApiModelProperty("转换")
    private String transform;

    @ApiModelProperty("输出列名")
    private String outColumn;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("源字段类型ID")
    private Integer inDataTypeId;

    @ApiModelProperty("源字段类型名称")
    private String inDataTypeName;

    @ApiModelProperty("输出字段类型ID")
    private Integer outDataTypeId;

    @ApiModelProperty("输出字段类型名称")
    private String outDataTypeName;

}
