package org.apex.dataverse.feign.xxljob;

import org.apex.dataverse.constants.ServiceNameConstants;
import org.apex.dataverse.model.R;
import org.apex.dataverse.model.ReturnT;
import org.apex.dataverse.model.XxlJobInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "xxlJobInfoFeignClient", value = ServiceNameConstants.DVS_XXL_JOB_ADMIN)
public interface XxlJobInfoFeignClient {

    @PostMapping({"/xxl-job-admin/jobinfo/addCustomizeJob"})
    ReturnT<Integer> addCustomizeJob(@RequestBody XxlJobInfo xxlJobInfo);

    @GetMapping({"/xxl-job-admin/jobinfo/pause"})
    ReturnT<String> pause(@RequestParam("id") int id);

    @GetMapping({"/xxl-job-admin/jobinfo/resume"})
    ReturnT<String> resume(@RequestParam("id") int id);

    @GetMapping({"/xxl-job-admin/jobinfo/delete"})
    ReturnT<String> delete(@RequestParam("id") int id);

    @GetMapping({"/xxl-job-admin/jobinfo/runTrigger"})
    ReturnT<String> runTriggerJob(@RequestParam("id") int id, @RequestParam("executorParam") String executorParam);

    @GetMapping({"/xxl-job-admin/jobinfo/checkTriggerExists"})
    ReturnT<String> checkTriggerExists(@RequestParam("id") int id);

    @PostMapping({"/xxl-job-admin/jobinfo/commonUpdate"})
    ReturnT<String> commonUpdate(@RequestBody XxlJobInfo xxlJobInfo);
}
