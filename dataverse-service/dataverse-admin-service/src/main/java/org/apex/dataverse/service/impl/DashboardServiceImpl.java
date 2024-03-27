package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.core.util.DateUtil;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.EnvEnum;
import org.apex.dataverse.enums.ExeStatusEnum;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.param.ListStorageParam;
import org.apex.dataverse.service.*;
import org.apex.dataverse.utils.DateUtils;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Watchable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName: DashboardServiceImpl
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:02
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    @Autowired
    private IDvsService dvsService;

    @Autowired
    private IDataRegionService dataRegionService;

    @Autowired
    private IDatasourceService datasourceService;

    @Autowired
    private IEtlJobService etlJobService;

    @Autowired
    private IBdmJobService bdmJobService;

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private ManageFeignClient manageFeignClient;

    @Autowired
    private IJobInstanceService jobInstanceService;


    @Override
    public DashboardStatisticsVO statistics() {
        DashboardStatisticsVO dashboardStatisticsVO = new DashboardStatisticsVO();
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<Dvs> dvsList = null;
        List<DataRegion> dataRegionList = null;
        List<Datasource> datasourceList = null;
        List<EtlJob> etlJobList = null;
        List<BdmJob> bdmJobList = null;
        List<StorageFeignVO> storageFeignVOList = manageFeignClient.listStorageVO(new ListStorageParam());
        // 查询数据空间
        dvsList = dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getTenantId, userInfo.getTenantId()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(dvsList)) {
            DvsStatisticsVO dvsStatisticsVO = new DvsStatisticsVO();
            dvsStatisticsVO.setDvsCount(dvsList.size());
            dvsStatisticsVO.setBasicDvsCount((int) dvsList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.BAISC.getValue()).count());
            dvsStatisticsVO.setDevDvsCount((int) dvsList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.DEV.getValue()).count());
            dvsStatisticsVO.setProdDvsCount((int) dvsList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.PROD.getValue()).count());
            dashboardStatisticsVO.setDvsStatisticsVO(dvsStatisticsVO);
            List<String> dvsCodes = dvsList.stream().map(Dvs::getDvsCode).distinct().collect(Collectors.toList());
            dataRegionList = dataRegionService.list(Wrappers.<DataRegion>lambdaQuery().in(DataRegion::getDvsCode, dvsCodes).eq(DataRegion::getIsDeleted, IsDeletedEnum.NO.getValue()));
            datasourceList = datasourceService.list(Wrappers.<Datasource>lambdaQuery().in(Datasource::getDvsCode, dvsCodes).eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue()));
            etlJobList = etlJobService.list(Wrappers.<EtlJob>lambdaQuery().in(EtlJob::getDvsCode, dvsCodes).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
            bdmJobList = bdmJobService.list(Wrappers.<BdmJob>lambdaQuery().in(BdmJob::getDvsCode, dvsCodes).eq(BdmJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
        }
        // 数据域
        if (CollectionUtil.isNotEmpty(dataRegionList)) {
            DataRegionStatisticsVO dataRegionStatisticsVO = new DataRegionStatisticsVO();
            dataRegionStatisticsVO.setDataRegionCount(dataRegionList.size());
            dataRegionStatisticsVO.setBasicDataRegionCount((int) dataRegionList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.BAISC.getValue()).count());
            dataRegionStatisticsVO.setDevDataRegionCount((int) dataRegionList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.DEV.getValue()).count());
            dataRegionStatisticsVO.setProdDataRegionCount((int) dataRegionList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.PROD.getValue()).count());
            dashboardStatisticsVO.setDataRegionStatisticsVO(dataRegionStatisticsVO);
        } else {
            dashboardStatisticsVO.setDataRegionStatisticsVO(new DataRegionStatisticsVO());
        }

        // 数据源
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            DatasourceStatisticsVO datasourceStatisticsVO = new DatasourceStatisticsVO();
            datasourceStatisticsVO.setDataSourceCount(datasourceList.size());
            datasourceStatisticsVO.setBasicDataSourceCount((int) datasourceList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.BAISC.getValue()).count());
            datasourceStatisticsVO.setDevDataSourceCount((int) datasourceList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.DEV.getValue()).count());
            datasourceStatisticsVO.setProdDataSourceCount((int) datasourceList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.PROD.getValue()).count());
            dashboardStatisticsVO.setDatasourceStatisticsVO(datasourceStatisticsVO);
        } else {
            dashboardStatisticsVO.setDatasourceStatisticsVO(new DatasourceStatisticsVO());
        }

        // 集成作业
        if (CollectionUtil.isNotEmpty(etlJobList)) {
            EtlJobStatisticsVO etlJobStatisticsVO = new EtlJobStatisticsVO();
            etlJobStatisticsVO.setEtlJobCount(etlJobList.size());
            etlJobStatisticsVO.setBasicEtlJobCount((int) etlJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.BAISC.getValue()).count());
            etlJobStatisticsVO.setDevEtlJobCount((int) etlJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.DEV.getValue()).count());
            etlJobStatisticsVO.setProdEtlJobCount((int) etlJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.PROD.getValue()).count());
            dashboardStatisticsVO.setEtlJobStatisticsVO(etlJobStatisticsVO);
        } else {
            dashboardStatisticsVO.setEtlJobStatisticsVO(new EtlJobStatisticsVO());
        }

        // 开发作业
        if (CollectionUtil.isNotEmpty(bdmJobList)) {
            BdmJobStatisticsVO bdmJobStatisticsVO = new BdmJobStatisticsVO();
            bdmJobStatisticsVO.setBdmJobCount(bdmJobList.size());
            bdmJobStatisticsVO.setBasicBdmJobCount((int) bdmJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.BAISC.getValue()).count());
            bdmJobStatisticsVO.setDevBdmJobCount((int) bdmJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.DEV.getValue()).count());
            bdmJobStatisticsVO.setProdBdmJobCount((int) bdmJobList.stream().filter(d -> d.getEnv().intValue() == EnvEnum.PROD.getValue()).count());
            dashboardStatisticsVO.setBdmJobStatisticsVO(bdmJobStatisticsVO);
        } else {
            dashboardStatisticsVO.setBdmJobStatisticsVO(new BdmJobStatisticsVO());
        }
        // 存储区
        if (CollectionUtil.isNotEmpty(storageFeignVOList)) {
            StorageStatisticsVO storageStatisticsVO = new StorageStatisticsVO();
            storageStatisticsVO.setStorageCount(storageFeignVOList.size());
            dashboardStatisticsVO.setStorageStatisticsVO(storageStatisticsVO);
        } else {
            dashboardStatisticsVO.setStorageStatisticsVO(new StorageStatisticsVO());
        }

        return dashboardStatisticsVO;
    }

    @Override
    public List<DashboardStatisticsJobVO> statisticsEtlJob(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException {
        validateDashboardStatisticsParam(dashboardStatisticsParam);
        List<DashboardStatisticsJobVO> dashboardStatisticsJobVOList = new ArrayList<>();
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<Dvs> dvsList = dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getTenantId, userInfo.getTenantId()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(dvsList)) {
            List<String> dvsCodes = dvsList.stream().map(Dvs::getDvsCode).collect(Collectors.toList());
            List<EtlJob> etlJobList = etlJobService.list(Wrappers.<EtlJob>lambdaQuery().in(EtlJob::getDvsCode, dvsCodes).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()).
                    between(EtlJob::getCreateTime, dashboardStatisticsParam.getStartTime(), dashboardStatisticsParam.getEndTime()));
            if (CollectionUtil.isNotEmpty(etlJobList)) {
                List<String> jobCodes = etlJobList.stream().map(EtlJob::getEtlJobCode).collect(Collectors.toList());
                List<JobInstance> jobInstanceList = jobInstanceService.list(Wrappers.<JobInstance>lambdaQuery().in(JobInstance::getJobCode, jobCodes));
                Map<String, List<EtlJob>> etlJobMap = etlJobList.stream().collect(Collectors.groupingBy(etlJob -> DateUtils.localDateTimeToStr(etlJob.getCreateTime(), DateUtils.DATETIME_DATE_FORMATTER)));
                if (CollectionUtil.isNotEmpty(etlJobMap)) {
                    etlJobMap.forEach((date, jobs) -> {
                        DashboardStatisticsJobVO dashboardStatisticsJobVO = new DashboardStatisticsJobVO();
                        dashboardStatisticsJobVO.setDate(date);
                        dashboardStatisticsJobVO.setBasicFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.BAISC.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.BAISC.getValue()));
                        dashboardStatisticsJobVO.setBasicSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.BAISC.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.BAISC.getValue()));
                        dashboardStatisticsJobVO.setDevFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.DEV.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.DEV.getValue()));
                        dashboardStatisticsJobVO.setDevSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.DEV.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.DEV.getValue()));
                        dashboardStatisticsJobVO.setProdFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.PROD.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.PROD.getValue()));
                        dashboardStatisticsJobVO.setProdSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.PROD.getValue()).map(EtlJob::getEtlJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.PROD.getValue()));
                        dashboardStatisticsJobVOList.add(dashboardStatisticsJobVO);
                    });
                }
            }
        }
        return dashboardStatisticsJobVOList;
    }

    /**
     * 获取不同环境下集成任务执行状态数量
     *
     * @param jobCodes
     * @param jobInstanceList
     * @param exeStatus
     * @param env
     * @return
     */
    private Integer getJobExeStatus(List<String> jobCodes, List<JobInstance> jobInstanceList, boolean exeStatus, int env) {
        // 查询成功任务数量
        if (CollectionUtil.isNotEmpty(jobInstanceList)) {
            if (exeStatus) {
                List<JobInstance> successJobInstances = jobInstanceList.stream().filter(l -> l.getEnv().intValue() == env && Objects.nonNull(l.getExeStatus()) && l.getExeStatus().intValue() == ExeStatusEnum.EXE_SUCCESS.getValue()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(successJobInstances)) {
                    return (int) successJobInstances.stream().filter(l -> jobCodes.contains(l.getJobCode())).count();
                }
            } else {
                // 查询失败任务数量
                List<JobInstance> failJobInstances = jobInstanceList.stream().filter(l -> l.getEnv().intValue() == env && Objects.nonNull(l.getExeStatus()) && l.getExeStatus().intValue() == ExeStatusEnum.EXE_FAIL.getValue()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(failJobInstances)) {
                    return (int) failJobInstances.stream().filter(l -> jobCodes.contains(l.getJobCode())).count();
                }
            }
        }
        return 0;
    }

    private void validateDashboardStatisticsParam(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException {
        if (Objects.isNull(dashboardStatisticsParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(dashboardStatisticsParam.getStartTime())) {
            throw new DtvsAdminException("开始时间为空");
        }
        if (StringUtils.isBlank(dashboardStatisticsParam.getEndTime())) {
            throw new DtvsAdminException("结束时间为空");
        }
        LocalDate startDate = DateUtils.str2LocalDate(dashboardStatisticsParam.getStartTime(), DateUtils.DATETIME_DATE_FORMATTER);
        LocalDate endDate = DateUtils.str2LocalDate(dashboardStatisticsParam.getEndTime(), DateUtils.DATETIME_DATE_FORMATTER);
        if (startDate.isAfter(endDate)) {
            throw new DtvsAdminException("开始时间不能大于结束时间");
        }
    }

    @Override
    public List<DashboardStatisticsJobVO> statisticsBdmJob(DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException {
        validateDashboardStatisticsParam(dashboardStatisticsParam);
        List<DashboardStatisticsJobVO> dashboardStatisticsJobVOList = new ArrayList<>();
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<Dvs> dvsList = dvsService.list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getTenantId, userInfo.getTenantId()).eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(dvsList)) {
            List<String> dvsCodes = dvsList.stream().map(Dvs::getDvsCode).collect(Collectors.toList());
            List<BdmJob> bdmJobList = bdmJobService.list(Wrappers.<BdmJob>lambdaQuery().in(BdmJob::getDvsCode, dvsCodes).eq(BdmJob::getIsDeleted, IsDeletedEnum.NO.getValue()).
                    between(BdmJob::getCreateTime, dashboardStatisticsParam.getStartTime(), dashboardStatisticsParam.getEndTime()));
            if (CollectionUtil.isNotEmpty(bdmJobList)) {
                List<String> jobCodes = bdmJobList.stream().map(BdmJob::getBdmJobCode).collect(Collectors.toList());
                List<JobInstance> jobInstanceList = jobInstanceService.list(Wrappers.<JobInstance>lambdaQuery().in(JobInstance::getJobCode, jobCodes));
                Map<String, List<BdmJob>> bdmJobMap = bdmJobList.stream().collect(Collectors.groupingBy(etlJob -> DateUtils.localDateTimeToStr(etlJob.getCreateTime(), DateUtils.DATETIME_DATE_FORMATTER)));
                if (CollectionUtil.isNotEmpty(bdmJobMap)) {
                    bdmJobMap.forEach((date, jobs) -> {
                        DashboardStatisticsJobVO dashboardStatisticsJobVO = new DashboardStatisticsJobVO();
                        dashboardStatisticsJobVO.setDate(date);
                        dashboardStatisticsJobVO.setBasicFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.BAISC.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.BAISC.getValue()));
                        dashboardStatisticsJobVO.setBasicSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.BAISC.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.BAISC.getValue()));
                        dashboardStatisticsJobVO.setDevFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.DEV.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.DEV.getValue()));
                        dashboardStatisticsJobVO.setDevSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.DEV.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.DEV.getValue()));
                        dashboardStatisticsJobVO.setProdFailJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.PROD.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, false, EnvEnum.PROD.getValue()));
                        dashboardStatisticsJobVO.setProdSuccessJobCount(getJobExeStatus(jobs.stream().filter(j -> j.getEnv().intValue() == EnvEnum.PROD.getValue()).map(BdmJob::getBdmJobCode).collect(Collectors.toList()), jobInstanceList, true, EnvEnum.PROD.getValue()));
                        dashboardStatisticsJobVOList.add(dashboardStatisticsJobVO);
                    });
                }
            }
        }
        return dashboardStatisticsJobVOList;
    }

    @Override
    public List<DashboardStatisticsExeCmdVO> statisticsExeCmd(DashboardStatisticsParam dashboardStatisticsParam) {
        List<DashboardStatisticsExeCmdVO> dashboardStatisticsExeCmdVOList = manageFeignClient.statisticsExeCmd(dashboardStatisticsParam);
        if (CollectionUtil.isNotEmpty(dashboardStatisticsExeCmdVOList)) {
            return dashboardStatisticsExeCmdVOList;
        }
        return null;
    }
}
