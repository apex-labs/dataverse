package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.ScheduleEdge;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.ScheduleEdgeMapper;
import org.apex.dataverse.param.ScheduleEdgeParam;
import org.apex.dataverse.service.IScheduleEdgeService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.RandomStringUtils;
import org.apex.dataverse.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 调度任务边集 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class ScheduleEdgeServiceImpl extends ServiceImpl<ScheduleEdgeMapper, ScheduleEdge> implements IScheduleEdgeService {

    public void batchSave(List<ScheduleEdgeParam> scheduleEdgeParamList, String scheduleCode) throws DtvsAdminException {
        List<ScheduleEdge> scheduleEdgeList = new ArrayList<>();
        scheduleEdgeParamList.stream().forEach(p -> {
            ScheduleEdge scheduleEdge = new ScheduleEdge();
            BeanUtils.copyProperties(p, scheduleEdge);
            scheduleEdge.setEdgeId(Long.valueOf(RandomStringUtils.randomNumeric(8)))
                    .setEdgeCode(RandomStringUtils.randomNumeric(8)).setScheduleCode(scheduleCode)
                    .setCreateTime(LocalDateTime.now());
            scheduleEdgeList.add(scheduleEdge);
        });
        if (!saveBatch(scheduleEdgeList)) {
            throw new DtvsAdminException("保存ScheduleEdge失败,调度任务编码 {}", scheduleCode);
        }
    }


    public void deleteSchdeleEdge(String scheduleCode,String fromNode,String toNode,Integer env) throws DtvsAdminException {
        if (CollectionUtil.isEmpty(list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).eq(ScheduleEdge::getEnv,env)))) {
            return;
        }
        if(StringUtil.isBlank(fromNode) && StringUtil.isBlank(toNode)){
            if (!remove(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).eq(ScheduleEdge::getEnv,env))) {
                throw new DtvsAdminException("删除ScheduleEdge失败,调度任务编码 {}", scheduleCode);
            }
        } else if (StringUtil.isNotBlank(fromNode) && StringUtil.isNotBlank(toNode)) {
            ScheduleEdge scheduleEdge = getOne(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).eq(ScheduleEdge::getFromNode, fromNode)
                    .eq(ScheduleEdge::getToNode, toNode).eq(ScheduleEdge::getEnv,env));
            if (null != scheduleEdge){
                if (!removeById(scheduleEdge.getEdgeId())) {
                    throw new DtvsAdminException("删除ScheduleEdge失败,调度任务编码 {}", scheduleCode);
                }

            }
        } else if (StringUtil.isNotBlank(fromNode)) {
            ScheduleEdge scheduleEdge = getOne(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).
                    eq(ScheduleEdge::getFromNode, fromNode).eq(ScheduleEdge::getEnv,env));
            if (null != scheduleEdge){
                if (!removeById(scheduleEdge.getEdgeId())) {
                    throw new DtvsAdminException("删除ScheduleEdge失败,调度任务编码 {}", scheduleCode);
                }

            }
        } else if (StringUtil.isNotBlank(toNode)) {
            ScheduleEdge scheduleEdge = getOne(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode).
                    eq(ScheduleEdge::getToNode, toNode).eq(ScheduleEdge::getEnv,env));
            if (null != scheduleEdge){
                if (!removeById(scheduleEdge.getEdgeId())) {
                    throw new DtvsAdminException("删除ScheduleEdge失败,调度任务编码 {}", scheduleCode);
                }

            }
        }
    }

    public List<ScheduleEdge> scheduleEdgeList(String scheduleCode){
        return list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode));
    }
}
