package org.apex.dataverse.port.service;

import org.apex.dataverse.port.entity.StoragePort;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 存储区和Port映射。指定存储区映射到哪些Port，对应存储区上的命令会提交到对应的Port上进行执行。 服务类
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
public interface IStoragePortService extends IService<StoragePort> {

    /**
     * Get Storage port by port id
     * @param portId port id
     * @return List<StoragePort>
     */
    List<StoragePort> getByPortId(Long portId);

    /**
     * Get storage port by port code
     * @param portCode portCode
     * @return List<StoragePort>
     */
    List<StoragePort> getByPortCode(String portCode);

    /**
     * Get by storage id and port id
     * @param storageId storageId
     * @param portId portId
     * @return StoragePort
     */
    StoragePort getByStorageAndPortId(Long storageId, Long portId);

}
