package org.apex.dataverse.controller;


import com.baomidou.mybatisplus.extension.api.R;
import io.swagger.models.auth.In;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.param.ListDataRegionParam;
import org.apex.dataverse.service.IDataRegionService;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DataRegionParam;
import org.apex.dataverse.param.PageDataRegionParam;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DataRegionVO;
import org.apex.dataverse.vo.DwLayerVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据域 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@RestController
@RequestMapping("/data-region")
@Api(tags = "数据域接口")
public class DataRegionController {

    @Autowired
    private IDataRegionService dataRegionService;

    @PostMapping("/page")
    @ApiOperation("数据域分页列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据域列表查询接口", dataTypeClass = PageDataRegionParam.class)})
    public ResultVO<PageResult<DataRegionVO>> pageDataRegion(@RequestBody PageDataRegionParam pageDataRegionParam) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.pageDataRegion(pageDataRegionParam));
    }

    @PostMapping("/addDataRegion")
    @ApiOperation("数据域新增")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据域新增接口", dataTypeClass = DataRegionParam.class)})
    public ResultVO<Boolean> addDataRegion(@RequestBody List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.addDataRegion(dataRegionParamList));
    }

    @PostMapping("/editDataRegion")
    @ApiOperation("数据域编辑")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据域编辑接口", dataTypeClass = DataRegionParam.class)})
    public ResultVO<Boolean> editDataRegion(@RequestBody List<DataRegionParam> dataRegionParamList) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.editDataRegion(dataRegionParamList));
    }

    @GetMapping("/dwLayer")
    @ApiOperation("数据域分层列表")
    public ResultVO<List<DwLayerVO>> dwLayer() {
        return ResultVO.success(dataRegionService.dwLayer());
    }

    @GetMapping("/dwLayerDetail")
    @ApiOperation("数据域分层明细列表")
    public ResultVO<List<DwLayerVO>> dwLayerDetail() {
        return ResultVO.success(dataRegionService.dwLayerDetail());
    }

    @GetMapping("/detail/{dataRegionId}")
    @ApiOperation("数据域详情")
    public ResultVO<DataRegionVO> detail(@PathVariable("dataRegionId") Long dataRegionId) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.detail(dataRegionId));
    }

    @GetMapping("/delete/{dataRegionId}")
    @ApiOperation("删除数据域")
    public ResultVO<Boolean> delete(@PathVariable("dataRegionId") Long dataRegionId) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.delete(dataRegionId));
    }

    @PostMapping("/listDataRegionByDvsAndEnv")
    @ApiOperation("数据空间对应数据域列表")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据域列表查询接口", dataTypeClass = ListDataRegionParam.class)})
    public ResultVO<List<DataRegionVO>> listDataRegionByDvsAndEnv(@RequestBody ListDataRegionParam listDataRegionParam) throws DtvsAdminException {
        return ResultVO.success(dataRegionService.listDataRegionByDvsAndEnv(listDataRegionParam));
    }

    @GetMapping("/getDwLayerDetail/{layer}")
    @ApiOperation("根据数据分层展示数据域分层详细列表: ODS DW ADS")
    public ResultVO<List<DwLayerVO>> getDwLayerDetail(@PathVariable("layer") String layer) {
        return ResultVO.success(dataRegionService.getDwLayerDetail(layer));
    }
}
