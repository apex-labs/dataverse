package org.apex.dataverse.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.enums.JobStateEnum;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.port.entity.JobHistory;
import org.apex.dataverse.port.service.IJobHistoryService;
import org.apex.dataverse.utils.DateUtils;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.DashboardStatisticsExeCmdVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: JobHistoryDAO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 16:07
 */
@Service
public class JobHistoryDAO {

    @Autowired
    private IJobHistoryService jobHistoryService;

    @Autowired
    private MapperFactory mapperFactory;

    public List<DashboardStatisticsExeCmdVO> statisticsExeCmd(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsManageException {
        validateDashboardStatisticsParam(dashboardStatisticsParam);
        List<DashboardStatisticsExeCmdVO> dashboardStatisticsExeCmdVOList = new ArrayList<>();
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<JobHistory> jobHistoryList = jobHistoryService.list(Wrappers.<JobHistory>lambdaQuery().between(JobHistory::getCreateTime, dashboardStatisticsParam.getStartTime(), dashboardStatisticsParam.getEndTime()).
                eq(JobHistory::getTenantId, userInfo.getTenantId()));
        if (CollectionUtil.isNotEmpty(jobHistoryList)) {
            Map<String, List<JobHistory>> jobHistoryMap = jobHistoryList.stream().collect(Collectors.groupingBy(jobHistory -> DateUtils.localDateTimeToStr(jobHistory.getCreateTime(), DateUtils.DATETIME_DATE_FORMATTER)));
            if (CollectionUtil.isNotEmpty(jobHistoryMap)) {
                jobHistoryMap.forEach((date, job) -> {
                    DashboardStatisticsExeCmdVO dashboardStatisticsExeCmdVO = new DashboardStatisticsExeCmdVO();
                    dashboardStatisticsExeCmdVO.setDate(date);
                    dashboardStatisticsExeCmdVO.setFailCount((int) job.stream().filter(j -> Objects.nonNull(j.getJobState()) && j.getJobState().intValue() == JobStateEnum.FAIL.getValue()).count());
                    dashboardStatisticsExeCmdVO.setSuccessCount((int) job.stream().filter(j -> Objects.nonNull(j.getJobState()) && j.getJobState().intValue() == JobStateEnum.SUCCESS.getValue()).count());
                    dashboardStatisticsExeCmdVOList.add(dashboardStatisticsExeCmdVO);

                });
            }
        }

        return dashboardStatisticsExeCmdVOList;
    }

    private void validateDashboardStatisticsParam(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsManageException {
        if (Objects.isNull(dashboardStatisticsParam)) {
            throw new DtvsManageException("参数为空");
        }
        if (StringUtils.isBlank(dashboardStatisticsParam.getStartTime())) {
            throw new DtvsManageException("开始时间为空");
        }
        if (StringUtils.isBlank(dashboardStatisticsParam.getEndTime())) {
            throw new DtvsManageException("开始时间为空");
        }
        LocalDate startDate = DateUtils.str2LocalDate(dashboardStatisticsParam.getStartTime(), DateUtils.DATETIME_DATE_FORMATTER);
        LocalDate endDate = DateUtils.str2LocalDate(dashboardStatisticsParam.getEndTime(), DateUtils.DATETIME_DATE_FORMATTER);
        if (startDate.isAfter(endDate)) {
            throw new DtvsManageException("开始时间不能大于结束时间");
        }
    }
}
