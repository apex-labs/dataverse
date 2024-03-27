package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.calcite.avatica.util.Casing;
import org.apache.calcite.avatica.util.Quoting;
import org.apache.calcite.config.Lex;
import org.apache.calcite.sql.*;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.calcite.sql.parser.ddl.SqlDdlParserImpl;
import org.apex.dataverse.constrant.SqlGrammarConst;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlRspPacket;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.DwLayerDetailEnum;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.enums.JobLifecycleEnum;
import org.apex.dataverse.enums.JobTypeEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.mapper.BdmJobGroupMapper;
import org.apex.dataverse.mapper.BdmJobMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.conn.PortConnection;
import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.service.*;
import org.apex.dataverse.util.ObjectMapperUtil;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * <p>
 * 大数据建模作业，Bigdata Data Modeling 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Service
@Slf4j
public class BdmJobServiceImpl extends ServiceImpl<BdmJobMapper, BdmJob> implements IBdmJobService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IDataTypeMapService dataTypeMapService;

    @Autowired
    private BdmJobGroupMapper bdmJobGroupMapper;

    @Autowired
    private BdmJobGroupServiceImpl bdmJobGroupService;

    @Autowired
    private IDataRegionService dataRegionService;

    @Autowired
    private IStorageBoxService storageBoxService;

    @Autowired
    private IBdmScriptService bdmScriptService;

    @Autowired
    private IBdmInTableService bdmInTableService;

    @Autowired
    private IBdmOutTableService bdmOutTableService;

    @Autowired
    private IDvsTableService dvsTableService;

    @Autowired
    private IDvsTableColumnService dvsTableColumnService;

    @Autowired
    private ITableStorageService tableStorageService;

    @Autowired
    private ITableStatisticsService tableStatisticsService;

    @Autowired
    private IDvsTableDdlService dvsTableDdlService;

    @Autowired
    private IOpenedWorksheetService openedWorksheetService;

    private PortExchanger portExchanger;

    @Autowired
    public void setPortExchanger(PortExchanger portExchanger) {
        this.portExchanger = portExchanger;
    }

    @Value("${nexus.odpc.file.location}")
    private String odpcFileLocation;

    /**
     * 分区日期目录 抽取数据使用
     */
    private String ts;

    @Autowired
    private ManageFeignClient manageFeignClient;

    @Override
    public PageResult<BdmJobVO> pageBdmJob(PageBdmJobParam pageBdmJobParam) throws DtvsAdminException {
        validateBdmJob(pageBdmJobParam);
        IPage<BdmJob> iPage = getIPage(pageBdmJobParam);
        PageResult<BdmJobVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), BdmJobVO.class));
        return pageResult;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long saveBdmJob(BdmJobParam saveBdmJobParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateSaveBdmJob(saveBdmJobParam);
        validateDuplicationSaveBdmJob(saveBdmJobParam, userInfo);
        // 设置数据建模作业编码
        saveBdmJobParam.setBdmJobCode(UCodeUtil.produce());
        // 设置数据建模作业作业状态
        saveBdmJobParam.setJobLifecycle(JobLifecycleEnum.ETL_DEVELOP.getValue());
        Long bdmJobId = saveOrUpdateBdmJob(saveBdmJobParam, userInfo);
//        // 保存数据建模脚本信息
//        saveBdmScript(saveBdmJobParam);
//        //保存数据建模作业依赖表信息
//        saveBdmInTable(saveBdmJobParam);
//        // 保存任务单元格打开记录
//        saveOrUpdateOpenedWorksheet(bdmJobId);
        return bdmJobId;
    }

    private void saveBdmInTable(BdmJobParam bdmJobParam) throws DtvsAdminException {
        if (CollectionUtil.isNotEmpty(bdmJobParam.getBdmInTableParamList())) {
            String bdmJobCode = bdmJobParam.getBdmJobCode();
            Map<String,Object> map = new HashMap<>();
            map.put("bdm_job_code",bdmJobCode);
            bdmInTableService.removeByMap(map);
            for (BdmInTableParam bdmInTableParam : bdmJobParam.getBdmInTableParamList()) {
                BdmInTable bdmInTable = mapperFactory.getMapperFacade().map(bdmInTableParam, BdmInTable.class);
                bdmInTable.setBdmJobCode(bdmJobCode);
                bdmInTable.setCreateTime(LocalDateTime.now());
                bdmInTable.setUpdateTime(LocalDateTime.now());
                if (!bdmInTableService.saveOrUpdate(bdmInTable)) {
                    throw new DtvsAdminException("保存数据建模作业依赖表失败");
                }
            }
        }
    }

    private void saveBdmScript(BdmJob bdmJob,BdmJobParam bdmJobParam) throws DtvsAdminException, SqlParseException {
        if (bdmJobParam.getBdmScriptParam() != null) {
            saveDdlSql(bdmJob,bdmJobParam.getBdmScriptParam());
            String bdmJobCode = bdmJobParam.getBdmJobCode();
            BdmScript bdmScript = mapperFactory.getMapperFacade().map(bdmJobParam.getBdmScriptParam(), BdmScript.class);
            bdmScript.setBdmJobCode(bdmJobCode);
            bdmScript.setCreateTime(LocalDateTime.now());
            bdmScript.setUpdateTime(LocalDateTime.now());
            List<BdmScript> bdmScriptList = bdmScriptService.list(Wrappers.<BdmScript>lambdaQuery().eq(BdmScript::getBdmJobCode, bdmJobCode).eq(BdmScript::getEnv, bdmJobParam.getEnv()).orderByDesc(BdmScript::getVersion));
            if (CollectionUtil.isNotEmpty(bdmScriptList)) {
                bdmScript.setVersion(bdmScriptList.get(0).getVersion() + 1);
            }else{
                bdmScript.setVersion(1);
            }
            if (!bdmScriptService.saveOrUpdate(bdmScript)) {
                throw new DtvsAdminException("保存数据建模作业脚本失败");
            }
        }
    }

    public void saveDdlSql(BdmJob bdmJob,BdmScriptParam bdmScriptParam) throws SqlParseException, DtvsAdminException {
        String sql = processSqlContent(bdmScriptParam.getBdmScript());
        String[] split = sql.split(";");
        for (String str : split) {
            if(str.toLowerCase().contains("create table ") || str.toLowerCase().contains("drop ")){
                //删除原先数据再新增
                clearEditBdmJobRelation(bdmJob);
                if(str.toLowerCase().contains("create table ")){
                    DataRegion dataRegion = getDataRegion(bdmJob);
                    StorageBox storageBox = getStorageBox(bdmJob,dataRegion);
                    saveCreateSql(bdmJob,dataRegion,storageBox,str);
                }
            }else if(str.toLowerCase().contains(SqlGrammarConst.INSERT_OVERWRITE)){
                String selectSql = str.substring(str.toLowerCase().indexOf(SqlGrammarConst.SELECT_WITH_BLANK_LOWER));
//                解析表名并查询表信息写入 bdmInTable
//                List<String> tableNameList = extractTableNameList(selectSql);
//                if(!CollectionUtils.isEmpty(tableNameList)){
//                    for (String tableName : tableNameList) {
//                        List<DvsTable> list = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableName, tableName).eq(DvsTable::getEnv, bdmJob.getEnv()));
//
//                    }
//                }
            }
        }
    }

    public static List<String> extractTableNameList(String sql) throws SqlParseException {
//        SqlParser.Config config = SqlParser.configBuilder().setLex(Lex.MYSQL).build();

        SqlParser.Config config = SqlParser.configBuilder()
                .setQuotedCasing(Casing.UNCHANGED)
                .setUnquotedCasing(Casing.UNCHANGED)
                .setQuoting(Quoting.BACK_TICK)
//                .setParserFactory(QuarkParserImpl.FACTORY)
//                .setLex(Lex.MYSQL)
                .build();


        SqlParser parser = SqlParser.create(sql,config);
        SqlNode parsed = parser.parseQuery();
//        SqlNode parsed = parser.parseStmt();
        List<String> tableNameList = new ArrayList<>();

        parseSqlNode(parsed, tableNameList);

        return tableNameList;
    }

    private static void parseSqlNode(SqlNode sqlNode, List<String> tableNameList) {
        SqlKind kind = sqlNode.getKind();
        switch (kind) {
            case IDENTIFIER:
                parseFromNode(sqlNode, tableNameList);
                break;
            case SELECT:
                SqlSelect select = (SqlSelect) sqlNode;
                parseFromNode(select.getFrom(), tableNameList);
                break;
            case UNION:
                ((SqlBasicCall) sqlNode).getOperandList().forEach(node -> {
                    parseSqlNode(node, tableNameList);
                });
                break;
            case ORDER_BY:
                handlerOrderBy(sqlNode, tableNameList);
                break;
            case INSERT:
                extractSourceTableInInsertSql(sqlNode,tableNameList);
                break;
        }
    }

    private static void parseFromNode(SqlNode from, List<String> tableNameList){
        SqlKind kind = from.getKind();
        switch (kind) {
            case IDENTIFIER:
                //最终的表名
                SqlIdentifier sqlIdentifier = (SqlIdentifier) from;
                tableNameList.add(sqlIdentifier.toString());
                break;
            case AS:
                SqlBasicCall sqlBasicCall = (SqlBasicCall) from;
                SqlNode selectNode = sqlBasicCall.getOperandList().get(0);
                parseSqlNode(selectNode, tableNameList);
                break;
            case JOIN:
                SqlJoin sqlJoin = (SqlJoin) from;
                SqlNode left = sqlJoin.getLeft();
                parseFromNode(left, tableNameList);
                SqlNode right = sqlJoin.getRight();
                parseFromNode(right, tableNameList);
                break;
            case SELECT:
                parseSqlNode(from, tableNameList);
                break;
        }
    }

    private static void handlerOrderBy(SqlNode node, List<String> tableNameList) {
        SqlOrderBy sqlOrderBy = (SqlOrderBy) node;
        SqlNode query = sqlOrderBy.query;
        parseSqlNode(query, tableNameList);
    }

    private static void extractSourceTableInInsertSql(SqlNode sqlNode, List<String> tableNameList) {
        SqlInsert sqlInsert = (SqlInsert) sqlNode;
        parseSqlNode(sqlInsert.getSource(),tableNameList);
//        tableNameList = new HashSet<>(test1(sqlInsert.getSource(), false));
//        final SqlNode targetTable = sqlInsert.getTargetTable();
//        if (targetTable instanceof SqlIdentifier) {
//            tableNameList.add(((SqlIdentifier) targetTable).toString());
//        }
    }

    private void validateDuplicationSaveBdmJob(BdmJobParam bdmJobParam, UserInfo userInfo) throws DtvsAdminException {
        List<BdmJob> bdmJobList = list(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getBdmJobName, bdmJobParam.getBdmJobName()).
                eq(BdmJob::getTenantId, userInfo.getTenantId()).eq(BdmJob::getEnv, bdmJobParam.getEnv()));
        if (CollectionUtil.isNotEmpty(bdmJobList)) {
            throw new DtvsAdminException("数据建模作业名称重复");
        }
    }

    private void validateSaveBdmJob(BdmJobParam bdmJobParam) throws DtvsAdminException {
        if (Objects.isNull(bdmJobParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(bdmJobParam.getDvsCode())) {
            throw new DtvsAdminException("数据空间编码参数为空");
        }
        if (StringUtils.isBlank(bdmJobParam.getDataRegionCode())) {
            throw new DtvsAdminException("数据域编码参数为空");
        }
        if (StringUtils.isBlank(bdmJobParam.getBdmJobName())) {
            throw new DtvsAdminException("数据建模名称参数为空");
        }
        if (StringUtils.isBlank(bdmJobParam.getBdmGroupCode())) {
            throw new DtvsAdminException("数据建模分组编码参数为空");
        }
        if (Objects.isNull(bdmJobParam.getEnv())) {
            throw new DtvsAdminException("数据建模环境参数为空");
        }
        if (bdmJobParam.getBdmScriptParam() != null) {
            validateBdmScriptParam(bdmJobParam);
        }
    }

    private void validateBdmScriptParam(BdmJobParam bdmJobParam) throws DtvsAdminException {
        if (Objects.isNull(bdmJobParam.getBdmScriptParam().getEnv())) {
            throw new DtvsAdminException("数据建模脚本环境为空");
        }
//        if (Objects.isNull(bdmJobParam.getBdmScriptParam().getEngineType())) {
//            throw new DtvsAdminException("数据建模脚本引擎类型为空");
//        }
//        if (!bdmJobParam.getBdmScriptParam().getBdmScript().contains(bdmJobParam.getBdmJobName())){
//            throw new DtvsAdminException("目标表名需要与作业名称一致");
//        }
    }

    private Long saveOrUpdateBdmJob(BdmJobParam bdmJobParam, UserInfo userInfo) throws DtvsAdminException {
        BdmJob bdmJob = mapperFactory.getMapperFacade().map(bdmJobParam, BdmJob.class);
        if (Objects.isNull(bdmJobParam.getBdmJobId())) {
            bdmJob.setCreateTime(LocalDateTime.now());
        }
        bdmJob.setUpdateTime(LocalDateTime.now());
        bdmJob.setIsDeleted(IsDeletedEnum.NO.getValue());
        bdmJob.setTenantId(userInfo.getTenantId());
        bdmJob.setTenantName(userInfo.getTenantName());
        bdmJob.setUserId(userInfo.getUserId());
        bdmJob.setUserName(userInfo.getUserName());
        bdmJob.setDeptId(userInfo.getDeptId());
        bdmJob.setDeptName(userInfo.getDeptName());
        if (!saveOrUpdate(bdmJob)) {
            throw new DtvsAdminException("保存数据建模作业失败");
        }
        return bdmJob.getBdmJobId();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editBdmJob(BdmJobParam editBdmJobParam) throws DtvsAdminException, SqlParseException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        BdmJob bdmJob = validateEditBdmJob(editBdmJobParam, userInfo);
        // 设置数据建模作业编码
        editBdmJobParam.setBdmJobCode(bdmJob.getBdmJobCode());
        // 设置数据建模作业作业状态
        editBdmJobParam.setJobLifecycle(bdmJob.getJobLifecycle());
        saveOrUpdateBdmJob(editBdmJobParam, userInfo);
        // 保留数据建模作业脚本信息
        saveBdmScript(bdmJob,editBdmJobParam);
        //保存数据建模作业依赖表信息
        saveBdmInTable(editBdmJobParam);
        return editBdmJobParam.getBdmJobId();
    }

    private BdmJob validateEditBdmJob(BdmJobParam editBdmJobParam, UserInfo userInfo) throws DtvsAdminException {
        BdmJob bdmJob;
        if (Objects.isNull(editBdmJobParam.getBdmJobId())) {
            throw new DtvsAdminException("数据建模作业ID为空");
        }
        if (Objects.isNull(bdmJob = getById(editBdmJobParam.getBdmJobId()))) {
            throw new DtvsAdminException("数据建模作业不存在");
        }
        if (!bdmJob.getJobLifecycle().equals(JobLifecycleEnum.ETL_DEVELOP.getValue())) {
            throw new DtvsAdminException("数据建模作业非作业开发中状态不允许修改");
        }
        validateSaveBdmJob(editBdmJobParam);

        List<BdmJob> bdmJobList = list(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getBdmJobName, editBdmJobParam.getBdmJobName()).
                eq(BdmJob::getTenantId, userInfo.getTenantId()).eq(BdmJob::getEnv, editBdmJobParam.getEnv()));
        if (CollectionUtil.isNotEmpty(bdmJobList)) {
            if (bdmJobList.stream().filter(e -> !e.getBdmJobId().equals(bdmJob.getBdmJobId())).count() > 0) {
                throw new DtvsAdminException("数据建模作业名称重复");
            }
        }
        return bdmJob;
    }

