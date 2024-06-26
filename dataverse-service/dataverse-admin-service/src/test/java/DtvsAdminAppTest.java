import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.apex.dataverse.enums.ExecutorRouteStrategyEnum;
import org.apex.dataverse.enums.MisfireStrategyEnum;
import org.apex.dataverse.enums.ScheduleTypeEnum;
import org.apex.dataverse.enums.TriggerStatusEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.admin.AdminFeignClient;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.model.XxlJobInfo;
import org.apex.dataverse.param.DvsAdsTableParam;
import org.apex.dataverse.param.DvsEnvParam;
import org.apex.dataverse.param.ListStorageParam;
import org.apex.dataverse.proxy.XxlJobGroupServiceProxy;
import org.apex.dataverse.proxy.XxlJobInfoServiceProxy;
import org.apex.dataverse.service.IAdminFeignService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: DtvsAdminAppTest
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/18 11:17
 */
public class DtvsAdminAppTest extends AppTest {

    @Autowired
    private ManageFeignClient manageFeignClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminFeignClient adminFeignClient;

    @Autowired
    private IAdminFeignService adminFeignService;

    @Test
    public void listStorage() throws JsonProcessingException {
        ListStorageParam listStorageParam = new ListStorageParam();
        System.out.println(objectMapper.writeValueAsString(manageFeignClient.listStorageVO(listStorageParam)));
    }

    @Test
    public void getDvsApiTable() throws JsonProcessingException {
        DvsAdsTableParam dvsAdsTableParam = new DvsAdsTableParam();
        dvsAdsTableParam.setTenantId(1L);
        List<DvsEnvParam> dvsEnvParamList = new ArrayList<>();
        DvsEnvParam dvsEnvParam = new DvsEnvParam();
        dvsEnvParam.setDvsCode("241yijx4");
        dvsEnvParam.setEnv(0);
        dvsEnvParamList.add(dvsEnvParam);
        dvsAdsTableParam.setDvsEnvParamList(dvsEnvParamList);
//        System.out.println(objectMapper.writeValueAsString(adminFeignService.listDvsAdsTable(dvsAdsTableParam)));
        System.out.println(objectMapper.writeValueAsString(adminFeignClient.listDvsAdsTable(dvsAdsTableParam)));
    }


}
