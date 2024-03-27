package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.DataRegion;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DataRegionParam;
import org.apex.dataverse.param.ListDataRegionParam;
import org.apex.dataverse.param.PageDataRegionParam;
import org.apex.dataverse.vo.DataRegionVO;
import org.apex.dataverse.vo.DwLayerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 数据域 服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
public interface IDataRegionService extends IService<DataRegion> {

    /**
     * 数据源分页查询接口
     * @param pageDataRegionParam
     * @return
     */
    PageResult<DataRegionVO> pageDataRegion(PageDataRegionParam pageDataRegionParam) throws DtvsAdminException;

    /**
     * 保持数据域
     * @param dataRegionParamList
     * @return
     */
    Boolean addDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException;

    /**
     * 编辑数据域
     * @param dataRegionParam
     * @return
     */
    Boolean editDataRegion(List<DataRegionParam> dataRegionParamList) throws DtvsAdminException;

    /**
     * 数据域分层信息展示
     * @return
     */
    List<DwLayerVO> dwLayer();

    /**
     * 数据域详细分层信息展示
     * @return
     */
    List<DwLayerVO> dwLayerDetail();

    /**
     * 数据域详情
     * @param dataRegionId
     * @return
     */
    DataRegionVO detail(Long dataRegionId) throws DtvsAdminException;

    /**
     * 删除数据域
     * @param dataRegionId
     * @return
     */
    Boolean delete(Long dataRegionId) throws DtvsAdminException;

    /**
     * 查询数据空间对应环境下的数据域
     * @param listDataRegionParam
     * @return
     */
    List<DataRegionVO> listDataRegionByDvsAndEnv(ListDataRegionParam listDataRegionParam) throws DtvsAdminException;

    /**
     * 根据数据分层展示分层详细列表
     * @param layer
     * @return
     */
    List<DwLayerVO> getDwLayerDetail(String layer);
}
