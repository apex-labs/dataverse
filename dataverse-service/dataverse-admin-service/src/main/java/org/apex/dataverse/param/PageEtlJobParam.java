package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageEtlJobParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 10:43
 */
@Data
public class PageEtlJobParam {

    @ApiModelProperty("数据集成分页参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("搜索关键词 ETL作业ID/ETL作业名称")
    private String keyword;

    @ApiModelProperty("作业生命周期/状态  作业开发中（草稿）：11 作业开发完成：12 作业测试中：21 作业测试完成(通过）：22 调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer jobLifecycle;

    @ApiModelProperty("发布状态，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("etl运行状态 1:成功,2:失败")
    private Integer etlResultStatus;

    @ApiModelProperty("数据分组CODE")
    private String groupCode;

    @ApiModelProperty("数据源类型")
    private Integer datasourceTypeId;

}
