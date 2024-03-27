package org.apex.dataverse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apex.dataverse.dto.FlowDAG;
import org.apex.dataverse.dto.JobSchduleCodeDto;
import org.apex.dataverse.dto.JobScheduleInfoDTO;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.dto.JobScheduleDTO;
import org.apex.dataverse.entity.JobSchedule;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.*;
import org.apex.dataverse.vo.JobScheduleVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 调度任务 服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
public interface IJobScheduleService extends IService<JobSchedule> {

    /**
     * 调度任务列表接口
     *
     * @param pageJobScheduleParam
     * @return
     */
    PageResult<JobScheduleVO> pageJobScheduleVO(PageJobScheduleParam pageJobScheduleParam) throws DtvsAdminException;

    /**
     * 保存调度任务
     *
     * @param jobScheduleParam
     * @return
     */
    Long saveJobSchedule(JobScheduleParam jobScheduleParam) throws DtvsAdminException, JsonProcessingException;


    /**
     * 查看调度任务详情
     *
     * @param jobScheduleInfoParam
     * @return
     */
    JobScheduleDTO jobScheduleInfo(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException;

    /**
     * 删除调度任务
     *
     * @param scheduleId
     * @return
     */
    Boolean deleteScheduleInfo(Long scheduleId) throws DtvsAdminException;

    /**
     * 更新调度状态
     * @param scheduleId
     * @param status
     * @return
     * @throws DtvsAdminException
     */
    Boolean setJobStatus(Long scheduleId, Integer status) throws DtvsAdminException;

    /**
     * 作业是否加入调度
     * @param jobCode
     * @param env
     * @return
     */
    Boolean checkJobAddSchedule(String jobCode,Integer env);


    FlowDAG publishSchedule(String scheduleCode,Integer env);

    /**
     * 新增编辑调度节点
     * @param saveScheduleNode
     * @return
     */
    Long saveOrUpdateScheduleNode(SaveScheduleNode saveScheduleNode) throws DtvsAdminException;

    /**
     * 调度节点，依赖列表查询
     * @param jobScheduleInfoParam
     * @return
     * @throws DtvsAdminException
     */
    JobScheduleInfoDTO scheduleInfo(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException;

    /**
     * 更新依赖生效、失效
     * @param jobScheduleDependenceParam
     * @throws DtvsAdminException
     */
    void updateNodeDependence(JobScheduleDependenceParam jobScheduleDependenceParam) throws DtvsAdminException;

    /**
     * 移除节点
     * @param removeNodeDependenceParam
     * @return
     * @throws DtvsAdminException
     */
    Boolean removeScheduleNode(RemoveNodeDependenceParam removeNodeDependenceParam) throws DtvsAdminException;

    /**
     * 移除节点依赖
     * @param jobScheduleInfoParam
     * @return
     * @throws DtvsAdminException
     */
    Boolean removeDependence(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException;

    /**
     * jobCode 有则返回加入的调度code、无null
     * @param jobCode
     * @param env
     * @return
     */
    JobSchduleCodeDto scheduleCodeByJobCode(String jobCode, Integer env);
}
