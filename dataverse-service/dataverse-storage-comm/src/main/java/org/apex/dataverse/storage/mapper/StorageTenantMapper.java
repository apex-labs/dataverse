package org.apex.dataverse.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.storage.entity.StorageTenant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 授权给租户的存储区 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface StorageTenantMapper extends BaseMapper<StorageTenant> {

}
