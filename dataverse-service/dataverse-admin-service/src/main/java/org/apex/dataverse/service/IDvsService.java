package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.Dvs;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DvsParam;
import org.apex.dataverse.param.PageDvsParam;
import org.apex.dataverse.vo.DvsVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IDvsService extends IService<Dvs> {

    /**
     * 分页查询数据空间
     * @param pageDvsParam
     * @return
     */
    PageResult<DvsVO> pageDvs(PageDvsParam pageDvsParam) throws DtvsAdminException;

    /**
     * 新增数据空间
     * @param DvsParam
     * @return
     */
    Long addDvs(DvsParam DvsParam) throws DtvsAdminException;

    /**
     * 编辑数据空间
     * @param DvsParam
     * @return
     */
    Long editDvs(DvsParam DvsParam) throws DtvsAdminException;
}
