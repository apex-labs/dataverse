package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class JobSchduleCodeDto {

    @ApiModelProperty("调度作业主键")
    private Long scheduleId;

    @ApiModelProperty("调度编码，同一调度在不同环境中编码相同")
    private String scheduleCode;

    public JobSchduleCodeDto(Long scheduleId, String scheduleCode) {
        this.scheduleId = scheduleId;
        this.scheduleCode = scheduleCode;
    }
}
