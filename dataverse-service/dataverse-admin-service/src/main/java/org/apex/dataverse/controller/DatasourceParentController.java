package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.dto.DatasourceColumnDTO;
import org.apex.dataverse.dto.DatasourceParentDTO;
import org.apex.dataverse.dto.DatasourceTableDTO;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.IDatasourceParentService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DatasourceParentVO;
import org.apex.dataverse.vo.TestDataSourceLinkVO;
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
 * @since 2023-05-15
 */
@RestController
@RequestMapping("/datasource-parent")
@Api(tags = "数据源接口")
public class DatasourceParentController {

    @Autowired
    private IDatasourceParentService datasourceParentService;

    @PostMapping("/page")
    @ApiOperation(value = "数据源列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageDatasourceParentParam.class)})
    public ResultVO<PageResult<DatasourceParentVO>> page(@RequestBody PageDatasourceParentParam pageDatasourceParentParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.pageDatasourceParent(pageDatasourceParentParam));
    }

    @PostMapping("/addDatasourceParent")
    @ApiOperation(value = "新增数据源")
    @ApiImplicitParams({@ApiImplicitParam(value = "新增参数", dataTypeClass = DatasourceParentParam.class)})
    public ResultVO<Long> addDatasourceParent(@RequestBody DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.addDatasourceParent(datasourceParentParam));
    }

    @PostMapping("/editDatasourceParent")
    @ApiOperation(value = "编辑数据源")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = DatasourceParentParam.class)})
    public ResultVO<Long> editDatasourceParent(@RequestBody DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.editDatasourceParent(datasourceParentParam));
    }

    @PostMapping("/testDatasourceParentLink")
    @ApiOperation(value = "测试数据源连接")
    @ApiImplicitParams({@ApiImplicitParam(value = "请求参数", dataTypeClass = DatasourceParentParam.class)})
    public ResultVO<List<TestDataSourceLinkVO>> testDatasourceParentLink(@RequestBody DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.testDatasourceParentLink(datasourceParentParam));
    }

    @PostMapping("/listDatasourceParentByType")
    @ApiOperation(value = "查询数据源列表接口")
    @ApiImplicitParams(@ApiImplicitParam(value = "查询参数", dataTypeClass = ListDataSourceParam.class))
    public ResultVO<List<DatasourceParentDTO>> listDatasourceParentByType(@RequestBody ListDataSourceParam listDataSourceParam) {
        return ResultVO.success(datasourceParentService.listDatasourceParentByType(listDataSourceParam));
    }

    @PostMapping("/getDatasourceTable")
    @ApiOperation(value = "查询数据源下对应的表接口")
    public ResultVO<List<DatasourceTableDTO>> getDatasourceTable(@RequestBody ListDatasourceTableParam listDatasourceTableParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.getDatasourceTable(listDatasourceTableParam));
    }

    @PostMapping("/getDatasourceTableAndColumn")
    @ApiOperation(value = "查询数据源下对应表的列接口")
    public ResultVO<List<DatasourceColumnDTO>> getDatasourceTableAndColumn(@RequestBody ListDatasourceTableColumnParam listDatasourceTableColumnParam) throws DtvsAdminException {
        return ResultVO.success(datasourceParentService.getDatasourceTableAndColumn(listDatasourceTableColumnParam));
    }

    @GetMapping("/detail/{parentId}")
    @ApiOperation("数据源详情")
    public ResultVO<DatasourceParentVO> detail(@PathVariable("parentId") Long parentId) throws DtvsAdminException {

        return ResultVO.success(datasourceParentService.detail(parentId));
    }

    @GetMapping("/delete/{parentId}")
    @ApiOperation("删除数据源")
    public ResultVO<Boolean> delete(@PathVariable("parentId") Long parentId) throws DtvsAdminException {

        return ResultVO.success(datasourceParentService.delete(parentId));
    }


}
