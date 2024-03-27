package org.apex.dataverse.service;


import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.Datasource;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.DatasourceParam;
import org.apex.dataverse.param.PageDatasourceParentParam;
import org.apex.dataverse.vo.DatasourceVO;
import org.apex.dataverse.vo.TestDataSourceLinkVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-08
 */
public interface IDatasourceService extends IService<Datasource> {

    /**
     * 数据源列表
     * @param pageDataSourceParam
     * @return
     */
    PageResult<DatasourceVO> pageDataSource(PageDatasourceParentParam pageDataSourceParam) throws DtvsAdminException;

    /**
     * 新增数据源
     * @param dataSourceParam
     * @return
     */
    Long addDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException;

    /**
     * 编辑数据源
     * @param dataSourceParam
     * @return
     */
    Long editDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException;

    /**
     * 测试数据源连接
     * @param dataSourceParam
     * @return
     */
    List<TestDataSourceLinkVO> testDataSourceLink(DatasourceParam dataSourceParam) throws DtvsAdminException;
}
