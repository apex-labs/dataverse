package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.DvsPort;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Dataverse port Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface DvsPortMapper extends BaseMapper<DvsPort> {

}
