package org.apex.dataverse.jobhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DtvsManageDemoHandler {


    @XxlJob("DtvsManageDemoHandler")
    public ReturnT<String> execute(String param) throws Exception {
        log.info("DtvsManageDemoHandler is triggered ,param:{}, jobId:{}", param, XxlJobHelper.getJobId());
        return ReturnT.SUCCESS;
    }
}
