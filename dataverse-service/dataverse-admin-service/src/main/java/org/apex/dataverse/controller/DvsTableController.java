package org.apex.dataverse.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.service.IDvsTableService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.DvsTableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 表 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@RestController
@RequestMapping("/dvs-table")
@Api(tags = "数据表相关接口")
public class DvsTableController {

    @Autowired
    private IDvsTableService dvsTableService;

    @PostMapping("/getDvsTableList")
    @ApiOperation("根据数据域查询数据表列表")
    public ResultVO<List<DvsTableVO>> listDvsTableByDataRegionCode(@RequestParam("dataRegionCode") String dataRegionCode) {
        return ResultVO.success(dvsTableService.listDvsTableByDataRegionCode(dataRegionCode));
    }

}
