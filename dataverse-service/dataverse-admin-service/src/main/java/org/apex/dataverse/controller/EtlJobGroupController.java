package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.EtlJobGroupParam;
import org.apex.dataverse.param.EtlJobGroupTreeParam;
import org.apex.dataverse.service.IEtlJobGroupService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.EtlJobGroupTreeVO;
import org.apex.dataverse.vo.EtlJobGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@RestController
@RequestMapping("/etl-job-group")
@Api(tags = "数据集成分组接口")
public class EtlJobGroupController {

    @Autowired
    private IEtlJobGroupService etlJobGroupService;

    @PostMapping("/list")
    @ApiOperation("展示数据集成分组全部信息")
    public ResultVO<List<EtlJobGroupVO>> listEtlJobGroup() {
        return ResultVO.success(etlJobGroupService.listEtlJobGroup());
    }

    @PostMapping("/listByDatasourceTypeId/{datasourceTypeId}")
    @ApiOperation("展示单一数据源类型下的数据集成分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据源类型ID", dataTypeClass = Integer.class)})
    public ResultVO<EtlJobGroupVO> listByDatasourceTypeId(@PathVariable("datasourceTypeId") Integer datasourceTypeId) {
        return ResultVO.success(etlJobGroupService.listByDatasourceTypeId(datasourceTypeId));
    }

    @PostMapping("/saveEtlJobGroup")
    @ApiOperation("保存数据集成分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存数据集成分组信息请求参数", dataTypeClass = EtlJobGroupParam.class)})
    public ResultVO<Long> saveEtlJobGroup(@RequestBody EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        return ResultVO.success(etlJobGroupService.saveEtlJobGroup(etlJobGroupParam));
    }

    @PostMapping("/editEtlJobGroup")
    @ApiOperation("编辑数据集成分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑数据集成分组信息请求参数", dataTypeClass = EtlJobGroupParam.class)})
    public ResultVO<Long> editEtlJobGroup(@RequestBody EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException {
        return ResultVO.success(etlJobGroupService.editEtlJobGroup(etlJobGroupParam));
    }

    @PostMapping("/deleteEtlJobGroup")
    @ApiOperation("删除数据集成分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "删除数据集成分组信息请求参数", dataTypeClass = Long.class)})
    public ResultVO<Long> deleteEtlJobGroup(@RequestParam("etlJobGroupId") Long etlJobGroupId) throws DtvsAdminException {
        return ResultVO.success(etlJobGroupService.deleteEtlJobGroup(etlJobGroupId));
    }

    @PostMapping("/dataRegion/etlJobGroupTree")
    @ApiOperation("获取数据域下的集成任务树")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = EtlJobGroupTreeParam.class)})
    public ResultVO<EtlJobGroupTreeVO> etlJobGroupTreeByDataRegion(@RequestBody EtlJobGroupTreeParam etlJobTreeParam) throws DtvsAdminException {
        return ResultVO.success(etlJobGroupService.etlJobGroupTreeByDataRegion(etlJobTreeParam));
    }

}
