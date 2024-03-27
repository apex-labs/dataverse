package org.apex.dataverse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ma.glasnost.orika.MapperFactory;
import org.apex.dataverse.entity.StorageBox;
import org.apex.dataverse.mapper.StorageBoxMapper;
import org.apex.dataverse.service.IStorageBoxService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apex.dataverse.vo.StorageBoxVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class StorageBoxServiceImpl extends ServiceImpl<StorageBoxMapper, StorageBox> implements IStorageBoxService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public List<StorageBoxVO> listStorageBoxByStorageId(Long storageId) {
        return mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getStorageId, storageId)), StorageBoxVO.class);
    }
}
