package org.apex.dataverse.feign.manage;

import org.apex.dataverse.constants.ServiceNameConstants;
import org.apex.dataverse.param.DashboardStatisticsParam;
import org.apex.dataverse.param.ListStorageParam;
import org.apex.dataverse.vo.DashboardStatisticsExeCmdVO;
import org.apex.dataverse.vo.StorageFeignVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(contextId = "ManageFeignClient", value = ServiceNameConstants.DVS_MANAGE_SERVICE)
public interface ManageFeignClient {

    @PostMapping("/api/dvs/manage/feign/listStorage")
    List<StorageFeignVO> listStorageVO(ListStorageParam listStorageParam);

    @PostMapping("/api/dvs/manage/feign/statisticsExeCmd")
    List<DashboardStatisticsExeCmdVO> statisticsExeCmd(DashboardStatisticsParam dashboardStatisticsParam);
}
