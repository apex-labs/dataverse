package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.PortEvent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Port的实例 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface PortEventMapper extends BaseMapper<PortEvent> {

}
