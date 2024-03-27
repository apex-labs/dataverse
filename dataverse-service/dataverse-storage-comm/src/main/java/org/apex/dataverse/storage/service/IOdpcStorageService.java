package org.apex.dataverse.storage.service;

import org.apex.dataverse.storage.entity.OdpcStorage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 自定义ODPC存储区（用自行开发的Spark/Flink Engine)，如基于HDFS的存储区+SparkEngine 服务类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
public interface IOdpcStorageService extends IService<OdpcStorage> {

    /**
     * Storage id
     */
    String STORAGE_ID = "storage_id";

    /**
     * Get odpc storage by storage id
     * @param storageId storage id
     * @return OdpcStorage
     */
    OdpcStorage getByStorageId(Long storageId);
}
