package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.Engine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 计算引擎注册表 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface EngineMapper extends BaseMapper<Engine> {

}
