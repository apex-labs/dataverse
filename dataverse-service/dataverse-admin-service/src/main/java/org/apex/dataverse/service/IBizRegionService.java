package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.BizRegion;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.BizRegionParam;
import org.apex.dataverse.param.PageBizRegionParam;
import org.apex.dataverse.vo.BizRegionVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IBizRegionService extends IService<BizRegion> {

    /**
     * 分页查询数据域
     * @param pageBizRegionParam
     * @return
     */
    PageResult<BizRegionVO> pageBizRegion(PageBizRegionParam pageBizRegionParam) throws DtvsAdminException;

    /**
     * 新增业务域
     * @param bizRegionParam
     * @return
     */
    Long addBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException;

    /**
     * 编辑业务域
     * @param bizRegionParam
     * @return
     */
    Long editBizRegion(BizRegionParam bizRegionParam) throws DtvsAdminException;

    /**
     * 业务域详情
     * @param bizRegionId
     * @return
     */
    BizRegionVO detail(Long bizRegionId) throws DtvsAdminException;

    /**
     * 删除业务域
     * @param bizRegionId
     * @return
     */
    Boolean delete(Long bizRegionId) throws DtvsAdminException;
}
