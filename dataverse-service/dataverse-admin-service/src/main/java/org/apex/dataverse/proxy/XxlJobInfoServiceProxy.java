package org.apex.dataverse.proxy;

import org.apex.dataverse.config.XxlJobConfig;
import org.apex.dataverse.constants.CommonConstants;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.xxljob.XxlJobInfoFeignClient;
import org.apex.dataverse.model.R;
import org.apex.dataverse.model.ReturnT;
import org.apex.dataverse.model.XxlJobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @ClassName: XxlJobInfoServiceProxy
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/18 15:24
 */
@Component
public class XxlJobInfoServiceProxy {

    @Autowired
    private XxlJobInfoFeignClient xxlJobFeignClient;

    @Autowired
    private XxlJobGroupServiceProxy xxlJobGroupServiceProxy;

    @Autowired
    private XxlJobConfig xxlJobConfig;

    /**
     * 添加调度任务
     *
     * @param xxlJobInfo
     * @return
     */
    public Integer addCustomizeJob(XxlJobInfo xxlJobInfo) throws DtvsAdminException {
        Integer jobGroupId = xxlJobGroupServiceProxy.findByAppName(xxlJobConfig.getAppName());
        if (Objects.isNull(jobGroupId)) {
            throw new DtvsAdminException("执行器分组不存在");
        }
        xxlJobInfo.setJobGroup(jobGroupId);
        ReturnT<Integer> r = xxlJobFeignClient.addCustomizeJob(xxlJobInfo);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("保存调度任务失败");
        }
        return r.getContent();
    }

    /**
     * 暂停调度任务
     *
     * @param id
     * @return
     */
    public String pause(Integer id) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.pause(id);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("暂停调度任务失败");
        }
        return r.getContent();
    }

    /**
     * 恢复调度任务
     *
     * @param id
     * @return
     */
    public String resume(Integer id) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.resume(id);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("恢复调度任务失败");
        }
        return r.getContent();
    }

    /**
     * 删除调度任务
     */
    public Boolean delete(Integer id) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.delete(id);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("删除调度任务失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 执行调度任务
     *
     * @param id
     * @param executorParam
     * @return
     */
    public Boolean runTriggerJob(Integer id, String executorParam) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.runTriggerJob(id, executorParam);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("执行调度任务失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 检查调度任务是否存在
     *
     * @param id
     * @return
     */
    public Boolean checkTriggerExists(Integer id) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.checkTriggerExists(id);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("检查调度任务存在失败");
        }
        return Boolean.TRUE;
    }

    /**
     * 更新调度任务
     *
     * @param xxlJobInfo
     * @return
     */
    public Boolean commonUpdate(XxlJobInfo xxlJobInfo) throws DtvsAdminException {
        ReturnT<String> r = xxlJobFeignClient.commonUpdate(xxlJobInfo);
        if (r.getCode() != CommonConstants.HTTP_OK) {
            throw new DtvsAdminException("更新调度任务失败");
        }
        return Boolean.TRUE;
    }
}
