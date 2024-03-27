package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.constrant.SqlGrammarConst;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.*;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.EtlJobGroupMapper;
import org.apex.dataverse.mapper.EtlJobMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.*;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.JDBCUtils;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.utils.sql.HiveUtils;
import org.apex.dataverse.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 数据抽取转换加载作业，Extract Transform and Load 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Service
public class EtlJobServiceImpl extends ServiceImpl<EtlJobMapper, EtlJob> implements IEtlJobService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IDatasourceParentService datasourceParentService;

    @Autowired
    private IDatasourceService datasourceService;

    @Autowired
    private ITableExtractService tableExtractService;

    @Autowired
    private ITableTransformService tableTransformService;

    @Autowired
    private ITableLoadService tableLoadService;

    @Autowired
    private IDvsTableService dvsTableService;

    @Autowired
    private IDvsTableColumnService dvsTableColumnService;

    @Autowired
    private IDataRegionService dataRegionService;

    @Autowired
    private IStorageBoxService storageBoxService;

    @Autowired
    private ITableStorageService tableStorageService;

    @Autowired
    private ITableStatisticsService tableStatisticsService;

    @Autowired
    private IDvsTableDdlService dvsTableDdlService;

    @Autowired
    private IDeployJobService deployJobService;

    @Autowired
    private IJobInstanceService jobInstanceService;

    @Autowired
    private IDvsParentService dvsParentService;

    @Autowired
    private IDataTypeMapService dataTypeMapService;

    @Autowired
    private EtlJobGroupMapper etlJobGroupMapper;

    @Value("${nexus.odpc.file.location}")
    private String odpcFileLocation;


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long saveEtlJob(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        DataRegion dataRegion = validateSaveEtlJob(saveEtlJobParam);
        validateDuplicationSaveEtlJob(saveEtlJobParam, userInfo);
        // 选择的数据源
        Datasource datasource = getDatasource(saveEtlJobParam);
        // 存储桶
        StorageBox storageBox = getEtlJobStorageBox(saveEtlJobParam);
        // 设置数据集成编码
        saveEtlJobParam.setEtlJobCode(UCodeUtil.produce());
        // 设置数据集成作业状态
        saveEtlJobParam.setJobLifecycle(JobLifecycleEnum.ETL_DEVELOP.getValue());
        Long etlJobId = saveOrUpdateEtlJob(saveEtlJobParam, userInfo);
        // 填充tableCode columnCode编码 table_transform转换数据类型
        fillEtlJobParam(saveEtlJobParam, storageBox, datasource);
        // 保存数据抽取表 数据转换表 数据集成表 数据表 静态数据表 数据存储表
        // 保存数据抽取表
        saveTableExtract(saveEtlJobParam);
        // 保存数据转换表
        saveTableTransform(saveEtlJobParam);
        // 保存数据Load表
        saveTableLoad(saveEtlJobParam);
        // 保存数据集成表
        saveDvsTable(saveEtlJobParam, userInfo, storageBox, dataRegion);
        return etlJobId;
    }

    private Datasource getDatasource(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        Datasource datasource = datasourceService.getOne(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceCode, saveEtlJobParam.getDatasourceCode()).
                eq(Datasource::getEnv, saveEtlJobParam.getEnv()).eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(datasource)) {
            throw new DtvsAdminException("数据源不存在");
        }
        return datasource;
    }

    private StorageBox getEtlJobStorageBox(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        StorageBox storageBox = storageBoxService.getOne(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getDvsCode, saveEtlJobParam.getDvsCode()).
                eq(StorageBox::getDwLayer, DwLayerEnum.ODS.getValue()).eq(StorageBox::getEnv, saveEtlJobParam.getEnv()));
        if (Objects.isNull(storageBox)) {
            throw new DtvsAdminException("数据空间盒子不存在");
        }
        return storageBox;
    }

    private void saveDvsTable(EtlJobParam saveEtlJobParam, UserInfo userInfo, StorageBox storageBox, DataRegion dataRegion) throws DtvsAdminException {
        List<DvsTable> dvsTableList = new ArrayList<>();
        List<DvsTableColumn> dvsTableColumnList = new ArrayList<>();
        List<TableStatistics> statisticsList = new ArrayList<>();
        List<TableStorage> tableStorageList = new ArrayList<>();
        List<DvsTableDdl> dvsTableDdlList = new ArrayList<>();
        saveEtlJobParam.getEtlJobTableParamList().forEach(e -> {
            DvsTable dvsTable = mapperFactory.getMapperFacade().map(e.getDvsTableParam(), DvsTable.class);
            dvsTable.setTableLifecycle(saveEtlJobParam.getJobLifecycle());
            dvsTable.setCreateTime(LocalDateTime.now());
            dvsTable.setUpdateTime(LocalDateTime.now());
            dvsTable.setTenantId(userInfo.getTenantId());
            dvsTable.setTenantName(userInfo.getTenantName());
            dvsTable.setDeptId(userInfo.getDeptId());
            dvsTable.setDeptName(userInfo.getDeptName());
            dvsTable.setUserId(userInfo.getUserId());
            dvsTable.setUserName(userInfo.getUserName());
            dvsTable.setTableName(dvsTable.getTableName().toLowerCase());
            dvsTable.setTableAlias(dvsTable.getTableAlias().toLowerCase());
            dvsTableList.add(dvsTable);

            TableStatistics tableStatistics = mapperFactory.getMapperFacade().map(e.getDvsTableParam(), TableStatistics.class);
            tableStatistics.setCreateTime(LocalDateTime.now());
            tableStatistics.setUpdateTime(LocalDateTime.now());
            statisticsList.add(tableStatistics);

            TableStorage tableStorage = mapperFactory.getMapperFacade().map(e.getDvsTableParam(), TableStorage.class);
            tableStorage.setCreateTime(LocalDateTime.now());
            if (Objects.nonNull(storageBox.getStorageFormat())) {
                tableStorage.setStorageFormat(StorageFormatEnum.getDescByValue(storageBox.getStorageFormat()));
            }
            tableStorage.setBoxCode(storageBox.getBoxCode());
            tableStorage.setStorageType(storageBox.getStorageType());
            if (!storageBox.getStorageType().equals(StorageTypeEnum.HDFS.getDesc())) {
                tableStorage.setDbname(storageBox.getStorageName());
                tableStorage.setStorageName(dvsTable.getTableName());
            } else {
                tableStorage.setStorageName(storageBox.getBoxName() + SqlGrammarConst.SLASH + dataRegion.getRegionName().toLowerCase() + SqlGrammarConst.SLASH + dvsTable.getTableName().toLowerCase());
            }
            tableStorageList.add(tableStorage);

            DvsTableDdl dvsTableDdl = mapperFactory.getMapperFacade().map(e.getDvsTableParam(), DvsTableDdl.class);
            //  创建表DDL
            dvsTableDdl.setDdlText(getEtlJobTableDdlText(dvsTable.getTableName(), e.getTableTransformParamList(), storageBox, e.getTableLoadParam()));
            dvsTableDdl.setVersion(0);
            dvsTableDdl.setIsDeleted(IsDeletedEnum.NO.getValue());
            dvsTableDdl.setCreateTime(LocalDateTime.now());
            dvsTableDdl.setUpdateTime(LocalDateTime.now());
            dvsTableDdlList.add(dvsTableDdl);


            List<DvsTableColumn> dvsTableColumns = mapperFactory.getMapperFacade().mapAsList(e.getDvsTableParam().getDvsTableColumnParamList(), DvsTableColumn.class);
            dvsTableColumns.forEach(c -> {
                c.setCreateTime(LocalDateTime.now());
            });
            dvsTableColumnList.addAll(dvsTableColumns);
        });

        if (!dvsTableService.saveOrUpdateBatch(dvsTableList)) {
            throw new DtvsAdminException("保存数据集成表失败");
        }
        if (!dvsTableColumnService.saveOrUpdateBatch(dvsTableColumnList)) {
            throw new DtvsAdminException("保存数据集成表对应的列失败");
        }
        if (!tableStatisticsService.saveOrUpdateBatch(statisticsList)) {
            throw new DtvsAdminException("保存数据集成统计表失败");
        }
        if (!tableStorageService.saveOrUpdateBatch(tableStorageList)) {
            throw new DtvsAdminException("保存数据集成存储表失败");
        }
        if (!dvsTableDdlService.saveOrUpdateBatch(dvsTableDdlList)) {
            throw new DtvsAdminException("保存数据集成表建表语句表失败");
        }
    }

    private String getEtlJobTableDdlText(String tableName, List<TableTransformParam> tableTransformParamList, StorageBox storageBox, TableLoadParam tableLoadParam) {
        if (!storageBox.getStorageType().equals(StorageTypeEnum.HDFS.getDesc())) {
            return JDBCUtils.createTableDdl(storageBox.getStorageType(), tableName, tableTransformParamList, tableLoadParam);
        } else {
            // todo 默认hive
            return HiveUtils.createTableDdl(tableName, tableTransformParamList, tableLoadParam, storageBox);
        }
    }

    private void saveTableLoad(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        List<TableLoad> tableLoadList = new ArrayList<>();
        saveEtlJobParam.getEtlJobTableParamList().forEach(e -> {
            TableLoad tableLoad = mapperFactory.getMapperFacade().map(e.getTableLoadParam(), TableLoad.class);
            tableLoad.setCreateTime(LocalDateTime.now());
            tableLoad.setUpdateTime(LocalDateTime.now());
            tableLoadList.add(tableLoad);
        });
        if (!tableLoadService.saveOrUpdateBatch(tableLoadList)) {
            throw new DtvsAdminException("保存数据集成Load表失败");
        }
    }

    private void saveTableTransform(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        List<TableTransform> tableTransformList = new ArrayList<>();
        saveEtlJobParam.getEtlJobTableParamList().forEach(e -> {
            List<TableTransform> tableTransforms = mapperFactory.getMapperFacade().mapAsList(e.getTableTransformParamList(), TableTransform.class);
            tableTransforms.forEach(t -> {
                t.setCreateTime(LocalDateTime.now());
            });
            tableTransformList.addAll(tableTransforms);
        });
        if (!tableTransformService.saveOrUpdateBatch(tableTransformList)) {
            throw new DtvsAdminException("保存数据集成转换表失败");
        }
    }

    private void saveTableExtract(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        List<TableExtract> tableExtractList = new ArrayList<>();
        saveEtlJobParam.getEtlJobTableParamList().forEach(e -> {
            TableExtract tableExtract = mapperFactory.getMapperFacade().map(e.getTableExtractParam(), TableExtract.class);
            tableExtract.setCreateTime(LocalDateTime.now());
            tableExtract.setUpdateTime(LocalDateTime.now());
            tableExtractList.add(tableExtract);
        });
        if (!tableExtractService.saveOrUpdateBatch(tableExtractList)) {
            throw new DtvsAdminException("保存数据集成抽取表失败");
        }
    }

    private void fillEtlJobParam(EtlJobParam saveEtlJobParam, StorageBox storageBox, Datasource datasource) {
//        String etlGroupCode = saveEtlJobParam.getEtlGroupCode();
        String etlJobCode = saveEtlJobParam.getEtlJobCode();
        for (EtlJobTableParam etlJobTableParam : saveEtlJobParam.getEtlJobTableParamList()) {
            String tableCode = UCodeUtil.produce();
            // table_extract
            etlJobTableParam.getTableExtractParam().setEtlJobCode(etlJobCode);
            etlJobTableParam.getTableExtractParam().setTableCode(tableCode);
            // table_transform
            etlJobTableParam.getTableTransformParamList().forEach(t -> {
                t.setEtlJobCode(etlJobCode);
                t.setTableCode(tableCode);
                t.setOutDataTypeName(getOutDataType(t.getInDataTypeId(), t.getInDataTypeName(), t.getOutDataTypeName(), storageBox.getStorageType(), datasource, t.getTransform()));
            });
            // table_load
            etlJobTableParam.getTableLoadParam().setEtlJobCode(etlJobCode);
            etlJobTableParam.getTableLoadParam().setTableCode(tableCode);
            if (!storageBox.getStorageType().equals(StorageTypeEnum.HDFS.getDesc())) {
                etlJobTableParam.getTableLoadParam().setStorageName(etlJobTableParam.getTableLoadParam().getTableName());
                etlJobTableParam.getTableLoadParam().setStorageAlias(etlJobTableParam.getTableLoadParam().getTableName());
            } else {
                etlJobTableParam.getTableLoadParam().setStorageName(storageBox.getStorageName());
                etlJobTableParam.getTableLoadParam().setStorageAlias(storageBox.getStorageName());
            }
            // dvs_table
            etlJobTableParam.getDvsTableParam().setTableCode(tableCode);
            // dvs_table_column
            etlJobTableParam.getDvsTableParam().getDvsTableColumnParamList().forEach(c -> {
                c.setTableCode(tableCode);
                c.setColumnCode(UCodeUtil.produce());
            });
        }
    }

    private String getOutDataType(Integer inDataTypeId, String inDataTypeName, String outDataTypeName, String storageType, Datasource datasource, String transform) {
        if (!storageType.equals(StorageTypeEnum.HDFS.getDesc())) {
            return JDBCUtils.getOutColumnDataType(inDataTypeId, inDataTypeName, outDataTypeName, storageType, datasource, transform);
        } else {
            // todo 这里默认hive数据类型
            return HiveUtils.getOutColumnDataType(inDataTypeId, inDataTypeName, outDataTypeName, datasource, transform);
        }
    }


    private Long saveOrUpdateEtlJob(EtlJobParam saveEtlJobParam, UserInfo userInfo) throws DtvsAdminException {
        EtlJob etlJob = mapperFactory.getMapperFacade().map(saveEtlJobParam, EtlJob.class);
        if (Objects.isNull(saveEtlJobParam.getEtlJobId())) {
            etlJob.setCreateTime(LocalDateTime.now());
        }
        etlJob.setIsDeleted(IsDeletedEnum.NO.getValue());
        etlJob.setTenantId(userInfo.getTenantId());
        etlJob.setTenantName(userInfo.getTenantName());
        etlJob.setDeptId(userInfo.getDeptId());
        etlJob.setDeptName(userInfo.getDeptName());
        etlJob.setUserId(userInfo.getUserId());
        etlJob.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("保存数据集成失败");
        }
        return etlJob.getEtlJobId();
    }

    private void validateDuplicationSaveEtlJob(EtlJobParam saveEtlJobParam, UserInfo userInfo) throws DtvsAdminException {
        List<EtlJob> etlJobList = list(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobName, saveEtlJobParam.getEtlJobName()).
                eq(EtlJob::getTenantId, userInfo.getTenantId()).eq(EtlJob::getEnv, saveEtlJobParam.getEnv()));
        if (CollectionUtil.isNotEmpty(etlJobList)) {
            throw new DtvsAdminException("数据集成名称重复");
        }
    }

    private DataRegion validateSaveEtlJob(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        if (Objects.isNull(saveEtlJobParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (StringUtils.isBlank(saveEtlJobParam.getDvsCode())) {
            throw new DtvsAdminException("数据空间编码参数为空");
        }
        if (StringUtils.isBlank(saveEtlJobParam.getDataRegionCode())) {
            throw new DtvsAdminException("数据域编码参数为空");
        }
        if (StringUtils.isBlank(saveEtlJobParam.getEtlGroupCode())) {
            throw new DtvsAdminException("数据分组编码参数为空");
        }
        if (StringUtils.isBlank(saveEtlJobParam.getEtlJobName())) {
            throw new DtvsAdminException("数据集成名称参数为空");
        }
        if (Objects.isNull(saveEtlJobParam.getDatasourceCode())) {
            throw new DtvsAdminException("数据集成数据源编码参数为空");
        }
        if (Objects.isNull(saveEtlJobParam.getEnv())) {
            throw new DtvsAdminException("数据集成环境参数为空");
        }
        DataRegion dataRegion = dataRegionService.getOne(Wrappers.<DataRegion>lambdaQuery().eq(DataRegion::getDataRegionCode, saveEtlJobParam.getDataRegionCode()).eq(DataRegion::getEnv, saveEtlJobParam.getEnv()));
        if (Objects.isNull(dataRegion)) {
            throw new DtvsAdminException("数据域不存在，请重新选择数据域");
        }
        if (!dataRegion.getDvsCode().equals(saveEtlJobParam.getDvsCode())) {
            throw new DtvsAdminException("数据空间编码和数据域对应的数据空间编码不一致");
        }
        if (Objects.isNull(etlJobGroupMapper.selectOne(Wrappers.<EtlJobGroup>lambdaQuery().eq(EtlJobGroup::getEtlGroupCode, saveEtlJobParam.getEtlGroupCode()).eq(EtlJobGroup::getDataRegionCode, dataRegion.getDataRegionCode())))) {
            throw new DtvsAdminException("数据分组编码不存在");
        }

        if (CollectionUtil.isEmpty(saveEtlJobParam.getEtlJobTableParamList())) {
            throw new DtvsAdminException("数据集成表信息参数为空");
        }

        List<DataTypeMap> dataTypeMapList = dataTypeMapService.list();
        for (EtlJobTableParam etlJobTableParam : saveEtlJobParam.getEtlJobTableParamList()) {
            validateTableExtractParam(etlJobTableParam.getTableExtractParam(), saveEtlJobParam);
            validateTableTransformParam(etlJobTableParam.getTableTransformParamList(), saveEtlJobParam);
            validateTableLoadParam(etlJobTableParam.getTableLoadParam(), saveEtlJobParam);
            validateDvsTableParam(etlJobTableParam.getDvsTableParam(), saveEtlJobParam, dataRegion, dataTypeMapList);
        }

        return dataRegion;
    }

    private void validateTableLoadParam(TableLoadParam tableLoadParam, EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        if (Objects.isNull(tableLoadParam.getEnv())) {
            throw new DtvsAdminException("数据集成输出环境参数为空");
        }
        if (!tableLoadParam.getEnv().equals(saveEtlJobParam.getEnv())) {
            throw new DtvsAdminException("数据LOAD环境和数据集成环境不一致");
        }
        if (StringUtils.isBlank(tableLoadParam.getTableName())) {
            throw new DtvsAdminException("数据集成输出表名称为空");
        }
        if (StringUtils.isBlank(tableLoadParam.getPkField())) {
            throw new DtvsAdminException("数据集成输出表主键字段为空");
        }
    }

    private void validateTableExtractParam(TableExtractParam tableExtractParam, EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        if (Objects.isNull(tableExtractParam.getEnv())) {
            throw new DtvsAdminException("数据集成输入环境为空");
        }
        if (!tableExtractParam.getEnv().equals(saveEtlJobParam.getEnv())) {
            throw new DtvsAdminException("数据集成环境和数据抽取环境不一致");
        }
        if (StringUtils.isBlank(tableExtractParam.getOriginTableName())) {
            throw new DtvsAdminException("数据集成输入源表名称为空");
        }
        if (Objects.isNull(tableExtractParam.getIncrType())) {
            throw new DtvsAdminException("数据集成输入增量标记类型为空");
        }
        if (!IncrTypeEnum.existsValue(tableExtractParam.getIncrType())) {
            throw new DtvsAdminException("增量抽取类型不存在");
        }
    }

    private void validateDvsTableParam(DvsTableParam dvsTableParam, EtlJobParam saveEtlJobParam, DataRegion dataRegion, List<DataTypeMap> dataTypeMapList) throws DtvsAdminException {
        if (Objects.isNull(dvsTableParam)) {
            throw new DtvsAdminException("数据集成表参数为空");
        }
        if (StringUtils.isBlank(dvsTableParam.getDvsCode())) {
            throw new DtvsAdminException("数据空间编码为空");
        }
        if (!dvsTableParam.getDvsCode().equals(dataRegion.getDvsCode())) {
            throw new DtvsAdminException("数据集成空间编码与数据集成表数据空间编码不一致");
        }
        if (StringUtils.isBlank(dvsTableParam.getDataRegionCode())) {
            throw new DtvsAdminException("数据域编码为空");
        }
        if (!dvsTableParam.getDataRegionCode().equals(dataRegion.getDataRegionCode())) {
            throw new DtvsAdminException("数据集成数据域编码和数据集成表数据域编码不一致");
        }
        if (StringUtils.isBlank(dvsTableParam.getTableName())) {
            throw new DtvsAdminException("数据表名称为空");
        }
        if (StringUtils.isBlank(dvsTableParam.getTableAlias())) {
            throw new DtvsAdminException("数据表别名为空");
        }
        if (Objects.isNull(dvsTableParam.getEnv())) {
            throw new DtvsAdminException("数据表环境为空");
        }
        if (Objects.isNull(dvsTableParam.getDwLayer())) {
            throw new DtvsAdminException("数据表分层为空");
        }
        if (!dataRegion.getDwLayer().equals(dvsTableParam.getDwLayer())) {
            throw new DtvsAdminException("数据域分层和数据集成表分层不一致");
        }
        if (Objects.isNull(dvsTableParam.getDwLayerDetail())) {
            throw new DtvsAdminException("数据表分层明细为空");
        }
        if (!dataRegion.getDwLayerDetail().equals(DwLayerDetailEnum.getDescByValue(dvsTableParam.getDwLayerDetail()))) {
            throw new DtvsAdminException("数据域分层明细和数据集成表分层明细不一致");
        }
        if (Objects.isNull(dvsTableParam.getIsStream())) {
            throw new DtvsAdminException("数据表是否流式为空");
        }
        if (!IsStreamEnum.existsValue(dvsTableParam.getIsStream())) {
            throw new DtvsAdminException("流式创建类型不存在");
        }
        if (Objects.isNull(dvsTableParam.getCreateMode())) {
            throw new DtvsAdminException("数据表创建模式为空");
        }
        if (!TableCreateModeEnum.existsValue(dvsTableParam.getCreateMode())) {
            throw new DtvsAdminException("数据表创建模式不存在");
        }
//        if (Objects.isNull(dvsTableParam.getTableLifecycle())) {
//            throw new DtvsAdminException("数据表生命周期为空");
//        }
//        if (JobLifecycleEnum.exists(dvsTableParam.getTableLifecycle())) {
//            throw new DtvsAdminException("数据表生命周期类型不存在");
//        }
        validateDvsTableColumnParam(dvsTableParam.getDvsTableColumnParamList(), saveEtlJobParam, dataTypeMapList);
    }

    private void validateDvsTableColumnParam(List<DvsTableColumnParam> dvsTableColumnParamList, EtlJobParam saveEtlJobParam, List<DataTypeMap> dataTypeMapList) throws DtvsAdminException {
        for (DvsTableColumnParam dvsTableColumnParam : dvsTableColumnParamList) {
            if (StringUtils.isBlank(dvsTableColumnParam.getColumnName())) {
                throw new DtvsAdminException("数据表列名称为空");
            }
            if (StringUtils.isBlank(dvsTableColumnParam.getColumnAlias())) {
                throw new DtvsAdminException("数据表列别名为空");
            }
            if (Objects.isNull(dvsTableColumnParam.getEnv())) {
                throw new DtvsAdminException("数据表列环境为空");
            }
            if (!dvsTableColumnParam.getEnv().equals(saveEtlJobParam.getEnv())) {
                throw new DtvsAdminException("数据表列环境跟数据集成环境不一致");
            }
            if (Objects.isNull(dvsTableColumnParam.getDataTypeId())) {
                throw new DtvsAdminException("数据表列数据类型ID为空");
            }
            if (dataTypeMapList.stream().filter(d -> d.getDataTypeId().equals(dvsTableColumnParam.getDataTypeId())).count() <= 0) {
                throw new DtvsAdminException("数据表列类型不存在");
            }
        }
    }

    private void validateTableTransformParam(List<TableTransformParam> tableTransformParamList, EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        if (CollectionUtil.isNotEmpty(tableTransformParamList)) {
            for (TableTransformParam tableTransformParam : tableTransformParamList) {
                if (Objects.isNull(tableTransformParam.getEnv())) {
                    throw new DtvsAdminException("数据集成转换环境为空");
                }
                if (!saveEtlJobParam.getEnv().equals(tableTransformParam.getEnv())) {
                    throw new DtvsAdminException("数据转换环境和数据集成环境不一致");
                }
            }
        }
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editEtlJob(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        DataRegion dataRegion = validateSaveEtlJob(saveEtlJobParam);
        EtlJob etlJob = validateEditEtlJob(saveEtlJobParam, userInfo);
        // 选择的数据源
        Datasource datasource = getDatasource(saveEtlJobParam);
        // 存储桶
        StorageBox storageBox = getEtlJobStorageBox(saveEtlJobParam);
        // 设置数据集成编码
        saveEtlJobParam.setEtlJobCode(etlJob.getEtlJobCode());
        // 设置数据集成作业状态
        saveEtlJobParam.setJobLifecycle(etlJob.getJobLifecycle());
        Long etlJobId = saveOrUpdateEtlJob(saveEtlJobParam, userInfo);
        // 填充tableCode columnCode编码 table_transform类型转换
        fillEtlJobParam(saveEtlJobParam, storageBox, datasource);
        // 保存数据抽取表 数据转换表 数据集成表 数据表 静态数据表 数据存储表
        // 保存数据抽取表
        saveTableExtract(saveEtlJobParam);
        // 保存数据转换表
        saveTableTransform(saveEtlJobParam);
        // 保存数据Load表
        saveTableLoad(saveEtlJobParam);
        // 保存数据集成表
        saveDvsTable(saveEtlJobParam, userInfo, storageBox, dataRegion);
        return etlJobId;
    }

    private EtlJob validateEditEtlJob(EtlJobParam saveEtlJobParam, UserInfo userInfo) throws DtvsAdminException {
        EtlJob etlJob;
        if (Objects.isNull(saveEtlJobParam.getEtlJobId())) {
            throw new DtvsAdminException("数据集成ID为空");
        }
        if (Objects.isNull(etlJob = getById(saveEtlJobParam.getEtlJobId()))) {
            throw new DtvsAdminException("数据集成不存在");
        }
        if (etlJob.getJobLifecycle().equals(JobLifecycleEnum.ONLINE.getValue()) ||
                etlJob.getJobLifecycle().equals(JobLifecycleEnum.DOWN_LINE.getValue()) ||
                etlJob.getJobLifecycle().equals(JobLifecycleEnum.DEPLOY_PROD.getValue()) ||
                etlJob.getJobLifecycle().equals(JobLifecycleEnum.DEPLOY_PROD.getValue()) ||
                etlJob.getJobLifecycle().equals(JobLifecycleEnum.PROD_TEST.getValue()) ||
                etlJob.getJobLifecycle().equals(JobLifecycleEnum.PROD_TEST_END.getValue())) {
            throw new DtvsAdminException("数据集成状态不允许修改");
        }

        List<EtlJob> etlJobList = list(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobName, saveEtlJobParam.getEtlJobName()).
                eq(EtlJob::getTenantId, userInfo.getTenantId()).eq(EtlJob::getEnv, saveEtlJobParam.getEnv()));
        if (CollectionUtil.isNotEmpty(etlJobList)) {
            if (etlJobList.stream().filter(e -> !e.getEtlJobId().equals(etlJob.getEtlJobId())).count() > 0) {
                throw new DtvsAdminException("数据集成名称重复");
            }
        }
        clearEditEtlJobRelation(etlJob);
        return etlJob;
    }

    private void clearEditEtlJobRelation(EtlJob etlJob) throws DtvsAdminException {
        Integer env = etlJob.getEnv();
        List<String> tableCodes = new ArrayList<>();
        List<TableExtract> tableExtractList;
        List<TableLoad> tableLoadList;
        // 删除旧的TableExtract
        if (CollectionUtil.isNotEmpty(tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJob.getEtlJobCode()).
                eq(TableExtract::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
            if (!tableExtractService.remove(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJob.getEtlJobCode()).
                    eq(TableExtract::getEnv, env))) {
                throw new DtvsAdminException("删除数据集成输入表失败");
            }
        }
        // 删除旧的TableTransform
        if (CollectionUtil.isNotEmpty(tableTransformService.list(Wrappers.<TableTransform>lambdaQuery().eq(TableTransform::getEtlJobCode, etlJob.getEtlJobCode()).
                eq(TableTransform::getEnv, env)))) {
            if (!tableTransformService.remove(Wrappers.<TableTransform>lambdaQuery().eq(TableTransform::getEtlJobCode, etlJob.getEtlJobCode()).
                    eq(TableTransform::getEnv, env))) {
                throw new DtvsAdminException("删除数据集成转换表失败");
            }
        }
        // 删除旧的TableLoad
        if (CollectionUtil.isNotEmpty(tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJob.getEtlJobCode()).
                eq(TableLoad::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
            if (!tableLoadService.remove(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJob.getEtlJobCode()).
                    eq(TableLoad::getEnv, env))) {
                throw new DtvsAdminException("删除数据集成输出表失败");
            }
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            // 删除旧的DvsTable
            if (CollectionUtil.isNotEmpty(dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getEnv, env)))) {
                if (!dvsTableService.remove(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                        eq(DvsTable::getEnv, env))) {
                    throw new DtvsAdminException("删除数据集成表失败");
                }
            }
            // 删除旧的DvsTableColumn
            if (CollectionUtil.isNotEmpty(dvsTableColumnService.list(Wrappers.<DvsTableColumn>lambdaQuery().in(DvsTableColumn::getTableCode, tableCodes).
                    eq(DvsTableColumn::getEnv, env)))) {
                if (!dvsTableColumnService.remove(Wrappers.<DvsTableColumn>lambdaQuery().in(DvsTableColumn::getTableCode, tableCodes).
                        eq(DvsTableColumn::getEnv, env))) {
                    throw new DtvsAdminException("删除数据集成表对应的列失败");
                }
            }
            // 删除旧的TableStorage
            if (CollectionUtil.isNotEmpty(tableStorageService.list(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodes).
                    eq(TableStorage::getEnv, env)))) {
                if (!tableStorageService.remove(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodes).
                        eq(TableStorage::getEnv, env))) {
                    throw new DtvsAdminException("删除数据集成表盒子失败");
                }
            }
            // 删除旧的TableStatistic
            if (CollectionUtil.isNotEmpty(tableStatisticsService.list(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).
                    eq(TableStatistics::getEnv, env)))) {
                if (!tableStatisticsService.remove(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).
                        eq(TableStatistics::getEnv, env))) {
                    throw new DtvsAdminException("删除数据集成表属性失败");
                }
            }
        }
    }

    @Override
    public PageResult<EtlJobVO> pageEtlJob(PageEtlJobParam pageEtlJobParam) throws DtvsAdminException {
        validatePageEtlJob(pageEtlJobParam);
        IPage<EtlJob> iPage = getIPage(pageEtlJobParam);
        PageResult<EtlJobVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), EtlJobVO.class));
        buildEtlJobVO(pageResult.getList());
        return pageResult;
    }

    private void buildEtlJobVO(List<EtlJobVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> etlJobCodeList = list.stream().map(EtlJobVO::getEtlJobCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(etlJobCodeList)) {
                // 数据集成抽取表
                List<TableExtract> tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().in(TableExtract::getEtlJobCode, etlJobCodeList));
                // 查询数据集成发布记录
                List<DeployJob> deployJobList = deployJobService.list(Wrappers.<DeployJob>lambdaQuery().in(DeployJob::getJobCode, etlJobCodeList));
                // 查询数据集成运行结果
                List<JobInstance> jobInstanceList = jobInstanceService.list(Wrappers.<JobInstance>lambdaQuery().in(JobInstance::getJobCode, etlJobCodeList));
                if (CollectionUtil.isNotEmpty(tableExtractList)) {
                    Map<String, Long> tableExtractMap = tableExtractList.stream().collect(Collectors.groupingBy(t -> t.getEtlJobCode() + t.getEnv(), Collectors.counting()));
                    if (CollectionUtil.isNotEmpty(tableExtractMap)) {
                        for (EtlJobVO etlJobVO : list) {
                            if (Objects.nonNull(tableExtractMap.get(etlJobVO.getEtlJobCode() + etlJobVO.getEnv()))) {
                                etlJobVO.setTableCount(tableExtractMap.get(etlJobVO.getEtlJobCode() + etlJobVO.getEnv()).intValue());
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(deployJobList)) {
                    for (EtlJobVO etlJobVO : list) {
                        DeployJob deployJob = deployJobList.stream().filter(d -> d.getJobCode().equals(etlJobVO.getEtlJobCode()) && d.getEnv().intValue() == etlJobVO.getEnv().intValue()).findFirst().orElse(null);
                        if (Objects.nonNull(deployJob)) {
                            etlJobVO.setDeployStatus(EnvEnum.getEnvDescByValue(deployJob.getEnv()));
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(jobInstanceList)) {
                    for (EtlJobVO etlJobVO : list) {
                        JobInstance jobInstance = jobInstanceList.stream().filter(d -> d.getJobCode().equals(etlJobVO.getEtlJobCode()) && d.getEnv().intValue() == etlJobVO.getEnv().intValue()).findFirst().orElse(null);
                        if (Objects.nonNull(jobInstance)) {
                            etlJobVO.setExeResult(jobInstance.getExeResult());
                        }
                    }
                }
            }
        }
    }

    private IPage<EtlJob> getIPage(PageEtlJobParam pageEtlJobParam) {
        //默认单页10条记录
        Page<EtlJob> page = new Page<>();
        PageQueryParam pageQueryParam = pageEtlJobParam.getPageQueryParam();
        if (pageQueryParam != null && pageQueryParam.getSize() != null) {
            page.setSize(pageQueryParam.getSize());
        } else {
            page.setSize(10);
        }

        if (pageQueryParam != null && pageQueryParam.getPageNo() != null) {
            page.setCurrent(pageQueryParam.getPageNo());
        } else {
            page.setCurrent(1);
        }
        if (pageQueryParam != null) {
            if (pageQueryParam.getDescs() != null) {
                page.setDescs(Optional.of(pageQueryParam.getDescs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
            if (pageQueryParam.getAscs() != null) {
                page.setAscs(Optional.of(pageQueryParam.getAscs()).get().stream().filter(Objects::nonNull).map(a -> com.baomidou.mybatisplus.core.toolkit.StringUtils.camelToUnderline(a)).collect(Collectors.toList()));
            }
        }
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        LambdaQueryWrapper<EtlJob> queryWrapper = Wrappers.lambdaQuery();
        // 根据数据类型查询
        if (Objects.nonNull(pageEtlJobParam.getDatasourceTypeId())) {
            List<DatasourceParent> datasourceParentList = datasourceParentService.list(Wrappers.<DatasourceParent>lambdaQuery().
                    eq(DatasourceParent::getDatasourceTypeId, pageEtlJobParam.getDatasourceTypeId()).
                    eq(DatasourceParent::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(datasourceParentList)) {
                queryWrapper.in(EtlJob::getDatasourceCode, datasourceParentList.stream().map(DatasourceParent::getDatasourceCode).collect(Collectors.toList()));
            }
        }
        // 根据集成任务的分组查询
        if (StringUtils.isNotBlank(pageEtlJobParam.getGroupCode())) {
            queryWrapper.eq(EtlJob::getEtlGroupCode, pageEtlJobParam.getGroupCode());
        }
        // 根据运行环境查询
        if (Objects.nonNull(pageEtlJobParam.getEnv())) {
            queryWrapper.eq(EtlJob::getEnv, pageEtlJobParam.getEnv());
        }
        // 根据发布状态查询
        if (Objects.nonNull(pageEtlJobParam.getJobLifecycle())) {
            queryWrapper.eq(EtlJob::getJobLifecycle, pageEtlJobParam.getJobLifecycle());
        }
        // todo 根据运行状态 这个字段目前数据库没有设置后续加上
        if (Objects.nonNull(pageEtlJobParam.getEtlResultStatus())) {
//            queryWrapper.eq(EtlJob::getEtlResultStatus, pageEtlJobParam.getEtlResultStatus());
        }
        // 搜索关键词 ETL作业ID/ETL作业名称
        if (StringUtils.isNotEmpty(pageEtlJobParam.getKeyword())) {
            if (pageEtlJobParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(EtlJob::getEtlJobId, pageEtlJobParam.getKeyword());
            } else {
                queryWrapper.like(EtlJob::getEtlJobName, pageEtlJobParam.getKeyword());
            }

        }
        queryWrapper.eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue());
        return page(page, queryWrapper);
    }

    private void validatePageEtlJob(PageEtlJobParam pageEtlJobParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageEtlJobParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageEtlJobParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    public PageResult<EtlJobVO> pageTreeByGroupCode(PageTreeByGroupCodeParam pageTreeByGroupCodeParam) {
        return null;
    }

    @Override
    public EtlJobVO detailEtlJobVO(Long etlJobId) throws DtvsAdminException {
        EtlJobVO etlJobVO = mapperFactory.getMapperFacade().map(validateEtlJobExists(etlJobId), EtlJobVO.class);
        String etlJobCode = etlJobVO.getEtlJobCode();
        Integer env = etlJobVO.getEnv();
        validateEtlJobCodeAndEnv(etlJobCode, env);
        List<String> tableCodes = null;
        List<DvsTable> dvsTableList = null;
        List<DvsTableColumn> dvsTableColumnList = null;
        List<TableStatistics> statisticsList = null;
        List<TableStorage> tableStorageList = null;
        List<DvsTableDdl> dvsTableDdlList = null;
        List<TableExtract> tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).eq(TableExtract::getEnv, env));
        List<TableLoad> tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).eq(TableLoad::getEnv, env));
        List<TableTransform> tableTransformList = tableTransformService.list(Wrappers.<TableTransform>lambdaQuery().eq(TableTransform::getEtlJobCode, etlJobCode).eq(TableTransform::getEnv, env));

        if (CollectionUtil.isNotEmpty(tableExtractList)) {
            tableCodes = tableExtractList.stream().map(TableExtract::getTableCode).distinct().collect(Collectors.toList());
        } else {
            throw new DtvsAdminException("数据集成对应表编码为空");
        }

        if (CollectionUtil.isNotEmpty(tableCodes)) {
            dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).eq(DvsTable::getEnv, env));
            dvsTableColumnList = dvsTableColumnService.list(Wrappers.<DvsTableColumn>lambdaQuery().in(DvsTableColumn::getTableCode, tableCodes).eq(DvsTableColumn::getEnv, env));
            statisticsList = tableStatisticsService.list(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).eq(TableStatistics::getEnv, env));
            tableStorageList = tableStorageService.list(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodes).eq(TableStorage::getEnv, env));
            dvsTableDdlList = dvsTableDdlService.list(Wrappers.<DvsTableDdl>lambdaQuery().in(DvsTableDdl::getTableCode, tableCodes));
        }
        List<EtlJobTableVO> etlJobTableVOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dvsTableList)) {
            for (DvsTable dvsTable : dvsTableList) {
                EtlJobTableVO etlJobTableVO = new EtlJobTableVO();
                String tableCode = dvsTable.getTableCode();
                if (CollectionUtil.isNotEmpty(tableExtractList)) {
                    TableExtract tableExtract = tableExtractList.stream().filter(t -> t.getEtlJobCode().equals(etlJobCode) && t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).findFirst().orElse(null);
                    if (Objects.nonNull(tableExtract)) {
                        etlJobTableVO.setTableExtractVO(mapperFactory.getMapperFacade().map(tableExtract, TableExtractVO.class));
                    }
                }
                if (CollectionUtil.isNotEmpty(tableTransformList)) {
                    List<TableTransform> tableTransforms = tableTransformList.stream().filter(t -> t.getEtlJobCode().equals(etlJobCode) && t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(tableTransforms)) {
                        etlJobTableVO.setTableTransformVOList(mapperFactory.getMapperFacade().mapAsList(tableTransforms, TableTransformVO.class));
                    }
                }
                if (CollectionUtil.isNotEmpty(tableLoadList)) {
                    TableLoad tableLoad = tableLoadList.stream().filter(t -> t.getEtlJobCode().equals(etlJobCode) && t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).findFirst().orElse(null);
                    if (Objects.nonNull(tableLoad)) {
                        etlJobTableVO.setTableLoadVO(mapperFactory.getMapperFacade().map(tableLoad, TableLoadVO.class));
                    }
                }
                DvsTableVO dvsTableVO = mapperFactory.getMapperFacade().map(dvsTable, DvsTableVO.class);
                if (CollectionUtil.isNotEmpty(dvsTableColumnList)) {
                    List<DvsTableColumn> dvsTableColumns = dvsTableColumnList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).collect(Collectors.toList());
                    List<DvsTableColumnVO> dvsTableColumnVOList = mapperFactory.getMapperFacade().mapAsList(dvsTableColumns, DvsTableColumnVO.class);
                    dvsTableVO.setDvsTableColumnVOList(dvsTableColumnVOList);
                }
                etlJobTableVO.setDvsTableVO(dvsTableVO);

                if (CollectionUtil.isNotEmpty(statisticsList)) {
                    TableStatistics tableStatistics = statisticsList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).findFirst().orElse(null);
                    if (Objects.nonNull(tableStatistics)) {
                        etlJobTableVO.setTableStatisticsVO(mapperFactory.getMapperFacade().map(tableStatistics, TableStatisticsVO.class));
                    }
                }
                if (CollectionUtil.isNotEmpty(tableStorageList)) {
                    TableStorage tableStorage = tableStorageList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().equals(env)).findFirst().orElse(null);
                    if (Objects.nonNull(tableStorage)) {
                        etlJobTableVO.setTableStorageVO(mapperFactory.getMapperFacade().map(tableStorage, TableStorageVO.class));
                    }
                }
                if (CollectionUtil.isNotEmpty(dvsTableDdlList)) {
                    DvsTableDdl dvsTableDdl = dvsTableDdlList.stream().filter(t -> t.getTableCode().equals(tableCode)).findFirst().orElse(null);
                    if (Objects.nonNull(dvsTableDdl)) {
                        etlJobTableVO.setDvsTableDdlVO(mapperFactory.getMapperFacade().map(dvsTableDdl, DvsTableDdlVO.class));
                    }
                }


                etlJobTableVOList.add(etlJobTableVO);
            }

            etlJobVO.setEtlJobTableVOList(etlJobTableVOList);
        }

        return etlJobVO;
    }

    private List<DvsTableVO> getDvsTableVOList(List<DvsTable> DvsTableList, Map<String, List<DvsTableColumnVO>> DvsTableColumnMap) {
        if (CollectionUtil.isNotEmpty(DvsTableList)) {
            List<DvsTableVO> dvsTableVOList = mapperFactory.getMapperFacade().mapAsList(DvsTableList, DvsTableVO.class);
            for (DvsTableVO DvsTableVO : dvsTableVOList) {
                DvsTableVO.setDvsTableColumnVOList(DvsTableColumnMap.get(DvsTableVO.getTableCode()));
            }
        }
        return null;
    }


    private List<TableLoadVO> getEtlJobTableLoadVOList(List<TableLoad> tableLoadList, Map<String, List<TableTransformVO>> tableTransformListMap) {
        if (CollectionUtil.isNotEmpty(tableLoadList)) {
            List<TableLoadVO> tableLoadVOList = mapperFactory.getMapperFacade().mapAsList(tableLoadList, TableLoadVO.class);
            for (TableLoadVO tableLoadVO : tableLoadVOList) {
                if (CollectionUtil.isNotEmpty(tableTransformListMap)) {
                    tableLoadVO.setTableTransformVOList(tableTransformListMap.get(tableLoadVO.getTableCode()));
                }
            }
            return tableLoadVOList;
        }
        return null;
    }

    private List<TableExtractVO> getEtlJobTableExtractVOList(List<TableExtract> tableExtractList, Map<String, List<TableTransformVO>> tableTransformListMap) {
        if (CollectionUtil.isNotEmpty(tableExtractList)) {
            List<TableExtractVO> tableExtractVOList = mapperFactory.getMapperFacade().mapAsList(tableExtractList, TableExtractVO.class);
            for (TableExtractVO tableExtractVO : tableExtractVOList) {
                if (CollectionUtil.isNotEmpty(tableTransformListMap)) {
                    tableExtractVO.setTableTransformVOList(tableTransformListMap.get(tableExtractVO.getTableCode()));
                }
            }
            return tableExtractVOList;
        }
        return null;
    }

    private void validateEtlJobCodeAndEnv(String etlJobCode, Integer env) throws DtvsAdminException {
        if (StringUtils.isBlank(etlJobCode)) {
            throw new DtvsAdminException("数据集成编码为空");
        }
        if (Objects.isNull(env)) {
            throw new DtvsAdminException("数据集成环境为空");
        }
    }

    private EtlJob validateEtlJobExists(Long etlJobId) throws DtvsAdminException {
        if (Objects.isNull(etlJobId)) {
            throw new DtvsAdminException("数据集成ID参数为空");
        }
        EtlJob etlJob = getOne(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobId, etlJobId).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(etlJob)) {
            throw new DtvsAdminException("数据集成不存在");
        }
        return etlJob;
    }

    @Override
    public Boolean setStatusDevelop(Long etlJobId) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<String> tableCodes = new ArrayList<>();
        List<TableExtract> tableExtractList;
        List<TableLoad> tableLoadList;
        EtlJob etlJob = validateEtlJobExists(etlJobId);
        String etlJobCode = etlJob.getEtlJobCode();
        Integer env = etlJob.getEnv();
