package org.apex.dataverse.port.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.mapper.DvsPortMapper;
import org.apex.dataverse.port.service.IDvsPortService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Dataverse port 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class DvsPortServiceImpl extends ServiceImpl<DvsPortMapper, DvsPort> implements IDvsPortService {

    /**
     * Dvs port code file name
     */
    private final static String DVS_PORT_CODE = "port_code";

    @Override
    public DvsPort getByCode(String dvsCode) {
        QueryWrapper<DvsPort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DVS_PORT_CODE, dvsCode);
        return this.getOne(queryWrapper);
    }
}
