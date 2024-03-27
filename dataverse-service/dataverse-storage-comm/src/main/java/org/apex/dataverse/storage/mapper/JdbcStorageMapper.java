package org.apex.dataverse.storage.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apex.dataverse.storage.entity.JdbcStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * JDBC存储区，如MYSQL, PGSQL, ORACLE, DORIS, STAR_ROCKS, CLICKHOUSE, ELASTICSEARCH Mapper 接口
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Mapper
public interface JdbcStorageMapper extends BaseMapper<JdbcStorage> {

}
