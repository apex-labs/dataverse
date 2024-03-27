package org.apex.dataverse.controller;


import org.apex.dataverse.param.DatasourceTypeMapParam;
import org.apex.dataverse.service.IDatasourceTypeMapService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DatasourceTypeMapVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-08
 */
@RestController
@RequestMapping("/datasource-type-map")
@Api(tags = "数据源地图接口")
public class DatasourceTypeMapController {

    @Autowired
    private IDatasourceTypeMapService datasourceTypeMapService;

    @PostMapping("/list")
    @ApiOperation(value = "数据源地图列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = DatasourceTypeMapParam.class)})
    public ResultVO<List<DatasourceTypeMapVO>> listDatasourceTypeMap(@RequestBody DatasourceTypeMapParam dataSourceTypeMapParam) {
        return new ResultVO<>(datasourceTypeMapService.listDatasourceTypeMap(dataSourceTypeMapParam));
    }

    @PostMapping("/listAll")
    @ApiOperation(value = "全部数据源地图列表")
    public ResultVO<List<DatasourceTypeMapVO>> listDatasourceTypeMap() {
        return new ResultVO<>(datasourceTypeMapService.listAllDatasourceTypeMap());
    }

}
