package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;


@Data
public class JobScheduleInfoDTO {


    @ApiModelProperty("调度任务节点参数")
    private List<ScheduleNodeDTO> scheduleNodeDTOList;

    @ApiModelProperty("依赖作业列表")
    private List<DependenceNodeDTO> dependenceNodeDTOList;

}
