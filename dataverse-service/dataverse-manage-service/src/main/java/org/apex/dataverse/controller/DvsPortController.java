package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.dao.DvsPortDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DvsPortPageParam;
import org.apex.dataverse.param.StoragePortParam;
import org.apex.dataverse.param.SaveDvsPortParam;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DvsPortVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: DvsPortController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/9 16:34
 */
@RestController
@RequestMapping("/dvs-port")
@Api(tags = "连接器port相关接口")
public class DvsPortController {

    @Autowired
    private DvsPortDAO dvsPortDAO;

    @PostMapping("/pageList")
    @ApiOperation("port分页列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DvsPortPageParam.class)})
    public ResultVO<PageResult<DvsPortVO>> pageList(@RequestBody DvsPortPageParam dvsPortPageParam) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.pageList(dvsPortPageParam));
    }

    @PostMapping("/add")
    @ApiOperation("保存dvs_port")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = SaveDvsPortParam.class)})
    public ResultVO<Long> add(@RequestBody SaveDvsPortParam saveDvsPortParam) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.add(saveDvsPortParam));
    }

    @PostMapping("/edit")
    @ApiOperation("编辑dvs_port")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = SaveDvsPortParam.class)})
    public ResultVO<Long> edit(@RequestBody SaveDvsPortParam saveDvsPortParam) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.edit(saveDvsPortParam));
    }

    @GetMapping("/detail/{portId}")
    @ApiOperation("dvs_port详情")
    public ResultVO<DvsPortVO> detail(@PathVariable("portId") Long portId) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.detail(portId));
    }

    @GetMapping("/delete/{portId}")
    @ApiOperation("删除dvs_port")
    public ResultVO<Boolean> delete(@PathVariable("portId") Long portId) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.delete(portId));
    }

    @PostMapping("/removeStoragePort")
    @ApiOperation("移除dvs_port下授权的存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = StoragePortParam.class)})
    public ResultVO<Boolean> removeStoragePort(@RequestBody List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.removeStoragePort(storagePortParamList));
    }

    @PostMapping("/addStoragePort")
    @ApiOperation("添加dvs_port下授权的存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = StoragePortParam.class)})
    public ResultVO<Boolean> addStoragePort(@RequestBody List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        return ResultVO.success(dvsPortDAO.addStoragePort(storagePortParamList));
    }


}
