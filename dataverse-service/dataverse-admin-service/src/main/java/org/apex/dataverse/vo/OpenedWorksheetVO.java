package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: OpenedWorksheetVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/6/15 17:12
 */
@Data
public class OpenedWorksheetVO {


    @ApiModelProperty("worksheetID")
    private Long worksheetId;

    @ApiModelProperty("作业Code，BDM作业或ETL作业")
    private String jobCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("作业类型，1：ETL Job, 2:BDM Job")
    private Boolean jobType;

    @ApiModelProperty("Work Sheet名称(作业名称）")
    private String jobName;

    @ApiModelProperty("是否关闭 ，0：否，1：是（当sheet关闭时，置为1，表示sheet已关闭）")
    private Boolean isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("关闭时间")
    private LocalDateTime closedTime;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;
}
