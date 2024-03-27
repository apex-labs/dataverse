package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据输入分页查询")
public class PageTableEtlJobParam {

    @ApiModelProperty("表输入数据源ID")
    private String datasourceTypeName;

    @ApiModelProperty("运行状态 1：新建，2：开发中，3：加入调度，4：测试中，5：上线/生产，6：下线")
    private Integer etlJobStatus;

    @ApiModelProperty("任务状态")
    private Long env;

    @ApiModelProperty("任务ID或名称")
    private Long tableEtlJobIdOrName;

}
