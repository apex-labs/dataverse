package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class DeployJobParam {

    @ApiModelProperty("调度发布任务ID")
    private Long deployJobId;

    @ApiModelProperty("调度发布ID")
    private Long deployId;

    @ApiModelProperty("被加入调度中原作业编码，ETL作业或BDM作业")
    private String jobCode;

    @ApiModelProperty("作业类型，1：ETL Job, 2:BDM Job")
    private Boolean jobType;

    @ApiModelProperty("作业名称")
    private String jobName;

    @ApiModelProperty("调度节点编码")
    private String nodeCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("发布前ETL规则。json格式，来自table_extract/table_transform/table_load三表中的数据，按json格式组装。非ETL作业为空。")
    private String odlEtl;

    @ApiModelProperty("发布后ETL规则。json格式，来自table_extract/table_transform/table_load三表中的数据（页面修改后传递至后端的），按json格式组装。非ETL作业为空。")
    private String nowEtl;

    @ApiModelProperty("发布前的BDM作业的脚本，非BDM作业为空")
    private String oldBdmScript;

    @ApiModelProperty("发布后的BDM作业的脚本，非BDM作业为空")
    private String nowBdmScript;

}
