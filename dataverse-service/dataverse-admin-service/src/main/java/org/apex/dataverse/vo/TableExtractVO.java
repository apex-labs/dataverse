package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: TableExtractVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@Data
public class TableExtractVO {

    @ApiModelProperty("抽取表ID")
    private Long tableExtractId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("Load至Dvs中的表code，同一表在不同环境（dev/prod)下相同")
    private String tableCode;

    @ApiModelProperty("环境，1：BASIC、2：DEV、3：TEST、4：PROD")
    private Integer env;

    @ApiModelProperty("源表名，数据源中的表")
    private String originTableName;

    @ApiModelProperty("源表备注，数据源表中的备注")
    private String originTableComment;

    @ApiModelProperty("增量类型。0：非增量抽取，1：日期增量，2：数值增量")
    private Integer incrType;

    @ApiModelProperty("增量标记字段，日期增量可多个字段，多个字段之间逗号分隔，数值增量支持单个字段。")
    private String incrFields;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("数据集成输入表转换展示")
    private List<TableTransformVO> tableTransformVOList;

}
