package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.DataStorage;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DataStorageParam;
import org.apex.dataverse.param.PageDataStorageParam;
import org.apex.dataverse.vo.DataStorageVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IDataStorageService extends IService<DataStorage> {

    /**
     * 分页查询存储区列表
     * @param pageDataStorageParam
     * @return
     */
    PageResult<DataStorageVO> pageDataStorage(PageDataStorageParam pageDataStorageParam) throws DtvsAdminException;

    /**
     * 保存数据存储区
     * @param dataStorageParam
     * @return
     */
    Long addDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException;

    /**
     * 编辑数据存储区
     * @param dataStorageParam
     * @return
     */
    Long editDataStorage(DataStorageParam dataStorageParam) throws DtvsAdminException;
}
