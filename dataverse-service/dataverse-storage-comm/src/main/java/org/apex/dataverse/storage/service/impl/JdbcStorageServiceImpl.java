package org.apex.dataverse.storage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.mapper.JdbcStorageMapper;
import org.apex.dataverse.storage.service.IJdbcStorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * JDBC存储区，如MYSQL, PGSQL, ORACLE, DORIS, STAR_ROCKS, CLICKHOUSE, ELASTICSEARCH 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class JdbcStorageServiceImpl extends ServiceImpl<JdbcStorageMapper, JdbcStorage> implements IJdbcStorageService {

    @Override
    public JdbcStorage getByStorageId(Long storageId) {
        QueryWrapper<JdbcStorage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(STORAGE_ID, storageId);
        return this.getOne(queryWrapper);
    }
}
