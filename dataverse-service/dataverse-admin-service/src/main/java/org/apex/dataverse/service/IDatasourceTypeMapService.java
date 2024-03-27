package org.apex.dataverse.service;

import org.apex.dataverse.entity.DatasourceTypeMap;
import org.apex.dataverse.param.DatasourceTypeMapParam;
import org.apex.dataverse.vo.DatasourceTypeMapVO;
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
public interface IDatasourceTypeMapService extends IService<DatasourceTypeMap> {

    /**
     * 返回数据源地图列表
     * @param dataSourceTypeMapParam
     * @return
     */
    List<DatasourceTypeMapVO> listDatasourceTypeMap(DatasourceTypeMapParam dataSourceTypeMapParam);

    /**
     * 获取全部的数据源地图
     * @return
     */
    List<DatasourceTypeMapVO> listAllDatasourceTypeMap();
}
