package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.dto.*;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.*;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.DvsMapper;
import org.apex.dataverse.mapper.JobScheduleMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.model.XxlJobInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.proxy.XxlJobGroupServiceProxy;
import org.apex.dataverse.proxy.XxlJobInfoServiceProxy;
import org.apex.dataverse.service.IDvsParentService;
import org.apex.dataverse.service.IJobScheduleService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.DateUtils;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import org.apex.dataverse.vo.JobScheduleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * <p>
 * 调度任务 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class JobScheduleServiceImpl extends ServiceImpl<JobScheduleMapper, JobSchedule> implements IJobScheduleService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private ScheduleNodeServiceImpl scheduleNodeService;

    @Autowired
    private ScheduleEdgeServiceImpl scheduleEdgeService;

    @Autowired
    private JobScheduleServiceImpl jobScheduleService;

    @Autowired
    private NodeJobsServiceImpl nodeJobsService;

    @Autowired
    private IDvsParentService dvsParentService;

    @Autowired
    private DvsMapper dvsMapper;

    @Autowired
    private XxlJobGroupServiceProxy xxlJobGroupServiceProxy;

    @Autowired
    private XxlJobInfoServiceProxy xxlJobInfoServiceProxy;

    @Override
    public PageResult<JobScheduleVO> pageJobScheduleVO(PageJobScheduleParam pageJobScheduleParam) throws DtvsAdminException {
        validateSchedule(pageJobScheduleParam);
        IPage<JobSchedule> iPage = getIPage(pageJobScheduleParam);
        PageResult<JobScheduleVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), JobScheduleVO.class));
        for (JobScheduleVO jobScheduleVO :pageResult.getList()){
            jobScheduleVO.setStartTimeValue(DateUtils.DateToString(jobScheduleVO.getStartTime()));
            jobScheduleVO.setEndTimeValue(DateUtils.DateToString(jobScheduleVO.getEndTime()));
            if(StringUtils.isNotEmpty(jobScheduleVO.getCron())){
                String[] cronStr = jobScheduleVO.getCron().split(" ");
                jobScheduleVO.setTime(cronStr[1]+":"+cronStr[2]);
                jobScheduleVO.setDayNum(cronStr[3].substring(2));
            }
        }
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long saveJobSchedule(JobScheduleParam jobScheduleParam) throws DtvsAdminException, JsonProcessingException {
        //校验
        SaveJobScheduleParam saveJobScheduleParam = mapperFactory.getMapperFacade().map(jobScheduleParam, SaveJobScheduleParam.class);
        validateSaveOrUpdateSchedule(saveJobScheduleParam);

        JobSchedule jobSchedule = this.getJobSchedule(jobScheduleParam);
        jobSchedule.setStartTime(DateUtils.StringToDate(jobScheduleParam.getStartTimeValue()));
        jobSchedule.setEndTime(DateUtils.StringToDate(jobScheduleParam.getEndTimeValue()));
        jobSchedule.setCron(cronStr(jobScheduleParam));
        jobSchedule.setUpdateTime(LocalDateTime.now());
        if (StringUtils.isEmpty(jobScheduleParam.getScheduleCode())){
            jobSchedule.setCreateTime(LocalDateTime.now());
            jobSchedule.setScheduleCode(UCodeUtil.produce());
            //新增调度
            jobSchedule.setJobId(Long.valueOf(addXxlJob("DvsAdminJobScheduleHandler",jobScheduleParam.getScheduleName(),jobSchedule.getCron(), "email",jobSchedule.getUserName() ,jobSchedule.getScheduleCode())));

            // 初始化开始节点
            ScheduleNode saveScheduleNode = new ScheduleNode(jobSchedule.getScheduleCode(),UCodeUtil.produce(),"开始节点",
                    NodeTyepEnum.START_NODE.getValue(),jobSchedule.getEnv(),LocalDateTime.now());
            if (!scheduleNodeService.save(saveScheduleNode)) {
                throw new DtvsAdminException("保存调度任务开始节点失败");
            }
        } else {
            JobSchedule schedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, jobSchedule.getScheduleCode())
                    .eq(JobSchedule::getEnv,jobScheduleParam.getEnv()));
            jobSchedule.setScheduleId(schedule.getScheduleId());
        }
          if (!saveOrUpdate(jobSchedule)) {
            throw new DtvsAdminException("保存JobSchedule失败,调度任务编码 {}", jobScheduleParam.getScheduleCode());
        }

        return jobSchedule.getScheduleId();
    }


    private String cronStr(JobScheduleParam jobScheduleParam){
        String[] time = jobScheduleParam.getTime().split(":");
        StringBuilder sb =  new StringBuilder();
        sb.append("0").append(" ").append(time[1]).append(" ").append(time[0]).append(" ").append("1/").append(jobScheduleParam.getDayNum()).append(" * ?");
        return sb.toString();
    }


    @Override
    @Transactional
    public Long saveOrUpdateScheduleNode(SaveScheduleNode saveScheduleNode) throws DtvsAdminException {
        validateSaveOrUpdateScheduleNode(saveScheduleNode);
        //job_schedule
        JobSchedule jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, saveScheduleNode.getScheduleCode()).
                eq(JobSchedule::getEnv, saveScheduleNode.getEnv()));
        jobSchedule.setUpdateTime(LocalDateTime.now());
        //scheduleNode
        ScheduleNode scheduleNode = null;
        if(saveScheduleNode.getIsAddNode() == 0){
            scheduleNode = mapperFactory.getMapperFacade().map(saveScheduleNode, ScheduleNode.class);
            scheduleNode.setCreateTime(LocalDateTime.now());
            scheduleNode.setNodeCode(UCodeUtil.produce());
            scheduleNode.setNodeType(NodeTyepEnum.DO_NODE.getValue());
            if (!scheduleNodeService.saveOrUpdate(scheduleNode)) {
                throw new DtvsAdminException("保存调度节点失败");
            }
        }

        String nodeCode;
        if(null == scheduleNode){
            nodeCode = saveScheduleNode.getNodeJobsParam().get(0).getNodeCode();
            scheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeCode, nodeCode)
                    .eq(ScheduleNode::getEnv, saveScheduleNode.getEnv()));
        }  else {
            nodeCode = scheduleNode.getNodeCode();
        }
        //nodejob
        String currentCode = nodeJobConfig(saveScheduleNode,nodeCode);

        //scheduleEdge
        edgeConfig(saveScheduleNode,currentCode);



        //update nodeNum
        List<ScheduleNode> scheduleNodeList = scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, saveScheduleNode.getScheduleCode()).
                eq(ScheduleNode::getEnv, saveScheduleNode.getEnv()));//节点数 任务数
        jobSchedule.setNodeNum(scheduleNodeList.size());
        //update jobNum
        List<String> scheduleCodeList = scheduleNodeList.stream().map(p -> p.getNodeCode()).collect(Collectors.toList());
        List<NodeJobs> jobCountNum = nodeJobsService.list(Wrappers.<NodeJobs>lambdaQuery().in(NodeJobs::getNodeCode, scheduleCodeList).
                eq(NodeJobs::getEnv,saveScheduleNode.getEnv()));
        jobSchedule.setJobNum(jobCountNum.size());
        if (!saveOrUpdate(jobSchedule)) {
            throw new DtvsAdminException("保存JobSchedule失败,调度任务编码 {}", jobSchedule.getScheduleCode());
        }
        return scheduleNode.getNodeId();
    }

    public JobScheduleInfoDTO scheduleInfo(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException{
        JobScheduleInfoDTO jobScheduleInfoDTO =new JobScheduleInfoDTO();

        List<ScheduleNode> list = scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, jobScheduleInfoParam.getScheduleCode())
                .eq(ScheduleNode::getEnv,jobScheduleInfoParam.getEnv()));
        if (CollectionUtil.isEmpty(list)){
            return jobScheduleInfoDTO;
        }

        //作业编码查询节点编码
        NodeJobs nodeJob = nodeJobsService.getOneNodeJobs(jobScheduleInfoParam.getJobCode(),jobScheduleInfoParam.getEnv());
        List<ScheduleEdge> scheduleEdgeList = new ArrayList<>();
        List<String> allNodeList = new ArrayList<>();
        if (null != nodeJob){
            scheduleEdgeList = scheduleEdgeService.list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, jobScheduleInfoParam.getScheduleCode()).
                    eq(ScheduleEdge::getToNode, nodeJob.getNodeCode()).eq(ScheduleEdge::getEnv,jobScheduleInfoParam.getEnv()));
            //作业列表排除当前节点及依赖节点
            // allNodeList.add(nodeJob.getNodeCode());
            if(CollectionUtil.isNotEmpty(scheduleEdgeList)){
                List<String> dependenceNode = scheduleEdgeList.stream().map(p -> p.getFromNode()).collect(Collectors.toList());
                allNodeList.addAll(dependenceNode);
            }
        }
        //作业列表
        List<ScheduleNode> scheduleNodeList;
        if(CollectionUtil.isNotEmpty(allNodeList)){
            scheduleNodeList = list.stream().filter(e -> !allNodeList.contains(e.getNodeCode())).collect(Collectors.toList());
        } else {
            scheduleNodeList = list;
        }
        NodeJobsDTO nodeJobsDTO;
        ScheduleNodeDTO scheduleNodeDTO;
        List<ScheduleNodeDTO> scheduleNodeDTOList = new ArrayList<>();
        for (ScheduleNode scheduleNode:scheduleNodeList){
            nodeJobsDTO = new NodeJobsDTO();
            NodeJobs nodejobInfo = nodeJobsService.getOne(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, scheduleNode.getNodeCode())
                    .eq(NodeJobs::getEnv,jobScheduleInfoParam.getEnv()));
            //集成作业不能依赖开发作业
            if (null != nodejobInfo && jobScheduleInfoParam.getJobType() == JobTypeEnum.ETL_JOB.getValue() && nodejobInfo.getJobType() == JobTypeEnum.BDM_JOB.getValue()) {
                continue;
            }
            if (null != nodejobInfo && scheduleNode.getNodeType() != NodeTyepEnum.START_NODE.getValue()){
                scheduleNodeDTO = new ScheduleNodeDTO();
                BeanUtils.copyProperties(nodejobInfo,nodeJobsDTO);
                scheduleNodeDTO.setNodeJobsDTO(nodeJobsDTO);
                BeanUtils.copyProperties(scheduleNode,scheduleNodeDTO);
                scheduleNodeDTOList.add(scheduleNodeDTO);
            }
        }
        jobScheduleInfoDTO.setScheduleNodeDTOList(scheduleNodeDTOList);

        //当前节点依赖的作业列表
        List<DependenceNodeDTO> dependenceNodeDTOList = new ArrayList<>();
        DependenceNodeDTO dependenceNodeDTO;
        for (ScheduleEdge scheduleEdge:scheduleEdgeList){
            NodeJobs dependenceNodejob = nodeJobsService.getOneNodeJobsByNodeCode(scheduleEdge.getFromNode(),jobScheduleInfoParam.getEnv());
            if (null != dependenceNodejob){
                dependenceNodeDTO = new DependenceNodeDTO(dependenceNodejob.getJobName(),dependenceNodejob.getNodeCode(),dependenceNodejob.getUserId(),dependenceNodejob.getUserName());
                dependenceNodeDTO.setIsValid(scheduleEdge.getIsValid());
                dependenceNodeDTOList.add(dependenceNodeDTO);
            }
        }
        jobScheduleInfoDTO.setDependenceNodeDTOList(dependenceNodeDTOList);
        return jobScheduleInfoDTO;
    }

    public void updateNodeDependence(JobScheduleDependenceParam jobScheduleDependenceParam) throws DtvsAdminException {
        JobSchedule checkJob = jobScheduleService.getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, jobScheduleDependenceParam.getScheduleCode())
                            .eq(JobSchedule::getEnv,jobScheduleDependenceParam.getEnv()));
        if(null == checkJob){
            throw new DtvsAdminException("调度任务不存在");
        }

        NodeJobs jobs = nodeJobsService.getOneNodeJobs(jobScheduleDependenceParam.getJobCode(),jobScheduleDependenceParam.getEnv());
        if (null == jobs){
            throw new DtvsAdminException("调度任务节点不存在");
        }

        ScheduleEdge scheduleEdge = scheduleEdgeService.getOne(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, jobScheduleDependenceParam.getScheduleCode()).
                eq(ScheduleEdge::getFromNode, jobScheduleDependenceParam.getDependenceCode()).eq(ScheduleEdge::getToNode, jobs.getNodeCode()).eq(ScheduleEdge::getEnv,jobScheduleDependenceParam.getEnv()));
        if (null == scheduleEdge){
            throw new DtvsAdminException("依赖关系为空");
        }
        scheduleEdge.setIsValid(jobScheduleDependenceParam.getIsValid());
        if (!scheduleEdgeService.saveOrUpdate(scheduleEdge) ){
            throw new DtvsAdminException("节点依赖生效/失效 失败");
        }
    }


    private void edgeConfig(SaveScheduleNode saveScheduleNode,String currentNodeCode) throws DtvsAdminException{
        if(saveScheduleNode.getNodeType() == NodeTyepEnum.START_NODE.getValue() ){
            ScheduleNode headNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, saveScheduleNode.getScheduleCode()).
                    eq(ScheduleNode::getNodeType, NodeTyepEnum.START_NODE.getValue()).eq(ScheduleNode::getEnv,saveScheduleNode.getEnv()));
            if(Objects.isNull(headNode)){
                throw new DtvsAdminException("根节点为空");
            }
            ScheduleEdge scheduleEdge = new ScheduleEdge(UCodeUtil.produce(),saveScheduleNode.getScheduleCode(),headNode.getNodeCode(),currentNodeCode, saveScheduleNode.getEnv(),LocalDateTime.now(),1);
            if (!scheduleEdgeService.saveOrUpdate(scheduleEdge)) {
                throw new DtvsAdminException("保存调度节点失败");
            }
        } else {
            if(CollectionUtil.isNotEmpty(saveScheduleNode.getScheduleEdgeParam())){
                for(ScheduleEdgeParam scheduleEdgeParam:saveScheduleNode.getScheduleEdgeParam()){
                    //ScheduleEdge
                    ScheduleEdge scheduleEdge = mapperFactory.getMapperFacade().map(scheduleEdgeParam, ScheduleEdge.class);
                    scheduleEdge.setEdgeCode(UCodeUtil.produce());
                    scheduleEdge.setCreateTime(LocalDateTime.now());
                    scheduleEdge.setIsValid(1);
                    //不是当前新增任务currentNodeCode为空
                    if (StringUtils.isNotEmpty(currentNodeCode)){
                        scheduleEdge.setToNode(currentNodeCode);
                    } else {
                        //查询当前作业的节点编码
                        NodeJobs jobs = nodeJobsService.getOneNodeJobs(saveScheduleNode.getJobCode(),saveScheduleNode.getEnv());
                        if (null == jobs){
                            throw new DtvsAdminException("调度任务节点不存在");
                        }
                        scheduleEdge.setToNode(jobs.getNodeCode());
                    }
                    if (!scheduleEdgeService.saveOrUpdate(scheduleEdge)) {
                        throw new DtvsAdminException("保存调度边界失败");
                    }
                }
            }
        }
    }

    private String nodeJobConfig(SaveScheduleNode saveScheduleNode,String addNodeCode) throws DtvsAdminException{
        String nodeCode="";
        for(NodeJobsParam nodeJobsParam:saveScheduleNode.getNodeJobsParam()){
            //依赖job是否已加入其它调度
             NodeJobs nodeJobs = nodeJobsService.getOneNodeJobs(saveScheduleNode.getJobCode(),saveScheduleNode.getEnv());
            if(null != nodeJobs){
                //依赖job加入到其它调度数据处理
                nodeJobHandle(saveScheduleNode.getScheduleCode(),saveScheduleNode.getEnv(),nodeJobs.getNodeCode(),nodeJobs);
            }
            //当前任务job
            if(StringUtils.isEmpty(nodeJobsParam.getNodeCode())){
                NodeJobs newNodeJobs = mapperFactory.getMapperFacade().map(nodeJobsParam, NodeJobs.class);
                newNodeJobs.setCreateTime(LocalDateTime.now());
                newNodeJobs.setNodeCode(addNodeCode);
                if (!nodeJobsService.save(newNodeJobs)) {
                    throw new DtvsAdminException("保存节点job失败");
                }
                nodeCode =  newNodeJobs.getNodeCode();
            }

        }
        return nodeCode;
    }

    private void nodeJobHandle(String scheduleCode,Integer env,String nodeCode,NodeJobs nodeJobs) throws DtvsAdminException{
        ScheduleNode checkScheduleNode = scheduleNodeService.getOne((Wrappers.<ScheduleNode>lambdaQuery().in(ScheduleNode::getNodeCode, nodeJobs.getNodeCode())
                .eq(ScheduleNode::getEnv,env)));
        if(null == checkScheduleNode){
            return;
        }
        if(!checkScheduleNode.getScheduleCode().equals(scheduleCode)){
            //判断任务下是否有子节点
            List<ScheduleEdge> nodeChildCheck = scheduleEdgeService.list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getFromNode, nodeCode)
                    .eq(ScheduleEdge::getEnv,env));
            if(CollectionUtil.isNotEmpty(nodeChildCheck)){
                throw new DtvsAdminException("当前作业已加入其它调度任务且有子节点 加入调度失败");
            } else {
                //删除当前作业在其他调度的节点信息
                List<String> fromCode = new ArrayList<>();
                fromCode.add(nodeJobs.getNodeCode());
                removeScheduleNodeInfo(checkScheduleNode.getScheduleCode(),nodeJobs.getJobCode(),env,fromCode);
            }
        }
    }

    public JobSchedule getJobSchedule(JobScheduleParam jobScheduleParam) {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        JobSchedule jobSchedule = mapperFactory.getMapperFacade().map(jobScheduleParam, JobSchedule.class);
        jobSchedule.setTenantId(userInfo.getTenantId());
        jobSchedule.setTenantName(userInfo.getTenantName());
        jobSchedule.setDeptId(userInfo.getDeptId());
        jobSchedule.setDeptName(userInfo.getDeptName());
        jobSchedule.setUserId(userInfo.getUserId());
        jobSchedule.setUserName(userInfo.getUserName());
        return jobSchedule;
    }

    private IPage<JobSchedule> getIPage(PageJobScheduleParam pageJobScheduleParam) throws DtvsAdminException {
        // 默认单页10条记录
        Page<JobSchedule> page = new Page<>();
        PageQueryParam pageQueryParam = pageJobScheduleParam.getPageQueryParam();
        if (pageQueryParam != null && pageQueryParam.getSize() != null) {
            page.setSize(pageQueryParam.getSize());
        } else {
            page.setSize(10);
        }

        if (pageQueryParam != null && pageQueryParam.getPageNo() != null) {
            page.setCurrent(pageQueryParam.getPageNo());
        } else {
            page.setCurrent(1);
        }
        if (pageQueryParam != null) {
            if (pageQueryParam.getDescs() != null) {
                page.setDescs(Optional.of(pageQueryParam.getDescs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
            if (pageQueryParam.getAscs() != null) {
                page.setAscs(Optional.of(pageQueryParam.getAscs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
        }

        LambdaQueryWrapper<JobSchedule> queryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(pageJobScheduleParam.getDvsCode())) {
            queryWrapper.eq(JobSchedule::getDvsCode, pageJobScheduleParam.getDvsCode());
        }
        if (StringUtils.isNotBlank(pageJobScheduleParam.getScheduleName())) {
            queryWrapper.like(JobSchedule::getScheduleName, pageJobScheduleParam.getScheduleName());
        }
        if (Objects.nonNull(pageJobScheduleParam.getEnv())) {
            queryWrapper.eq(JobSchedule::getEnv, pageJobScheduleParam.getEnv());
        }
        if (Objects.nonNull(pageJobScheduleParam.getScheduleLifecycle())) {
            queryWrapper.eq(JobSchedule::getScheduleLifecycle, pageJobScheduleParam.getScheduleLifecycle());
        }
        return page(page, queryWrapper);
    }

    private void validateSchedule(PageJobScheduleParam pageJobScheduleParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageJobScheduleParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageJobScheduleParam.setPageQueryParam(pageQueryParam);
        if (StringUtils.isNotBlank(pageJobScheduleParam.getDvsCode())) {
            if (CollectionUtil.isEmpty(dvsParentService.list(Wrappers.<DvsParent>lambdaQuery().eq(DvsParent::getDvsCode, pageJobScheduleParam.getDvsCode())))) {
                throw new DtvsAdminException("数据空间编码不存在");
            }
        }
    }

    @Override
    public JobScheduleDTO jobScheduleInfo(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException{
        JobScheduleDTO jobScheduleDTO = new JobScheduleDTO();
        JobSchedule jobSchedule;
        if (StringUtils.isEmpty(jobScheduleInfoParam.getScheduleCode()) && StringUtils.isEmpty(jobScheduleInfoParam.getJobCode())){
            throw new DtvsAdminException("调度编码&作业编码都为空");
        }
        if(StringUtils.isEmpty(jobScheduleInfoParam.getScheduleCode())){
            NodeJobs nodeJob = nodeJobsService.getOneNodeJobs(jobScheduleInfoParam.getJobCode(),jobScheduleInfoParam.getEnv());
            if (null == nodeJob){
                throw new DtvsAdminException("作业不存在");
            }
            ScheduleNode scheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeCode, nodeJob.getNodeCode())
                    .eq(ScheduleNode::getEnv,jobScheduleInfoParam.getEnv()));
            if (null == scheduleNode){
                throw new DtvsAdminException("作业节点不存在");
            }
            jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, scheduleNode.getScheduleCode()).eq(JobSchedule::getEnv,jobScheduleInfoParam.getEnv()));
        } else {
            jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, jobScheduleInfoParam.getScheduleCode()).eq(JobSchedule::getEnv,jobScheduleInfoParam.getEnv()));
        }

        BeanUtils.copyProperties(jobSchedule,jobScheduleDTO);

        List<ScheduleEdgeDTO> scheduleEdgeDTOList = new ArrayList<>();
        List<ScheduleEdge> scheduleEdgeList = scheduleEdgeService.scheduleEdgeList(jobSchedule.getScheduleCode());
        if(!CollectionUtil.isEmpty(scheduleEdgeList)){
            scheduleEdgeList.stream().forEach(p->{
                ScheduleEdgeDTO scheduleEdgeDTO = new ScheduleEdgeDTO();
                BeanUtils.copyProperties(p,scheduleEdgeDTO);
                scheduleEdgeDTOList.add(scheduleEdgeDTO);
            });
        }
        jobScheduleDTO.setScheduleEdgeDTOList(scheduleEdgeDTOList);

        jobScheduleDTO.setScheduleNodeDTOList(scheduleNodeService.scheduleNodeList(jobSchedule.getScheduleCode()));
        return jobScheduleDTO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean deleteScheduleInfo(Long scheduleId) throws DtvsAdminException {
        JobSchedule jobSchedule = validateJobScheduleExists(scheduleId);
        String scheduleCode = jobSchedule.getScheduleCode();
        // todo 判断是否有调度作业 有调度作业不允许删除
        List<ScheduleNode> scheduleNodeList = scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode).
                eq(ScheduleNode::getEnv, jobSchedule.getEnv()).
                eq(ScheduleNode::getNodeType, NodeTyepEnum.DO_NODE.getValue()));
        if (CollectionUtil.isNotEmpty(scheduleNodeList)) {
            throw new DtvsAdminException("调度作业存在作业中的节点,不允许删除");
        }
        if (!removeById(scheduleId)) {
            throw new DtvsAdminException("删除调度作业失败");
        }
        // 根据作业编码和环境删除对应的作业节点 节点任务 调度实例 作业实例 调度边界
        if (CollectionUtil.isNotEmpty(scheduleEdgeService.list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).eq(ScheduleEdge::getEnv, jobSchedule.getEnv())))) {
            if (!scheduleEdgeService.remove(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).eq(ScheduleEdge::getEnv, jobSchedule.getEnv()))) {
                throw new DtvsAdminException("删除调度边集schedule_edge失败");
            }
        }

        scheduleNodeList = scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode).
                eq(ScheduleNode::getEnv, jobSchedule.getEnv()));
        if (CollectionUtil.isNotEmpty(scheduleNodeList)) {
            if (!scheduleNodeService.remove(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode).
                    eq(ScheduleNode::getEnv, jobSchedule.getEnv()))) {
                throw new DtvsAdminException("删除调度节点schedule_node失败");
            }

            List<String> nodeCodeList = scheduleNodeList.stream().map(ScheduleNode::getNodeCode).distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(nodeJobsService.list(Wrappers.<NodeJobs>lambdaQuery().in(NodeJobs::getNodeCode, nodeCodeList).eq(NodeJobs::getEnv, jobSchedule.getEnv())))) {
                if (!nodeJobsService.remove(Wrappers.<NodeJobs>lambdaQuery().in(NodeJobs::getNodeCode, nodeCodeList).eq(NodeJobs::getEnv, jobSchedule.getEnv()))) {
                    throw new DtvsAdminException("删除节点任务node_jobs失败");
                }
            }
        }

        return Boolean.TRUE;
    }

    private JobSchedule validateJobScheduleExists(Long scheduleId) throws DtvsAdminException {
        JobSchedule jobSchedule;
        if (Objects.isNull(scheduleId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(jobSchedule = getById(scheduleId))) {
            throw new DtvsAdminException("调度任务不存在");
        }
        if (StringUtils.isBlank(jobSchedule.getScheduleCode())) {
            throw new DtvsAdminException("调度作业编码为空");
        }
        return jobSchedule;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Boolean removeScheduleNode(RemoveNodeDependenceParam removeNodeDependenceParam)throws DtvsAdminException {
        JobSchedule jobSchedule = validateDeleteScheduleNode(removeNodeDependenceParam);
        return removeScheduleNodeInfo(jobSchedule.getScheduleCode(),removeNodeDependenceParam.getJobCode(),removeNodeDependenceParam.getEnv(),removeNodeDependenceParam.getFromCode());
    }

    /**
     * 移除节点
     * @param scheduleCode
     * @param jobCode
     * @param env
     * @param fromCodeList
     * @return
     * @throws DtvsAdminException
     */
    private boolean removeScheduleNodeInfo(String scheduleCode,String jobCode,Integer env,List<String> fromCodeList) throws DtvsAdminException{
        //作业编码查询节点编码
        NodeJobs nodeJob = nodeJobsService.getOneNodeJobs(jobCode,env);
        if (null == nodeJob){
            throw new DtvsAdminException("作业不存在");
        }
        if (!nodeJobsService.remove(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getJobCode, jobCode).eq(NodeJobs::getEnv,env))){
            throw new DtvsAdminException("删除调度节点失败");
        }
        if (!scheduleNodeService.remove(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeCode, nodeJob.getNodeCode()).eq(ScheduleNode::getEnv,env))){
            throw new DtvsAdminException("删除调度节点失败");
        }

        if(CollectionUtil.isNotEmpty(fromCodeList)){
            for(String fromCode:fromCodeList){
                //delete edge
                scheduleEdgeService.deleteSchdeleEdge(scheduleCode,fromCode,nodeJob.getNodeCode(),env);
            }
        }

        //有根节点的边移除
        ScheduleNode headNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode)
                .eq(ScheduleNode::getNodeType, NodeTyepEnum.START_NODE.getValue()).eq(ScheduleNode::getEnv,env));
        if (null == headNode){
            return true;
        }

        ScheduleEdge dependenceHeadNode = scheduleEdgeService.getOne(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getFromNode, headNode.getNodeCode())
                .eq(ScheduleEdge::getToNode, nodeJob.getNodeCode()).eq(ScheduleEdge::getEnv,env));
        if(null != dependenceHeadNode){
            if(!scheduleEdgeService.removeById(dependenceHeadNode.getEdgeId())){
                throw new DtvsAdminException("删除调度节点失败");
            }
        }
        return true;
    }

    public JobSchduleCodeDto scheduleCodeByJobCode(String jobCode,Integer env){
        //作业编码查询节点编码
        NodeJobs nodeJob = nodeJobsService.getOneNodeJobs(jobCode,env);
        if (null == nodeJob){
            return null;
        }
        ScheduleNode scheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeCode, nodeJob.getNodeCode()).eq(ScheduleNode::getEnv, env));
        if(null == scheduleNode){
            return null;
        }
        JobSchedule jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, scheduleNode.getScheduleCode()).eq(JobSchedule::getEnv, env));
        if(null == jobSchedule){
            return null;
        }
        JobSchduleCodeDto jobSchduleCodeDto = new JobSchduleCodeDto(jobSchedule.getScheduleId(),jobSchedule.getScheduleCode());
        return jobSchduleCodeDto;
    }

    @Override
    public Boolean removeDependence(JobScheduleInfoParam jobScheduleInfoParam) throws DtvsAdminException {
        if (Objects.isNull(jobScheduleInfoParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(jobScheduleInfoParam.getScheduleCode())) {
            throw new DtvsAdminException("调度任务编码不存在");
        }
        JobSchedule jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, jobScheduleInfoParam.getScheduleCode())
                            .eq(JobSchedule::getEnv,jobScheduleInfoParam.getEnv()));
        if (Objects.isNull(jobSchedule)) {
            throw new DtvsAdminException("调度任务不存在");
        }
        //作业编码查询节点编码
        NodeJobs nodeJob = nodeJobsService.getOneNodeJobs(jobScheduleInfoParam.getJobCode(),jobScheduleInfoParam.getEnv());
        if (null == nodeJob){
            throw new DtvsAdminException("作业不存在");
        }
        scheduleEdgeService.deleteSchdeleEdge(jobScheduleInfoParam.getScheduleCode(),jobScheduleInfoParam.getFromCode(),nodeJob.getNodeCode(),jobScheduleInfoParam.getEnv());
        return true;
    }




    @Override
    public Boolean setJobStatus(Long scheduleId, Integer status) throws DtvsAdminException {
        JobSchedule jobSchedule = getById(scheduleId);
        jobSchedule.setScheduleLifecycle(status);
        jobSchedule.setUpdateTime(LocalDateTime.now());
        if (!saveOrUpdate(jobSchedule)) {
            throw new DtvsAdminException("更新调度任务状态失败,调度任务编码 {}", jobSchedule.getScheduleCode());
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean checkJobAddSchedule(String jobCode,Integer env){
        NodeJobs nodeJobs = nodeJobsService.getOneNodeJobs(jobCode, env);
        if (null != nodeJobs) {
            return true;
        }
        return false;
    }


    @Override
    public FlowDAG publishSchedule(String scheduleCode,Integer env) {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        FlowDAG flowDAG = new FlowDAG();
        //job_schedule
        List<JobSchedule> jobScheduleList = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, scheduleCode)
                .eq(JobSchedule::getEnv,env)), JobSchedule.class);
        if(CollectionUtil.isEmpty(jobScheduleList)){
            return null;
        }

        JobSchedule jobSchedule = jobScheduleList.get(0);
        // flowInfo
        FlowInfo flowInfo = new FlowInfo(
                Long.valueOf(RandomStringUtils.randomNumeric(8)),String.valueOf(userInfo.getTenantId()),
                jobSchedule.getScheduleName(),0,System.currentTimeMillis(),
                Timestamp.valueOf(jobSchedule.getStartTime()).getTime(),Timestamp.valueOf(jobSchedule.getEndTime()).getTime());
        flowDAG.setFlowInfo(flowInfo);

        Map<Long, Node> nodes = new HashMap<>();
        Map<Long, Edge> edges = new HashMap<>();
        Map<String, NodeJobs> nodeJobsMap = new HashMap<>();
        //schedule_node
        List<ScheduleNode> scheduleNodeList = scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, jobSchedule.getScheduleCode())
                                                .eq(ScheduleNode::getEnv,env));
        for(ScheduleNode scheduleNode:scheduleNodeList){
            //scheduleEdgeList
            List<ScheduleEdge> scheduleEdgeList = scheduleEdgeService.list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getFromNode, scheduleNode.getNodeCode()).eq(ScheduleEdge::getEnv,env));
            List<Long> toNodeEdgeList = scheduleEdgeList.stream().map(ScheduleEdge::getEdgeId).collect(Collectors.toList());
            if(scheduleNode.getNodeType() == 0 ){
                //headNode
                Node headNode = new Node(Long.valueOf(scheduleNode.getNodeCode()),scheduleNode.getNodeName(),null,null);
                headNode.setNextEdge(toNodeEdgeList.toArray(new Long[toNodeEdgeList.size()]));
                nodes.put(Long.valueOf(scheduleNode.getNodeCode()),headNode);
                flowDAG.setHeadNode(headNode);

            }
            //node_jobs
            List<NodeJobs> nodeJobsList = nodeJobsService.list(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, scheduleNode.getNodeCode()).eq(NodeJobs::getEnv,env));
            if(CollectionUtil.isNotEmpty(nodeJobsList)){
                nodeJobsMap.put(String.valueOf(nodeJobsList.get(0).getNodeJobId()),nodeJobsList.get(0));
            }
            Node node = new Node(Long.valueOf(scheduleNode.getNodeCode()),scheduleNode.getNodeName(),null,nodeJobsMap);
            nodes.put(Long.valueOf(scheduleNode.getNodeCode()),node);

            //schedule_edge
            edgeConfig(scheduleEdgeList,edges);
        }
        //nodes
        flowDAG.setNodes(nodes);
        //edges
        flowDAG.setEdges(edges);
        return flowDAG;
    }

    public Map<Long, Edge> edgeConfig (List<ScheduleEdge> scheduleEdgeList,Map<Long, Edge> edgeMap){
        Edge edge;
        for(ScheduleEdge scheduleEdge:scheduleEdgeList){
            edge = new Edge(scheduleEdge.getEdgeId(),Long.valueOf(scheduleEdge.getFromNode()),Long.valueOf(scheduleEdge.getToNode()));
            edgeMap.put(scheduleEdge.getEdgeId(),edge);
        }
        return edgeMap;
    }
