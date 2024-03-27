package org.apex.dataverse.storage.service.impl;

import org.apex.dataverse.storage.entity.Storage;
import org.apex.dataverse.storage.mapper.StorageMapper;
import org.apex.dataverse.storage.service.IStorageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements IStorageService {

}
