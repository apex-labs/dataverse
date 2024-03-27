package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.JobHistory;
import org.apex.dataverse.port.mapper.JobHistoryMapper;
import org.apex.dataverse.port.service.IJobHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 作业运行历史记录表 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class JobHistoryServiceImpl extends ServiceImpl<JobHistoryMapper, JobHistory> implements IJobHistoryService {

}
