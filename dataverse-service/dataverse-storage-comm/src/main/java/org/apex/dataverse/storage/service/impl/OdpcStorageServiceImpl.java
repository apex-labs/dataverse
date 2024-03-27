package org.apex.dataverse.storage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apex.dataverse.storage.entity.JdbcStorage;
import org.apex.dataverse.storage.entity.OdpcStorage;
import org.apex.dataverse.storage.mapper.OdpcStorageMapper;
import org.apex.dataverse.storage.service.IOdpcStorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 自定义ODPC存储区（用自行开发的Spark/Flink Engine)，如基于HDFS的存储区+SparkEngine 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class OdpcStorageServiceImpl extends ServiceImpl<OdpcStorageMapper, OdpcStorage> implements IOdpcStorageService {


    @Override
    public OdpcStorage getByStorageId(Long storageId) {
        QueryWrapper<OdpcStorage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(STORAGE_ID, storageId);
        return this.getOne(queryWrapper);
    }
}
