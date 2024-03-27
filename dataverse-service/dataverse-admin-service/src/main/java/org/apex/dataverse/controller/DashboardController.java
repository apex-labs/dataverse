package org.apex.dataverse.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.service.*;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DashboardStatisticsExeCmdVO;
import org.apex.dataverse.vo.DashboardStatisticsJobVO;
import org.apex.dataverse.vo.DashboardStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: DashboardController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 9:45
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {


    @Autowired
    private IDashboardService dashboardService;


    @PostMapping("/statistics")
    @ApiOperation("看板统计")
    public ResultVO<DashboardStatisticsVO> statistics() {
        return ResultVO.success(dashboardService.statistics());
    }

    @PostMapping("/statisticsEtlJob")
    @ApiOperation("看板统计集成作业任务")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DashboardStatisticsParam.class)})
    public ResultVO<List<DashboardStatisticsJobVO>> statisticsEtlJob(@RequestBody  DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException {
        return ResultVO.success(dashboardService.statisticsEtlJob(dashboardStatisticsParam));
    }

    @PostMapping("/statisticsBdmJob")
    @ApiOperation("看板统计开发作业任务")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DashboardStatisticsParam.class)})
    public ResultVO<List<DashboardStatisticsJobVO>> statisticsBdmJob(@RequestBody  DashboardStatisticsParam dashboardStatisticsParam) throws DtvsAdminException {
        return ResultVO.success(dashboardService.statisticsBdmJob(dashboardStatisticsParam));
    }

    @PostMapping("/statisticsExeCmd")
    @ApiOperation("看板统计执行命令")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DashboardStatisticsParam.class)})
    public ResultVO<List<DashboardStatisticsExeCmdVO>> statisticsExeCmd(@RequestBody DashboardStatisticsParam dashboardStatisticsParam) {
        return ResultVO.success(dashboardService.statisticsExeCmd(dashboardStatisticsParam));
    }



}
