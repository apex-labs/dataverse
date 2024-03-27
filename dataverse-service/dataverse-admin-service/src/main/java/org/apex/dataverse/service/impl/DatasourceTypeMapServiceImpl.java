package org.apex.dataverse.service.impl;

import org.apex.dataverse.entity.DatasourceTypeMap;
import org.apex.dataverse.mapper.DatasourceTypeMapMapper;
import org.apex.dataverse.param.DatasourceTypeMapParam;
import org.apex.dataverse.service.IDatasourceTypeMapService;
import org.apex.dataverse.vo.DatasourceTypeMapVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-08
 */
@Service
public class DatasourceTypeMapServiceImpl extends ServiceImpl<DatasourceTypeMapMapper, DatasourceTypeMap> implements IDatasourceTypeMapService {

    @Autowired
    private MapperFactory mapperFactory;

    @Override
    public List<DatasourceTypeMapVO> listDatasourceTypeMap(DatasourceTypeMapParam dataSourceTypeMapParam) {
        LambdaQueryWrapper<DatasourceTypeMap> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (Objects.isNull(dataSourceTypeMapParam)) {
            return mapperFactory.getMapperFacade().mapAsList(list(), DatasourceTypeMapVO.class);
        } else {
            if (Objects.nonNull(dataSourceTypeMapParam.getDatasourceType())) {
                lambdaQueryWrapper.eq(DatasourceTypeMap::getDatasourceTypeId, dataSourceTypeMapParam.getDatasourceType());
            }
            if (StringUtils.isNotBlank(dataSourceTypeMapParam.getDatasourceTypeName())) {
                lambdaQueryWrapper.eq(DatasourceTypeMap::getDatasourceTypeName, dataSourceTypeMapParam.getDatasourceTypeName());
            }
            return mapperFactory.getMapperFacade().mapAsList(list(lambdaQueryWrapper), DatasourceTypeMapVO.class);
        }

    }

    @Override
    public List<DatasourceTypeMapVO> listAllDatasourceTypeMap() {
        return mapperFactory.getMapperFacade().mapAsList(list(), DatasourceTypeMapVO.class);
    }
}