//        if (!etlJob.getJobLifecycle().equals(JobLifecycleEnum.ETL_DEVELOP_END)) {
//            throw new DtvsAdminException("数据集成状态只能从新建状态到开发状态");
//        }
//        etlJob.setJobLifecycle(JobLifecycleEnum.ETL_DEVELOP_END.getValue());
        etlJob.setUpdateTime(LocalDateTime.now());
        etlJob.setTenantId(userInfo.getTenantId());
        etlJob.setTenantName(userInfo.getTenantName());
        etlJob.setDeptId(userInfo.getDeptId());
        etlJob.setDeptName(userInfo.getDeptName());
        etlJob.setUserId(userInfo.getUserId());
        etlJob.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("更新数据集成状态为开发失败");
        }
        if (CollectionUtil.isNotEmpty(tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).
                eq(TableExtract::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).
                eq(TableLoad::getEnv, env)))) {
            tableCodes.addAll(tableLoadList.stream().map(TableLoad::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(dvsTableList)) {
                dvsTableList.forEach(t -> {
                    t.setUpdateTime(LocalDateTime.now());
                    t.setTableLifecycle(JobLifecycleEnum.ETL_DEVELOP.getValue());
                    t.setTenantId(userInfo.getTenantId());
                    t.setTenantName(userInfo.getTenantName());
                    t.setDeptId(userInfo.getDeptId());
                    t.setDeptName(userInfo.getDeptName());
                    t.setUserId(userInfo.getUserId());
                    t.setUserName(userInfo.getUserName());
                });
                if (!dvsTableService.saveOrUpdateBatch(dvsTableList)) {
                    throw new DtvsAdminException("更新数据集成表状态失败");
                }
            }

        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setStatusSchedule(Long etlJobId) {
        return null;
    }

    @Override
    public Boolean setStatusTest(Long etlJobId) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<String> tableCodes = new ArrayList<>();
        List<TableExtract> tableExtractList;
        List<TableLoad> tableLoadList;
        EtlJob etlJob = validateEtlJobExists(etlJobId);
        String etlJobCode = etlJob.getEtlJobCode();
        Integer env = etlJob.getEnv();
        if (!etlJob.getJobLifecycle().equals(JobLifecycleEnum.ETL_DEVELOP_END)) {
            throw new DtvsAdminException("数据集成状态只能从开发状态到测试状态");
        }
        etlJob.setJobLifecycle(JobLifecycleEnum.ETL_TEST.getValue());
        etlJob.setUpdateTime(LocalDateTime.now());
        etlJob.setTenantId(userInfo.getTenantId());
        etlJob.setTenantName(userInfo.getTenantName());
        etlJob.setDeptId(userInfo.getDeptId());
        etlJob.setDeptName(userInfo.getDeptName());
        etlJob.setUserId(userInfo.getUserId());
        etlJob.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("更新数据集成状态为测试失败");
        }
        if (CollectionUtil.isNotEmpty(tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).
                eq(TableExtract::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).
                eq(TableLoad::getEnv, env)))) {
            tableCodes.addAll(tableLoadList.stream().map(TableLoad::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(dvsTableList)) {
                dvsTableList.forEach(t -> {
                    t.setUpdateTime(LocalDateTime.now());
                    t.setTableLifecycle(JobLifecycleEnum.ETL_TEST.getValue());
                    t.setTenantId(userInfo.getTenantId());
                    t.setTenantName(userInfo.getTenantName());
                    t.setDeptId(userInfo.getDeptId());
                    t.setDeptName(userInfo.getDeptName());
                    t.setUserId(userInfo.getUserId());
                    t.setUserName(userInfo.getUserName());
                });
                if (!dvsTableService.saveOrUpdateBatch(dvsTableList)) {
                    throw new DtvsAdminException("更新数据集成表状态失败");
                }
            }

        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setStatusOnline(Long etlJobId) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<String> tableCodes = new ArrayList<>();
        List<TableExtract> tableExtractList;
        List<TableLoad> tableLoadList;
        EtlJob etlJob = validateEtlJobExists(etlJobId);
        String etlJobCode = etlJob.getEtlJobCode();
        Integer env = etlJob.getEnv();
        if (!etlJob.getJobLifecycle().equals(JobLifecycleEnum.ETL_TEST_END)) {
            throw new DtvsAdminException("数据集成状态只能从测试结束状态到上线状态");
        }
        etlJob.setJobLifecycle(JobLifecycleEnum.DEPLOY_PROD.getValue());
        etlJob.setUpdateTime(LocalDateTime.now());
        etlJob.setTenantId(userInfo.getTenantId());
        etlJob.setTenantName(userInfo.getTenantName());
        etlJob.setDeptId(userInfo.getDeptId());
        etlJob.setDeptName(userInfo.getDeptName());
        etlJob.setUserId(userInfo.getUserId());
        etlJob.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("更新数据集成状态为上线失败");
        }
        if (CollectionUtil.isNotEmpty(tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).
                eq(TableExtract::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).
                eq(TableLoad::getEnv, env)))) {
            tableCodes.addAll(tableLoadList.stream().map(TableLoad::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(dvsTableList)) {
                dvsTableList.forEach(t -> {
                    t.setUpdateTime(LocalDateTime.now());
                    t.setTableLifecycle(JobLifecycleEnum.ONLINE.getValue());
                    t.setTenantId(userInfo.getTenantId());
                    t.setTenantName(userInfo.getTenantName());
                    t.setDeptId(userInfo.getDeptId());
                    t.setDeptName(userInfo.getDeptName());
                    t.setUserId(userInfo.getUserId());
                    t.setUserName(userInfo.getUserName());
                });
                if (!dvsTableService.saveOrUpdateBatch(dvsTableList)) {
                    throw new DtvsAdminException("更新数据集成表状态失败");
                }
            }

        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean setStatusDownLine(Long etlJobId) throws DtvsAdminException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        List<String> tableCodes = new ArrayList<>();
        List<TableExtract> tableExtractList;
        List<TableLoad> tableLoadList;
        EtlJob etlJob = validateEtlJobExists(etlJobId);
        String etlJobCode = etlJob.getEtlJobCode();
        Integer env = etlJob.getEnv();
        if (!etlJob.getJobLifecycle().equals(JobLifecycleEnum.ONLINE)) {
            throw new DtvsAdminException("数据集成状态只能从上线状态到下线状态");
        }
        etlJob.setJobLifecycle(JobLifecycleEnum.DOWN_LINE.getValue());
        etlJob.setUpdateTime(LocalDateTime.now());
        etlJob.setTenantId(userInfo.getTenantId());
        etlJob.setTenantName(userInfo.getTenantName());
        etlJob.setDeptId(userInfo.getDeptId());
        etlJob.setDeptName(userInfo.getDeptName());
        etlJob.setUserId(userInfo.getUserId());
        etlJob.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("更新数据集成状态为下线失败");
        }
        if (CollectionUtil.isNotEmpty(tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).
                eq(TableExtract::getEnv, env)))) {
            tableCodes.addAll(tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).
                eq(TableLoad::getEnv, env)))) {
            tableCodes.addAll(tableLoadList.stream().map(TableLoad::getTableCode).collect(Collectors.toList()));
        }
        if (CollectionUtil.isNotEmpty(tableCodes)) {
            tableCodes = tableCodes.stream().distinct().collect(Collectors.toList());
            List<DvsTable> dvsTableList = dvsTableService.list(Wrappers.<DvsTable>lambdaQuery().in(DvsTable::getTableCode, tableCodes).
                    eq(DvsTable::getTenantId, userInfo.getTenantId()));
            if (CollectionUtil.isNotEmpty(dvsTableList)) {
                dvsTableList.forEach(t -> {
                    t.setUpdateTime(LocalDateTime.now());
                    t.setTableLifecycle(JobLifecycleEnum.DOWN_LINE.getValue());
                    t.setTenantId(userInfo.getTenantId());
                    t.setTenantName(userInfo.getTenantName());
                    t.setDeptId(userInfo.getDeptId());
                    t.setDeptName(userInfo.getDeptName());
                    t.setUserId(userInfo.getUserId());
                    t.setUserName(userInfo.getUserName());
                });
                if (!dvsTableService.saveOrUpdateBatch(dvsTableList)) {
                    throw new DtvsAdminException("更新数据集成表状态失败");
                }
            }

        }
        return Boolean.TRUE;
    }

    @Override
    public List<EnumInfoVO> isStreamEnum() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (IsStreamEnum enumInfo : IsStreamEnum.values()) {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(enumInfo.getValue());
            enumInfoVO.setDesc(enumInfo.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }
        return enumInfoVOList;
    }

    @Override
    public List<EnumInfoVO> createModeEnum() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (TableCreateModeEnum enumInfo : TableCreateModeEnum.values()) {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(enumInfo.getValue());
            enumInfoVO.setDesc(enumInfo.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }
        return enumInfoVOList;
    }

    @Override
    public List<EnumInfoVO> jobLifecycleEnum() {
        List<EnumInfoVO> enumInfoVOList = new ArrayList<>();
        for (JobLifecycleEnum enumInfo : JobLifecycleEnum.values()) {
            EnumInfoVO enumInfoVO = new EnumInfoVO();
            enumInfoVO.setValue(enumInfo.getValue());
            enumInfoVO.setDesc(enumInfo.getDesc());
            enumInfoVOList.add(enumInfoVO);
        }
        return enumInfoVOList;
    }

    @Override
    public Long updateEtlJobName(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        validateUpdateEtlJobName(saveEtlJobParam);
        EtlJob etlJob = new EtlJob();
        etlJob.setEtlJobId(saveEtlJobParam.getEtlJobId());
        etlJob.setEtlJobName(saveEtlJobParam.getEtlJobName());
        if (!saveOrUpdate(etlJob)) {
            throw new DtvsAdminException("编辑数据集成任务名称失败");
        }
        return saveEtlJobParam.getEtlJobId();
    }

    private void validateUpdateEtlJobName(EtlJobParam saveEtlJobParam) throws DtvsAdminException {
        EtlJob etlJob;
        if (Objects.isNull(saveEtlJobParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(saveEtlJobParam.getEtlJobId())) {
            throw new DtvsAdminException("集成任务作业ID参数为空");
        }
        if (StringUtils.isBlank(saveEtlJobParam.getEtlJobName())) {
            throw new DtvsAdminException("集成任务作业名称为空");
        }
        if (Objects.isNull(etlJob = getOne(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobId, saveEtlJobParam.getEtlJobId()).eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("集成任务不存在");
        }
        EtlJob nameEtlJob = getOne(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobName, saveEtlJobParam.getEtlJobName()).
                eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()).eq(EtlJob::getEnv, etlJob.getEnv()).
                eq(EtlJob::getDvsCode, etlJob.getDvsCode()).eq(EtlJob::getDataRegionCode, etlJob.getDataRegionCode()));
        if (Objects.nonNull(nameEtlJob)) {
            if (nameEtlJob.getEtlJobId().longValue() != etlJob.getEtlJobId().longValue()) {
                throw new DtvsAdminException("集成任务名称已存在");
            }
        }

    }
}
