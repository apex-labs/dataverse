package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.BdmJobGroupParam;
import org.apex.dataverse.service.IBdmJobGroupService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.BdmJobGroupVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 大数据建模作业分组 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
@RestController
@RequestMapping("/bdm-job-group")
@Api(tags = "数据开发分组接口")
public class BdmJobGroupController {

    @Autowired
    private IBdmJobGroupService bdmJobGroupService;

    @PostMapping("/list")
    @ApiOperation("展示数据开发分组信息")
    public ResultVO<List<BdmJobGroupVO>> listBdmJobGroup() {
        return ResultVO.success(bdmJobGroupService.listBdmJobGroup());
    }

    @PostMapping("/getBdmJobGroupList")
    @ApiOperation("根据数据域查询开发分组信息")
    public ResultVO<List<BdmJobGroupVO>> listBdmJobGroupByDataRegionCode(@RequestParam("dataRegionCode") String dataRegionCode) {
        return ResultVO.success(bdmJobGroupService.listBdmJobGroupByDataRegionCode(dataRegionCode));
    }

    @PostMapping("/saveBdmJobGroup")
    @ApiOperation("保存数据开发分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存数据开发分组信息请求参数", dataTypeClass = BdmJobGroupParam.class)})
    public ResultVO<Integer> saveBdmJobGroup(@RequestBody BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        return ResultVO.success(bdmJobGroupService.saveBdmJobGroup(bdmJobGroupParam));
    }

    @PostMapping("/editBdmJobGroup")
    @ApiOperation("编辑数据开发分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑数据开发分组信息请求参数", dataTypeClass = BdmJobGroupParam.class)})
    public ResultVO<Integer> editBdmJobGroup(@RequestBody BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException {
        return ResultVO.success(bdmJobGroupService.editBdmJobGroup(bdmJobGroupParam));
    }

    @PostMapping("/deleteBdmJobGroup")
    @ApiOperation("删除数据开发分组信息")
    @ApiImplicitParams({@ApiImplicitParam(value = "删除数据开发分组信息请求参数", dataTypeClass = Long.class)})
    public ResultVO<Integer> deleteBdmJobGroup(@RequestParam("bdmJobGroupId") Integer bdmJobGroupId) throws DtvsAdminException {
        return ResultVO.success(bdmJobGroupService.deleteBdmJobGroup(bdmJobGroupId));
    }

}
