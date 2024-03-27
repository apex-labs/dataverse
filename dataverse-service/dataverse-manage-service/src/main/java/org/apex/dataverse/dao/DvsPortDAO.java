package org.apex.dataverse.dao;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.enums.DvsPortStateEnum;
import org.apex.dataverse.enums.IsDeletedEnum;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.model.PageResult;
import org.apex.dataverse.model.UserInfo;
import org.apex.dataverse.param.DvsPortPageParam;
import org.apex.dataverse.param.SaveDvsPortParam;
import org.apex.dataverse.param.StoragePortParam;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.entity.StoragePort;
import org.apex.dataverse.port.service.IDvsPortService;
import org.apex.dataverse.port.service.IStoragePortService;
import org.apex.dataverse.storage.entity.Storage;
import org.apex.dataverse.storage.service.IStorageService;
import org.apex.dataverse.util.PageQueryParam;
import org.apex.dataverse.util.UCodeUtil;
import org.apex.dataverse.utils.NexusUserInfoUtils;
import org.apex.dataverse.vo.DvsPortVO;
import org.apex.dataverse.vo.StoragePortVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @ClassName: DvsPortDAO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/20 15:26
 */
@Service
public class DvsPortDAO {

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private IDvsPortService dvsPortService;

    @Autowired
    private IStoragePortService storagePortService;

    @Autowired
    private IStorageService storageService;

    public PageResult<DvsPortVO> pageList(DvsPortPageParam dvsPortPageParam) throws DtvsManageException {
        validatePageDvsPort(dvsPortPageParam);
        IPage<DvsPort> iPage = getIPage(dvsPortPageParam);
        PageResult<DvsPortVO> pageResult = mapperFactory.getMapperFacade().map(iPage, PageResult.class);
        pageResult.setList(mapperFactory.getMapperFacade().mapAsList(iPage.getRecords(), DvsPortVO.class));
        buildPageDvsPortVO(pageResult.getList());
        return pageResult;
    }

