package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: SaveEtlJobParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 9:58
 */
@Data
public class EtlJobParam {

    @ApiModelProperty("ETL作业ID")
    private Long etlJobId;

    @ApiModelProperty("ETL作业编码，同一作业在不同环境下etl_job_code相同")
    private String etlJobCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域")
    private String dataRegionCode;

    @ApiModelProperty("ETL作业名称")
    private String etlJobName;

    @ApiModelProperty("Etl作业分组编码")
    private String etlGroupCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("ETL作业描述")
    private String description;

    @ApiModelProperty("数据源ID")
    private String datasourceCode;

    @ApiModelProperty("定时规则，cron表达式")
    @Deprecated
    private String cron;

    @ApiModelProperty("作业生命周期/状态  作业开发中（草稿）：11 作业开发完成：12 作业测试中：21 作业测试完成(通过）：22 调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer jobLifecycle;

    @ApiModelProperty("数据集成相关表参数")
    private List<EtlJobTableParam> etlJobTableParamList;

}
