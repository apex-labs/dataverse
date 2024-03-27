package org.apex.dataverse.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.storage.entity.Storage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface StorageMapper extends BaseMapper<Storage> {

}
