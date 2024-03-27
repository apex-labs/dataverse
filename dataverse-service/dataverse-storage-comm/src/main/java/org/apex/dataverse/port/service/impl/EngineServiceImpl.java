package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.Engine;
import org.apex.dataverse.port.mapper.EngineMapper;
import org.apex.dataverse.port.service.IEngineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 计算引擎注册表 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class EngineServiceImpl extends ServiceImpl<EngineMapper, Engine> implements IEngineService {

}
