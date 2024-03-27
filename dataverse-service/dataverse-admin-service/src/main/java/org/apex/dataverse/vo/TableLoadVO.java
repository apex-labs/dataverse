package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @ClassName: TableLoadVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@Data
public class TableLoadVO {

    @ApiModelProperty("tableLoadId")
    private Long tableLoadId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("Load至Dvs中的表名称")
    private String tableName;

    @ApiModelProperty("主键字段")
    private String pkField;

    @ApiModelProperty("主键字段说明")
    private String pkFieldComment;

    @ApiModelProperty("分区字段")
    private String partitionField;

    @ApiModelProperty("单分区抽取最多行数")
    private Integer partitionMaxRows;

    @ApiModelProperty("存储表名称，帕斯卡命名法则")
    private String storageName;

    @ApiModelProperty("存储表别名，中文名称")
    private String storageAlias;

    @ApiModelProperty("表备注说明")
    private String comment;

    @ApiModelProperty("定时规则，cron表达式")
    private String cron;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("数据集成输出表转换展示")
    private List<TableTransformVO> tableTransformVOList;

}
