package org.apex.dataverse.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apex.dataverse.service.IStorageBoxService;
import org.apex.dataverse.vo.StorageBoxFeignVO;
import org.apex.dataverse.vo.StorageBoxVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/listStorageBox/{storageId}")
    @ApiOperation("feign接口查询数据空间下的存储桶")
    List<StorageBoxVO> listStorageBoxByStorageId(@PathVariable("storageId") Long storageId) {
        return storageBoxService.listStorageBoxByStorageId(storageId);
    }
}
