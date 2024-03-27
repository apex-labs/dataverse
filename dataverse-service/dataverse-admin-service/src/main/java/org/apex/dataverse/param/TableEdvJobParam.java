package org.apex.dataverse.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


@Data
@ApiModel("数据开发任务列表查询")
public class TableEdvJobParam implements Serializable {

    @ApiModelProperty("开发任务ID或名称")
    private String edvJobIdOrName;

    @ApiModelProperty("任务状态 1：新建，2：开发中，3：加入调度，4：测试中，5：上线/生产，6：下线")
    private Integer edvJobStatus;

}
