package org.apex.dataverse.port.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.mapper.StoragePortMapper;
import org.apex.dataverse.port.service.IStoragePortService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 存储区和Port映射。指定存储区映射到哪些Port，对应存储区上的命令会提交到对应的Port上进行执行。 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class StoragePortServiceImpl extends ServiceImpl<StoragePortMapper, StoragePort> implements IStoragePortService {

    private final static String PORT_ID = "port_id";

    private final static String PORT_CODE = "port_code";

    private final static String STORAGE_ID = "storage_id";

    @Override
    public List<StoragePort> getByPortId(Long portId) {
        QueryWrapper<StoragePort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(PORT_ID, portId);
        return this.list(queryWrapper);
    }

    @Override
    public List<StoragePort> getByPortCode(String portCode) {
        QueryWrapper<StoragePort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(PORT_CODE, portCode);
        return this.list(queryWrapper);
    }

    @Override
    public StoragePort getByStorageAndPortId(Long storageId, Long portId) {
        QueryWrapper<StoragePort> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(STORAGE_ID, storageId);
        queryWrapper.eq(PORT_ID, portId);
        return this.getOne(queryWrapper);
    }
}
