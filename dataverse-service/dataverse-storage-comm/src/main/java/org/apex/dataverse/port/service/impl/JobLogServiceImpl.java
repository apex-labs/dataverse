package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.JobLog;
import org.apex.dataverse.port.mapper.JobLogMapper;
import org.apex.dataverse.port.service.IJobLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 作业运行日志表 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class JobLogServiceImpl extends ServiceImpl<JobLogMapper, JobLog> implements IJobLogService {

}
