package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName: BdmJobVO
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/15 15:09
 */
@Data
public class BdmJobVO {

    @ApiModelProperty("大数据建模作业ID")
    private Long bdmJobId;

    @ApiModelProperty("数据建模编码，同一作业在不同环境下bdmJobCode相同")
    private String bdmJobCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("数据域编码。同一数据域在不同环境下编码相同。data_region_code+env可唯一确认一个数据域")
    private String dataRegionCode;

    @ApiModelProperty("大数据建模作业名称")
    private String bdmJobName;

    @ApiModelProperty("大数据建模作业分组编码")
    private String bdmGroupCode;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)")
    private String engineType;

    @ApiModelProperty("大数据建模作业描述")
    private String description;

    @ApiModelProperty("作业生命周期/状态  作业开发中（草稿）：11 作业开发完成：12 作业测试中：21 作业测试完成(通过）：22 调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer jobLifecycle;

    @ApiModelProperty("是否删除：0否，1：是")
    private Integer isDeleted;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人所属租户ID")
    private Long tenantId;

    @ApiModelProperty("创建人所属租户名称【冗余】")
    private String tenantName;

    @ApiModelProperty("创建人所属部门ID")
    private Long deptId;

    @ApiModelProperty("创建人所属部门名称【冗余】")
    private String deptName;

    @ApiModelProperty("创建人ID")
    private Long userId;

    @ApiModelProperty("创建人名称【冗余】")
    private String userName;

    @ApiModelProperty("数据建模作业脚本信息展示")
    private BdmScriptVO bdmScriptVO;

    @ApiModelProperty("数据建模作业依赖表展示")
    private List<BdmInTableVO> bdmInTableVOList;

}
