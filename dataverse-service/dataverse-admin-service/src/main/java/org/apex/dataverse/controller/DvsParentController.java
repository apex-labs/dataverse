package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.IDvsParentService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DvsParentVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.vo.DvsVO;
import org.apex.dataverse.vo.StorageFeignVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@RestController
@RequestMapping("/dvs-parent")
@Api(tags = "数据空间接口")
public class DvsParentController {

    @Autowired
    private IDvsParentService dvsParentService;

    @Autowired
    private ManageFeignClient manageFeignClient;

    @PostMapping("/page")
    @ApiOperation(value = "数据空间列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageDvsParentParam.class)})
    public ResultVO<PageResult<DvsParentVO>> pageDvsParent(@RequestBody PageDvsParentParam pageDvsParentParam) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.pageDvsParent(pageDvsParentParam));
    }

    @PostMapping("/addDvsParent")
    @ApiOperation(value = "新增数据空间")
    @ApiImplicitParams({@ApiImplicitParam(value = "新增参数", dataTypeClass = DvsParentParam.class)})
    public ResultVO<Long> addDvsParent(@RequestBody DvsParentParam dvsParentParam) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.addDvsParent(dvsParentParam));
    }

    @PostMapping("/editDvsParent")
    @ApiOperation(value = "编辑数据空间")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = DvsParam.class)})
    public ResultVO<Long> editDvsParent(@RequestBody DvsParentParam dvsParentParam) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.editDvsParent(dvsParentParam));
    }

    @GetMapping("/detail/{parentId}")
    @ApiOperation(value = "数据空间详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = Long.class)})
    public ResultVO<DvsParentVO> detail(@PathVariable("parentId") Long parentId) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.detail(parentId));
    }

    @Deprecated
    @PostMapping("/addStorageBox")
    @ApiOperation(value = "添加数据空间存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "添加数据空间存储区参数", dataTypeClass = StorageBoxParam.class)})
    public ResultVO<Boolean> addStorageBox(@RequestBody List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.addStorageBox(storageBoxParamList));
    }

    @Deprecated
    @PostMapping("/editStorageBox")
    @ApiOperation(value = "编辑数据空间存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑数据空间存储区参数", dataTypeClass = StorageBoxParam.class)})
    public ResultVO<Boolean> editStorageBox(@RequestBody List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.editStorageBox(storageBoxParamList));
    }

    @PostMapping("/listStorage")
    @ApiOperation("查询数据存储列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询数据存储列表参数", dataTypeClass = ListStorageParam.class)})
    public ResultVO<List<StorageFeignVO>> listStorageVO(@RequestBody ListStorageParam listStorageParam) {
        return ResultVO.success(manageFeignClient.listStorageVO(listStorageParam));
    }

    @GetMapping("/delete/{parentId}")
    @ApiOperation(value = "删除数据空间")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> delete(@PathVariable("parentId") Long parentId) throws DtvsAdminException {
        return ResultVO.success(dvsParentService.delete(parentId));
    }

}
