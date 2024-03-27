package org.apex.dataverse.controller;


import org.apex.dataverse.service.IDataTypeMapService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DataTypeMapVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 数据类型字典表，脚本初始化 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/data-type-map")
@Api(tags = "数据类型接口")
public class DataTypeMapController {

    @Autowired
    private IDataTypeMapService dataTypeMapService;

    @PostMapping("/list")
    @ApiOperation(value = "数据类型列表")
    public ResultVO<List<DataTypeMapVO>> list() {
        return ResultVO.success(dataTypeMapService.listDataTypeMap());
    }

}
