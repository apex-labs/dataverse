package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.param.DvsAdsTableParam;
import org.apex.dataverse.service.IAdminFeignService;
import org.apex.dataverse.service.IStorageBoxService;
import org.apex.dataverse.vo.DvsApiTableVO;
import org.apex.dataverse.vo.StorageBoxVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName: AdminFeignController
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/17 10:46
 */
@RestController
@RequestMapping("/api/dvs/admin/feign")
@Api(tags = "dvs-admin对外feign接口服务")
public class AdminFeignController {

    @Autowired
    private IStorageBoxService storageBoxService;

    @Autowired
    private IAdminFeignService adminFeignService;

    @PostMapping("/listStorageBox/{storageId}")
    @ApiOperation("feign接口查询数据空间下的存储桶")
    List<StorageBoxVO> listStorageBoxByStorageId(@PathVariable("storageId") Long storageId) {
        return storageBoxService.listStorageBoxByStorageId(storageId);
    }

    @PostMapping("/listDvsAdsTable")
    @ApiOperation("feign接口查询相同存储区下,数据空间,环境模式, ADS数据域, 对应表")
    List<DvsApiTableVO> listDvsAdsTable(@RequestBody DvsAdsTableParam dvsAdsTableParam) {
        return adminFeignService.listDvsAdsTable(dvsAdsTableParam);
    }
}
