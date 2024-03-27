package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: DashboardStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 9:55
 */
@Data
public class DashboardStatisticsVO {

    @ApiModelProperty("数据空间看板统计")
    private DvsStatisticsVO dvsStatisticsVO;

    @ApiModelProperty("数据域看板统计")
    private DataRegionStatisticsVO dataRegionStatisticsVO;

    @ApiModelProperty("数据源看板统计")
    private DatasourceStatisticsVO datasourceStatisticsVO;

    @ApiModelProperty("集成作业看板统计")
    private EtlJobStatisticsVO etlJobStatisticsVO;

    @ApiModelProperty("开发作业看板统计")
    private BdmJobStatisticsVO bdmJobStatisticsVO;

    @ApiModelProperty("存储区看板统计")
    private StorageStatisticsVO storageStatisticsVO;
}