//
//    @Override
//    @Transactional(rollbackFor = Throwable.class)
//    public Long addJobSchedule(SaveJobScheduleParam saveJobScheduleParam) throws DtvsAdminException , JsonProcessingException {
//        validateSaveOrUpdateSchedule(saveJobScheduleParam);
//        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
//        String scheduleCode = UCodeUtil.produce();
//        // 保存调度作业
//        JobSchedule jobSchedule = mapperFactory.getMapperFacade().map(saveJobScheduleParam, JobSchedule.class);
//        jobSchedule.setScheduleCode(scheduleCode);
//        jobSchedule.setCreateTime(LocalDateTime.now());
//        jobSchedule.setUpdateTime(LocalDateTime.now());
//        jobSchedule.setTenantId(userInfo.getTenantId());
//        jobSchedule.setTenantName(userInfo.getTenantName());
//        jobSchedule.setDeptId(userInfo.getDeptId());
//        jobSchedule.setDeptName(userInfo.getDeptName());
//        jobSchedule.setUserId(userInfo.getUserId());
//        jobSchedule.setUserName(userInfo.getUserName());
//        jobSchedule.setRawData(saveJobScheduleParam.getRawData());
//        if (!saveOrUpdate(jobSchedule)) {
//            throw new DtvsAdminException("保存调度作业失败");
//        }
//        // 保存调度作业实例
//        String scheduleInstanceCode = UCodeUtil.produce();
//        ScheduleInstance scheduleInstance = new ScheduleInstance();
//        scheduleInstance.setScheduleInstanceCode(scheduleInstanceCode);
//        scheduleInstance.setScheduleCode(scheduleCode);
//        scheduleInstance.setEnv(jobSchedule.getEnv());
//        scheduleInstance.setCreateTime(LocalDateTime.now());
//        scheduleInstance.setUpdateTime(LocalDateTime.now());
//        if (!scheduleInstanceService.saveOrUpdate(scheduleInstance)) {
//            throw new DtvsAdminException("保存调度作业实例失败");
//        }
//        // todo 这里暂时不优化为批量入库 保持逻辑清晰 开始节点 结束节点都是虚拟节点
//        // 初始化开始节点
//        String nodeCode = UCodeUtil.produce();
//        ScheduleNode saveScheduleNode = new ScheduleNode();
//        saveScheduleNode.setScheduleCode(scheduleCode);
//        saveScheduleNode.setNodeCode(nodeCode);
//        saveScheduleNode.setNodeName("开始节点");
//        saveScheduleNode.setNodeType(NodeTyepEnum.START_NODE.getValue());
//        saveScheduleNode.setEnv(jobSchedule.getEnv());
//        saveScheduleNode.setCreateTime(LocalDateTime.now());
//        if (!scheduleNodeService.saveOrUpdate(saveScheduleNode)) {
//            throw new DtvsAdminException("保存调度任务开始节点失败");
//        }
//        //  初始化结束节点
//        String endNodeCode = UCodeUtil.produce();
//        ScheduleNode endSaveScheduleNode = new ScheduleNode();
//        endSaveScheduleNode.setScheduleCode(scheduleCode);
//        endSaveScheduleNode.setNodeCode(endNodeCode);
//        endSaveScheduleNode.setNodeName("结束节点");
//        endSaveScheduleNode.setNodeType(NodeTyepEnum.END_NODE.getValue());
//        endSaveScheduleNode.setEnv(jobSchedule.getEnv());
//        endSaveScheduleNode.setCreateTime(LocalDateTime.now());
//        if (!scheduleNodeService.saveOrUpdate(endSaveScheduleNode)) {
//            throw new DtvsAdminException("保存调度任务结束节点失败");
//        }
//        //add job String handler,String jobDesc,String cron,String email,String author,String schduleCode
//        addXxlJob("","","", "email","" ,"schduleCode");
//        return jobSchedule.getScheduleId();
//    }



    private void validateSaveOrUpdateScheduleNode(SaveScheduleNode saveScheduleNode) throws DtvsAdminException {
        if (Objects.isNull(saveScheduleNode)) {
            throw new DtvsAdminException("任务节点参数为空");
        }
        if (StringUtils.isBlank(saveScheduleNode.getScheduleCode())) {
            throw new DtvsAdminException("调度任务编码不存在");
        }
//        if (StringUtils.isBlank(saveScheduleNode.getNodeName())) {
//            throw new DtvsAdminException("任务节点名称参数为空");
//        }
//        if (Objects.isNull(saveScheduleNode.getNodeType())) {
//            throw new DtvsAdminException("任务节点类型参数为空");
//        }
//        if (!NodeTyepEnum.existsByValue(saveScheduleNode.getNodeType())) {
//            throw new DtvsAdminException("任务节点类型不存在");
//        }
        if (Objects.isNull(saveScheduleNode.getEnv())) {
            throw new DtvsAdminException("任务节点环境参数为空");
        }
        if (!EnvEnum.existsValue(saveScheduleNode.getEnv())) {
            throw new DtvsAdminException("任务节点环境参数不存在");
        }
        if (Objects.isNull(saveScheduleNode.getNodeJobsParam())) {
            throw new DtvsAdminException("任务节点Job参数为空");
        }
        for(NodeJobsParam nodeJobsParam:saveScheduleNode.getNodeJobsParam()){
            if (Objects.isNull(nodeJobsParam.getUserId())) {
                throw new DtvsAdminException("作业负责人信息为空");
            }
            if (Objects.isNull(nodeJobsParam.getUserName())) {
                throw new DtvsAdminException("作业负责人信息为空");
            }
        }

        JobSchedule jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, saveScheduleNode.getScheduleCode()).
                eq(JobSchedule::getEnv, saveScheduleNode.getEnv()));
        if (Objects.isNull(jobSchedule)) {
            throw new DtvsAdminException("调度任务不存在");
        }
