package org.apex.dataverse.service;

import org.apex.dataverse.entity.StorageBox;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apex.dataverse.vo.StorageBoxVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IStorageBoxService extends IService<StorageBox> {

    /**
     * 通过存储区ID查询数据空间下存储桶
     * @param storageId
     * @return
     */
    List<StorageBoxVO> listStorageBoxByStorageId(Long storageId);
}
