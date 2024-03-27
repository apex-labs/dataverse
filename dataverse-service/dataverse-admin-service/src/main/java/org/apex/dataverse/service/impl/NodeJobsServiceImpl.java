package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.NodeJobs;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.NodeJobsMapper;
import org.apex.dataverse.service.INodeJobsService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 调度任务点集中的作业 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class NodeJobsServiceImpl extends ServiceImpl<NodeJobsMapper, NodeJobs> implements INodeJobsService {

    public void batchSave(List<NodeJobs> nodeJobsList, String scheduleCode) throws DtvsAdminException {
        if (!saveBatch(nodeJobsList)) {
            throw new DtvsAdminException("保存NodeJobs失败,调度任务编码 {}", scheduleCode);
        }
    }

    public void removeNodeJobs(String nodeCode) throws DtvsAdminException {
        if (CollectionUtil.isEmpty(list(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, nodeCode)))) {
            return;
        }
        if (!remove(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, nodeCode))) {
            throw new DtvsAdminException("删除NodeJobs失败,调度任务编码 {}", nodeCode);
        }
    }

    public List<NodeJobs> nodeJobsList(String nodeCode){
        return list(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, nodeCode));
    }

    public NodeJobs getOneNodeJobs(String jobCode,Integer env){
        return getOne(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getJobCode, jobCode).eq(NodeJobs::getEnv,env));
    }

    public NodeJobs getOneNodeJobsByNodeCode(String nodeCode,Integer env){
        return getOne(Wrappers.<NodeJobs>lambdaQuery().eq(NodeJobs::getNodeCode, nodeCode).eq(NodeJobs::getEnv,env));
    }
}
