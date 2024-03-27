package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class DependenceNodeDTO {

    /**
     * 作业名称
     */
    private String jobName;

    /**
     * 调度节点编码
     */
    private String nodeCode;
    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;

    @ApiModelProperty("是否生效:生效 1 ,失效 0")
    private Integer isValid;

    public DependenceNodeDTO() {
    }

    public DependenceNodeDTO(String jobName, String nodeCode, Long userId, String userName) {
        this.jobName = jobName;
        this.nodeCode = nodeCode;
        this.userId = userId;
        this.userName = userName;
    }
}
