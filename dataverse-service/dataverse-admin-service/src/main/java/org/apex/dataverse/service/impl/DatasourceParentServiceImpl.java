package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.dto.DatasourceColumnDTO;
import org.apex.dataverse.dto.DatasourceParentDTO;
import org.apex.dataverse.dto.DatasourceTableDTO;
import org.apex.dataverse.enums.DataSourceTypeEnum;
import org.apex.dataverse.enums.EnvEnum;
import org.apex.dataverse.enums.TestDatasourceConnResultEnum;
import org.apex.dataverse.mapper.DatasourceMapper;
import org.apex.dataverse.mapper.DatasourceParentMapper;
import org.apex.dataverse.mapper.DvsMapper;
import org.apex.dataverse.mapper.DvsParentMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.*;
import org.apex.dataverse.service.IDatasourceParentService;
import org.apex.dataverse.service.IDatasourceService;
import org.apex.dataverse.service.IJdbcSourceService;
import org.apex.dataverse.service.IKafkaSourceService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.MysqlUtils;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.SqlUtils;
import org.apex.dataverse.utils.UCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-05-15
 */
@Service
public class DatasourceParentServiceImpl extends ServiceImpl<DatasourceParentMapper, DatasourceParent> implements IDatasourceParentService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IDatasourceService datasourceService;

    @Autowired
    private IJdbcSourceService jdbcSourceService;

    @Autowired
    private IKafkaSourceService kafkaSourceService;

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Autowired
    private DvsParentMapper dvsMapper;

    @Override
    public PageResult<DatasourceParentVO> pageDatasourceParent(PageDatasourceParentParam pageDatasourceParentParam) throws DtvsAdminException {
        validatePageDatasourceParent(pageDatasourceParentParam);
        IPage<DatasourceParent> iPage = getIPage(pageDatasourceParentParam);
        PageResult<DatasourceParentVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DatasourceParentVO.class));
        buildDatasourceParentVO(pageResult.getList());
        return pageResult;
    }

    private void buildDatasourceParentVO(List<DatasourceParentVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> datasourceCodeList = list.stream().map(DatasourceParentVO::getDatasourceCode).collect(Collectors.toList());
            List<String> dvsCodeList = list.stream().map(DatasourceParentVO::getDvsCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(datasourceCodeList) && CollectionUtil.isNotEmpty(dvsCodeList)) {
                List<Datasource> datasourceList = datasourceService.list(Wrappers.<Datasource>lambdaQuery().in(Datasource::getDatasourceCode, datasourceCodeList));
                List<DvsParent> dvsList = dvsMapper.selectList(Wrappers.<DvsParent>lambdaQuery().in(DvsParent::getDvsCode, dvsCodeList).
                        eq(DvsParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
                if (CollectionUtil.isNotEmpty(datasourceList)) {
                    List<DatasourceVO> datasourceVOList = mapperFactory.getMapperFacade().mapAsList(datasourceList, DatasourceVO.class);
                    Map<String, List<DatasourceVO>> datasourceMap = datasourceVOList.stream().collect(Collectors.groupingBy(DatasourceVO::getDatasourceCode));
                    if (CollectionUtil.isNotEmpty(datasourceMap)) {
                        for (DatasourceParentVO datasourceParentVO : list) {
                            datasourceParentVO.setDatasourceVOList(datasourceMap.get(datasourceParentVO.getDatasourceCode()));
                            if (CollectionUtil.isNotEmpty(dvsList)) {
                                DvsParent dvsParent = dvsList.stream().filter(d -> d.getDvsCode().equals(datasourceParentVO.getDvsCode())).findFirst().orElse(null);
                                if (Objects.nonNull(dvsParent)) {
                                    datasourceParentVO.setDvsName(dvsParent.getParentAlias());
                                }
                            }
                        }
                    }
                    List<JdbcSource> jdbcSourceList = jdbcSourceService.list(Wrappers.<JdbcSource>lambdaQuery().in(JdbcSource::getDatasourceCode, datasourceCodeList));
                    List<KafkaSource> kafkaSourceList = kafkaSourceService.list(Wrappers.<KafkaSource>lambdaQuery().in(KafkaSource::getDatasourceCode, datasourceCodeList));
                    if (CollectionUtil.isNotEmpty(jdbcSourceList)) {
                        List<JdbcSourceVO> jdbcSourceVOList = mapperFactory.getMapperFacade().mapAsList(jdbcSourceList, JdbcSourceVO.class);
                        Map<String, List<JdbcSourceVO>> jdbcSourceVOMap = jdbcSourceVOList.stream().collect(Collectors.groupingBy(j -> j.getDatasourceCode() + j.getEnv()));
                        if (CollectionUtil.isNotEmpty(jdbcSourceVOMap)) {
                            for (DatasourceParentVO datasourceParentVO : list) {
                                for (DatasourceVO datasourceVO : datasourceParentVO.getDatasourceVOList()) {
                                    if (CollectionUtil.isNotEmpty(jdbcSourceVOMap.get(datasourceVO.getDatasourceCode() + datasourceVO.getEnv()))) {
                                        datasourceVO.setJdbcSourceVO(jdbcSourceVOMap.get(datasourceVO.getDatasourceCode() + datasourceVO.getEnv()).get(0));
                                    }
                                }
                            }
                        }
                    }
                    if (CollectionUtil.isNotEmpty(kafkaSourceList)) {
                        List<KafkaSourceVO> kafkaSourceVOList = mapperFactory.getMapperFacade().mapAsList(kafkaSourceList, KafkaSourceVO.class);
                        Map<String, List<KafkaSourceVO>> kafkaSourceVOMap = kafkaSourceVOList.stream().collect(Collectors.groupingBy(k -> k.getDatasourceCode() + k.getEnv()));
                        if (CollectionUtil.isNotEmpty(kafkaSourceVOMap)) {
                            for (DatasourceParentVO datasourceParentVO : list) {
                                for (DatasourceVO datasourceVO : datasourceParentVO.getDatasourceVOList()) {
                                    if (CollectionUtil.isNotEmpty(kafkaSourceVOMap.get(datasourceVO.getDatasourceCode() + datasourceVO.getEnv()))) {
                                        datasourceVO.setKafkaSourceVO(kafkaSourceVOMap.get(datasourceVO.getDatasourceCode() + datasourceVO.getEnv()).get(0));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    }

    private IPage<DatasourceParent> getIPage(PageDatasourceParentParam pageDatasourceParentParam) {
        //默认单页10条记录
        Page<DatasourceParent> page = new Page<>();
        PageQueryParam pageQueryParam = pageDatasourceParentParam.getPageQueryParam();
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

        LambdaQueryWrapper<DatasourceParent> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(pageDatasourceParentParam.getDataSourceTypeId())) {
            queryWrapper.eq(DatasourceParent::getDatasourceTypeId, pageDatasourceParentParam.getDataSourceTypeId());
        }
        if (Objects.nonNull(pageDatasourceParentParam.getDataSourceReadAndWrite())) {
            queryWrapper.eq(DatasourceParent::getDatasourceReadWrite, pageDatasourceParentParam.getDataSourceReadAndWrite());
        }
        // 数据源ID 数据源名称 数据源简称
        if (StringUtils.isNotEmpty(pageDatasourceParentParam.getKeyword())) {
            queryWrapper.like(DatasourceParent::getParentId, pageDatasourceParentParam.getKeyword()).
                    or(w -> w.like(DatasourceParent::getDatasourceName, pageDatasourceParentParam.getKeyword())).
                    or(w -> w.like(DatasourceParent::getDatasourceAbbr, pageDatasourceParentParam.getKeyword()));

        }
        queryWrapper.eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue());
        return page(page, queryWrapper);
    }

    private void validatePageDatasourceParent(PageDatasourceParentParam pageDatasourceParentParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDatasourceParentParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDatasourceParentParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        validateAddDatasourceParent(datasourceParentParam);
        validateAddDatasourceParentDuplicate(datasourceParentParam);
        // 获取用户信息 需要controller层才能使用
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        // 新增数据源父类
        datasourceParentParam.setDatasourceCode(UCodeUtil.produce());
        Long parentId = saveOrUpdateDatasourceParent(datasourceParentParam, userInfo);
        // 新增数据源
        List<DatasourceParam> datasourceParamList = saveDatasource(datasourceParentParam, userInfo);
        // 新增数据源具体配置
        saveDatasourceConf(datasourceParamList);
        return parentId;
    }

    private void saveDatasourceConf(List<DatasourceParam> datasourceParamList) throws DtvsAdminException {
        List<JdbcSource> jdbcSourceList = new ArrayList<>();
        List<KafkaSource> kafkaSourceList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(datasourceParamList)) {
            for (DatasourceParam datasourceParam : datasourceParamList) {
                if (Objects.nonNull(datasourceParam.getJdbcSourceParam())) {
                    JdbcSource jdbcSource = mapperFactory.getMapperFacade().map(datasourceParam.getJdbcSourceParam(), JdbcSource.class);
                    jdbcSource.setCreateTime(LocalDateTime.now());
                    jdbcSource.setUpdateTime(LocalDateTime.now());
                    jdbcSource.setDatasourceName(datasourceParam.getDatasourceName());
                    jdbcSourceList.add(jdbcSource);
                }
                if (Objects.nonNull(datasourceParam.getKafkaSourceParam())) {
                    KafkaSource kafkaSource = mapperFactory.getMapperFacade().map(datasourceParam.getKafkaSourceParam(), KafkaSource.class);
                    kafkaSource.setDatasourceName(datasourceParam.getDatasourceName());
                    kafkaSourceList.add(kafkaSource);
                }
            }
        }
        // 保存JDBC配置
        if (CollectionUtil.isNotEmpty(jdbcSourceList)) {
            if (!jdbcSourceService.saveBatch(jdbcSourceList)) {
                throw new DtvsAdminException("保存数据源JDBC配置失败");
            }
        }
        // 保存Kafka配置
        if (CollectionUtil.isNotEmpty(kafkaSourceList)) {
            if (!kafkaSourceService.saveBatch(kafkaSourceList)) {
                throw new DtvsAdminException("保存数据源Kafka配置失败");
            }
        }
    }

    private List<DatasourceParam> saveDatasource(DatasourceParentParam datasourceParentParam, UserInfo userInfo) throws DtvsAdminException {
        List<Datasource> datasourceList = mapperFactory.getMapperFacade().mapAsList(datasourceParentParam.getDatasourceParamList(), Datasource.class);
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            datasourceList.forEach(d -> {
                d.setDatasourceCode(datasourceParentParam.getDatasourceCode());
                d.setCreateTime(LocalDateTime.now());
                d.setUpdateTime(LocalDateTime.now());
                d.setIsDeleted(IsDeletedEnum.NO.getValue());
                d.setTenantId(userInfo.getTenantId());
                d.setTenantName(userInfo.getTenantName());
                d.setDeptId(userInfo.getDeptId());
                d.setDeptName(userInfo.getDeptName());
                d.setUserId(userInfo.getUserId());
                d.setUserName(userInfo.getUserName());
            });
            if (!datasourceService.saveBatch(datasourceList)) {
                throw new DtvsAdminException("保存数据源失败");
            }
            datasourceParentParam.getDatasourceParamList().forEach(di ->{
                Datasource datasource = datasourceList.stream().filter(dsi -> dsi.getDatasourceName().equals(di.getDatasourceName())).findFirst().orElse(null);
                if (Objects.nonNull(datasource)) {
                    di.setDatasourceId(datasource.getDatasourceId());
                    if (Objects.nonNull(di.getJdbcSourceParam())) {
                        di.getJdbcSourceParam().setDatasourceCode(datasource.getDatasourceCode());
                    }
                    if (Objects.nonNull(di.getKafkaSourceParam())) {
                        di.getKafkaSourceParam().setDatasourceCode(datasource.getDatasourceCode());
                    }
                }
            });

            return datasourceParentParam.getDatasourceParamList();
        }
        return null;
    }

    private Long saveOrUpdateDatasourceParent(DatasourceParentParam datasourceParentParam, UserInfo userInfo) throws DtvsAdminException {
        DatasourceParent datasourceParent = mapperFactory.getMapperFacade().map(datasourceParentParam, DatasourceParent.class);
        if (Objects.isNull(datasourceParent.getParentId())) {
            datasourceParent.setCreateTime(LocalDateTime.now());
        }
        datasourceParent.setUpdateTime(LocalDateTime.now());
        datasourceParent.setIsDeleted(IsDeletedEnum.NO.getValue());
        datasourceParent.setTenantId(userInfo.getTenantId());
        datasourceParent.setTenantName(userInfo.getTenantName());
        datasourceParent.setDeptId(userInfo.getDeptId());
        datasourceParent.setDeptName(userInfo.getDeptName());
        datasourceParent.setUserId(userInfo.getUserId());
        datasourceParent.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(datasourceParent)) {
            throw new DtvsAdminException("保存数据源失败");
        }
        return datasourceParent.getParentId();
    }

    private void validateAddDatasourceParentDuplicate(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        // 保证租户内唯一
        List<DatasourceParent> datasourceParentList = list(Wrappers.<DatasourceParent>lambdaQuery().
                eq(DatasourceParent::getDatasourceName, datasourceParentParam.getDatasourceName()).
                eq(DatasourceParent::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceParentList)) {
            throw new DtvsAdminException("数据源父类名称重复");
        }
        List<DatasourceParent> datasourceParentList2 = list(Wrappers.<DatasourceParent>lambdaQuery().eq(DatasourceParent::getDatasourceAbbr, datasourceParentParam.getDatasourceAbbr()).
                eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceParentList2)) {
            throw new DtvsAdminException("数据源父类简称重复");
        }
    }

    private void validateAddDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        if (Objects.isNull(datasourceParentParam)) {
            throw new DtvsAdminException("数据源父类参数为空");
        }
        if (StringUtils.isBlank(datasourceParentParam.getDvsCode())) {
            throw new DtvsAdminException("数据源父类数据空间编码为空");
        }
        if (Objects.isNull(datasourceParentParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源父类类型为空");
        }
        if (StringUtils.isBlank(datasourceParentParam.getDatasourceTypeName())) {
            throw new DtvsAdminException("数据源父类类型名称为空");
        }
        if (StringUtils.isBlank(datasourceParentParam.getDatasourceName())) {
            throw new DtvsAdminException("数据源父类名称为空");
        }
        if (!datasourceParentParam.getDatasourceName().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据源父类名称仅包含数字、字母、下划线,且不能以数字开头");
        }
        if (datasourceParentParam.getDatasourceName().length() > 50) {
            throw new DtvsAdminException("数据源父类名称超过最大长度");
        }
        if (StringUtils.isBlank(datasourceParentParam.getDatasourceAbbr())) {
            throw new DtvsAdminException("数据源父类简称为空");
        }
        if (!datasourceParentParam.getDatasourceAbbr().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据源父类简称仅包含数字、字母、下划线,且不能以数字开头");
        }
        if (datasourceParentParam.getDatasourceAbbr().length() > 20) {
            throw new DtvsAdminException("数据源父类简称超过最大长度");
        }
        if (Objects.isNull(datasourceParentParam.getEnvMode())) {
            throw new DtvsAdminException("数据源父类开发模式为空");
        }
        if (Objects.isNull(datasourceParentParam.getDatasourceReadWrite())) {
            throw new DtvsAdminException("数据源父类读写权限为空");
        }
        if (CollectionUtil.isEmpty(datasourceParentParam.getDatasourceParamList())) {
            throw new DtvsAdminException("数据源信息为空");
        }
        for (DatasourceParam datasourceParam : datasourceParentParam.getDatasourceParamList()) {
            if (Objects.isNull(datasourceParam.getJdbcSourceParam()) && Objects.isNull(datasourceParam.getKafkaSourceParam())) {
                throw new DtvsAdminException("数据源配置信息为空");
            }
            if (Objects.nonNull(datasourceParam.getJdbcSourceParam())) {
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getDatasourceName())) {
                    throw new DtvsAdminException("数据源JDBC配置信息数据源名称为空");
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getJdbcUrl())) {
                    throw new DtvsAdminException("数据源JDBC配置信息jdbcUrl为空");
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getUserName())) {
                    throw new DtvsAdminException("数据源JDBC配置信息用户名为空");
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getJdbcUrl())) {
                    throw new DtvsAdminException("数据源JDBC配置信息密码为空");
                }
            }
            if (Objects.isNull(datasourceParam.getEnv())) {
                throw new DtvsAdminException("数据源模式为空");
            }
            if (Objects.isNull(datasourceParam.getConnType())) {
                throw new DtvsAdminException("数据源连接类型为空");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        DatasourceParent datasourceParent = validateEditDatasourceParent(datasourceParentParam);
        // 获取用户信息 需要controller层才能使用
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        // 编辑数据源父类
        Long parentId = saveOrUpdateDatasourceParent(datasourceParentParam, userInfo);
        // 新增数据源
        datasourceParentParam.setDatasourceCode(datasourceParent.getDatasourceCode());
        List<DatasourceParam> dataSourceInstanceParamList = saveDatasource(datasourceParentParam, userInfo);
        // 新增数据源实例配置
        saveDatasourceConf(dataSourceInstanceParamList);
        return parentId;
    }

    private DatasourceParent validateEditDatasourceParent(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        DatasourceParent datasourceParent;
        validateAddDatasourceParent(datasourceParentParam);
        if (Objects.isNull(datasourceParentParam.getParentId())) {
            throw new DtvsAdminException("数据源父类ID为空");
        }
        if (Objects.isNull(datasourceParent = getOne(Wrappers.<DatasourceParent>lambdaQuery().eq(DatasourceParent::getParentId, datasourceParentParam.getParentId()).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据源父类不存在");
        }
        List<DatasourceParent> datasourceParentList = list(Wrappers.<DatasourceParent>lambdaQuery().
                eq(DatasourceParent::getDatasourceName, datasourceParentParam.getDatasourceName()).
                eq(DatasourceParent::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceParentList)) {
            if (datasourceParentList.stream().filter(d -> !d.getParentId().equals(datasourceParent.getParentId())).count() > 0) {
                throw new DtvsAdminException("数据源父类名称重复");
            }
        }
        List<DatasourceParent> datasourceParentList2 = list(Wrappers.<DatasourceParent>lambdaQuery().eq(DatasourceParent::getDatasourceAbbr, datasourceParentParam.getDatasourceAbbr()).
                eq(DatasourceParent::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceParentList2)) {
            if (datasourceParentList2.stream().filter(d -> !d.getParentId().equals(datasourceParent.getParentId())).count() > 0) {
                throw new DtvsAdminException("数据源父类简称重复");
            }
        }
        // 删除旧的关联关系 Datasource JdbcSource kafkaSource ...
        List<Datasource> datasourceList = datasourceService.list(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceCode, datasourceParentParam.getDatasourceCode()).eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            // 删除数据源
            List<Long> datasourceIds = datasourceList.stream().map(Datasource::getDatasourceId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(datasourceIds)) {
                if (!datasourceService.removeByIds(datasourceIds)) {
                    throw new DtvsAdminException("删除数据源失败");
                }
                if (CollectionUtil.isNotEmpty(jdbcSourceService.list(Wrappers.<JdbcSource>lambdaQuery().eq(JdbcSource::getDatasourceCode, datasourceParentParam.getDatasourceCode())))) {
                    if (!jdbcSourceService.remove(Wrappers.<JdbcSource>lambdaQuery().eq(JdbcSource::getDatasourceCode, datasourceParentParam.getDatasourceCode()))) {
                        throw new DtvsAdminException("删除数据源JDBC配置失败");
                    }
                }
                if (CollectionUtil.isNotEmpty(kafkaSourceService.list(Wrappers.<KafkaSource>lambdaQuery().eq(KafkaSource::getDatasourceCode, datasourceParentParam.getDatasourceCode())))) {
                    if (!kafkaSourceService.remove(Wrappers.<KafkaSource>lambdaQuery().eq(KafkaSource::getDatasourceCode, datasourceParentParam.getDatasourceCode()))) {
                        throw new DtvsAdminException("删除数据源Kafka配置失败");
                    }
                }
            }

        }

        return datasourceParent;
    }

    @Override
    public List<TestDataSourceLinkVO> testDatasourceParentLink(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        List<TestDataSourceLinkVO> testDataSourceLinkVOList = new ArrayList<>();
        validateTestDatsSourceLinkVO(datasourceParentParam);
        if (datasourceParentParam.getDatasourceTypeId().equals(DataSourceTypeEnum.MYSQL.getValue())) {
            testJdbcDataSourceLink(datasourceParentParam, testDataSourceLinkVOList);
        }
        return testDataSourceLinkVOList;
    }

    private void testJdbcDataSourceLink(DatasourceParentParam datasourceParentParam, List<TestDataSourceLinkVO> testDataSourceLinkVOList) {
        for (DatasourceParam datasourceParam : datasourceParentParam.getDatasourceParamList()) {
            TestDataSourceLinkVO testDataSourceLinkVO = new TestDataSourceLinkVO();
            testDataSourceLinkVO.setEnv(datasourceParam.getEnv());
            testDataSourceLinkVO.setInstanceName(datasourceParam.getDatasourceName());
            testDataSourceLinkVO.setConnType(datasourceParam.getConnType());
            testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.SUCCESS.getValue());
            JdbcSourceParam jdbcSourceParam = datasourceParam.getJdbcSourceParam();

            try {
                MysqlUtils.testMysqlJdbcConnection(jdbcSourceParam.getJdbcUrl(), jdbcSourceParam.getUserName(), jdbcSourceParam.getPassword(), testDataSourceLinkVO);
            } catch (SQLException e) {
                testDataSourceLinkVO.setErrMsg(e.getMessage());
                testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
            } catch (Throwable throwable) {
                testDataSourceLinkVO.setErrMsg(throwable.getMessage());
                testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
            }
            if (testDataSourceLinkVO.getResult() == TestDatasourceConnResultEnum.FAILURE.getValue()) {
                testDataSourceLinkVO.setErrMsg(testDataSourceLinkVO.getErrMsg() + " please check jdbcUrl or user or password");
            }
            testDataSourceLinkVOList.add(testDataSourceLinkVO);
        }
    }

    private void validateTestDatsSourceLinkVO(DatasourceParentParam datasourceParentParam) throws DtvsAdminException {
        if (Objects.isNull(datasourceParentParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源父类类型为空");
        }
        if (CollectionUtil.isEmpty(datasourceParentParam.getDatasourceParamList())) {
            throw new DtvsAdminException("数据源信息为空");
        }
        for (DatasourceParam datasourceParam : datasourceParentParam.getDatasourceParamList()) {
            if (Objects.isNull(datasourceParam.getJdbcSourceParam()) && Objects.isNull(datasourceParam.getKafkaSourceParam())) {
                throw new DtvsAdminException("数据源配置信息为空");
            }
            if (Objects.nonNull(datasourceParam.getJdbcSourceParam())) {
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getDatasourceName())) {
                    throw new DtvsAdminException("数据源JDBC配置信息数据源名称为空");
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getJdbcUrl())) {
                    throw new DtvsAdminException("数据源JDBC配置信息jdbcUrl为空");
                }
                if (!SqlUtils.matchJdbcUrl(datasourceParam.getJdbcSourceParam().getJdbcUrl(), datasourceParentParam.getDatasourceTypeId())) {
                    throw new DtvsAdminException("数据源JDBC配置信息jdbcUrl格式错误:" + datasourceParam.getJdbcSourceParam().getJdbcUrl());
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getUserName())) {
                    throw new DtvsAdminException("数据源JDBC配置信息用户名为空");
                }
                if (StringUtils.isBlank(datasourceParam.getJdbcSourceParam().getJdbcUrl())) {
                    throw new DtvsAdminException("数据源JDBC配置信息密码为空");
                }
            }
            if (Objects.isNull(datasourceParam.getEnv())) {
                throw new DtvsAdminException("数据源模式为空");
            }
            if (Objects.isNull(datasourceParam.getConnType())) {
                throw new DtvsAdminException("数据源连接类型为空");
            }
        }
    }

    @Override
    public List<DatasourceParentDTO> listDatasourceParentByType(ListDataSourceParam listDataSourceParam) {
        LambdaQueryWrapper<DatasourceParent> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(listDataSourceParam)) {
            if (Objects.nonNull(listDataSourceParam.getDatasourceTypeId())) {
                queryWrapper.eq(DatasourceParent::getDatasourceTypeId, listDataSourceParam.getDatasourceTypeId());
            }
        }
        queryWrapper.eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue());
        return mapperFactory.getMapperFacade().mapAsList(list(queryWrapper), DatasourceParentDTO.class);
    }

    @Override
    public List<DatasourceTableDTO> getDatasourceTable(ListDatasourceTableParam listDatasourceTableParam) throws DtvsAdminException {
        if (Objects.isNull(listDatasourceTableParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(listDatasourceTableParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源类型ID参数为空");
        }
        if (Objects.isNull(listDatasourceTableParam.getEnv())) {
            throw new DtvsAdminException("数据源模式参数为空");
        }
        if (StringUtils.isBlank(listDatasourceTableParam.getDatasourceCode())) {
            throw new DtvsAdminException("数据源编码参数为空");
        }
        if (CollectionUtil.isEmpty(datasourceService.list(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceTypeId, listDatasourceTableParam.getDatasourceTypeId()).
                eq(Datasource::getDatasourceCode, listDatasourceTableParam.getDatasourceCode()).
                eq(Datasource::getEnv, listDatasourceTableParam.getEnv()).
                eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("数据源不存在");
        }
        if (listDatasourceTableParam.getDatasourceTypeId().equals(DataSourceTypeEnum.MYSQL.getValue())) {
            return MysqlUtils.getDatasourceTable(jdbcSourceService.getOne(Wrappers.<JdbcSource>lambdaQuery().
                    eq(JdbcSource::getDatasourceCode,listDatasourceTableParam.getDatasourceCode()).
                    eq(JdbcSource::getEnv, listDatasourceTableParam.getEnv())));
        }
        return null;
    }

    @Override
    public List<DatasourceColumnDTO> getDatasourceTableAndColumn(ListDatasourceTableColumnParam listDatasourceTableColumnParam) throws DtvsAdminException {
        if (Objects.isNull(listDatasourceTableColumnParam)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(listDatasourceTableColumnParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源类型ID参数为空");
        }
        if (Objects.isNull(listDatasourceTableColumnParam.getEnv())) {
            throw new DtvsAdminException("数据源环境参数为空");
        }
        if (StringUtils.isBlank(listDatasourceTableColumnParam.getDatasourceCode())) {
            throw new DtvsAdminException("数据源编码参数为空");
        }
        if (StringUtils.isBlank(listDatasourceTableColumnParam.getTableName())) {
            throw new DtvsAdminException("数据表名称参数为空");
        }
        if (listDatasourceTableColumnParam.getDatasourceTypeId().equals(DataSourceTypeEnum.MYSQL.getValue())) {
            return MysqlUtils.getDatasourceTableAndColumn(jdbcSourceService.getOne(Wrappers.<JdbcSource>lambdaQuery().
                    eq(JdbcSource::getDatasourceCode, listDatasourceTableColumnParam.getDatasourceCode()).
                    eq(JdbcSource::getEnv, listDatasourceTableColumnParam.getEnv())), listDatasourceTableColumnParam.getTableName());
        }
        return null;
    }

    @Override
    public DatasourceParentVO detail(Long parentId) throws DtvsAdminException {
        DatasourceParent datasourceParent;
        if (Objects.isNull(parentId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(datasourceParent = getOne(Wrappers.<DatasourceParent>lambdaQuery().eq(DatasourceParent::getParentId, parentId).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("父类数据源不存在");
        }
        DatasourceParentVO datasourceParentVO = mapperFactory.getMapperFacade().map(datasourceParent, DatasourceParentVO.class);
        List<DatasourceVO> datasourceVOList = mapperFactory.getMapperFacade().mapAsList(datasourceService.list(Wrappers.<Datasource>lambdaQuery().
                eq(Datasource::getDatasourceCode, datasourceParent.getDatasourceCode()).
                eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue())), DatasourceVO.class);
        if (CollectionUtil.isNotEmpty(datasourceVOList)) {
            List<JdbcSourceVO> jdbcSourceVOList = mapperFactory.getMapperFacade().mapAsList(jdbcSourceService.list(Wrappers.<JdbcSource>lambdaQuery().eq(JdbcSource::getDatasourceCode, datasourceParent.getDatasourceCode())), JdbcSourceVO.class);
            List<KafkaSourceVO> kafkaSourceVOList = mapperFactory.getMapperFacade().mapAsList(kafkaSourceService.list(Wrappers.<KafkaSource>lambdaQuery().eq(KafkaSource::getDatasourceCode, datasourceParent.getDatasourceCode())), KafkaSourceVO.class);
            datasourceVOList.forEach(datasourceVO -> {
                if (CollectionUtil.isNotEmpty(jdbcSourceVOList)) {
                    JdbcSourceVO jdbcSourceVO = jdbcSourceVOList.stream().filter(j -> j.getEnv().intValue() == datasourceVO.getEnv().intValue()).findFirst().orElse(null);
                    datasourceVO.setJdbcSourceVO(jdbcSourceVO);
                }
                if (CollectionUtil.isNotEmpty(kafkaSourceVOList)) {
                    KafkaSourceVO kafkaSourceVO = kafkaSourceVOList.stream().filter(j -> j.getEnv().intValue() == datasourceVO.getEnv().intValue()).findFirst().orElse(null);
                    datasourceVO.setKafkaSourceVO(kafkaSourceVO);
                }
            });
            datasourceParentVO.setDatasourceVOList(datasourceVOList);
        }

        return datasourceParentVO;
    }

    @Override
    public Boolean delete(Long parentId) throws DtvsAdminException {
        DatasourceParent datasourceParent;
        if (Objects.isNull(parentId)) {
            throw new DtvsAdminException("参数为空");
        }
        if (Objects.isNull(datasourceParent = getOne(Wrappers.<DatasourceParent>lambdaQuery().eq(DatasourceParent::getParentId, parentId).eq(DatasourceParent::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsAdminException("父类数据源不存在");
        }
        DatasourceParent updateDatasourceParent = new DatasourceParent();
        updateDatasourceParent.setParentId(datasourceParent.getParentId());
        updateDatasourceParent.setIsDeleted(IsDeletedEnum.YES.getValue());
        updateDatasourceParent.setUpdateTime(LocalDateTime.now());
        if (!saveOrUpdate(updateDatasourceParent)) {
            throw new DtvsAdminException("父类数据源删除失败");
        }

        List<Datasource> datasourceList = datasourceService.list(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceCode, datasourceParent.getDatasourceCode()).eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            List<Datasource> updateDatasourceList = new ArrayList<>();
            datasourceList.forEach(datasource -> {
                Datasource updateDatasource = new Datasource();
                updateDatasource.setDatasourceId(datasource.getDatasourceId());
                updateDatasource.setIsDeleted(IsDeletedEnum.YES.getValue());
                updateDatasource.setUpdateTime(LocalDateTime.now());
                updateDatasourceList.add(updateDatasource);
            });
            if (!datasourceService.saveOrUpdateBatch(updateDatasourceList)) {
                throw new DtvsAdminException("删除数据源失败");
            }
        }

        return Boolean.TRUE;
    }
}