//    @Override
//    public PageResult<BdmJobVO> pageTreeByGroupCode(PageTreeByGroupCodeParam pageTreeByGroupCodeParam) throws DtvsAdminException {
//        return null;
//    }

    private void validateBdmJob(PageBdmJobParam pageBdmJobParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageBdmJobParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        pageBdmJobParam.setPageQueryParam(pageQueryParam);
    }

    private IPage<BdmJob> getIPage(PageBdmJobParam pageBdmJobParam) {
        //默认单页10条记录
        Page<BdmJob> page = new Page<>();
        PageQueryParam pageQueryParam = pageBdmJobParam.getPageQueryParam();
        if (pageQueryParam != null && pageQueryParam.getPageNo() != null) {
            page.setCurrent(pageQueryParam.getPageNo());
        } else {
            page.setCurrent(1);
        }
        if (pageQueryParam != null && pageQueryParam.getSize() != null) {
            page.setSize(pageQueryParam.getSize());
        } else {
            page.setSize(10);
        }
        if (pageQueryParam != null) {
            if (pageQueryParam.getAscs() != null) {
                page.setAscs(Optional.of(pageQueryParam.getAscs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
            if (pageQueryParam.getDescs() != null) {
                page.setDescs(Optional.of(pageQueryParam.getDescs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
        }

        LambdaQueryWrapper<BdmJob> queryWrapper = Wrappers.lambdaQuery();
        // 搜索关键词 Bdm作业ID/Bdm作业名称
        if (StringUtils.isNotEmpty(pageBdmJobParam.getKeyword())) {
            if (pageBdmJobParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(BdmJob::getBdmJobId, pageBdmJobParam.getKeyword());
            } else {
                queryWrapper.like(BdmJob::getBdmJobName, pageBdmJobParam.getKeyword());
            }
        }
        // 根据作业状态查询
        if (Objects.nonNull(pageBdmJobParam.getJobLifecycle())) {
            queryWrapper.eq(BdmJob::getJobLifecycle, pageBdmJobParam.getJobLifecycle());
        }
        // 根据运行环境查询
        if (Objects.nonNull(pageBdmJobParam.getEnv())) {
            queryWrapper.eq(BdmJob::getEnv, pageBdmJobParam.getEnv());
        }
        // 根据数据作业的分组查询
        if (StringUtils.isNotBlank(pageBdmJobParam.getBdmGroupCode())) {
            queryWrapper.eq(BdmJob::getBdmGroupCode, pageBdmJobParam.getBdmGroupCode());
        }
        // 根据数据域编码查询
        if (StringUtils.isNotBlank(pageBdmJobParam.getDataRegionCode())) {
            queryWrapper.eq(BdmJob::getDataRegionCode, pageBdmJobParam.getDataRegionCode());
        }
        // 根据数据空间编码查询
        if (StringUtils.isNotBlank(pageBdmJobParam.getDvsCode())) {
            queryWrapper.eq(BdmJob::getDvsCode, pageBdmJobParam.getDvsCode());
        }
        // 根据运行状态 这个字段目前数据库没有设置后续加上
//        if (Objects.nonNull(pageBdmJobParam.getBdmResultStatus())) {
//            queryWrapper.eq(BdmJob::getBdmResultStatus, pageBdmJobParam.getBdmResultStatus());
//        }
        return page(page, queryWrapper);
    }

    @Override
    public List<BdmJobGroupVO> treeBdmJob(String dataRegionCode) throws DtvsAdminException {
        List<BdmJobGroupVO> bdmJobGroupVOList = new ArrayList<>();
        List<BdmJobGroup> bdmJobGroups = bdmJobGroupMapper.selectList(Wrappers.<BdmJobGroup>lambdaQuery().eq(BdmJobGroup::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId())
                .eq(BdmJobGroup::getDataRegionCode,dataRegionCode));
        for (BdmJobGroup bdmJobGroup : bdmJobGroups) {
            BdmJobGroupVO bdmJobGroupVO = new BdmJobGroupVO();
            BeanUtils.copyProperties(bdmJobGroup,bdmJobGroupVO);
            List<BdmJobVO> bdmJobs = mapperFactory.getMapperFacade().mapAsList(list(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getBdmGroupCode, bdmJobGroup.getBdmGroupCode())), BdmJobVO.class);
            if(!CollectionUtils.isEmpty(bdmJobs)){
                for (BdmJobVO bdmJobVO : bdmJobs) {
                    BdmJob bdmJob = new BdmJob();
                    BeanUtils.copyProperties(bdmJobVO,bdmJob);
                    DataRegion dataRegion = getDataRegion(bdmJob);
                    StorageBox storageBox = getStorageBox(bdmJob, dataRegion);
                    ListStorageParam listStorageParam = new ListStorageParam();
                    List<Long> storageIds = new ArrayList<>();
                    storageIds.add(storageBox.getStorageId());
                    listStorageParam.setStorageIds(storageIds);
                    List<StorageFeignVO> storageFeignVOS = manageFeignClient.listStorageVO(listStorageParam);
                    if(!CollectionUtils.isEmpty(storageFeignVOS)){
                        bdmJobVO.setEngineType(storageFeignVOS.get(0).getEngineType());
                    }
                }
            }
            bdmJobGroupVO.setBdmJobVOList(bdmJobs);
            bdmJobGroupVOList.add(bdmJobGroupVO);
        }
        return bdmJobGroupService.buildBdmJobGroupVOTree(bdmJobGroupVOList);
    }

    @Override
    public BdmJobVO detailBdmJobVO(Long bdmJobId) throws DtvsAdminException {
        BdmJobVO bdmJobVO = mapperFactory.getMapperFacade().map(validateBdmJobExists(bdmJobId), BdmJobVO.class);
        String bdmJobCode = bdmJobVO.getBdmJobCode();
        Integer env = bdmJobVO.getEnv();
        validateBdmJobCodeAndEnv(bdmJobCode, env);
        List<BdmScript> bdmScriptList = bdmScriptService.list(Wrappers.<BdmScript>lambdaQuery().eq(BdmScript::getBdmJobCode, bdmJobCode).eq(BdmScript::getEnv, env).orderByDesc(BdmScript::getVersion));
        bdmJobVO.setBdmScriptVO(getBdmJobBdmScriptVOList(bdmScriptList));
        List<BdmInTable> bdmInTableList = bdmInTableService.list(Wrappers.<BdmInTable>lambdaQuery().eq(BdmInTable::getBdmJobCode, bdmJobCode).eq(BdmInTable::getEnv, env));
        bdmJobVO.setBdmInTableVOList(getBdmJobBdmInTableVOList(bdmInTableList));
//        saveOrUpdateOpenedWorksheet(bdmJobId);
        return bdmJobVO;
    }

    private void saveOrUpdateOpenedWorksheet(Long bdmJobId) throws DtvsAdminException {
        BdmJobVO bdmJobVO = mapperFactory.getMapperFacade().map(validateBdmJobExists(bdmJobId), BdmJobVO.class);
        //保存任务单元格打开记录
        OpenedWorksheet openedWorksheet = new OpenedWorksheet();
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<OpenedWorksheet> openedWorksheetList = openedWorksheetService.list(Wrappers.<OpenedWorksheet>lambdaQuery().eq(OpenedWorksheet::getEnv, bdmJobVO.getEnv()).
                eq(OpenedWorksheet::getJobCode, bdmJobVO.getBdmJobCode()));
        if (CollectionUtil.isNotEmpty(openedWorksheetList)) {
            openedWorksheet.setWorksheetId(openedWorksheetList.get(0).getWorksheetId());
        }
        openedWorksheet.setJobCode(bdmJobVO.getBdmJobCode());
        openedWorksheet.setEnv(bdmJobVO.getEnv());
        openedWorksheet.setJobType(JobTypeEnum.BDM_JOB.getValue());
        openedWorksheet.setJobName(bdmJobVO.getBdmJobName());
        openedWorksheet.setIsDeleted(0);
        openedWorksheet.setCreateTime(LocalDateTime.now());
        openedWorksheet.setUserId(userInfo.getUserId());
        openedWorksheet.setUserName(userInfo.getUserName());

        if(!openedWorksheetService.saveOrUpdate(openedWorksheet)){
            throw new DtvsAdminException("保存任务单元格打开记录失败");
        }
    }

    private BdmScriptVO getBdmJobBdmScriptVOList(List<BdmScript> bdmScriptList) {
        if (CollectionUtil.isNotEmpty(bdmScriptList)) {
            return mapperFactory.getMapperFacade().map(bdmScriptList.get(0), BdmScriptVO.class);
        }
        return null;
    }

    private List<BdmInTableVO> getBdmJobBdmInTableVOList(List<BdmInTable> bdmInTableList) {
        if (CollectionUtil.isNotEmpty(bdmInTableList)) {
            return mapperFactory.getMapperFacade().mapAsList(bdmInTableList, BdmInTableVO.class);
        }
        return null;
    }

    private void validateBdmJobCodeAndEnv(String bdmJobCode, Integer env) throws DtvsAdminException {
        if (StringUtils.isBlank(bdmJobCode)) {
            throw new DtvsAdminException("数据建模作业编码为空");
        }
        if (Objects.isNull(env)) {
            throw new DtvsAdminException("数据建模作业环境为空");
        }
    }

    private BdmJob validateBdmJobExists(Long bdmJobId) throws DtvsAdminException {
        if (Objects.isNull(bdmJobId)) {
            throw new DtvsAdminException("数据建模作业ID参数为空");
        }
        BdmJob bdmJob = getById(bdmJobId);
        if (Objects.isNull(bdmJob)) {
            throw new DtvsAdminException("数据建模作业不存在");
        }
        return bdmJob;
    }

    @Override
    public Boolean setBdmJobStatus(Long bdmJobId, Integer status) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        BdmJob bdmJob = validateBdmJobExists(bdmJobId);
        validateBdmJobStatus(bdmJob,status);
        String bdmJobCode = bdmJob.getBdmJobCode();
        Integer env = bdmJob.getEnv();
        List<BdmOutTable> bdmOutTableList;
        List<String> tableCodes = new ArrayList<>();
        bdmJob.setUpdateTime(LocalDateTime.now());
        if (!saveOrUpdate(bdmJob)) {
            throw new DtvsAdminException("更新数据建模作业状态失败");
        }
        if (CollectionUtil.isNotEmpty(bdmOutTableList = bdmOutTableService.list(Wrappers.<BdmOutTable>lambdaQuery().eq(BdmOutTable::getBdmJobCode, bdmJobCode).
                eq(BdmOutTable::getEnv, env)))) {
            tableCodes.addAll(bdmOutTableList.stream().map(BdmOutTable::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            List<DvsTable> DvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(DvsTableList)) {
                DvsTableList.forEach(t -> {
                    t.setUpdateTime(LocalDateTime.now());
                    t.setTableLifecycle(status);
                });
                if (!dvsTableService.saveOrUpdateBatch(DvsTableList)) {
                    throw new DtvsAdminException("更新数据建模作业状态失败");
                }
            }
        }
        return Boolean.TRUE;
    }

    private void validateBdmJobStatus(BdmJob bdmJob, Integer status) throws DtvsAdminException {
        if(status == JobLifecycleEnum.ETL_DEVELOP_END.getValue()){
            //状态改为作业开发完成
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.ETL_DEVELOP.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从开发中状态到开发完成状态");
            }
        }else if(status == JobLifecycleEnum.ETL_TEST.getValue()){
            //状态改为作业测试中
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.ETL_DEVELOP_END.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从开发完成状态到测试中状态");
            }
        }else if(status == JobLifecycleEnum.ETL_TEST_END.getValue()){
            //状态改为作业测试完成
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.ETL_TEST.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从测试中状态到测试完成状态");
            }
        } else if(status == JobLifecycleEnum.BDM_DEVELOP.getValue()){
            //状态改为调度编排中
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.ETL_TEST_END.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从测试完成状态到调度编排中状态");
            }
        } else if(status == JobLifecycleEnum.BDM_DEVELOP_END.getValue()){
            //状态改为调度编排完成
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.BDM_DEVELOP.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从调度编排中状态到调度编排完成状态");
            }
        } else if(status == JobLifecycleEnum.BDM_TEST.getValue()){
            //状态改为调度测试中
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.BDM_DEVELOP_END.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从编排调度完成状态到调度测试中状态");
            }
        } else if(status == JobLifecycleEnum.BDM_TEST_END.getValue()){
            //状态改为调度测试完成
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.BDM_TEST.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从调度测试中状态到调度测试完成状态");
            }
        } else if(status == JobLifecycleEnum.DEPLOY_PROD.getValue()){
            //状态改为已发布生产
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.BDM_TEST_END.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从调度测试完成状态到发布生产状态");
            }
        } else if(status == JobLifecycleEnum.PROD_TEST.getValue()){
            //状态改为生产测试中
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.DEPLOY_PROD.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从已发布生产状态到生产测试中状态");
            }
        }else if(status == JobLifecycleEnum.PROD_TEST_END.getValue()){
            //状态改为生产测试完成
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.PROD_TEST.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从生产测试中状态到生产测试完成状态");
            }
        }else if(status == JobLifecycleEnum.ONLINE.getValue()){
            //状态改为已上线
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.PROD_TEST_END.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从生产测试完成状态到上线状态");
            }
        }else if(status == JobLifecycleEnum.DOWN_LINE.getValue()){
            //状态改为已下线
            if (bdmJob.getJobLifecycle() != JobLifecycleEnum.ONLINE.getValue()) {
                throw new DtvsAdminException("数据建模作业状态只能从已上线状态到下线状态");
            }
        }
        bdmJob.setJobLifecycle(status);
    }

    public DataRegion getDataRegion(BdmJob bdmJob) throws DtvsAdminException {
//        List<DataRegion> dataRegionList = dataRegionService.list(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getEnv, bdmJob.getEnv()).
//                eq(DataRegion::getDvsCode, bdmJob.getDvsCode()));
        DataRegion dataRegion = dataRegionService.getOne(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionCode, bdmJob.getDataRegionCode()).
                eq(DataRegion::getEnv, bdmJob.getEnv()));
        if (dataRegion == null) {
            throw new DtvsAdminException("数据域不存在");
        }
        return dataRegion;
    }

    public StorageBox getStorageBox(BdmJob bdmJob,DataRegion dataRegion) throws DtvsAdminException {
        List<StorageBox> storageBoxList = storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getEnv, bdmJob.getEnv())
                .eq(StorageBox::getDvsCode, bdmJob.getDvsCode()).eq(StorageBox::getDwLayer, dataRegion.getDwLayer()));
        if (CollectionUtil.isEmpty(storageBoxList)) {
            throw new DtvsAdminException("数据空间存储桶不存在");
        }
        return storageBoxList.get(0);
    }

    public String processSqlContent(String sql){
        //匹配单行注释
        sql = sql.replaceAll("--.*","");
        sql = sql.replaceAll("`","");
        //匹配/* */ 形式的多行注释
        Pattern pattern = Pattern.compile("/\\*.*?\\*/",Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sql);
        sql = matcher.replaceAll("");
        return sql;
    }

    public String saveCreateSql(BdmJob bdmJob,DataRegion dataRegion,StorageBox storageBox,String sql) throws SqlParseException, DtvsAdminException {
        String tableName = "";
        SqlParser.Config sqlConfig = SqlParser.configBuilder().setParserFactory(SqlDdlParserImpl.FACTORY).build();
        SqlParser sqlParser = SqlParser.create(sql,sqlConfig);
        SqlNode sqlNode = sqlParser.parseStmt();
        if(sqlNode.getKind().equals(SqlKind.CREATE_TABLE)){
            SqlDdl sqlDdl = (SqlDdl) sqlNode;
            SqlNodeList sqlNodes = SqlNodeList.of(sqlDdl.getOperandList().get(1));
            String[] split = sqlNodes.getList().toString().replace("`", "")
                    .replace("[", "")
                    .replace("]", "").split(", ");
            List<DvsTableColumn> dvsTableColumnList = new ArrayList<>();
            for (String s : split) {
                String[] s1 = s.split(" ");
                DvsTableColumn dvsTableColumn = new DvsTableColumn();
                dvsTableColumn.setColumnName(s1[0]);
                dvsTableColumn.setColumnAlias(s1[0]);
                dvsTableColumn.setEnv(bdmJob.getEnv());
                DataTypeMap dataTypeMap = dataTypeMapService.findDataTypeMapByName(s1[1].equals("INTEGER") ? "INT" : s1[1]);
                dvsTableColumn.setDataTypeId(dataTypeMap.getDataTypeId());
                dvsTableColumn.setDataTypeName(dataTypeMap.getDataTypeName());
                dvsTableColumn.setShortDataTypeId(dataTypeMap.getShortDataTypeId());
                dvsTableColumn.setIsPrimaryKey(0);
                dvsTableColumnList.add(dvsTableColumn);
            }
            tableName = sqlDdl.getOperandList().get(0).toString().toLowerCase();
            saveBdmOutTable(bdmJob,dvsTableColumnList,dataRegion,storageBox,tableName,sql);
        }
        return tableName;
    }

    public Object executeSql(SqlParam sqlParam) throws InvalidCmdException,
            InterruptedException, InvalidConnException, PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException {
        BdmJob bdmJob = getById(sqlParam.getBdmJobId());
        if (Objects.isNull(bdmJob)) {
            throw new DtvsAdminException("数据建模作业不存在");
        }
        String sql = processSqlContent(sqlParam.getSql());
        DataRegion dataRegion = getDataRegion(bdmJob);
        StorageBox storageBox = getStorageBox(bdmJob,dataRegion);

        String[] split = sql.split(";");
        sql = split[split.length - 1];
        if(sql.indexOf("CREATE TABLE ") >= 0 || sql.indexOf("create table ") >= 0 || sql.indexOf("DROP ") >= 0 || sql.indexOf("drop ") >= 0){
            throw new DtvsAdminException("CREATE语句无需执行，请直接保存");
//            if(!sqlParam.getSql().contains(tableName)){
//                throw new DtvsAdminException("目标表名需要与作业名称一致");
//            }
        }
        sqlParam.setSql(sql);
        sqlParam.setEnv(bdmJob.getEnv());
        sqlParam.setStorageId(String.valueOf(storageBox.getStorageId()));
        List<StoreInfo> storeInfos = new ArrayList<>();

        if(!CollectionUtils.isEmpty(sqlParam.getBdmInTableParamList()) && sqlParam.getBdmInTableParamList().size() > 0){
            ts = LocalDateTime.now().plusDays(-1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            log.info("DtvsAdminSelect exe ts info:{}", ts);
            for (BdmInTableParam bdmInTableParam : sqlParam.getBdmInTableParamList()) {
                StoreInfo storeInfo = new StoreInfo();
                TableStorage tableStorage = tableStorageService.getOne((Wrappers.<TableStorage>lambdaQuery().eq(TableStorage::getTableCode, bdmInTableParam.getTableCode())
                        .eq(TableStorage::getEnv, bdmInTableParam.getEnv())));
                if (Objects.nonNull(tableStorage)) {
                    storeInfo.setStorePath(tableStorage.getStorageName() + SqlGrammarConst.SLASH + ts);
                    storeInfo.setTableName(bdmInTableParam.getTableName());
                    storeInfos.add(storeInfo);
                }
            }
        }

        sqlParam.setStoreInfos(storeInfos);

        Object execute = execute(sqlParam);

        if(((ExeSqlRspPacket) execute).getMessage() != null){
            throw new DtvsAdminException(((ExeSqlRspPacket) execute).getMessage());
        }
        return execute;
    }

    public Object execute(SqlParam sqlParam) throws InterruptedException, PortConnectionException, NoPortNodeException, JsonProcessingException, InvalidCmdException, InvalidConnException {
        log.info("sqlParam:{}", ObjectMapperUtil.toJson(sqlParam));
        ExeSqlReqPacket exeSqlReqPacket = new ExeSqlReqPacket();
        exeSqlReqPacket.setExeEnv(sqlParam.getEnv().byteValue());
        exeSqlReqPacket.setStoreInfos(sqlParam.getStoreInfos());
        exeSqlReqPacket.setSql(sqlParam.getSql());
        exeSqlReqPacket.setCommandId(org.apex.dataverse.core.util.UCodeUtil.produce());
        exeSqlReqPacket.setSourceStorageId(Long.parseLong(sqlParam.getStorageId()));

        Request<Packet> request = new Request<>();
        request.setPacket(exeSqlReqPacket);

        Header header = Header.newHeader(CmdSet.EXE_SQL.getReq());
        request.setHeader(header);

        PortConnection conn = portExchanger.getConn(sqlParam.getStorageId());
        Response<Packet> response = null;
        try {
            response = conn.exe(request);
        } finally {
            portExchanger.close(conn);
        }
        return response.getPacket();
    }

    private void saveBdmOutTable(BdmJob bdmJob, List<DvsTableColumn> dvsTableColumnList, DataRegion dataRegion, StorageBox storageBox,String tableName,String sql) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        if (CollectionUtil.isNotEmpty(dvsTableColumnList)) {
            String bdmJobCode = bdmJob.getBdmJobCode();
            String tableCode = UCodeUtil.produce();

            BdmOutTable bdmOutTable = new BdmOutTable();
            bdmOutTable.setBdmJobCode(bdmJobCode);
            bdmOutTable.setTableCode(tableCode);
            bdmOutTable.setEnv(bdmJob.getEnv());
            bdmOutTable.setDataRegionCode(dataRegion.getDataRegionCode());
            bdmOutTable.setBdmOutType(1);
            bdmOutTable.setTableName(tableName);
            bdmOutTable.setTableAlias(tableName);
            bdmOutTable.setDescription(bdmJob.getDescription());
            bdmOutTable.setDwLayer(dataRegion.getDwLayer());
            bdmOutTable.setDwLayerDetail(dataRegion.getDwLayerDetail());
            bdmOutTable.setCreateTime(LocalDateTime.now());
            bdmOutTable.setUpdateTime(LocalDateTime.now());

            DvsTable dvsTable = new DvsTable();
            dvsTable.setTableCode(tableCode);
            dvsTable.setDvsCode(dataRegion.getDvsCode());
            dvsTable.setDataRegionCode(dataRegion.getDataRegionCode());
            dvsTable.setTableName(tableName);
            dvsTable.setTableAlias(tableName);
            dvsTable.setEnv(bdmJob.getEnv());
            dvsTable.setDwLayer(dataRegion.getDwLayer());
            dvsTable.setDwLayerDetail(DwLayerDetailEnum.getValueByDesc(dataRegion.getDwLayerDetail()));
            dvsTable.setIsStream(0);
            dvsTable.setCreateMode(1);
            dvsTable.setTableLifecycle(JobLifecycleEnum.ETL_DEVELOP.getValue());
            dvsTable.setDescription(bdmJob.getDescription());
            dvsTable.setCreateTime(LocalDateTime.now());
            dvsTable.setUpdateTime(LocalDateTime.now());
            dvsTable.setTenantId(userInfo.getTenantId());
            dvsTable.setTenantName(userInfo.getTenantName());
            dvsTable.setUserId(userInfo.getUserId());
            dvsTable.setUserName(userInfo.getUserName());
            dvsTable.setDeptId(userInfo.getDeptId());
            dvsTable.setDeptName(userInfo.getDeptName());

            TableStatistics tableStatistics = new TableStatistics();
            tableStatistics.setTableCode(tableCode);
            tableStatistics.setEnv(bdmJob.getEnv());
            tableStatistics.setUpdateTime(LocalDateTime.now());
            tableStatistics.setCreateTime(LocalDateTime.now());

            TableStorage tableStorage = new TableStorage();
            tableStorage.setTableCode(tableCode);
            tableStorage.setBoxCode(storageBox.getBoxCode());
            tableStorage.setEnv(bdmJob.getEnv());
            tableStorage.setDbname(dataRegion.getRegionName());
            tableStorage.setStorageName(storageBox.getBoxName() + SqlGrammarConst.SLASH + dataRegion.getRegionName().toLowerCase() + SqlGrammarConst.SLASH + tableName.toLowerCase());
            tableStorage.setStorageType(storageBox.getStorageType());
            tableStorage.setCreateTime(LocalDateTime.now());
            tableStorage.setLastUpdateTime(LocalDateTime.now());

            dvsTableColumnList.forEach(c -> {
                c.setColumnCode(UCodeUtil.produce());
                c.setTableCode(tableCode);
                c.setCreateTime(LocalDateTime.now());
                c.setUpdateTime(LocalDateTime.now());
            });

            DvsTableDdl dvsTableDdl = new DvsTableDdl();
            dvsTableDdl.setTableCode(tableCode);
            dvsTableDdl.setDdlText(sql);
            dvsTableDdl.setVersion(1);
            dvsTableDdl.setIsDeleted(IsDeletedEnum.NO.getValue());
            dvsTableDdl.setCreateTime(LocalDateTime.now());
            dvsTableDdl.setUpdateTime(LocalDateTime.now());

            // 保存BdmOutTable
            if (!bdmOutTableService.saveOrUpdate(bdmOutTable)) {
                throw new DtvsAdminException("保存数据建模作业输出表失败");
            }
            // 保存DvsTable
            if (!dvsTableService.saveOrUpdate(dvsTable)) {
                throw new DtvsAdminException("保存数据表失败");
            }
            // 保存DvsTableColumn
            if (!dvsTableColumnService.saveOrUpdateBatch(dvsTableColumnList)) {
                throw new DtvsAdminException("保存数据表对应列失败");
            }
            // 保存TableStatistic
            if (!tableStatisticsService.saveOrUpdate(tableStatistics)) {
                throw new DtvsAdminException("保存数据表对应表属性信息失败");
            }
            // 保存TableStorage
            if (!tableStorageService.saveOrUpdate(tableStorage)) {
                throw new DtvsAdminException("保存数据表对应表存储桶失败");
            }
            // 保存DvsTableDdl
            if(!dvsTableDdlService.saveOrUpdate(dvsTableDdl)){
                throw new DtvsAdminException("保存表的建表语句失败");
            }
        }
    }

    private void clearEditBdmJobRelation(BdmJob bdmJob) throws DtvsAdminException {
        Integer env = bdmJob.getEnv();
        List<String> tableCodes = new ArrayList<>();
        List<BdmOutTable> bdmOutTableList;
        // 删除BdmOutTable
        if (CollectionUtil.isNotEmpty(bdmOutTableList = bdmOutTableService.list(Wrappers.<BdmOutTable>lambdaQuery().eq(BdmOutTable::getBdmJobCode, bdmJob.getBdmJobCode()).
                eq(BdmOutTable::getEnv, env)))) {
            tableCodes.addAll(bdmOutTableList.stream().map(BdmOutTable::getTableCode).collect(Collectors.toList()));
            if (!bdmOutTableService.remove(Wrappers.<BdmOutTable>lambdaQuery().eq(BdmOutTable::getBdmJobCode, bdmJob.getBdmJobCode()).
                    eq(BdmOutTable::getEnv, env))) {
                throw new DtvsAdminException("删除数据建模作业输出表失败");
            }
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            // 删除DvsTable
            if (CollectionUtil.isNotEmpty(dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getEnv, env)))) {
                if (!dvsTableService.remove(Wrappers.<DvsTable>lambdaQuery().eq(DvsTable::getEnv, env)
                        .in(DvsTable::getTableCode, tableCodes))) {
                    throw new DtvsAdminException("删除数据表失败");
                }
            }
            // 删除DvsTableColumn
            if (CollectionUtil.isNotEmpty(dvsTableColumnService.list(Wrappers.<DvsTableColumn>lambdaQuery().eq(DvsTableColumn::getEnv, env).
                    in(DvsTableColumn::getTableCode, tableCodes)))) {
                if (!dvsTableColumnService.remove(Wrappers.<DvsTableColumn>lambdaQuery().in(DvsTableColumn::getTableCode, tableCodes).
                        eq(DvsTableColumn::getEnv, env))) {
                    throw new DtvsAdminException("删除数据表对应的列失败");
                }
            }
            // 删除旧的TableStorage
            if (CollectionUtil.isNotEmpty(tableStorageService.list(Wrappers.<TableStorage>lambdaQuery().eq(TableStorage::getEnv, env)
                    .in(TableStorage::getTableCode, tableCodes)))) {
                if (!tableStorageService.remove(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodes).
                        eq(TableStorage::getEnv, env))) {
                    throw new DtvsAdminException("删除数据表盒子失败");
                }
            }
            // 删除TableStatistic
            if (CollectionUtil.isNotEmpty(tableStatisticsService.list(Wrappers.<TableStatistics>lambdaQuery().eq(TableStatistics::getEnv, env)
                    .in(TableStatistics::getTableCode, tableCodes)))) {
                if (!tableStatisticsService.remove(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).
                        eq(TableStatistics::getEnv, env))) {
                    throw new DtvsAdminException("删除数据表属性失败");
                }
            }
            // 删除DvsTableDdl
            if (CollectionUtil.isNotEmpty(dvsTableDdlService.list(Wrappers.<DvsTableDdl>lambdaQuery().in(DvsTableDdl::getTableCode,tableCodes)))){
                if (!dvsTableDdlService.remove(Wrappers.<DvsTableDdl>lambdaQuery().in(DvsTableDdl::getTableCode, tableCodes))){
                    throw new DtvsAdminException("删除表的建表语句失败");
                }
            }
        }
    }

    public Object displayTableStructure(SqlParam sqlParam) throws InvalidCmdException, InterruptedException, InvalidConnException,
            PortConnectionException, NoPortNodeException, JsonProcessingException, SqlParseException, DtvsAdminException{
        List<BdmOutTable> bdmOutTableList;
        BdmJob bdmJob;
//        if (Objects.isNull(bdmJob = getById(sqlParam.getBdmJobId()))) {
//            throw new DtvsAdminException("数据建模作业不存在");
//        }
//        if (CollectionUtil.isEmpty(bdmOutTableList = bdmOutTableService.list(Wrappers.<BdmOutTable>lambdaQuery()
//                .eq(BdmOutTable::getBdmJobCode, bdmJob.getBdmJobCode()).eq(BdmOutTable::getEnv, bdmJob.getEnv())))){
//            throw new DtvsAdminException("该作业任务还未创建目标表");
//        }else{
            if(1 == 1){
//                if(sqlParam.getEngineType() == 1){
                //引擎为spark

                String sql = sqlParam.getSql();
                if (sql.contains(SqlGrammarConst.INSERT_OVERWRITE)) {
                    sql = sql.substring(sql.indexOf(SqlGrammarConst.SELECT_WITH_BLANK_LOWER));
                } else if (sql.contains(SqlGrammarConst.INSERT_INTO_LOWER)) {
                    sql = sql.substring(sql.indexOf(SqlGrammarConst.SELECT_WITH_BLANK_LOWER));
                }

                sqlParam.setSql("desc " + sql);
//                List<BdmInTableParam> list = new ArrayList<>();
//                BdmInTableParam bdmInTableParam = new BdmInTableParam();
//                bdmInTableParam.setTableName(bdmOutTableList.get(0).getTableName());
//                bdmInTableParam.setTableCode(bdmOutTableList.get(0).getTableCode());
//                bdmInTableParam.setEnv(bdmOutTableList.get(0).getEnv());
//                list.add(bdmInTableParam);
//                sqlParam.setBdmInTableParamList(list);
                return executeSql(sqlParam);
            }

//        }
        return null;
    }

}
