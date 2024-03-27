package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.dao.PortGroupDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PortGroupPageParam;
import org.apex.dataverse.param.PortGroupParam;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.PortGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName: PortGroupController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/9 16:35
 */
@RestController
@RequestMapping("/port-group")
@Api(tags = "连接器port分组接口")
public class PortGroupController {

    @Autowired
    private PortGroupDAO portGroupDAO;

    @PostMapping("/pageList")
    @ApiOperation("连接器分组分页信息列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = PortGroupPageParam.class)})
    public ResultVO<PageResult<PortGroupVO>> pageList(@RequestBody PortGroupPageParam portGroupPageParam) throws DtvsManageException {
        return ResultVO.success(portGroupDAO.pageList(portGroupPageParam));
    }

    @PostMapping("/add")
    @ApiOperation("保存连接器分组")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = PortGroupParam.class)})
    public ResultVO<Long> add(@RequestBody PortGroupParam portGroupParam) throws DtvsManageException {
        return ResultVO.success(portGroupDAO.add(portGroupParam));
    }

    @PostMapping("/edit")
    @ApiOperation("编辑连接器分组")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = PortGroupParam.class)})
    public ResultVO<Long> edit(@RequestBody PortGroupParam portGroupParam) throws DtvsManageException {
        return ResultVO.success(portGroupDAO.edit(portGroupParam));
    }

    @GetMapping("/detail/{groupId}")
    @ApiOperation("编辑连接器分组")
    public ResultVO<PortGroupVO> detail(@PathVariable("groupId") Long groupId) throws DtvsManageException {
        return ResultVO.success(portGroupDAO.detail(groupId));
    }

    @GetMapping("/delete/{groupId}")
    @ApiOperation("删除连接器分组")
    public ResultVO<Boolean> delete(@PathVariable("groupId") Long groupId) throws DtvsManageException {
        return ResultVO.success(portGroupDAO.delete(groupId));
    }
}
