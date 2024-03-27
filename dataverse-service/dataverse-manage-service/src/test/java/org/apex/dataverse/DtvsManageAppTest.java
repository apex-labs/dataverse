package org.apex.dataverse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apex.dataverse.dao.StorageDAO;
import org.apex.dataverse.exception.DtvsManageException;
import org.apex.dataverse.param.SaveJdbcStorageParam;
import org.apex.dataverse.param.SaveOdpcStorageParam;
import org.apex.dataverse.param.SaveStorageParam;
import org.apex.dataverse.port.entity.DvsPort;
import org.apex.dataverse.port.service.IDvsPortService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

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


}
