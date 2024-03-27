package org.apex.dataverse.feign.xxljob;

import org.apex.dataverse.constants.ServiceNameConstants;
import org.apex.dataverse.model.R;
import org.apex.dataverse.model.ReturnT;
import org.apex.dataverse.model.XxlJobInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "xxlJobGroupFeignClient", value = ServiceNameConstants.DVS_XXL_JOB_ADMIN)
public interface XxlJobGroupFeignClient {

    @GetMapping(value = "/xxl-job-admin/jobgroup/findByAppName")
    ReturnT<Integer> findByAppName(@RequestParam("appName") String appName);
}
