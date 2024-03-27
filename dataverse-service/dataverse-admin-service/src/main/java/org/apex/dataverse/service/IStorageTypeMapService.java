package org.apex.dataverse.service;

import org.apex.dataverse.entity.StorageTypeMap;
import org.apex.dataverse.param.StorageTypeMapParam;
import org.apex.dataverse.vo.StorageTypeMapVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IStorageTypeMapService extends IService<StorageTypeMap> {

    /**
     * 查询存储区类型列表
     * @param storageTypeMapParam
     * @return
     */
    List<StorageTypeMapVO> listStorageTypeMap(StorageTypeMapParam storageTypeMapParam);
}
