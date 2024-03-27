package org.apex.dataverse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ma.glasnost.orika.MapperFactory;
import org.apex.dataverse.entity.DataTypeMap;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.DataTypeMapMapper;
import org.apex.dataverse.service.IDataTypeMapService;
import org.apex.dataverse.vo.DataTypeMapVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * <p>
 * 数据类型字典表，脚本初始化 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class DataTypeMapServiceImpl extends ServiceImpl<DataTypeMapMapper, DataTypeMap> implements IDataTypeMapService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public List<DataTypeMapVO> listDataTypeMap() {
        return mapperFactory.getMapperFacade().mapAsList(list(), DataTypeMapVO.class);
    }

    @Override
    public DataTypeMap findDataTypeMapByName(String dataTypeName) throws DtvsAdminException{
        List<DataTypeMap> dataTypeMapList = list(Wrappers.<DataTypeMap>lambdaQuery().eq(DataTypeMap::getDataTypeName, dataTypeName));
        if(CollectionUtils.isEmpty(dataTypeMapList)){
            throw new DtvsAdminException("数据类型字典 " + dataTypeName + " 不存在");
        }
        return dataTypeMapList.get(0);
    }
}
