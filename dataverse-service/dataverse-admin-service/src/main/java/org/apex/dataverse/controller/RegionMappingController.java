package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageRegionMappingParam;
import org.apex.dataverse.param.RegionMappingParam;
import org.apex.dataverse.service.IRegionMappingService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.RegionMappingVO;
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
@RequestMapping("/region-mapping")
@Api(tags = "业务域映射接口")
public class RegionMappingController {

    @Autowired
    private IRegionMappingService regionMappingService;

    @PostMapping("/page")
    @ApiOperation(value = "业务域映射列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageRegionMappingParam.class)})
    public ResultVO<PageResult<RegionMappingVO>> page(@RequestBody PageRegionMappingParam pageRegionMappingParam) throws DtvsAdminException {
        return ResultVO.success(regionMappingService.pageRegionMapping(pageRegionMappingParam));
    }

    @PostMapping("/addRegionMapping")
    @ApiOperation(value = "新增业务域映射")
    @ApiImplicitParams({@ApiImplicitParam(value = "新增参数", dataTypeClass = RegionMappingParam.class)})
    public ResultVO<Long> addRegionMapping(@RequestBody RegionMappingParam regionMappingParam) throws DtvsAdminException {
        return ResultVO.success(regionMappingService.addRegionMapping(regionMappingParam));
    }

    @PostMapping("/editRegionMapping")
    @ApiOperation(value = "编辑业务域映射")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = RegionMappingParam.class)})
    public ResultVO<Long> editRegionMapping(@RequestBody RegionMappingParam regionMappingParam) throws DtvsAdminException {
        return ResultVO.success(regionMappingService.editRegionMapping(regionMappingParam));
    }

}
