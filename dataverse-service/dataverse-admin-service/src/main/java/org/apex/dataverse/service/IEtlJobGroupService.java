package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.EtlJobGroup;
import org.apex.dataverse.param.EtlJobGroupParam;
import org.apex.dataverse.param.EtlJobGroupTreeParam;
import org.apex.dataverse.vo.EtlJobGroupTreeVO;
import org.apex.dataverse.vo.EtlJobGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
public interface IEtlJobGroupService extends IService<EtlJobGroup> {

    /**
     * 展示数据集成分组列表
     * @return
     */
    List<EtlJobGroupVO> listEtlJobGroup();

    /**
     * 保存数据集成分组
     * @param etlJobGroupParam
     * @return
     */
    Long saveEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException;

    /**
     * 编辑数据集成分组
     * @param etlJobGroupParam
     * @return
     */
    Long editEtlJobGroup(EtlJobGroupParam etlJobGroupParam) throws DtvsAdminException;

    /**
     * 删除数据集成分组
     * @param etlJobGroupId
     * @return
     */
    Long deleteEtlJobGroup(Long etlJobGroupId) throws DtvsAdminException;

    /**
     * 展示单一数据源类型下的数据集成分组信息
     * @param datasourceTypeId
     * @return
     */
    EtlJobGroupVO listByDatasourceTypeId(Integer datasourceTypeId);

    /**
     * 根据数据域查询数据集成分组树
     * @param etlJobTreeParam
     * @return
     */
    EtlJobGroupTreeVO etlJobGroupTreeByDataRegion(EtlJobGroupTreeParam etlJobTreeParam) throws DtvsAdminException;
}
