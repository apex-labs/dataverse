package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.PortEvent;
import org.apex.dataverse.port.mapper.PortEventMapper;
import org.apex.dataverse.port.service.IPortEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Port的实例 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class PortEventServiceImpl extends ServiceImpl<PortEventMapper, PortEvent> implements IPortEventService {

}
