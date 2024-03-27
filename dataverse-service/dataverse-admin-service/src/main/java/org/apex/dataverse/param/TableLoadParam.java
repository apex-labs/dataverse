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
@ApiModel("数据集成源表到目标表信息")
public class TableLoadParam {

    @ApiModelProperty("Load至Dvs中的表ID")
    private Long tableLoadId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty(value = "环境，0：BASIC、1：DEV、2：PROD", required = true)
    private Integer env;

    @ApiModelProperty(value = "Load至Dvs中的表名称", required = true)
    private String tableName;

    @ApiModelProperty(value = "主键字段", required = true)
    private String pkField;

    @ApiModelProperty("主键字段说明")
    private String pkFieldComment;

    @ApiModelProperty("分区字段")
    private String partitionField;

    @ApiModelProperty("单分区抽取最多行数")
    private Integer partitionMaxRows;

    @ApiModelProperty(value = "存储表名称", required = true)
    private String storageName;

    @ApiModelProperty(value = "存储表别名，中文名称", required = true)
    private String storageAlias;

    @ApiModelProperty("表备注说明")
    private String comment;

    @ApiModelProperty("cron表达式")
    @Deprecated
    private String cron;
}
