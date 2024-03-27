package org.apex.dataverse.port.service.impl;

import org.apex.dataverse.port.entity.StorageConn;
import org.apex.dataverse.port.mapper.StorageConnMapper;
import org.apex.dataverse.port.service.IStorageConnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Port端链接引擎记录历史 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Service
public class StorageConnServiceImpl extends ServiceImpl<StorageConnMapper, StorageConn> implements IStorageConnService {

}
