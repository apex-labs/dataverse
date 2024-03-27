package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.PortConn;
import org.apex.dataverse.port.mapper.PortConnMapper;
import org.apex.dataverse.port.service.IPortConnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 客户(Driver)端链接Port的链接记录表 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class PortConnServiceImpl extends ServiceImpl<PortConnMapper, PortConn> implements IPortConnService {

}
