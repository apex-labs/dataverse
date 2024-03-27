package org.apex.dataverse.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.dto.FlowDAG;
import org.apex.dataverse.dto.JobSchduleCodeDto;
import org.apex.dataverse.dto.JobScheduleInfoDTO;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.dto.JobScheduleDTO;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.IJobScheduleService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.JobScheduleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 调度任务 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@RestController
@RequestMapping("/job-schedule")
@Api(tags = "调度任务接口")
public class JobScheduleController {

    @Autowired
    private IJobScheduleService jobScheduleService;

    @PostMapping("page")
    @ApiOperation("调度任务列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "调度任务列表参数", dataTypeClass = PageJobScheduleParam.class)})
    public ResultVO<PageResult<JobScheduleVO>> page(@RequestBody PageJobScheduleParam pageJobScheduleParam) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.pageJobScheduleVO(pageJobScheduleParam));
    }

    @PostMapping("saveJobSchedule")
    @ApiOperation("保存调度任务")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存调度任务参数", dataTypeClass = JobScheduleParam.class)})
    public ResultVO<Long> saveJobSchedule(@RequestBody JobScheduleParam jobScheduleParam) throws DtvsAdminException, JsonProcessingException{
        return ResultVO.success(jobScheduleService.saveJobSchedule(jobScheduleParam));
    }

    @PostMapping("scheduleInfo")
    @ApiOperation("调度作业及依赖列表")
    public ResultVO<JobScheduleInfoDTO> scheduleInfo(@RequestBody JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException{
        return ResultVO.success(jobScheduleService.scheduleInfo(jobScheduleInfoParam));
    }

    @PostMapping("updateNodeDependence")
    @ApiOperation("更新调度依赖生效/失效")
    public ResultVO<Void> updateNodeDependence(@RequestBody JobScheduleDependenceParam jobScheduleDependenceParam) throws DtvsAdminException{
        jobScheduleService.updateNodeDependence(jobScheduleDependenceParam);
        return ResultVO.success();
    }


    @PostMapping("jobScheduleInfo")
    @ApiOperation("生成调度树结构数据查询")
    @ApiImplicitParams({@ApiImplicitParam(value = "查看调度任务详情")})
    public ResultVO<JobScheduleDTO> jobScheduleInfo(@RequestBody JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException{
        return ResultVO.success(jobScheduleService.jobScheduleInfo(jobScheduleInfoParam));
    }


    @PostMapping("/saveOrUpdateScheduleNode")
    @ApiOperation("添加更新调度节点")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = SaveScheduleNode.class)})
    public ResultVO<Long> saveOrUpdateScheduleNode(@RequestBody SaveScheduleNode saveScheduleNode) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.saveOrUpdateScheduleNode(saveScheduleNode));
    }

    @PostMapping("deleteSchedule/{scheduleId}")
    @ApiOperation("删除调度任务")
    @ApiImplicitParams({@ApiImplicitParam(value = "删除调度任务")})
    public ResultVO<Boolean> deleteSchedule(@PathVariable("scheduleId") Long scheduleId) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.deleteScheduleInfo(scheduleId));
    }

    @PostMapping("/removeScheduleNode")
    @ApiOperation("删除调度节点")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = RemoveNodeDependenceParam.class)})
    public ResultVO<Boolean> removeScheduleNode(@RequestBody RemoveNodeDependenceParam removeNodeDependenceParam) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.removeScheduleNode(removeNodeDependenceParam));
    }

    @PostMapping("removeDependence")
    @ApiOperation("移除调度依赖")
    public ResultVO<Boolean> removeDependence(@RequestBody JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException{
        return ResultVO.success(jobScheduleService.removeDependence(jobScheduleInfoParam));
    }

    @GetMapping("/scheduleCodeByJobCode/{env}/{jobCode}")
    @ApiOperation("jobCode查是否加入调度 有则返回加入的调度code、无null")
    public ResultVO<JobSchduleCodeDto> scheduleCodeByJobCode(@PathVariable("env") Integer env, @PathVariable("jobCode") String jobCode) {
        return ResultVO.success(jobScheduleService.scheduleCodeByJobCode(jobCode,env));
    }


    @GetMapping("/setJobStatus/{scheduleId}/{status}")
    @ApiOperation("设置调度任务状态")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置调度任务状态请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setJobStatus(@PathVariable("scheduleId") Long scheduleId, @PathVariable("status") Integer status) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.setJobStatus(scheduleId,status));
    }

    @GetMapping("/checkJobAddSchedule/{env}/{jobCode}")
    @ApiOperation("检查作业是否加入调度")
    public ResultVO<Boolean> checkJobAddSchedule(@PathVariable("env") Integer env,@PathVariable("jobCode") String jobCode) throws DtvsAdminException {
        return ResultVO.success(jobScheduleService.checkJobAddSchedule(jobCode,env));
    }


    @GetMapping("publishSchedule")
    @ApiOperation("发布调度任务, 组织DAG对象")
    @ApiImplicitParams({@ApiImplicitParam(value = "发布调度任务")})
    public ResultVO<FlowDAG> publishSchedule(@RequestParam String scheduleCode,@RequestParam Integer env) {
        return ResultVO.success(jobScheduleService.publishSchedule(scheduleCode,env));
    }

}
