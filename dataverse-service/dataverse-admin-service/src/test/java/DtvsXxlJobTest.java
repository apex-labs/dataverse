import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import org.apex.dataverse.enums.ExecutorRouteStrategyEnum;
import org.apex.dataverse.enums.MisfireStrategyEnum;
import org.apex.dataverse.enums.ScheduleTypeEnum;
import org.apex.dataverse.enums.TriggerStatusEnum;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.feign.manage.ManageFeignClient;
import org.apex.dataverse.model.XxlJobInfo;
import org.apex.dataverse.param.ListStorageParam;
import org.apex.dataverse.proxy.XxlJobGroupServiceProxy;
import org.apex.dataverse.proxy.XxlJobInfoServiceProxy;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * @ClassName: DtvsAdminAppTest
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/18 11:17
 */
public class DtvsXxlJobTest extends AppTest{


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private XxlJobGroupServiceProxy xxlJobGroupServiceProxy;

    @Autowired
    private XxlJobInfoServiceProxy xxlJobInfoServiceProxy;

    @Test
    public void testXxlJobGroupName() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobGroupServiceProxy.findByAppName("dvs-admin")));
    }

    @Test
    public void testAddXxlJob() throws DtvsAdminException, JsonProcessingException {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        int jobGroup = xxlJobGroupServiceProxy.findByAppName("dvs-admin");
        xxlJobInfo.setJobGroup(jobGroup);
        xxlJobInfo.setJobDesc("test");
        xxlJobInfo.setAddTime(new Date());
        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfo.setAuthor("wwd");
        xxlJobInfo.setAlarmEmail("wwd@163.com");

        // 调度时间策略
        xxlJobInfo.setScheduleType(ScheduleTypeEnum.CRON.getDesc());
        xxlJobInfo.setScheduleConf("0 0 0 * * ? *");

        xxlJobInfo.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.getDesc());
        xxlJobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST.getDesc());

        // 调度执行方法
        xxlJobInfo.setExecutorHandler("DtvsAdminDemoHandler");
        xxlJobInfo.setExecutorParam("");
        // 执行超时时间 默认0
        xxlJobInfo.setExecutorTimeout(0);
        // 失败重试次数 默认0
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());

        xxlJobInfo.setTriggerStatus(TriggerStatusEnum.RUN.getValue());
        // 触发器下次调度时间 默认0
        xxlJobInfo.setTriggerNextTime(0);
        // 触发器上次调度时间 默认0
        xxlJobInfo.setTriggerLastTime(0);

        xxlJobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.addCustomizeJob(xxlJobInfo)));
    }

    @Test
    public void updateXxlJob() throws DtvsAdminException, JsonProcessingException {
        XxlJobInfo xxlJobInfo = new XxlJobInfo();
        xxlJobInfo.setId(2);
        xxlJobInfo.setJobDesc("tt");
        int jobGroup = xxlJobGroupServiceProxy.findByAppName("dvs-admin");
        xxlJobInfo.setJobGroup(jobGroup);
        xxlJobInfo.setUpdateTime(new Date());
        xxlJobInfo.setAuthor("wwd");
        xxlJobInfo.setAlarmEmail("wwd@163.com");

        // 调度时间策略
        xxlJobInfo.setScheduleType(ScheduleTypeEnum.CRON.getDesc());
        xxlJobInfo.setScheduleConf("0 0 0 * * ? *");

        xxlJobInfo.setMisfireStrategy(MisfireStrategyEnum.DO_NOTHING.getDesc());
        xxlJobInfo.setExecutorRouteStrategy(ExecutorRouteStrategyEnum.FIRST.getDesc());
        // 调度执行方法
        xxlJobInfo.setExecutorHandler("DtvsAdminDemoHandler");
        xxlJobInfo.setExecutorParam("");
        // 执行超时时间 默认0
        xxlJobInfo.setExecutorTimeout(0);
        // 失败重试次数 默认0
        xxlJobInfo.setExecutorFailRetryCount(0);
        xxlJobInfo.setGlueType(GlueTypeEnum.BEAN.getDesc());

        xxlJobInfo.setTriggerStatus(TriggerStatusEnum.RUN.getValue());
        // 触发器下次调度时间 默认0
        xxlJobInfo.setTriggerNextTime(0);
        // 触发器上次调度时间 默认0
        xxlJobInfo.setTriggerLastTime(0);

        xxlJobInfo.setExecutorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name());
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.commonUpdate(xxlJobInfo)));
    }

    @Test
    public void testXxlJobTrigger() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.runTriggerJob(2, "")));
    }

    @Test
    public void testXxlJobStop() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.pause(2)));
    }

    @Test
    public void testXxlJobResume() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.resume(2)));
    }

    @Test
    public void checkTriggerExists() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.checkTriggerExists(2)));
    }

    @Test
    public void deleteXxlJobInfo() throws DtvsAdminException, JsonProcessingException {
        System.out.println(objectMapper.writeValueAsString(xxlJobInfoServiceProxy.delete(2)));
    }


}
