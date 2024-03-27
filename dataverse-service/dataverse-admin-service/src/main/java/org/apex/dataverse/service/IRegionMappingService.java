package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.RegionMapping;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.PageRegionMappingParam;
import org.apex.dataverse.param.RegionMappingParam;
import org.apex.dataverse.vo.RegionMappingVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IRegionMappingService extends IService<RegionMapping> {

    /**
     * 分页查询业务域映射
     * @param pageRegionMappingParam
     * @return
     */
    PageResult<RegionMappingVO> pageRegionMapping(PageRegionMappingParam pageRegionMappingParam) throws DtvsAdminException;

    /**
     * 新增业务域映射
     * @param regionMappingParam
     * @return
     */
    Long addRegionMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException;

    /**
     * 编辑业务域映射
     * @param regionMappingParam
     * @return
     */
    Long editRegionMapping(RegionMappingParam regionMappingParam) throws DtvsAdminException;
}
