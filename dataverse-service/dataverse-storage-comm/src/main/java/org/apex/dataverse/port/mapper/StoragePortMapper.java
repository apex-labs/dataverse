package org.apex.dataverse.port.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.port.entity.StoragePort;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 存储区和Port映射。指定存储区映射到哪些Port，对应存储区上的命令会提交到对应的Port上进行执行。 Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface StoragePortMapper extends BaseMapper<StoragePort> {

}
