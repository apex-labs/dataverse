package org.apex.dataverse.service;

import org.apex.dataverse.entity.DataTypeMap;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.vo.DataTypeMapVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 数据类型字典表，脚本初始化 服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IDataTypeMapService extends IService<DataTypeMap> {

    /**
     * 查询数据类型列表
     * @return
     */
    List<DataTypeMapVO> listDataTypeMap();

    DataTypeMap findDataTypeMapByName(String dataTypeName) throws DtvsAdminException;
}
