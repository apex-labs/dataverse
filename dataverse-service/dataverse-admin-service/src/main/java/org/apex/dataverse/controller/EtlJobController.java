package org.apex.dataverse.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.EtlJobParam;
import org.apex.dataverse.param.PageEtlJobParam;
import org.apex.dataverse.param.PageTreeByGroupCodeParam;
import org.apex.dataverse.service.IEtlJobService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.EnumInfoVO;
import org.apex.dataverse.vo.EtlJobVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据抽取转换加载作业，Extract Transform and Load 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@RestController
@RequestMapping("/etl-job")
@Api(tags = "数据集成接口")
public class EtlJobController {

    @Autowired
    private IEtlJobService etlJobService;

    @PostMapping("/page")
    @ApiOperation("数据集成列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据集成列表请求参数", dataTypeClass = PageEtlJobParam.class)})
    public ResultVO<PageResult<EtlJobVO>> pageEtlJob(@RequestBody PageEtlJobParam pageEtlJobParam) throws DtvsAdminException {
        return ResultVO.success(etlJobService.pageEtlJob(pageEtlJobParam));
    }

    @PostMapping("/saveEtlJob")
    @ApiOperation("保存数据集成接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存数据集成接口请求参数", dataTypeClass = EtlJobParam.class)})
    public ResultVO<Long> saveEtlJob(@RequestBody EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        return ResultVO.success(etlJobService.saveEtlJob(saveEtlJobParam));
    }

    @PostMapping("/editEtlJob")
    @ApiOperation("编辑数据集成接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑数据集成接口请求参数", dataTypeClass = EtlJobParam.class)})
    public ResultVO<Long> editEtlJob(@RequestBody EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        return ResultVO.success(etlJobService.editEtlJob(saveEtlJobParam));
    }

    @Deprecated
    @PostMapping("/pageTreeByGroupCode")
    @ApiOperation("通过数据集成分组code展示数据集成列表树")
    @ApiImplicitParams({@ApiImplicitParam(value = "通过数据集成分组code展示数据集成列表树请求参数", dataTypeClass = PageTreeByGroupCodeParam.class)})
    public ResultVO<PageResult<EtlJobVO>> pageTreeByGroupCode(@RequestBody PageTreeByGroupCodeParam pageTreeByGroupCodeParam) {
        return ResultVO.success(etlJobService.pageTreeByGroupCode(pageTreeByGroupCodeParam));
    }

    @GetMapping("/detailEtlJobVO/{etlJobId}")
    @ApiOperation("通过数据集成ID查询数据集成详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "通过数据集成ID查询数据集成详情请求参数", dataTypeClass = Long.class)})
    public ResultVO<EtlJobVO> detailEtlJobVO(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.detailEtlJobVO(etlJobId));
    }

    @GetMapping("/setStatusDevelop/{etlJobId}")
    @ApiOperation("设置数据集成状态为开发")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据集成状态为开发请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setStatusDevelop(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.setStatusDevelop(etlJobId));
    }

    @GetMapping("/setStatusSchedule/{etlJobId}")
    @ApiOperation("设置数据集成状态为加入调度")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据集成状态为加入调度请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setStatusSchedule(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.setStatusSchedule(etlJobId));
    }

    @GetMapping("/setStatusTest/{etlJobId}")
    @ApiOperation("设置数据集成状态为测试")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据集成状态为测试请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setStatusTest(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.setStatusTest(etlJobId));
    }

    @GetMapping("/setStatusOnline/{etlJobId}")
    @ApiOperation("设置数据集成状态为上线")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据集成状态为上线请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setStatusOnline(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.setStatusOnline(etlJobId));
    }

    @GetMapping("/setStatusDownLine/{etlJobId}")
    @ApiOperation("设置数据集成状态为下线")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据集成状态为下线请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setStatusDownLine(@PathVariable("etlJobId") Long etlJobId) throws DtvsAdminException {
        return ResultVO.success(etlJobService.setStatusDownLine(etlJobId));
    }

    @GetMapping("/isStreamEnum")
    @ApiOperation(("流式创建枚举"))
    public ResultVO<List<EnumInfoVO>> isStreamEnum() {
        return ResultVO.success(etlJobService.isStreamEnum());
    }

    @GetMapping("/createModeEnum")
    @ApiOperation(("表创建模式枚举"))
    public ResultVO<List<EnumInfoVO>> createModeEnum() {
        return ResultVO.success(etlJobService.createModeEnum());
    }

    @GetMapping("/jobLifecycleEnum")
    @ApiOperation(("任务生命周期状态枚举"))
    public ResultVO<List<EnumInfoVO>> jobLifecycleEnum() {
        return ResultVO.success(etlJobService.jobLifecycleEnum());
    }

    @PostMapping("/updateEtlJobName")
    @ApiOperation("修改数据集成接口名称")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = EtlJobParam.class)})
    public ResultVO<Long> updateEtlJobName(@RequestBody EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        return ResultVO.success(etlJobService.updateEtlJobName(saveEtlJobParam));
    }

}
