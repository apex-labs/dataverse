package org.apex.dataverse.controller;


import org.apex.dataverse.param.StorageTypeMapParam;
import org.apex.dataverse.service.IStorageTypeMapService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.StorageTypeMapVO;
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
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/storage-type-map")
@Api(tags = "存储区类型接口")
public class StorageTypeMapController {

    @Autowired
    private IStorageTypeMapService storageTypeMapService;

    @PostMapping("/list")
    @ApiOperation(value = "存储区类型列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = StorageTypeMapParam.class)})
    public ResultVO<List<StorageTypeMapVO>> listStorageTypeMap(@RequestBody StorageTypeMapParam storageTypeMapParam) {
        return new ResultVO<>(storageTypeMapService.listStorageTypeMap(storageTypeMapParam));
    }

}
