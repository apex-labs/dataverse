package org.apex.dataverse.storage.service.impl;

import org.apex.dataverse.storage.entity.StorageTenant;
import org.apex.dataverse.storage.mapper.StorageTenantMapper;
import org.apex.dataverse.storage.service.IStorageTenantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 授权给租户的存储区 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class StorageTenantServiceImpl extends ServiceImpl<StorageTenantMapper, StorageTenant> implements IStorageTenantService {

}
