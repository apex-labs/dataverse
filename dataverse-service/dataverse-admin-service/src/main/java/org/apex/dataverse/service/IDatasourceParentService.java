package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.dto.DatasourceColumnDTO;
import org.apex.dataverse.dto.DatasourceParentDTO;
import org.apex.dataverse.dto.DatasourceTableDTO;
import org.apex.dataverse.entity.DatasourceParent;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.*;
import org.apex.dataverse.vo.DatasourceParentVO;
import org.apex.dataverse.vo.TestDataSourceLinkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
public interface IDatasourceParentService extends IService<DatasourceParent> {

    /**
     * 数据源列表接口
     * @param pageDatasourceParam
     * @return
     */
    PageResult<DatasourceParentVO> pageDatasourceParent(PageDatasourceParentParam pageDatasourceParam) throws DtvsAdminException;

    /**
     * 添加数据源
     * @param datasourceParentParam
     * @return
     */
    Long addDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException;

    /**
     * 编辑数据源
     * @param datasourceParentParam
     * @return
     */
    Long editDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException;

    /**
     * 测试数据源连接
     * @param dataSourceParam
     * @return
     */
    List<TestDataSourceLinkVO> testDatasourceParentLink(DatasourceParentParam datasourceParentParam) throws DtvsAdminException;

    /**
     * 根据数据源类型返回数据源列表
     * @param listDataSourceParam
     * @return
     */
    List<DatasourceParentDTO> listDatasourceParentByType(ListDataSourceParam listDataSourceParam);

    /**
     * 查询数据源下对应的表
     * @param listDatasourceTableParam
     * @return
     */
    List<DatasourceTableDTO> getDatasourceTable(ListDatasourceTableParam listDatasourceTableParam) throws DtvsAdminException;

    /**
     * 查询数据源表对应的列
     * @param listDatasourceTableColumnParam
     * @return
     */
    List<DatasourceColumnDTO> getDatasourceTableAndColumn(ListDatasourceTableColumnParam listDatasourceTableColumnParam) throws DtvsAdminException;

    /**
     * 查询父类空间详情
     * @param parentId
     * @return
     */
    DatasourceParentVO detail(Long parentId) throws DtvsAdminException;

    /**
     * 删除父类空间
     * @param parentId
     * @return
     */
    Boolean delete(Long parentId) throws DtvsAdminException;
}
