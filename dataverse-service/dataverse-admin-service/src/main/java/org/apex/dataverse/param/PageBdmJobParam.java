package org.apex.dataverse.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.util.PageQueryParam;

/**
 * @ClassName: PageBdmJobParam
 * @Author: wwd
 * @TODO:
 * @Date: 2023/5/18 14:13
 */
@Data
public class PageBdmJobParam {

    @ApiModelProperty("分页查询参数")
    private PageQueryParam pageQueryParam;

    @ApiModelProperty("搜索关键词 BDM作业ID/BDM作业名称")
    private String keyword;

    @ApiModelProperty("作业生命周期/状态  作业开发中（草稿）：11 作业开发完成：12 作业测试中：21 作业测试完成(通过）：22 调度编排中：31 调度编排完成：32 调度测试中：41 调度测试完成(通过）：42 已发布生产：51 生产测试中：61 生产测试完成(通过）：62 已上线：71 已下线：72")
    private Integer jobLifecycle;

    @ApiModelProperty("环境，0：BASIC、1：DEV、2：PROD")
    private Integer env;

    @ApiModelProperty("数据分组CODE")
    private String bdmGroupCode;

    @ApiModelProperty("数据域编码")
    private String dataRegionCode;

    @ApiModelProperty("数据空间编码，一个数据空间编码对应一个（Basic模式）或两个Dvs(DEV-PROD模式)，且编码相同")
    private String dvsCode;

    @ApiModelProperty("bdm运行状态 1:成功,2:失败")
    private Integer bdmResultStatus;




}
