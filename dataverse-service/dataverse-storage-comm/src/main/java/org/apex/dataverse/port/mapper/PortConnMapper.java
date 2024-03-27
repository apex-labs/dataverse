package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.PortConn;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 客户(Driver)端链接Port的链接记录表 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface PortConnMapper extends BaseMapper<PortConn> {

}
