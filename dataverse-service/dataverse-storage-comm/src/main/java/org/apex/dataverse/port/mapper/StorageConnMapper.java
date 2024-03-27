package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.StorageConn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Port端链接引擎记录历史 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface StorageConnMapper extends BaseMapper<StorageConn> {

}
