package org.apex.dataverse.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Repository;


@Repository
@Getter
@RefreshScope
public class XxlJobUrl {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.admin.addresses}/jobinfo/addCustomizeJob")
    private String addJobUrl;
    /**
     * 根据appName 查找job group
     */
    @Value("${xxl.job.admin.addresses}/jobgroup/findByAppName")
    private String findJobGroupUrl;

    /**
     * 调度中心暂停任务接口
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/pause")
    private String pauseJobUrl;

    /**
     * 调度中心继续任务接口
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/resume")
    private String resumeJobUrl;

    /**
     * 调度中心移除任务接口
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/remove")
    private String deleteJobUrl;

    /**
     * 调度中心编辑任务时间规则接口
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/commonUpdate")
    private String cronEditUrl;

    /**
     * 获取任务最近一次执行日志
     */
    @Value("${xxl.job.admin.addresses}/joblog/getJobLastLog")
    private String checkJobLastLogUrl;


    /**
     * 调度中心立即执行任务接口
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/trigger")
    private String triggerJobUrl;

    /**
     * 检测任务是否过期
     */
    @Value("${xxl.job.admin.addresses}/jobinfo/checkTriggerExists")
    private String checkTriggerExistsUrl;
}
