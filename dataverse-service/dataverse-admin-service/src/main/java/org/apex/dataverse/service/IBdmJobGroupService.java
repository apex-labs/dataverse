package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.BdmJobGroup;
import org.apex.dataverse.param.BdmJobGroupParam;
import org.apex.dataverse.vo.BdmJobGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 大数据建模作业分组 服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-18
 */
public interface IBdmJobGroupService extends IService<BdmJobGroup> {

    /**
     * 展示数据开发分组列表
     * @return
     */
    List<BdmJobGroupVO> listBdmJobGroup();

    /**
     * 根据数据域查询开发分组信息
     * @param dataRegionCode
     * @return
     */
    List<BdmJobGroupVO> listBdmJobGroupByDataRegionCode(String dataRegionCode);

    /**
     * 保存数据开发分组
     * @param bdmJobGroupParam
     * @return
     */
    Integer saveBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException;

    /**
     * 编辑数据开发分组
     * @param bdmJobGroupParam
     * @return
     */
    Integer editBdmJobGroup(BdmJobGroupParam bdmJobGroupParam) throws DtvsAdminException;

    /**
     * 删除数据开发分组
     * @param bdmJobGroupId
     * @return
     */
    Integer deleteBdmJobGroup(Integer bdmJobGroupId) throws DtvsAdminException;
}