//        ScheduleNode nameScheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().
//                eq(ScheduleNode::getEnv, saveScheduleNode.getEnv()).eq(ScheduleNode::getScheduleCode, saveScheduleNode.getScheduleCode()));
//        if (Objects.isNull(saveScheduleNode.getScheduleId())) {
//            if (Objects.nonNull(nameScheduleNode)) {
//                throw new DtvsAdminException("调度节点名称已存在");
//            }
//        } else {
//            ScheduleNode scheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeId, saveScheduleNode.getNodeId()).
//                    eq(ScheduleNode::getEnv, saveScheduleNode.getEnv()).eq(ScheduleNode::getScheduleCode, saveScheduleNode.getScheduleCode()));
//            if (Objects.isNull(scheduleNode)) {
//                throw new DtvsAdminException("调度节点不存在");
//            }
//            if (Objects.nonNull(nameScheduleNode) && scheduleNode.getNodeId().longValue() != nameScheduleNode.getNodeId().longValue()) {
//                throw new DtvsAdminException("调度节点名称已存在");
//            }
//        }


    }

    private void validateSaveOrUpdateSchedule(SaveJobScheduleParam saveJobScheduleParam) throws DtvsAdminException {
        if (Objects.isNull(saveJobScheduleParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(saveJobScheduleParam.getScheduleName())) {
            throw new DtvsAdminException("调度作业名称参数为空");
        }
        if (StringUtils.isBlank(saveJobScheduleParam.getDvsCode())) {
            throw new DtvsAdminException("调度作业空间编码参数为空");
        }
        if (Objects.isNull(saveJobScheduleParam.getEnv())) {
            throw new DtvsAdminException("调度作业环境参数为空");
        }
        if (!EnvEnum.existsValue(saveJobScheduleParam.getEnv())) {
            throw new DtvsAdminException("调度作业环境参数不存在");
        }
        if (!checkDayNum(saveJobScheduleParam.getDayNum())) {
            throw new DtvsAdminException("间隔数值应为正整数");
        }
        if (Objects.isNull(saveJobScheduleParam.getScheduleLifecycle())) {
            throw new DtvsAdminException("调度作业调度生命周期参数为空");
        }
        if (!ScheduleLifecycleEnum.existsByValue(saveJobScheduleParam.getScheduleLifecycle())) {
            throw new DtvsAdminException("调度作业调度生命周期参数不存在");
        }
        List<Dvs> dvsList = dvsMapper.selectList(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsCode, saveJobScheduleParam.getDvsCode()).
                eq(Dvs::getIsDeleted, IsDeletedEnum.NO.getValue()).eq(Dvs::getEnv, saveJobScheduleParam.getEnv()));
        if (CollectionUtil.isEmpty(dvsList)) {
            throw new DtvsAdminException("调度作业数据空间不存在");
        }
        JobSchedule nameJobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleName, saveJobScheduleParam.getScheduleName()).
                eq(JobSchedule::getDvsCode, saveJobScheduleParam.getDvsCode()).eq(JobSchedule::getEnv, saveJobScheduleParam.getEnv()).
                eq(JobSchedule::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (Objects.isNull(saveJobScheduleParam.getScheduleId())) {
            if(Objects.nonNull(nameJobSchedule)){
                throw new DtvsAdminException("调度作业名称已存在");
            }
        } else {
            JobSchedule jobSchedule = getById(saveJobScheduleParam.getScheduleId());
            if (Objects.isNull(jobSchedule)) {
                throw new DtvsAdminException("调度作业不存在");
            }
            if (Objects.nonNull(nameJobSchedule) && nameJobSchedule.getScheduleId().longValue() != jobSchedule.getScheduleId().longValue()) {
                throw new DtvsAdminException("调度作业名称已存在");
            }
        }
    }

    private boolean checkDayNum(String dayNum){
        Pattern pattern = compile("[0-9]*");
        return pattern.matcher(dayNum).matches();
    }

    private JobSchedule validateDeleteScheduleNode(RemoveNodeDependenceParam removeNodeDependenceParam) throws DtvsAdminException{
        if (Objects.isNull(removeNodeDependenceParam)) {
            throw new DtvsAdminException("任务节点参数为空");
        }
        if (StringUtils.isBlank(removeNodeDependenceParam.getScheduleCode())) {
            throw new DtvsAdminException("调度任务编码不存在");
        }

        JobSchedule jobSchedule = getOne(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getScheduleCode, removeNodeDependenceParam.getScheduleCode())
                                .eq(JobSchedule::getEnv,removeNodeDependenceParam.getEnv()));
        if (Objects.isNull(jobSchedule)) {
            throw new DtvsAdminException("调度任务不存在");
        }
        return jobSchedule;
    }

    public Integer addXxlJob(String handler,String jobDesc,String cron,String email,String author,String schduleCode) throws DtvsAdminException, JsonProcessingException {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        int jobGroup = xxlJobGroupServiceProxy.findByAppName("dvs-admin");
        xxlJobInfo.setJobGroup(jobGroup);
        xxlJobInfo.setJobDesc(jobDesc);
        xxlJobInfo.setAddTime(new Date());
        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfo.setAuthor(author);
        xxlJobInfo.setAlarmEmail(email);
        xxlJobInfo.setExecutorParam(schduleCode);

        // 调度时间策略
        xxlJobInfo.setScheduleType(ScheduleTypeEnum.CRON.getDesc());
        xxlJobInfo.setScheduleConf(cron);
        // 调度执行方法
        xxlJobInfo.setExecutorHandler(handler);
        xxlJobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        xxlJobInfo.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.getDesc());
        xxlJobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST.getDesc());

        // 调度执行方法
        // 执行超时时间 默认0
        xxlJobInfo.setExecutorTimeout(0);
        // 失败重试次数 默认0
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());

        xxlJobInfo.setTriggerStatus(TriggerStatusEnum.RUN.getValue());
        // 触发器下次调度时间 默认0
        xxlJobInfo.setTriggerNextTime(0);
        // 触发器上次调度时间 默认0
        xxlJobInfo.setTriggerLastTime(0);
//        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.addCustomizeJob(xxlJobInfo)));
        return xxlJobInfoServiceProxy.addCustomizeJob(xxlJobInfo);
    }



}