    private void buildPageDvsPortVO(List<DvsPortVO> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            List<String> portCodes = list.stream().map(DvsPortVO::getPortCode).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(portCodes)) {
                List<StoragePort> storagePortList = storagePortService.list(Wrappers.<StoragePort>lambdaQuery().in(StoragePort::getPortCode, portCodes));
                if (CollectionUtil.isNotEmpty(storagePortList)) {
                    List<StoragePortVO> storagePortVOList = mapperFactory.getMapperFacade().mapAsList(storagePortList, StoragePortVO.class);
                    for (DvsPortVO dvsPortVO : list) {
                        Map<String, List<StoragePortVO>> dvsPortMap = storagePortVOList.stream().collect(Collectors.groupingBy(StoragePortVO::getPortCode));
                        dvsPortVO.setStoragePortVOList(dvsPortMap.get(dvsPortVO.getPortCode()));
                    }
                }
            }
        }
    }

    private IPage<DvsPort> getIPage(DvsPortPageParam dvsPortPageParam) {
        //默认单页10条记录
        Page<DvsPort> page = new Page<>();
        PageQueryParam pageQueryParam = dvsPortPageParam.getPageQueryParam();
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

        LambdaQueryWrapper<DvsPort> queryWrapper = Wrappers.lambdaQuery();
        if (Objects.nonNull(dvsPortPageParam.getPortName())) {
            queryWrapper.like(DvsPort::getPortName, dvsPortPageParam.getPortName());
        }
        if (StringUtils.isNotBlank(dvsPortPageParam.getGroupName())) {
            queryWrapper.eq(DvsPort::getGroupName, dvsPortPageParam.getGroupName());
        }
        if (Objects.nonNull(dvsPortPageParam.getGroupCode())) {
            queryWrapper.eq(DvsPort::getGroupCode, dvsPortPageParam.getGroupCode());
        }
        queryWrapper.eq(DvsPort::getIsDeleted, IsDeletedEnum.NO.getValue());

        return dvsPortService.page(page, queryWrapper);
    }

    private void validatePageDvsPort(DvsPortPageParam dvsPortPageParam) throws DtvsManageException {
        PageQueryParam pageQueryParam = dvsPortPageParam.getPageQueryParam();
        if (Objects.isNull(pageQueryParam)) {
            throw new DtvsManageException("分页参数为空");
        }
        if (Objects.isNull(pageQueryParam.getPageNo())) {
            pageQueryParam.setPageNo(1L);
        }
        if (Objects.isNull(pageQueryParam.getSize())) {
            pageQueryParam.setPageNo(10L);
        }
        dvsPortPageParam.setPageQueryParam(pageQueryParam);
    }

    public Long add(SaveDvsPortParam saveDvsPortParam) throws DtvsManageException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateSaveOrUpdateDvsPort(saveDvsPortParam);
        DvsPort dvsPort = mapperFactory.getMapperFacade().map(saveDvsPortParam, DvsPort.class);
        dvsPort.setPortCode(UCodeUtil.produce());
        dvsPort.setCreateTime(LocalDateTime.now());
        dvsPort.setCreatorId(userInfo.getUserId());
        dvsPort.setCreatorName(userInfo.getUserName());
        if (!dvsPortService.saveOrUpdate(dvsPort)) {
            throw new DtvsManageException("保存连接器失败");
        }
        return dvsPort.getPortId();
    }

    private void validateSaveOrUpdateDvsPort(SaveDvsPortParam saveDvsPortParam) throws DtvsManageException {
        if (Objects.isNull(saveDvsPortParam)) {
            throw new DtvsManageException("参数为空");
        }
        if (StringUtils.isBlank(saveDvsPortParam.getPortName())) {
            throw new DtvsManageException("连接器名称为空");
        }
        if (Objects.isNull(saveDvsPortParam.getMaxDriverConns())) {
            saveDvsPortParam.setMaxDriverConns(2);
        }
        if (StringUtils.isBlank(saveDvsPortParam.getState())) {
            saveDvsPortParam.setState(DvsPortStateEnum.BUILD.getDesc());
        } else {
            if (!DvsPortStateEnum.exists(saveDvsPortParam.getState())) {
                throw new DtvsManageException("连接器状态不存在");
            }
        }
        if (Objects.isNull(saveDvsPortParam.getHeartbeatHz())) {
            saveDvsPortParam.setHeartbeatHz(3000);
        }

        DvsPort dvsPort;
        DvsPort nameDvsPort = dvsPortService.getOne(Wrappers.<DvsPort>lambdaQuery().eq(DvsPort::getPortName, saveDvsPortParam.getPortName()).
                eq(DvsPort::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(saveDvsPortParam.getPortId())) {
            if (Objects.nonNull(nameDvsPort)) {
                throw new DtvsManageException("连接器名称已存在");
            }
        } else {
            dvsPort = dvsPortService.getOne(Wrappers.<DvsPort>lambdaQuery().eq(DvsPort::getPortId, saveDvsPortParam.getPortId()).
                    eq(DvsPort::getIsDeleted, IsDeletedEnum.NO.getValue()));
            if (Objects.isNull(dvsPort)) {
                throw new DtvsManageException("连接器不存在");
            }
            saveDvsPortParam.setPortCode(dvsPort.getPortCode());
            List<StoragePort> storagePortList = storagePortService.list(Wrappers.<StoragePort>lambdaQuery().eq(StoragePort::getPortCode, dvsPort.getPortCode()));
            if (CollectionUtil.isNotEmpty(storagePortList)) {
                throw new DtvsManageException("连接器下有配置的存储区,不允许编辑");
            }
        }

    }

    public Long edit(SaveDvsPortParam saveDvsPortParam) throws DtvsManageException {
        UserInfo userInfo = NexusUserInfoUtils.getUserInfo();
        validateSaveOrUpdateDvsPort(saveDvsPortParam);
        DvsPort dvsPort = mapperFactory.getMapperFacade().map(saveDvsPortParam, DvsPort.class);
        dvsPort.setCreateTime(LocalDateTime.now());
        dvsPort.setCreatorId(userInfo.getUserId());
        dvsPort.setCreatorName(userInfo.getUserName());
        if (!dvsPortService.saveOrUpdate(dvsPort)) {
            throw new DtvsManageException("保存连接器失败");
        }
        return dvsPort.getPortId();
    }

    public DvsPortVO detail(Long portId) throws DtvsManageException {
        DvsPort dvsPort = validateDvsPortExists(portId);
        DvsPortVO dvsPortVO = mapperFactory.getMapperFacade().map(dvsPort, DvsPortVO.class);
        List<StoragePortVO> storagePortVOList = mapperFactory.getMapperFacade().mapAsList(storagePortService.list(Wrappers.<StoragePort>lambdaQuery().eq(StoragePort::getPortId, portId)), StoragePortVO.class);
        if (CollectionUtil.isNotEmpty(storagePortVOList)) {
            dvsPortVO.setStoragePortVOList(storagePortVOList);
        }
        return dvsPortVO;
    }

    private DvsPort validateDvsPortExists(Long portId) throws DtvsManageException {
        DvsPort dvsPort;
        if (Objects.isNull(portId)) {
            throw new DtvsManageException("参数为空");
        }
        if (Objects.isNull(dvsPort = dvsPortService.getOne(Wrappers.<DvsPort>lambdaQuery().eq(DvsPort::getPortId, portId).
                eq(DvsPort::getIsDeleted, IsDeletedEnum.NO.getValue())))) {
            throw new DtvsManageException("连接器不存在");
        }

        return dvsPort;
    }

    public Boolean delete(Long portId) throws DtvsManageException {
        DvsPort dvsPort = validateDvsPortExists(portId);
        List<StoragePort> storagePortList = storagePortService.list(Wrappers.<StoragePort>lambdaQuery().eq(StoragePort::getPortCode, dvsPort.getPortCode()));
        if (CollectionUtil.isNotEmpty(storagePortList)) {
            throw new DtvsManageException("连接器下有配置的存储区,不允许删除");
        }
        DvsPort updateDvsPort = new DvsPort();
        updateDvsPort.setPortId(dvsPort.getPortId());
        updateDvsPort.setIsDeleted(IsDeletedEnum.YES.getValue());
        if (!dvsPortService.saveOrUpdate(updateDvsPort)) {
            throw new DtvsManageException("删除连接器失败");
        }
        return Boolean.TRUE;
    }

    public Boolean removeStoragePort(List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        List<Long> storagePortIds =validateRemoveStoragePort(storagePortParamList);
        // todo 这里是否增加业务判断删除待定
        if (!storagePortService.removeByIds(storagePortIds)) {
            throw new DtvsManageException("删除连接器对应的存储区失败");
        }
        return Boolean.TRUE;
    }

    private List<Long> validateRemoveStoragePort(List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        if (CollectionUtil.isEmpty(storagePortParamList)) {
            throw new DtvsManageException("参数为空");
        }
        List<Long> storagePortIds = storagePortParamList.stream().map(StoragePortParam::getStoragePortId).collect(Collectors.toList());;
        if (CollectionUtil.isEmpty(storagePortIds)) {
            throw new DtvsManageException("连接器对应存储区ID为空");
        }
        if (CollectionUtil.isEmpty(storagePortService.listByIds(storagePortIds))) {
            throw new DtvsManageException("连接器对应存储区不存在");
        }
        return storagePortIds;
    }

    public Boolean addStoragePort(List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        validateStoragePort(storagePortParamList);
        List<StoragePort> storagePortList = mapperFactory.getMapperFacade().mapAsList(storagePortParamList, StoragePort.class);
        storagePortList.forEach(s -> {
            s.setCreateTime(LocalDateTime.now());
        });
        if (!storagePortService.saveOrUpdateBatch(storagePortList)) {
            throw new DtvsManageException("保存连接器存储区失败");
        }
        return Boolean.TRUE;
    }

    private void validateStoragePort(List<StoragePortParam> storagePortParamList) throws DtvsManageException {
        if (CollectionUtil.isEmpty(storagePortParamList)) {
            throw new DtvsManageException("参数为空");
        }
        List<Long> portIds = storagePortParamList.stream().map(StoragePortParam::getPortId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(portIds)) {
            throw new DtvsManageException("连接器ID参数不能为空");
        }
        List<DvsPort> dvsPorts = dvsPortService.list(Wrappers.<DvsPort>lambdaQuery().in(DvsPort::getPortId, portIds).eq(DvsPort::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isEmpty(dvsPorts)) {
            throw new DtvsManageException("连接器不存在");
        }

        List<Long> storageIds = storagePortParamList.stream().map(StoragePortParam::getStorageId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(storageIds)) {
            throw new DtvsManageException("存储区ID参数不能为空");
        }
        List<Storage> storages = storageService.list(Wrappers.<Storage>lambdaQuery().in(Storage::getStorageId, storageIds).eq(Storage::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (CollectionUtil.isEmpty(storages)) {
            throw new DtvsManageException("存储区不存在");
        }

        for (StoragePortParam storagePortParam : storagePortParamList) {
            if (Objects.isNull(storagePortParam.getStorageId())) {
                throw new DtvsManageException("存储ID不能为空");
            }
            if (StringUtils.isBlank(storagePortParam.getConnType())) {
                throw new DtvsManageException("存储区连接类型不能为空");
            }
            if (StringUtils.isBlank(storagePortParam.getEngineType())) {
                throw new DtvsManageException("存储区引擎类型不能为空");
            }
            if (Objects.isNull(storagePortParam.getPortId())) {
                throw new DtvsManageException("连接器ID不能为空");
            }
            if (StringUtils.isBlank(storagePortParam.getPortCode())) {
                throw new DtvsManageException("连接器编码不能为空");
            }
            Storage storage = storages.stream().filter(s -> s.getStorageId().longValue() == storagePortParam.getStorageId().longValue()).findFirst().orElse(null);
            if (Objects.isNull(storage)) {
                throw new DtvsManageException("根据存储ID查找不到存储区");
            }
            if (!storage.getConnType().equals(storagePortParam.getConnType())) {
                throw new DtvsManageException("连接器选择的存储区连接类型跟存储区连接类型不一致");
            }
            if (!storage.getEngineType().equals(storagePortParam.getEngineType())) {
                throw new DtvsManageException("连接器选择的存储区引擎类型跟存储区引擎类型不一致");
            }
            DvsPort dvsPort = dvsPorts.stream().filter(d -> d.getPortId().longValue() == storagePortParam.getPortId().longValue()).findFirst().orElse(null);
            if (Objects.isNull(dvsPort)) {
                throw new DtvsManageException("根据连接器ID查找不到连接器");
            }
            if (!dvsPort.getPortCode().equals(storagePortParam.getPortCode())) {
                throw new DtvsManageException("连接器编码跟实际不一致");
            }
        }
    }
}
