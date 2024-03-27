package org.apex.dataverse.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import ma.glasnost.orika.MapperFactory;
import org.apex.dataverse.entity.DvsTable;
import org.apex.dataverse.mapper.DvsTableMapper;
import org.apex.dataverse.param.SaveDvsTableParam;
import org.apex.dataverse.service.IDvsTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.DvsTableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 表 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
public class DvsTableServiceImpl extends ServiceImpl<DvsTableMapper, DvsTable> implements IDvsTableService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private DvsTableMapper dvsTableMapper;

    @Override
    public List<SaveDvsTableParam> saveDvsTable(List<SaveDvsTableParam> saveDvsTableParamList) {
        return null;
    }

    @Override
    public Boolean deleteDvsTableByIds(List<Long> tableIds) {
        return null;
    }

    @Override
    public List<DvsTableVO> listDvsTableByDataRegionCode(String dataRegionCode){
        List<DvsTableVO> list = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<DvsTable>lambdaQuery().eq(DvsTable::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())
                .eq(DvsTable::getDataRegionCode, dataRegionCode).orderByAsc(DvsTable::getDwLayer)), DvsTableVO.class);
        return list;
    }
}
