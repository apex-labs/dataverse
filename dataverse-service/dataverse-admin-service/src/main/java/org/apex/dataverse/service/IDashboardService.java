package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.vo.DashboardStatisticsExeCmdVO;
import org.apex.dataverse.vo.DashboardStatisticsJobVO;
import org.apex.dataverse.vo.DashboardStatisticsVO;

import java.util.List;

public interface IDashboardService {

    /**
     * 统计租户对应 数据空间 数据域 数据源 集成作业 开发作业 存储区数量
     * @return
     */
    DashboardStatisticsVO statistics();

    /**
     * 按时间统计集成作业数量
     * @param dashboardStatisticsParam
     * @return
     */
    List<DashboardStatisticsJobVO> statisticsEtlJob(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException;

    /**
     * 按时间统计开发作业数量
     * @param dashboardStatisticsParam
     * @return
     */
    List<DashboardStatisticsJobVO> statisticsBdmJob(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException;

    /**
     * 按时间统计执行命令数量
     * @param dashboardStatisticsParam
     * @return
     */
    List<DashboardStatisticsExeCmdVO> statisticsExeCmd(DashboardStatisticsParam dashboardStatisticsParam);
}
