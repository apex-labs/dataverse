package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.dto.NodeJobsDTO;
import org.apex.dataverse.dto.ScheduleNodeDTO;
import org.apex.dataverse.entity.NodeJobs;
import org.apex.dataverse.entity.ScheduleNode;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.ScheduleNodeMapper;
import org.apex.dataverse.param.ScheduleNodeParam;
import org.apex.dataverse.service.IScheduleNodeService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * <p>
 * 调度任务点集 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class ScheduleNodeServiceImpl extends ServiceImpl<ScheduleNodeMapper, ScheduleNode> implements IScheduleNodeService {

    @Autowired
    private NodeJobsServiceImpl nodeJobsService;

    public int batchSave(List<ScheduleNodeParam> scheduleNodeParamList, String scheduleCode) throws DtvsAdminException {
        AtomicReference<Integer> jobSize = new AtomicReference<>(0);
        List<ScheduleNode> scheduleNodeList = new ArrayList<>();
        List<NodeJobs> nodeJobsList = new ArrayList<>();
        scheduleNodeParamList.stream().forEach(p -> {
            ScheduleNode scheduleNode = new ScheduleNode();
            BeanUtils.copyProperties(p, scheduleNode);
            scheduleNode.setScheduleCode(scheduleCode);
            scheduleNode.setNodeId(Long.valueOf(RandomStringUtils.randomNumeric(8)));
            scheduleNode.setCreateTime(LocalDateTime.now());
            scheduleNodeList.add(scheduleNode);

            p.getNodeJobsParamList().forEach(n -> {
                NodeJobs nodeJobs = new NodeJobs();
                BeanUtils.copyProperties(n, nodeJobs);
                nodeJobs.setNodeJobId(Long.valueOf(RandomStringUtils.randomNumeric(8)));
                nodeJobs.setCreateTime(LocalDateTime.now());
                nodeJobsList.add(nodeJobs);
            });
            //save node_job
            try {
                nodeJobsService.batchSave(nodeJobsList, scheduleCode);
            } catch (DtvsAdminException e) {
                e.printStackTrace();
            }
            jobSize.updateAndGet(v -> v + nodeJobsList.size());
        });
        //save schedule_node
        if (!saveOrUpdateBatch(scheduleNodeList)) {
            throw new DtvsAdminException("保存ScheduleNode失败,调度任务编码 {}", scheduleCode);
        }

        return jobSize.get();
    }

    public int batchUpdate(List<ScheduleNodeParam> scheduleNodeParamList, String scheduleCode) throws DtvsAdminException {
        //delete schedule_node node_Job
        this.deleteScheduleNode(scheduleCode,null);
        //save schedule_node node_jobs
        int wordCnt = this.batchSave(scheduleNodeParamList, scheduleCode);
        return wordCnt;
    }

    public void deleteScheduleNode(String scheduleCode,String nodeCode) throws DtvsAdminException{
        List<ScheduleNode> scheduleList = list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode));
        if (CollectionUtil.isEmpty(scheduleList)) {
            return;
        }
        //delete schedule_node
        if (!remove(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode))) {
            throw new DtvsAdminException("删除ScheduleNode失败,调度任务编码 {}", scheduleCode);
        }
        for(ScheduleNode scheduleNode:scheduleList){
            //delete node_jobs
            nodeJobsService.removeNodeJobs(scheduleNode.getNodeCode());

        }

    }

    public List<ScheduleNodeDTO> scheduleNodeList(String scheduleCode){
        List<ScheduleNodeDTO> scheduleNodeDTOList = new ArrayList<>();
        List<ScheduleNode> scheduleNodeList = list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode));
        if (CollectionUtil.isEmpty(scheduleNodeList)) {
            return scheduleNodeDTOList;
        }
        scheduleNodeList.stream().forEach(p->{
            ScheduleNodeDTO scheduleNodeDTO = new ScheduleNodeDTO();
            BeanUtils.copyProperties(p,scheduleNodeDTO);

            List<NodeJobs> nodeJobsList = nodeJobsService.nodeJobsList(p.getNodeCode());
            if(!CollectionUtil.isEmpty(nodeJobsList)){
                nodeJobsList.stream().forEach(n->{
                    NodeJobsDTO nodeJobsDTO = new NodeJobsDTO();
                    BeanUtils.copyProperties(n,nodeJobsDTO);
                    scheduleNodeDTO.setNodeJobsDTO(nodeJobsDTO);

                });
            }
            scheduleNodeDTOList.add(scheduleNodeDTO);
        });
        return scheduleNodeDTOList;
    }

}
