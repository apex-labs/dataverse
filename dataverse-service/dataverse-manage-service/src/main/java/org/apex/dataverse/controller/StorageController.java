package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.dao.StorageDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.AttachStorageTenantParam;
import org.apex.dataverse.param.SaveStorageParam;
import org.apex.dataverse.param.StoragePageParam;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.EnumInfoVO;
import org.apex.dataverse.vo.StorageTenantVO;
import org.apex.dataverse.vo.StorageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName: StorageController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/9 16:35
 */
@RestController
@RequestMapping("/storage")
@Api(tags = "存储区接口")
public class StorageController {

    @Autowired
    private StorageDAO storageDAO;

    @PostMapping("/page")
    @ApiOperation("存储区列表接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = StoragePageParam.class)})
    public ResultVO<PageResult<StorageVO>> page(@RequestBody StoragePageParam storagePageParam) throws DtvsManageException {
        return ResultVO.success(storageDAO.pageStorage(storagePageParam));
    }

    @PostMapping("/add")
    @ApiOperation("保存存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = SaveStorageParam.class)})
    public ResultVO<Long> add(@RequestBody SaveStorageParam saveStorageParam) throws DtvsManageException {
        return ResultVO.success(storageDAO.add(saveStorageParam));
    }

    @PostMapping("/edit")
    @ApiOperation("编辑存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = SaveStorageParam.class)})
    public ResultVO<Long> edit(@RequestBody SaveStorageParam saveStorageParam) throws DtvsManageException {
        return ResultVO.success(storageDAO.edit(saveStorageParam));
    }

    @PostMapping("/detail")
    @ApiOperation("存储区详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = Long.class)})
    public ResultVO<StorageVO> detail(Long storageId) throws DtvsManageException  {
        return ResultVO.success(storageDAO.detail(storageId));
    }

    @Deprecated
    @PostMapping("/attachStorageTenant")
    @ApiOperation("存储区租户授权")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = AttachStorageTenantParam.class)})
    public ResultVO<Boolean> attachStorageTenant(@RequestBody List<AttachStorageTenantParam> attachStorageTenantParamList) throws DtvsManageException  {
        return ResultVO.success(storageDAO.attachStorageTenant(attachStorageTenantParamList));
    }

    // todo 租户列表接口 暂时取数据库初始化
    @PostMapping("/tenants")
    @ApiOperation("租户列表")
    public ResultVO<List<StorageTenantVO>> tenants() {
        return ResultVO.success(storageDAO.tenants());
    }


    @PostMapping("/storageTypeList")
    @ApiOperation("存储类型列表")
    public ResultVO<List<EnumInfoVO>> storageTypeList() {
        return ResultVO.success(storageDAO.storageTypeList());
    }

    @PostMapping("/engineTypeList")
    @ApiOperation("引擎类型列表")
    public ResultVO<List<EnumInfoVO>> engineTypeList() {
        return ResultVO.success(storageDAO.engineTypeList());
    }

    @PostMapping("/connTypeList")
    @ApiOperation("连接类型列表")
    public ResultVO<List<EnumInfoVO>> connTypeList() {
        return ResultVO.success(storageDAO.connTypeList());
    }

    @PostMapping("/delete")
    @ApiOperation("删除存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> delete(Long storageId) throws DtvsManageException  {
        return ResultVO.success(storageDAO.delete(storageId));
    }

}
