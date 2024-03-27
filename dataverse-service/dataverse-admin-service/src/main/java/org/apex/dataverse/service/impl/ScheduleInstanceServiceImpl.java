package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.ScheduleInstance;
import org.apex.dataverse.mapper.ScheduleInstanceMapper;
import org.apex.dataverse.service.IScheduleInstanceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 调度实例 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-31
 */
@Service
public class ScheduleInstanceServiceImpl extends ServiceImpl<ScheduleInstanceMapper, ScheduleInstance> implements IScheduleInstanceService {

}
