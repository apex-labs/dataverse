package org.apex.dataverse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.entity.BdmJob;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.param.BdmJobParam;
import org.apex.dataverse.param.PageBdmJobParam;
import org.apex.dataverse.param.SqlParam;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.vo.BdmJobGroupVO;
import org.apex.dataverse.vo.BdmJobVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 大数据建模作业，Bigdata Data Modeling 服务类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
public interface IBdmJobService extends IService<BdmJob> {

    /**
     * 数据开发列表分页展示
     * @param pageBdmJobParam
     * @return
     */
    PageResult<BdmJobVO> pageBdmJob(PageBdmJobParam pageBdmJobParam) throws DtvsAdminException;

    /**
     * 保存数据开发任务
     * @param saveBdmJobParam
     * @return
     */
    Long saveBdmJob(BdmJobParam saveBdmJobParam) throws DtvsAdminException;

    /**
     * 编辑数据开发任务
     * @param editBdmJobParam
     * @return
     */
    Long editBdmJob(BdmJobParam editBdmJobParam) throws DtvsAdminException, SqlParseException;

//    /**
//     * 通过数据开发分组code展示数据集成列表树
//     * @param pageTreeByGroupCodeParam
//     * @return
//     */
//    PageResult<BdmJobVO> pageTreeByGroupCode(PageTreeByGroupCodeParam pageTreeByGroupCodeParam) throws DtvsAdminException;

    /**
     * 展示数据集成列表树
     * @return
     */
    List<BdmJobGroupVO> treeBdmJob(String dataRegionCode) throws DtvsAdminException;


    /**
     * 通过数据建模作业ID查询数据建模作业详情
     * @param bdmJobId
     * @return
     * @throws DtvsAdminException
     */
    BdmJobVO detailBdmJobVO(Long bdmJobId) throws DtvsAdminException;

    /**
     * 设置数据建模作业状态为开发
     * @param bdmJobId
     * @param status
     * @return
     * @throws DtvsAdminException
     */
    Boolean setBdmJobStatus(Long bdmJobId, Integer status) throws DtvsAdminException;

    /**
     * 单次执行sql
     * @param sqlParam
     * @return
     * @throws InvalidCmdException
     * @throws InterruptedException
     * @throws InvalidConnException
     * @throws PortConnectionException
     * @throws NoPortNodeException
     * @throws JsonProcessingException
     * @throws SqlParseException
     * @throws DtvsAdminException
     */
    Object executeSql(SqlParam sqlParam) throws InvalidCmdException, InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException;


    /**
     * 显示表结构
     * @param sqlParam
     * @return
     * @throws InvalidCmdException
     * @throws InterruptedException
     * @throws InvalidConnException
     * @throws PortConnectionException
     * @throws NoPortNodeException
     * @throws JsonProcessingException
     * @throws SqlParseException
     * @throws DtvsAdminException
     */
    Object displayTableStructure(SqlParam sqlParam) throws InvalidCmdException, InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException;
}
