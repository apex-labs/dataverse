package org.apex.dataverse.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import org.apex.dataverse.entity.Dvs;
import org.apex.dataverse.entity.StorageBox;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.mapper.DvsMapper;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DvsParam;
import org.apex.dataverse.param.PageDvsParam;
import org.apex.dataverse.param.StorageBoxParam;
import org.apex.dataverse.service.IDvsService;
import org.apex.dataverse.service.IStorageBoxService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.utils.UCodeUtil;
import org.apex.dataverse.vo.DvsVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Service
@Deprecated
public class DvsServiceImpl extends ServiceImpl<DvsMapper, Dvs> implements IDvsService {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IStorageBoxService storageBoxService;

    @Override
    public PageResult<DvsVO> pageDvs(PageDvsParam pageDvsParam) throws DtvsAdminException {
        validateDvs(pageDvsParam);
        IPage<Dvs> iPage = getIPage(pageDvsParam);
        PageResult<DvsVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DvsVO.class));
        return pageResult;
    }

    private IPage<Dvs> getIPage(PageDvsParam pageDvsParam) {
        //默认单页10条记录
        Page<Dvs> page = new Page<>();
        PageQueryParam pageQueryParam = pageDvsParam.getPageQueryParam();
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

        LambdaQueryWrapper<Dvs> queryWrapper = Wrappers.lambdaQuery();

        if (StringUtils.isNotEmpty(pageDvsParam.getKeyword())) {
            if (pageDvsParam.getKeyword().matches("[0-9]+$")) {
                queryWrapper.eq(Dvs::getDvsId, Long.parseLong(pageDvsParam.getKeyword()));
            } else {
                queryWrapper.like(Dvs::getDvsName, pageDvsParam.getKeyword())
                        .or(w -> w.like(Dvs::getDvsAlias, pageDvsParam.getKeyword()))
                        .or(w -> w.like(Dvs::getDvsAbbr, pageDvsParam.getKeyword()));
            }
        }
        return page(page, queryWrapper);
    }

    private void validateDvs(PageDvsParam pageDvsParam) throws DtvsAdminException {
        PageQueryParam pageQueryParam = pageDvsParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsAdminException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        pageDvsParam.setPageQueryParam(pageQueryParam);
    }

    @Override
    public Long addDvs(DvsParam dvsParam) throws DtvsAdminException {
        validateAddDvs(dvsParam);
        validateAddDvsDuplicates(dvsParam);
        // 新增数据空间 + 存储桶
        Long DvsId = saveOrUpdateDvs(dvsParam);
        saveStorageBox(UCodeUtil.produce(), dvsParam);
        return DvsId;
    }

    private void saveStorageBox(String dvsCode, DvsParam dvsParam) throws DtvsAdminException {

    }

    private Long saveOrUpdateDvs(DvsParam dvsParam) throws DtvsAdminException {
        Dvs dvs = mapperFactory.getMapperFacade().map(dvsParam, Dvs.class);
        if (Objects.isNull(dvsParam.getDvsId())) {
            dvs.setCreateTime(LocalDateTime.now());
        }
        dvs.setCreateTime(LocalDateTime.now());
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        dvs.setTenantId(userInfo.getTenantId());
        dvs.setTenantName(userInfo.getTenantName());
        dvs.setDeptId(userInfo.getDeptId());
        dvs.setDeptName(userInfo.getDeptName());
        dvs.setUserId(userInfo.getUserId());
        dvs.setUserName(userInfo.getUserName());
        if (!saveOrUpdate(dvs)) {
            throw new DtvsAdminException("保存数据空间失败");
        }
        return dvs.getDvsId();
    }

    private void validateAddDvsDuplicates(DvsParam dvsParam) throws DtvsAdminException {
        List<Dvs> dvsList = list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsName, dvsParam.getDvsName()).
                eq(Dvs::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(dvsList)) {
            throw new DtvsAdminException("数据空间名称重复");
        }
    }

    private void validateAddDvs(DvsParam dvsParam) throws DtvsAdminException {
        if (Objects.isNull(dvsParam)) {
            throw new DtvsAdminException("数据空间参数为空");
        }
        if (StringUtils.isBlank(dvsParam.getDvsName())) {
            throw new DtvsAdminException("数据空间名称为空");
        }
        if (dvsParam.getDvsName().length() > 50) {
            throw new DtvsAdminException("数据空间名称长度超过50");
        }
        if (!dvsParam.getDvsName().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据空间名称，字母开头，包含字母、数字、下划线");
        }
        if (StringUtils.isBlank(dvsParam.getDvsAlias())) {
            throw new DtvsAdminException("数据空间别名为空");
        }
        if (dvsParam.getDvsAlias().length() > 50) {
            throw new DtvsAdminException("数据空间别名长度超过50");
        }
        if (!dvsParam.getDvsAlias().matches("[\\u4e00-\\u9fa5]+")) {
            throw new DtvsAdminException("数据空间别名，中文显示");
        }
        if (StringUtils.isBlank(dvsParam.getDvsAbbr())) {
            throw new DtvsAdminException("数据空间简称为空");
        }
        if (dvsParam.getDvsAbbr().length() > 20) {
            throw new DtvsAdminException("数据空间简称长度超过20");
        }
        if (!dvsParam.getDvsAbbr().matches("[a-zA-Z][a-zA-Z0-9_]+$")) {
            throw new DtvsAdminException("数据空间简称，字母开头，包含字母、数字、下划线");
        }
        if (Objects.isNull(dvsParam.getEnv())) {
            throw new DtvsAdminException("数据空间环境模式为空");
        }
        if (Objects.isNull(dvsParam.getDvsStatus())) {
            throw new DtvsAdminException("数据空间状态为空");
        }
        if (Objects.isNull(dvsParam.getLayerAtaIsolation())) {
            throw new DtvsAdminException("数据空间数据隔离级别为空");
        }

    }

    @Override
    public Long editDvs(DvsParam dvsParam) throws DtvsAdminException {
        validateEditDvs(dvsParam);
        // 编辑数据空间 + 存储桶
        Long dvsId = saveOrUpdateDvs(dvsParam);
        saveStorageBox(dvsParam.getDvsCode(), dvsParam);
        return dvsId;
    }

    private void validateEditDvs(DvsParam dvsParam) throws DtvsAdminException {
        Dvs dvs;
        validateAddDvs(dvsParam);
        if (Objects.isNull(dvsParam.getDvsId())) {
            throw new DtvsAdminException("数据空间ID为空");
        }
        if (Objects.isNull(dvs = getById(dvsParam.getDvsId()))) {
            throw new DtvsAdminException("数据空间不存在");
        }
        List<Dvs> DvsList = list(Wrappers.<Dvs>lambdaQuery().eq(Dvs::getDvsName, dvsParam.getDvsName()).
                eq(Dvs::getTenantId, NexusUserInfoUtils.getUserInfo().getTenantId()));
        if (CollectionUtil.isNotEmpty(DvsList)) {
            if (DvsList.stream().filter(d -> !d.getDvsId().equals(dvs.getDvsId())).count() > 0) {
                throw new DtvsAdminException("数据空间名称重复");
            }
        }
        // 清空空间下的存储桶
        List<StorageBox> storageBoxList = storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().
                eq(StorageBox::getDvsCode, dvsParam.getDvsId()));
        if (CollectionUtil.isNotEmpty(storageBoxList)) {
            if (!storageBoxService.remove(Wrappers.<StorageBox>lambdaQuery().
                    eq(StorageBox::getDvsCode, dvsParam.getDvsId()))) {
                throw new DtvsAdminException("删除数据空间下的存储桶失败");
            }
        }
    }
}
