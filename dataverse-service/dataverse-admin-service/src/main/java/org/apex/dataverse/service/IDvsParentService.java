package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.DvsParent;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DvsParentParam;
import org.apex.dataverse.param.PageDvsParentParam;
import org.apex.dataverse.param.StorageBoxParam;
import org.apex.dataverse.vo.DvsParentVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apex.dataverse.vo.DvsVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
public interface IDvsParentService extends IService<DvsParent> {

    /**
     * 数据空间列表
     * @param pageDvsParentParam
     * @return
     */
    PageResult<DvsParentVO> pageDvsParent(PageDvsParentParam pageDvsParentParam) throws DtvsAdminException;

    /**
     * 添加数据空间
     * @param DvsParentParam
     * @return
     */
    Long addDvsParent(DvsParentParam DvsParentParam) throws DtvsAdminException;

    /**
     * 编辑数据空间
     * @param DvsParentParam
     * @return
     */
    Long editDvsParent(DvsParentParam DvsParentParam) throws DtvsAdminException;

    /**
     * 设置数据空间存储区
     * @param storageBoxParamList
     * @return
     */
    Boolean addStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException;

    /**
     * 编辑数据空间存储区
     * @param storageBoxParamList
     * @return
     */
    Boolean editStorageBox(List<StorageBoxParam> storageBoxParamList) throws DtvsAdminException;

    /**
     * 查询数据空间详情
     * @param parentId
     * @return
     */
    DvsParentVO detail(Long parentId) throws DtvsAdminException;

    /**
     * 删除数据空间
     * @param parentId
     * @return
     */
    Boolean delete(Long parentId) throws DtvsAdminException;
}
