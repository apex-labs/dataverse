package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DataStorageParam;
import org.apex.dataverse.param.PageDataStorageParam;
import org.apex.dataverse.service.IDataStorageService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DataStorageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/data-storage")
@Api(tags = "数据存储区接口")
public class DataStorageController {

    @Autowired
    private IDataStorageService dataStorageService;

    @PostMapping("/page")
    @ApiOperation(value = "数据存储区列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageDataStorageParam.class)})
    public ResultVO<PageResult<DataStorageVO>> page(@RequestBody PageDataStorageParam pageDataStorageParam) throws DtvsAdminException {
        return ResultVO.success(dataStorageService.pageDataStorage(pageDataStorageParam));
    }

    @PostMapping("/addDataStorage")
    @ApiOperation(value = "保存数据存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存参数", dataTypeClass = DataStorageParam.class)})
    public ResultVO<Long> addDataStorage(@RequestBody DataStorageParam dataStorageParam) throws DtvsAdminException {
        return ResultVO.success(dataStorageService.addDataStorage(dataStorageParam));
    }

    @PostMapping("/editDataStorage")
    @ApiOperation(value = "编辑数据存储区")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = DataStorageParam.class)})
    public ResultVO<Long> editDataStorage(@RequestBody DataStorageParam dataStorageParam) throws DtvsAdminException {
        return ResultVO.success(dataStorageService.editDataStorage(dataStorageParam));
    }

}
