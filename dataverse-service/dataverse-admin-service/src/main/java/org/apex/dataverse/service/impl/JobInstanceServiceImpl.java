package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.JobInstance;
import org.apex.dataverse.mapper.JobInstanceMapper;
import org.apex.dataverse.service.IJobInstanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class JobInstanceServiceImpl extends ServiceImpl<JobInstanceMapper, JobInstance> implements IJobInstanceService {

}
