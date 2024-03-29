package org.apex.dataverse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apex.dataverse.dao.StorageDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.feign.admin.AdminFeignClient;
import org.apex.dataverse.param.*;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.service.IDvsPortService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DtvsManageAppTest
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/9 18:42
 */
public class DtvsManageAppTest extends AppTest{

    @Autowired
    private IDvsPortService dvsPortService;

    @Autowired
    private StorageDAO storageDAO;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Test
    public void saveDvsPort() {
        DvsPort dvsPort = new DvsPort();
        dvsPort.setPort(1);
        dvsPort.setIp("localhost");
        dvsPort.setPortCode("ss");
        dvsPort.setPortName("wwd");
        dvsPort.setDescription("wwd");
        dvsPort.setConnectedDrivers(1);
        dvsPort.setCreateTime(LocalDateTime.now());
        dvsPort.setCreatorId(1L);
        dvsPort.setCreatorName("wwd");
        dvsPort.setGroupCode("wwd");
        dvsPort.setGroupName("wwd");
        dvsPort.setHeartbeatHz(1);
        dvsPort.setMaxDriverConns(1);
        dvsPort.setRegistryCode("dd");
        dvsPort.setState("wwd");
        dvsPortService.save(dvsPort);
    }

    @Test
    public void test() throws JsonProcessingException, DtvsManageException {
        System.out.println(objectMapper.writeValueAsString(storageDAO.connTypeList()));
        SaveStorageParam saveStorageParam = new SaveStorageParam();
        saveStorageParam.setStorageName("wwd");
        saveStorageParam.setStorageAlias("wwd");
        saveStorageParam.setStorageAbbr("wwd");
        saveStorageParam.setStorageType("HDFS");
        saveStorageParam.setConnType("ODPC");
        saveStorageParam.setEngineType("SPARK");
        saveStorageParam.setDescription("这是第一次保存存储区");
        SaveOdpcStorageParam saveOdpcStorageParam = new SaveOdpcStorageParam();
        saveOdpcStorageParam.setStorageType("HDFS");
        saveOdpcStorageParam.setDatanodes("127.0.0.1");
        saveStorageParam.setSaveOdpcStorageParam(saveOdpcStorageParam);
        SaveJdbcStorageParam saveJdbcStorageParam = new SaveJdbcStorageParam();
        System.out.println(storageDAO.add(saveStorageParam));
    }

    @Test
    public void adminFeignClient() throws JsonProcessingException {
        DvsAdsTableParam dvsAdsTableParam = new DvsAdsTableParam();
        dvsAdsTableParam.setTenantId(1L);
        List<DvsEnvParam> dvsEnvParamList = new ArrayList<>();
        DvsEnvParam dvsEnvParam = new DvsEnvParam();
        dvsEnvParam.setDvsCode("241yijx4");
        dvsEnvParam.setEnv(0);
        dvsEnvParamList.add(dvsEnvParam);
        dvsAdsTableParam.setDvsEnvParamList(dvsEnvParamList);
        System.out.println(objectMapper.writeValueAsString(adminFeignClient.listDvsAdsTable(dvsAdsTableParam)));

    }


}
