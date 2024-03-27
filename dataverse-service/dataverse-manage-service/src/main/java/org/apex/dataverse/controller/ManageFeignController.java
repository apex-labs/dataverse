package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.dao.JobHistoryDAO;
import org.apex.dataverse.dao.StorageDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.param.ListStorageParam;
import org.apex.dataverse.vo.DashboardStatisticsExeCmdVO;
import org.apex.dataverse.vo.StorageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: ManageFeignController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/15 16:35
 */
@RestController
@RequestMapping("/api/dvs/manage/feign")
@Api(tags = "存储区对外feign接口")
public class ManageFeignController {

    @Autowired
    private StorageDAO storageDAO;

    @Autowired
    private JobHistoryDAO jobHistoryDAO;

    @PostMapping("/listStorage")
    @ApiOperation("feign接口调用返回存储区列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = ListStorageParam.class)})
    List<StorageVO> listStorageVO(@RequestBody ListStorageParam listStorageParam) {
        return storageDAO.listStorageVO(listStorageParam);
    }

    @PostMapping("/statisticsExeCmd")
    @ApiOperation("feign接口调用返回执行命令列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DashboardStatisticsParam.class)})
    List<DashboardStatisticsExeCmdVO> statisticsExeCmd(@RequestBody DashboardStatisticsParam dashboardStatisticsParam) throws DtvsManageException {
        return jobHistoryDAO.statisticsExeCmd(dashboardStatisticsParam);
    }

}
