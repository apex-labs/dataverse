package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.Datasource;
import org.apex.dataverse.entity.JdbcSource;
import org.apex.dataverse.entity.KafkaSource;
import org.apex.dataverse.enums.DataSourceTypeEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.DatasourceMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DataSourceInstanceParam;
import org.apex.dataverse.param.DatasourceParam;
import org.apex.dataverse.param.PageDatasourceParentParam;
import org.apex.dataverse.service.IDatasourceService;
import org.apex.dataverse.service.IJdbcSourceService;
import org.apex.dataverse.service.IKafkaSourceService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.DatasourceVO;
import org.apex.dataverse.vo.TestDataSourceLinkVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-08
 */
@Service
@Deprecated
public class DatasourceServiceImpl extends ServiceImpl<DatasourceMapper, Datasource> implements IDatasourceService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IJdbcSourceService jdbcInstanceService;

    @Autowired
    private IKafkaSourceService kafkaInstanceService;

    @Autowired
    private DatasourceMapper datasourceMapper;

    @Override
    public PageResult<DatasourceVO> pageDataSource(PageDatasourceParentParam pageDataSourceParam) throws DtvsAdminException {
        validatePageDataSource(pageDataSourceParam);
        IPage<Datasource> iPage = getIPage(pageDataSourceParam);
        PageResult<DatasourceVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DatasourceVO.class));
        buildDataSourceVO(pageResult.getList());
        return pageResult;
    }

    private void buildDataSourceVO(List<DatasourceVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<Long> datasourceIds = list.stream().map(DatasourceVO::getDatasourceId).collect(Collectors.toList());

        }
    }

    private IPage<Datasource> getIPage(PageDatasourceParentParam pageDataSourceParam) {
        //默认单页10条记录
        Page<Datasource> page = new Page<>();
        PageQueryParam pageQueryParam = pageDataSourceParam.getPageQueryParam();
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

        LambdaQueryWrapper<Datasource> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(pageDataSourceParam.getDataSourceTypeId())) {
            queryWrapper.eq(Datasource::getDatasourceTypeId, pageDataSourceParam.getDataSourceTypeId());
        }
        if (Objects.nonNull(pageDataSourceParam.getDataSourceReadAndWrite())) {
            queryWrapper.eq(Datasource::getDatasourceReadWrite, pageDataSourceParam.getDataSourceReadAndWrite());
        }
        if (StringUtils.isNotEmpty(pageDataSourceParam.getKeyword())) {
            queryWrapper.like(Datasource::getDatasourceName, pageDataSourceParam.getKeyword()).or(w -> w.like(Datasource::getDatasourceId, pageDataSourceParam.getKeyword()));

        }
        return page(page, queryWrapper);
    }

    private void validatePageDataSource(PageDatasourceParentParam pageDataSourceParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDataSourceParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDataSourceParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long addDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException {
        validateAddDataSource(dataSourceParam);
        validateAddDataSourceDuplicate(dataSourceParam);
        // 新增数据源
        Long datasourceId = saveOrUpdateDataSource(dataSourceParam);
        // 新增数据源实例
        List<DataSourceInstanceParam> dataSourceInstanceParamList = saveDatasourceInstance(datasourceId, dataSourceParam);
        // 新增数据源实例配置
        saveDatasourceInstanceConf(dataSourceInstanceParamList);
        return datasourceId;
    }

    private void validateAddDataSourceDuplicate(DatasourceParam dataSourceParam) throws DtvsAdminException {
        // 租户内唯一
        List<Datasource> datasourceList = list(Wrappers.<Datasource>lambdaQuery().
                eq(Datasource::getDatasourceName, dataSourceParam.getDatasourceName()).
                eq(Datasource::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            throw new DtvsAdminException("数据源名称重复");
        }
        List<Datasource> datasourceList2 = list(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceAbbr, dataSourceParam.getDatasourceAbbr()));
        if (CollectionUtil.isNotEmpty(datasourceList2)) {
            throw new DtvsAdminException("数据源简称重复");
        }
    }

    private void saveDatasourceInstanceConf(List<DataSourceInstanceParam> dataSourceInstanceParamList) throws DtvsAdminException {
        List<JdbcSource> jdbcInstanceList = new ArrayList<>();
        List<KafkaSource> kafkaInstanceList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dataSourceInstanceParamList)) {
            for (DataSourceInstanceParam dataSourceInstanceParam : dataSourceInstanceParamList) {
                if (Objects.nonNull(dataSourceInstanceParam.getJdbcInstanceParam())) {
                    JdbcSource jdbcInstance = mapperFactory.getMapperFacade().map(dataSourceInstanceParam.getJdbcInstanceParam(), JdbcSource.class);
                    jdbcInstance.setCreateTime(LocalDateTime.now());
                    jdbcInstance.setUpdateTime(LocalDateTime.now());
                    jdbcInstance.setDatasourceName(dataSourceInstanceParam.getInstanceName());
                    jdbcInstanceList.add(jdbcInstance);
                }
                if (Objects.nonNull(dataSourceInstanceParam.getKafkaInstanceParam())) {
                    KafkaSource kafkaInstance = mapperFactory.getMapperFacade().map(dataSourceInstanceParam.getKafkaInstanceParam(), KafkaSource.class);
                    kafkaInstance.setDatasourceName(dataSourceInstanceParam.getInstanceName());
                    kafkaInstanceList.add(kafkaInstance);
                }
            }
        }
        // 保存JDBC配置
        if (CollectionUtil.isNotEmpty(jdbcInstanceList)) {
            if (!jdbcInstanceService.saveBatch(jdbcInstanceList)) {
                throw new DtvsAdminException("保存数据源JDBC配置失败");
            }
        }
        // 保存Kafka配置
        if (CollectionUtil.isNotEmpty(kafkaInstanceList)) {
            if (!kafkaInstanceService.saveBatch(kafkaInstanceList)) {
                throw new DtvsAdminException("保存数据源Kafka配置失败");
            }
        }
    }

    private List<DataSourceInstanceParam> saveDatasourceInstance(Long datasourceId, DatasourceParam dataSourceParam) throws DtvsAdminException {

        return null;
    }

    private Long saveOrUpdateDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException {
        Datasource datasource = mapperFactory.getMapperFacade().map(dataSourceParam, Datasource.class);
        if (Objects.isNull(datasource.getDatasourceId())) {
            datasource.setCreateTime(LocalDateTime.now());
        }
        datasource.setUpdateTime(LocalDateTime.now());
        // 获取用户信息 需要controller层才能使用
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        datasource.setTenantId(userInfo.getTenantId());
        datasource.setTenantName(userInfo.getTenantName());
        datasource.setDeptId(userInfo.getDeptId());
        datasource.setDeptName(userInfo.getDeptName());
        datasource.setUserId(userInfo.getUserId());
        datasource.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(datasource)) {
            throw new DtvsAdminException("保存数据源失败");
        }
        return datasource.getDatasourceId();
    }

    private void validateAddDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException {
        if (Objects.isNull(dataSourceParam)) {
            throw new DtvsAdminException("数据源参数为空");
        }
        if (Objects.isNull(dataSourceParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源类型为空");
        }
        if (StringUtils.isBlank(dataSourceParam.getDatasourceTypeName())) {
            throw new DtvsAdminException("数据源类型名称为空");
        }
        if (StringUtils.isBlank(dataSourceParam.getDatasourceName())) {
            throw new DtvsAdminException("数据源名称为空");
        }
        if (!dataSourceParam.getDatasourceName().matches("[\\u4e00-\\u9fa5]+")) {
            throw new DtvsAdminException("数据源名称只能为中文");
        }
        if (dataSourceParam.getDatasourceName().length() > 50) {
            throw new DtvsAdminException("数据源名称超过最大长度");
        }
        if (StringUtils.isBlank(dataSourceParam.getDatasourceAbbr())) {
            throw new DtvsAdminException("数据源简称为空");
        }
        if (!dataSourceParam.getDatasourceAbbr().matches("[^0-9][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据源简称仅包含数字、字母、下划线,且不能以数字开头");
        }
        if (dataSourceParam.getDatasourceAbbr().length() > 20) {
            throw new DtvsAdminException("数据源简称超过最大长度");
        }
        if (Objects.isNull(dataSourceParam.getEnv())) {
            throw new DtvsAdminException("数据源开发模式为空");
        }
        if (Objects.isNull(dataSourceParam.getDatasourceReadWrite())) {
            throw new DtvsAdminException("数据源读写权限为空");
        }

//        for (DataSourceInstanceParam dataSourceInstanceParam : dataSourceParam.getDataSourceInstanceParam()) {
//            if (Objects.isNull(dataSourceInstanceParam.getJdbcInstanceParam()) && Objects.isNull(dataSourceInstanceParam.getKafkaInstanceParam())) {
//                throw new DtvsAdminException("数据源数据实例配置信息为空");
//            }
//            if (Objects.nonNull(dataSourceInstanceParam.getJdbcInstanceParam())) {
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getInstanceName())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息实例名称为空");
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息jdbcUrl为空");
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getUserName())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息用户名为空");
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息密码为空");
//                }
//            }
//            if (Objects.isNull(dataSourceInstanceParam.getEnv())) {
//                throw new DtvsAdminException("数据源数据实例模式为空");
//            }
//            if (Objects.isNull(dataSourceInstanceParam.getConnType())) {
//                throw new DtvsAdminException("数据源数据实例连接类型为空");
//            }
//        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public Long editDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException {
        validateEditDataSource(dataSourceParam);
        // 编辑数据源
        Long datasourceId = saveOrUpdateDataSource(dataSourceParam);
        // 新增数据源实例
        List<DataSourceInstanceParam> dataSourceInstanceParamList = saveDatasourceInstance(datasourceId, dataSourceParam);
        // 新增数据源实例配置
        saveDatasourceInstanceConf(dataSourceInstanceParamList);
        return datasourceId;
    }

    private void validateEditDataSource(DatasourceParam dataSourceParam) throws DtvsAdminException {
        Datasource dataSource;
        validateAddDataSource(dataSourceParam);
        if (Objects.isNull(dataSourceParam.getDatasourceId())) {
            throw new DtvsAdminException("数据源ID为空");
        }
        if (Objects.isNull(dataSource = getById(dataSourceParam.getDatasourceId()))) {
            throw new DtvsAdminException("数据源不存在");
        }
        List<Datasource> datasourceList = list(Wrappers.<Datasource>lambdaQuery().
                eq(Datasource::getDatasourceName, dataSourceParam.getDatasourceName()).
                eq(Datasource::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(datasourceList)) {
            if (datasourceList.stream().filter(d -> !d.getDatasourceId().equals(dataSource.getDatasourceId())).count() > 0) {
                throw new DtvsAdminException("数据源名称重复");
            }
        }
        List<Datasource> datasourceList2 = list(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceAbbr, dataSourceParam.getDatasourceAbbr()).
                eq(Datasource::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(datasourceList2)) {
            if (datasourceList2.stream().filter(d -> !d.getDatasourceId().equals(dataSource.getDatasourceId())).count() > 0) {
                throw new DtvsAdminException("数据源简称重复");
            }
        }
        // 删除旧的关联关系 datasourceInstance jdbcInstance kafkaInstance ...
    }

    @Override
    public List<TestDataSourceLinkVO> testDataSourceLink(DatasourceParam dataSourceParam) throws DtvsAdminException {
        List<TestDataSourceLinkVO> testDataSourceLinkVOList = new ArrayList<>();
        validateTestDatsSourceLinkVO(dataSourceParam);
        if (dataSourceParam.getDatasourceTypeId().equals(DataSourceTypeEnum.MYSQL.getValue())) {
            testJdbcDataSourceLink(dataSourceParam, testDataSourceLinkVOList);
        }
        return testDataSourceLinkVOList;
    }

    private void testJdbcDataSourceLink(DatasourceParam dataSourceParam, List<TestDataSourceLinkVO> testDataSourceLinkVOList) {
//        for (DataSourceInstanceParam dataSourceInstanceParam : dataSourceParam.getDataSourceInstanceParam()) {
//            TestDataSourceLinkVO testDataSourceLinkVO = new TestDataSourceLinkVO();
//            testDataSourceLinkVO.setEnv(dataSourceInstanceParam.getEnv());
//            testDataSourceLinkVO.setInstanceName(dataSourceInstanceParam.getInstanceName());
//            testDataSourceLinkVO.setConnType(dataSourceInstanceParam.getConnType());
//            testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.SUCCESS.getValue());
//            JdbcInstanceParam jdbcInstanceParam = dataSourceInstanceParam.getJdbcInstanceParam();
//
//            try {
//                MysqlUtils.testMysqlJdbcConnection(jdbcInstanceParam.getJdbcUrl(), jdbcInstanceParam.getUserName(), jdbcInstanceParam.getPassword(), testDataSourceLinkVO);
//            } catch (SQLException e) {
//                testDataSourceLinkVO.setErrMsg(e.getMessage());
//                testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
//            } catch (Throwable throwable) {
//                testDataSourceLinkVO.setErrMsg(throwable.getMessage());
//                testDataSourceLinkVO.setResult(TestDatasourceConnResultEnum.FAILURE.getValue());
//            }
//            if (testDataSourceLinkVO.getResult() == TestDatasourceConnResultEnum.FAILURE.getValue()) {
//                testDataSourceLinkVO.setErrMsg(testDataSourceLinkVO.getErrMsg() + " please check jdbcUrl or user or password");
//            }
//            testDataSourceLinkVOList.add(testDataSourceLinkVO);
//        }
    }

    private void validateTestDatsSourceLinkVO(DatasourceParam dataSourceParam) throws DtvsAdminException {
        if (Objects.isNull(dataSourceParam.getDatasourceTypeId())) {
            throw new DtvsAdminException("数据源类型为空");
        }
//        if (CollectionUtil.isEmpty(dataSourceParam.getDataSourceInstanceParam())) {
//            throw new DtvsAdminException("数据源数据实例配置信息为空");
//        }
//        for (DataSourceInstanceParam dataSourceInstanceParam : dataSourceParam.getDataSourceInstanceParam()) {
//            if (Objects.isNull(dataSourceInstanceParam.getJdbcInstanceParam()) && Objects.isNull(dataSourceInstanceParam.getKafkaInstanceParam())) {
//                throw new DtvsAdminException("数据源数据实例配置信息为空");
//            }
//            if (Objects.nonNull(dataSourceInstanceParam.getJdbcInstanceParam())) {
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getInstanceName())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息实例名称为空");
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息jdbcUrl为空");
//                }
//                if (!SqlUtils.matchJdbcUrl(dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl(), dataSourceParam.getDatasourceTypeId())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息jdbcUrl格式错误:" + dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl());
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getUserName())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息用户名为空");
//                }
//                if (StringUtils.isBlank(dataSourceInstanceParam.getJdbcInstanceParam().getJdbcUrl())) {
//                    throw new DtvsAdminException("数据源数据实例JDBC配置信息密码为空");
//                }
//            }
//            if (Objects.isNull(dataSourceInstanceParam.getEnv())) {
//                throw new DtvsAdminException("数据源数据实例模式为空");
//            }
//            if (Objects.isNull(dataSourceInstanceParam.getConnType())) {
//                throw new DtvsAdminException("数据源数据实例连接类型为空");
//            }
//        }
    }

}
