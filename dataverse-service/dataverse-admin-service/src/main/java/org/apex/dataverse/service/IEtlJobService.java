package org.apex.dataverse.service;

import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.EtlJob;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.EtlJobParam;
import org.apex.dataverse.param.PageEtlJobParam;
import org.apex.dataverse.param.PageTreeByGroupCodeParam;
import org.apex.dataverse.vo.EnumInfoVO;
import org.apex.dataverse.vo.EtlJobVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 数据抽取转换加载作业，Extract Transform and Load 服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
public interface IEtlJobService extends IService<EtlJob> {

    /**
     * 保持数据集成接口
     * @param saveEtlJobParam
     * @return
     */
    Long saveEtlJob(EtlJobParam saveEtlJobParam) throws DtvsAdminException;

    /**
     * 编辑数据集成接口
     * @param saveEtlJobParam
     * @return
     */
    Long editEtlJob(EtlJobParam saveEtlJobParam) throws DtvsAdminException;

    /**
     * 查询数据集成分页列表
     * @param pageEtlJobParam
     * @return
     */
    PageResult<EtlJobVO> pageEtlJob(PageEtlJobParam pageEtlJobParam) throws DtvsAdminException;

    /**
     * 通过分组code查询数据集成列表分页信息
     * @param pageTreeByGroupCodeParam
     * @return
     */
    PageResult<EtlJobVO> pageTreeByGroupCode(PageTreeByGroupCodeParam pageTreeByGroupCodeParam);

    /**
     * 查询数据集成详情
     * @param etlJobId
     * @return
     */
    EtlJobVO detailEtlJobVO(Long etlJobId) throws DtvsAdminException;

    /**
     * 设置数据集成状态为开发
     * @param etlJobId
     * @return
     */
    Boolean setStatusDevelop(Long etlJobId) throws DtvsAdminException;

    /**
     * 设置数据集成状态为加入调度
     * @param etlJobId
     * @return
     */
    Boolean setStatusSchedule(Long etlJobId);

    /**
     * 设置数据集成状态测试
     * @param etlJobId
     * @return
     */
    Boolean setStatusTest(Long etlJobId) throws DtvsAdminException;

    /**
     * 设置数据集成状态为上线
     * @param etlJobId
     * @return
     */
    Boolean setStatusOnline(Long etlJobId) throws DtvsAdminException;

    /**
     * 设置数据集成状态为下线
     * @param etlJobId
     * @return
     */
    Boolean setStatusDownLine(Long etlJobId) throws DtvsAdminException;

    /**
     * 是否流式枚举
     * @return
     */
    List<EnumInfoVO> isStreamEnum();

    /**
     * 表创建模式枚举
     * @return
     */
    List<EnumInfoVO> createModeEnum();

    /**
     * 任务周期状态枚举
     * @return
     */
    List<EnumInfoVO> jobLifecycleEnum();

    /**
     * 修改集成任务名称
     * @param saveEtlJobParam
     * @return
     */
    Long updateEtlJobName(EtlJobParam saveEtlJobParam) throws DtvsAdminException;
}
