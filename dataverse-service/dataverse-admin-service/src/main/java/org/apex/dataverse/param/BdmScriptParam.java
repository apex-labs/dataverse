package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: BdmScriptParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 14:18
 */
@Data
public class BdmScriptParam {

//    @ApiModelProperty("数据建模任务ID")
//    private Long bdmScriptId;

    @ApiModelProperty("数据建模编码，同一作业在不同环境下bdmJobCode相同")
    private String bdmJobCode;

    @ApiModelProperty("数据建模脚本")
    private String bdmScript;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("引擎类型，1:spark引擎，2：flink引擎")
    private Integer engineType;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
