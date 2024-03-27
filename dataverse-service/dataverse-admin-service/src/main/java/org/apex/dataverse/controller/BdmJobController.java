package org.apex.dataverse.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.BdmJobParam;
import org.apex.dataverse.param.PageBdmJobParam;
import org.apex.dataverse.param.SqlParam;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.service.IBdmJobService;
import org.apex.dataverse.util.ResultVO;
import org.apex.dataverse.vo.BdmJobGroupVO;
import org.apex.dataverse.vo.BdmJobVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 大数据建模作业，Bigdata Data Modeling 前端控制器
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@RestController
@RequestMapping("/bdm-job")
@Api(tags = "数据开发接口")
public class BdmJobController {

    @Autowired
    private IBdmJobService bdmJobService;

    @PostMapping("/page")
    @ApiOperation("数据开发列表接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "数据开发列表请求参数", dataTypeClass = PageBdmJobParam.class)})
    public ResultVO<PageResult<BdmJobVO>> pageBdmJob(@RequestBody PageBdmJobParam pageBdmJobParam) throws DtvsAdminException {
        return ResultVO.success(bdmJobService.pageBdmJob(pageBdmJobParam));
    }

    @PostMapping("/saveBdmJob")
    @ApiOperation("保存数据开发接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "保存数据开发请求参数", dataTypeClass = BdmJobParam.class)})
    public ResultVO<Long> saveBdmJob(@RequestBody BdmJobParam pageBdmJobParam) throws DtvsAdminException {
        return ResultVO.success(bdmJobService.saveBdmJob(pageBdmJobParam));
    }

    @PostMapping("/editBdmJob")
    @ApiOperation("编辑数据开发接口")
    @ApiImplicitParams({@ApiImplicitParam(value = "编辑数据开发请求参数", dataTypeClass = BdmJobParam.class)})
    public ResultVO<Long> editBdmJob(@RequestBody BdmJobParam pageBdmJobParam) throws DtvsAdminException, SqlParseException {
        return ResultVO.success(bdmJobService.editBdmJob(pageBdmJobParam));
    }

//    @PostMapping("/pageTreeByGroupCode")
//    @ApiOperation("通过数据开发分组code展示数据集成列表树")
//    @ApiImplicitParams({@ApiImplicitParam(value = "通过数据开发分组code展示数据集成列表树请求参数", dataTypeClass = PageTreeByGroupCodeParam.class)})
//    public ResultVO<PageResult<BdmJobVO>> pageTreeByGroupCode(@RequestBody PageTreeByGroupCodeParam pageTreeByGroupCodeParam) throws DtvsAdminException {
//        return ResultVO.success(bdmJobService.pageTreeByGroupCode(pageTreeByGroupCodeParam));
//    }

    @PostMapping("/treeBdmJob")
    @ApiOperation("展示数据集成列表树")
    public ResultVO<List<BdmJobGroupVO>> treeBdmJob(@RequestParam("dataRegionCode") String dataRegionCode) throws DtvsAdminException {
        return ResultVO.success(bdmJobService.treeBdmJob(dataRegionCode));
    }

    @GetMapping("/detailBdmJobVO/{bdmJobId}")
    @ApiOperation("通过ID查询数据建模作业详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "通过数据建模作业ID查询数据建模作业详情请求参数", dataTypeClass = Long.class)})
    public ResultVO<BdmJobVO> detailBdmJobVO(@PathVariable("bdmJobId") Long bdmJobId) throws DtvsAdminException {
        return ResultVO.success(bdmJobService.detailBdmJobVO(bdmJobId));
    }

    @GetMapping("/setBdmJobStatus/{bdmJobId}/{status}")
    @ApiOperation("设置数据建模作业状态")
    @ApiImplicitParams({@ApiImplicitParam(value = "设置数据建模作业请求参数", dataTypeClass = Long.class)})
    public ResultVO<Boolean> setBdmJobStatus(@PathVariable("bdmJobId") Long bdmJobId, @PathVariable("status") Integer status) throws DtvsAdminException {
        return ResultVO.success(bdmJobService.setBdmJobStatus(bdmJobId,status));
    }

    @PostMapping("/executeSql")
    @ApiOperation("单次执行SQL语句")
    public ResultVO<Object> query(@RequestBody SqlParam sqlParam) throws InvalidCmdException,
            InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException {
        return ResultVO.success(bdmJobService.executeSql(sqlParam));
    }

    @PostMapping("/displayTableStructure")
    @ApiOperation("显示表结构")
    public ResultVO<Object> displayTableStructure(@RequestBody SqlParam sqlParam) throws InvalidCmdException,
            InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException {
        return ResultVO.success(bdmJobService.displayTableStructure(sqlParam));
    }
}
