package org.apex.dataverse.controller;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.BizRegionParam;
import org.apex.dataverse.param.PageBizRegionParam;
import org.apex.dataverse.service.IBizRegionService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.BizRegionVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/biz-region")
@Api(tags = "业务域接口")
public class BizRegionController {

    @Autowired
    private IBizRegionService bizRegionService;

    @PostMapping("/page")
    @ApiOperation(value = "业务域列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "查询参数", dataTypeClass = PageBizRegionParam.class)})
    public ResultVO<PageResult<BizRegionVO>> page(@RequestBody PageBizRegionParam pageBizRegionParam) throws DtvsAdminException {
        return ResultVO.success(bizRegionService.pageBizRegion(pageBizRegionParam));
    }

    @PostMapping("/addBizRegion")
    @ApiOperation(value = "新增业务域")
    @ApiImplicitParams({@ApiImplicitParam(value = "新增参数", dataTypeClass = BizRegionParam.class)})
    public ResultVO<Long> page(@RequestBody BizRegionParam bizRegionParam) throws DtvsAdminException {
        return ResultVO.success(bizRegionService.addBizRegion(bizRegionParam));
    }

    @PostMapping("/editBizRegion")
    @ApiOperation(value = "编辑业务域")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑参数", dataTypeClass = BizRegionParam.class)})
    public ResultVO<Long> editBizRegion(@RequestBody BizRegionParam bizRegionParam) throws DtvsAdminException {
        return ResultVO.success(bizRegionService.editBizRegion(bizRegionParam));
    }

    @GetMapping("/detail/{bizRegionId}")
    @ApiOperation(("业务域详情"))
    public ResultVO<BizRegionVO> detail(@PathVariable("bizRegionId") Long bizRegionId) throws DtvsAdminException {
        return ResultVO.success(bizRegionService.detail(bizRegionId));
    }

    @GetMapping("/delete/{bizRegionId}")
    @ApiOperation(("删除业务域"))
    public ResultVO<Boolean> delete(@PathVariable("bizRegionId") Long bizRegionId) throws DtvsAdminException {
        return ResultVO.success(bizRegionService.delete(bizRegionId));
    }

}
