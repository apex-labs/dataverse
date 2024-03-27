package org.apex.dataverse.storage.service;

import org.apex.dataverse.storage.entity.JdbcStorage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * JDBC存储区，如MYSQL, PGSQL, ORACLE, DORIS, STAR_ROCKS, CLICKHOUSE, ELASTICSEARCH 服务类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
public interface IJdbcStorageService extends IService<JdbcStorage> {

    /**
     * Storage id
     */
    String STORAGE_ID = "storage_id";

    /**
     * Get jdbc storage by storage id
     * @param storageId storage id
     * @return JdbcStorage
     */
    JdbcStorage getByStorageId(Long storageId);

}
