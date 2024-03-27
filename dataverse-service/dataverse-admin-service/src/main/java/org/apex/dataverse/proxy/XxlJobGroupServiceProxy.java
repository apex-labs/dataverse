package org.apex.dataverse.proxy;

import org.apex.dataverse.constants.CommonConstants;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.xxljob.XxlJobGroupFeignClient;
import org.apex.dataverse.model.R;
import org.apex.dataverse.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @ClassName: XxlJobGroupServiceProxy
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/18 15:24
 */
@Component
public class XxlJobGroupServiceProxy {

    @Autowired
    private XxlJobGroupFeignClient xxlJobGroupFeignClient;

    /**
     * 添加调度任务
     *
     * @param appName
     * @return
     */
    public Integer findByAppName(@RequestParam("appName") String appName) throws DtvsAdminException {
        ReturnT<Integer> r = xxlJobGroupFeignClient.findByAppName(appName);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("查询调度任务分组失败");
        }
        return r.getContent();
    }

}
