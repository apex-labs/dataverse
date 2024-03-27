package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.StorageTypeMap;
import org.apex.dataverse.mapper.StorageTypeMapMapper;
import org.apex.dataverse.param.StorageTypeMapParam;
import org.apex.dataverse.service.IStorageTypeMapService;
import org.apex.dataverse.vo.StorageTypeMapVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class StorageTypeMapServiceImpl extends ServiceImpl<StorageTypeMapMapper, StorageTypeMap> implements IStorageTypeMapService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public List<StorageTypeMapVO> listStorageTypeMap(StorageTypeMapParam storageTypeMapParam) {
        List<StorageTypeMap> storageTypeMapList = null;
        LambdaQueryWrapper<StorageTypeMap> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(storageTypeMapParam)) {
            if (StringUtils.isNotBlank(storageTypeMapParam.getStorageTypeName())) {
                queryWrapper.eq(StorageTypeMap::getStorageTypeName, storageTypeMapParam.getStorageTypeName());
            }
            if (Objects.nonNull(storageTypeMapParam.getStorageConnType())) {
                queryWrapper.eq(StorageTypeMap::getStorageConnType, storageTypeMapParam.getStorageConnType());
            }
            storageTypeMapList = list(queryWrapper);
        } else {
            storageTypeMapList = list();
        }
        return mapperFactory.getMapperFacade().mapAsList(storageTypeMapList, StorageTypeMapVO.class);
    }
}
