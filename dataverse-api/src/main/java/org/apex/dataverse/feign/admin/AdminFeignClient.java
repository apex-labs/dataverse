package org.apex.dataverse.feign.admin;

import org.apex.dataverse.constants.ServiceNameConstants;
import org.apex.dataverse.param.DvsAdsTableParam;
import org.apex.dataverse.vo.DvsApiTableVO;
import org.apex.dataverse.vo.StorageBoxFeignVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(contextId = "adminFeignClient", value = ServiceNameConstants.DVS_ADMIN_SERVICE)
public interface AdminFeignClient {

    /**
     * 根据存储ID获取数据空间下配置的数据盒子
     * @param storageId
     * @return
     */
    @PostMapping("/api/dvs/admin/feign/listStorageBox/{storageId}")
    List<StorageBoxFeignVO> listStorageBoxByStorageId(@PathVariable("storageId") Long storageId);

    /**
     * 查询ADS下对应的数据表
     * @param dvsAdsTableParam
     * @return
     */
    @PostMapping("/api/dvs/admin/feign/listDvsAdsTable")
    List<DvsApiTableVO> listDvsAdsTable(@RequestBody DvsAdsTableParam dvsAdsTableParam);
}
