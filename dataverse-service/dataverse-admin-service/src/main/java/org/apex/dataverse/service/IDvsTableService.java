package org.apex.dataverse.service;

import org.apex.dataverse.entity.DvsTable;
import org.apex.dataverse.param.SaveDvsTableParam;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apex.dataverse.vo.DvsTableVO;

import java.util.List;

/**
 * <p>
 * 表 服务类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
public interface IDvsTableService extends IService<DvsTable> {

    /**
     * 保存数据表
     * @param saveDvsTableParamList
     * @return
     */
    List<SaveDvsTableParam> saveDvsTable(List<SaveDvsTableParam> saveDvsTableParamList);

    /**
     * 删除数据表
     * @param tableIds
     * @return
     */
    Boolean deleteDvsTableByIds(List<Long> tableIds);

    /**
     * 根据数据域查询数据表列表
     * @param dataRegionCode
     * @return
     */
    List<DvsTableVO> listDvsTableByDataRegionCode(String dataRegionCode);
}
