package org.apex.dataverse.jobhandler;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFactory;
import org.apache.commons.lang3.StringUtils;
import org.apex.dataverse.constrant.SqlGrammarConst;
import org.apex.dataverse.core.enums.CmdSet;
import org.apex.dataverse.core.enums.ExeEnv;
import org.apex.dataverse.core.exception.InvalidCmdException;
import org.apex.dataverse.core.exception.InvalidConnException;
import org.apex.dataverse.core.msg.Header;
import org.apex.dataverse.core.msg.Request;
import org.apex.dataverse.core.msg.Response;
import org.apex.dataverse.core.msg.packet.Packet;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlJobReqPacket;
import org.apex.dataverse.core.msg.packet.dd.ExeSqlJobRspPacket;
import org.apex.dataverse.core.msg.packet.di.DiReqPacket;
import org.apex.dataverse.core.msg.packet.di.DiRspPacket;
import org.apex.dataverse.core.msg.packet.info.ResultInfo;
import org.apex.dataverse.core.msg.packet.info.SqlInfo;
import org.apex.dataverse.core.msg.packet.info.StoreInfo;
import org.apex.dataverse.core.msg.packet.info.output.HdfsOutput;
import org.apex.dataverse.core.msg.serialize.ISerialize;
import org.apex.dataverse.core.util.ObjectMapperUtil;
import org.apex.dataverse.entity.*;
import org.apex.dataverse.enums.*;
import org.apex.dataverse.exception.DtvsAdminException;
import org.apex.dataverse.port.core.exception.NoPortNodeException;
import org.apex.dataverse.port.driver.conn.PortConnection;
import org.apex.dataverse.port.driver.conn.pool.PortExchanger;
import org.apex.dataverse.port.driver.exception.PortConnectionException;
import org.apex.dataverse.service.*;
import org.apex.dataverse.utils.DateUtils;
import org.apex.dataverse.utils.UCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DvsAdminJobScheduleHandler
 *
 *
 * <p> 调度执行逻辑 根据任务ID查询对应的任务调度 通过任务调度查找任务调度下对应的作业(集成作业开发作业) </p>
 * <p> 根据开发作业形成的DAG树以及依赖关系 按照树的层级从上往下执行的作业执行时判断是否有依赖关系 </p>
 * <p> 同级的任务节点没有依赖关系的执行不分先后 </p>
 * <p> 执行任务时判断依赖任务是否执行成功 </p>
 * @Author: wwd
 * @TODO:
 * @Date: 2024/2/27 16:34
 */
@Component
@Slf4j
public class DvsAdminJobScheduleHandler {

    @Autowired
    private IJobScheduleService jobScheduleService;

    @Autowired
    private IScheduleInstanceService scheduleInstanceService;

    @Autowired
    private IJobInstanceService jobInstanceService;

    @Autowired
    private IScheduleNodeService scheduleNodeService;

    @Autowired
    private IScheduleEdgeService scheduleEdgeService;

    @Autowired
    private INodeJobsService nodeJobsService;

    @Autowired
    private MapperFactory mapperFactory;

    @Autowired
    private ITableExtractService tableExtractService;

    @Autowired
    private ITableTransformService tableTransformService;

    @Autowired
    private ITableLoadService tableLoadService;

    @Autowired
    private ITableStorageService tableStorageService;

    @Autowired
    private ITableStatisticsService tableStatisticsService;

    @Autowired
    private IDvsTableService dvsTableService;

    @Autowired
    private IDvsTableColumnService dvsTableColumnService;

    @Autowired
    private IDvsTableDdlService dvsTableDdlService;

    @Autowired
    private IEtlJobService etlJobService;

    @Autowired
    private IBdmJobService bdmJobService;

    @Autowired
    private IBdmScriptService bdmScriptService;

    @Autowired
    private IBdmOutTableService bdmOutTableService;

    @Autowired
    private IDatasourceService datasourceService;

    @Autowired
    private IJdbcSourceService jdbcSourceService;

    @Autowired
    private PortExchanger portExchanger;

    @Autowired
    private IStorageBoxService storageBoxService;

    @Autowired
    private IBdmInTableService bdmInTableService;

    /**
     * 分区日期目录 抽取数据使用
     */
    private String ts;


    @XxlJob("DvsAdminJobScheduleHandler")
    public ReturnT<String> execute(String param) throws Exception {
        Long jobId = XxlJobHelper.getJobId();
        log.info("DvsAdminJobScheduleHandler is triggered ,param:{}, jobId:{}", param, jobId);
        ts = LocalDateTime.now().plusDays(-1L).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        log.info("DvsAdminJobScheduleHandler exe ts info:{}", ts);

        // 根据jobId查询对应的任务调度
        List<JobSchedule> jobScheduleList = getJobScheduleList(jobId);
        JobSchedule jobScheduleM = jobScheduleList.get(0);
        String scheduleCode = jobScheduleM.getScheduleCode();
        String dvsCode = jobScheduleM.getDvsCode();
        Integer env = jobScheduleM.getEnv();
        // 保存或更新调度实例参数 每次新生成
        List<ScheduleInstance> saveScheduleInstanceList = new ArrayList<>();
        // 任务节点
        List<ScheduleNode> scheduleNodeList = getScheduleNodeList(scheduleCode);
        // 任务节点对应的边界
        List<ScheduleEdge> scheduleEdgeList = getScheduleEdgeList(scheduleCode);
        // 任务节点对应的节点任务
        List<NodeJobs> nodeJobsList = getNodeJobsList(scheduleNodeList);
        // 保存或更新调度实例参数 每次新生成实例
        List<JobInstance> saveJobInstanceList = new ArrayList<>();
        // 获取空间下对应的存储桶
        List<StorageBox> storageBoxList = getStorageBoxList(dvsCode, env);

        jobScheduleList.forEach(jobSchedule -> {
            try {
                handlerJobSchedule(jobSchedule, scheduleNodeList, scheduleEdgeList, nodeJobsList, saveJobInstanceList, storageBoxList, saveScheduleInstanceList);
            } catch (DtvsAdminException | InvalidCmdException | NoPortNodeException | InvalidConnException |
                     InterruptedException | PortConnectionException | JsonProcessingException e) {
//                throw new RuntimeException(e);
                log.error("执行调度任务抛出异常:{}", e);
            }
        });

        if (CollectionUtil.isNotEmpty(saveScheduleInstanceList)) {
            if (!scheduleInstanceService.saveOrUpdateBatch(saveScheduleInstanceList)) {
                throw new DtvsAdminException("保存调度实例失败");
            }
        }
        if (CollectionUtil.isNotEmpty(saveJobInstanceList)) {
            if (!jobInstanceService.saveOrUpdateBatch(saveJobInstanceList)) {
                throw new DtvsAdminException("保存任务实例失败");
            }
        }

//        if (CollectionUtil.isNotEmpty(saveJobInstanceList)) {
//            if (!jobInstanceService.saveOrUpdateBatch(saveJobInstanceList)) {
//                throw new DtvsAdminException("更新任务实例失败");
//            }
//            if (CollectionUtil.isNotEmpty(saveScheduleInstanceList)) {
//                // 按更新时间升序排序
//                List<JobInstance> saveJobInstanceListTemp = saveJobInstanceList.stream().sorted(Comparator.comparing(JobInstance::getUpdateTime)).collect(Collectors.toList());
//                saveScheduleInstanceList.forEach(s -> {
//                    List<JobInstance> jobInstanceList = saveJobInstanceListTemp.stream().filter(j -> j.getScheduleInstanceCode().equals(s.getScheduleInstanceCode()) && s.getEnv().intValue() == j.getEnv().intValue() && j.getExeStatus().intValue() != ExeStatusEnum.EXE_SUCCESS.getValue()).collect(Collectors.toList());
//                    if (CollectionUtil.isEmpty(jobInstanceList)) {
//                        s.setExeStatus(ExeStatusEnum.EXE_SUCCESS.getValue());
//                        // 调度成功截止任务实例偏移编码
//                        s.setOffset(saveJobInstanceListTemp.get(saveJobInstanceListTemp.size() - 1).getScheduleInstanceCode());
//                    } else {
//                        s.setExeStatus(ExeStatusEnum.EXE_FAIL.getValue());
//                        // 调失败截止任务实例偏移编码
//                        s.setOffset(jobInstanceList.get(jobInstanceList.size() - 1).getScheduleInstanceCode());
//                    }
//                    s.setUpdateTime(LocalDateTime.now());
//                });
//                if (!scheduleInstanceService.saveOrUpdateBatch(saveScheduleInstanceList)) {
//                    throw new DtvsAdminException("更新调度实例失败");
//                }
//            }
//        }


        return ReturnT.SUCCESS;
    }

    private List<StorageBox> getStorageBoxList(String dvsCode, Integer env) {
        return storageBoxService.list(Wrappers.<StorageBox>lambdaQuery().eq(StorageBox::getDvsCode, dvsCode).eq(StorageBox::getEnv, env));
    }

    private List<JobInstance> getSaveJobInstanceList(List<NodeJobs> nodeJobsList, List<ScheduleInstance> saveScheduleInstanceList, List<ScheduleNode> scheduleNodeList) throws DtvsAdminException {
        List<JobInstance> jobInstanceList = null;
        // 任务实例
        if (CollectionUtil.isNotEmpty(nodeJobsList)) {
            List<String> nodeCodeList = nodeJobsList.stream().map(NodeJobs::getNodeCode).distinct().collect(Collectors.toList());
            jobInstanceList = jobInstanceService.list(Wrappers.<JobInstance>lambdaQuery().in(JobInstance::getNodeCode, nodeCodeList));
            if (CollectionUtil.isEmpty(jobInstanceList)) {
                jobInstanceList = new ArrayList<>();
            }
            for (NodeJobs nodeJobs : nodeJobsList) {
                JobInstance jobInstance = null;
                if (CollectionUtil.isNotEmpty(jobInstanceList)) {
                    jobInstance = jobInstanceList.stream().filter(j -> j.getNodeCode().equals(nodeJobs.getNodeCode()) && j.getEnv().longValue() == nodeJobs.getEnv().longValue()).findFirst().orElse(null);
                }
                if (Objects.isNull(jobInstance)) {
                    jobInstance = mapperFactory.getMapperFacade().map(nodeJobs, JobInstance.class);
                    jobInstance.setCreateTime(LocalDateTime.now());
                }
                ScheduleInstance scheduleInstance = saveScheduleInstanceList.stream().filter(s -> s.getEnv().longValue() == nodeJobs.getEnv().longValue()).findFirst().orElse(null);
                if (Objects.isNull(scheduleInstance)) {
                    throw new DtvsAdminException("任务调度实例不存在");
                }
                jobInstance.setScheduleInstanceCode(scheduleInstance.getScheduleInstanceCode());
                if (CollectionUtil.isNotEmpty(scheduleNodeList)) {
                    ScheduleNode scheduleNode = scheduleNodeList.stream().filter(s -> s.getNodeCode().equals(nodeJobs.getNodeCode()) && s.getEnv().longValue() == nodeJobs.getEnv().longValue()).findFirst().orElse(null);
                    if (Objects.nonNull(scheduleNode)) {
                        jobInstance.setNodeId(scheduleNode.getNodeId());
                    }
                }

                jobInstanceList.add(jobInstance);
            }
        }
        return jobInstanceList;
    }

    private List<NodeJobs> getNodeJobsList(List<ScheduleNode> scheduleNodeList) {
        if (CollectionUtil.isNotEmpty(scheduleNodeList)) {
            List<String> nodeCodeList = scheduleNodeList.stream().map(ScheduleNode::getNodeCode).distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(nodeCodeList)) {
                return nodeJobsService.list(Wrappers.<NodeJobs>lambdaQuery().in(NodeJobs::getNodeCode, nodeCodeList));
            }
        }
        return null;
    }

    private List<ScheduleEdge> getScheduleEdgeList(String scheduleCode) {
        return scheduleEdgeService.list(Wrappers.<ScheduleEdge>lambdaQuery().eq(ScheduleEdge::getScheduleCode, scheduleCode));
    }

    private List<ScheduleNode> getScheduleNodeList(String scheduleCode) {
        return scheduleNodeService.list(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getScheduleCode, scheduleCode));
    }

    private List<ScheduleInstance> getSaveScheduleInstanceList(List<JobSchedule> jobScheduleList) {
        // 调度实例
        List<ScheduleInstance> scheduleInstanceList = scheduleInstanceService.list(Wrappers.<ScheduleInstance>lambdaQuery().eq(ScheduleInstance::getScheduleCode, jobScheduleList.get(0).getScheduleCode()));
        if (CollectionUtil.isEmpty(scheduleInstanceList)) {
            scheduleInstanceList = new ArrayList<>();
        }
        for (JobSchedule jobSchedule : jobScheduleList) {
            ScheduleInstance scheduleInstance = null;
            if (CollectionUtil.isNotEmpty(scheduleInstanceList)) {
                scheduleInstance = scheduleInstanceList.stream().filter(s -> s.getScheduleCode().equals(jobSchedule.getScheduleCode()) && s.getEnv().longValue() == jobSchedule.getEnv().longValue()).findFirst().orElse(null);
            }
            if (Objects.isNull(scheduleInstance)) {
                scheduleInstance = mapperFactory.getMapperFacade().map(jobSchedule, ScheduleInstance.class);
                scheduleInstance.setScheduleInstanceCode(UCodeUtil.produce());
                scheduleInstance.setCreateTime(LocalDateTime.now());
            }
            scheduleInstance.setUpdateTime(LocalDateTime.now());
            scheduleInstanceList.add(scheduleInstance);
        }

        return scheduleInstanceList;
    }

    private void handlerJobSchedule(JobSchedule jobSchedule, List<ScheduleNode> scheduleNodeList, List<ScheduleEdge> scheduleEdgeList, List<NodeJobs> nodeJobsList, List<JobInstance> saveJobInstanceList, List<StorageBox> storageBoxList, List<ScheduleInstance> saveScheduleInstanceList) throws DtvsAdminException, InvalidCmdException, NoPortNodeException, InvalidConnException, InterruptedException, PortConnectionException, JsonProcessingException {
        // todo 本次生命周期状态先不做校验
        // 执行 BASIC/DEV/PROD 环境下的任务调度
        Integer dev = jobSchedule.getEnv();
        // 根节点编码
        String rootNodeCode = scheduleNodeList.stream().filter(s -> s.getEnv().intValue() == dev.intValue() && s.getNodeType().intValue() == NodeTyepEnum.START_NODE.getValue()).findFirst().get().getNodeCode();
        log.info(" job schedule start node:{}", rootNodeCode);
        List<ScheduleNode> scheduleNodeEnvList = null;
        List<ScheduleEdge> scheduleEdgeEnvList = null;
        List<NodeJobs> nodeJobsEnvList = null;
        List<String> exeNodeJobsList = new ArrayList<>();
        Map<String, List<NodeJobs>> exeNodeJobsMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(scheduleNodeList)) {
            scheduleNodeEnvList = scheduleNodeList.stream().filter(s -> s.getEnv().intValue() == dev.intValue()).collect(Collectors.toList());
            log.info("job schedule node list:{}", ObjectMapperUtil.toJson(scheduleNodeEnvList));
            if (CollectionUtil.isNotEmpty(scheduleEdgeList)) {
                scheduleEdgeEnvList = scheduleEdgeList.stream().filter(s -> s.getEnv().intValue() == dev.intValue()).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(nodeJobsList)) {
                nodeJobsEnvList = nodeJobsList.stream().filter(s -> s.getEnv().intValue() == dev.intValue()).collect(Collectors.toList());
                exeNodeJobsMap = nodeJobsEnvList.stream().collect(Collectors.groupingBy(NodeJobs::getNodeCode));
            }
            if (CollectionUtil.isNotEmpty(scheduleEdgeEnvList)) {
                log.info("job schedule edge node list:{}", ObjectMapperUtil.toJson(scheduleEdgeEnvList));
                List<String> headerScheduleNodes = scheduleEdgeEnvList.stream().filter(s -> s.getFromNode().equals(rootNodeCode)).map(ScheduleEdge::getToNode).distinct().collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(headerScheduleNodes)) {
                    Boolean flag = Boolean.TRUE;
                    while (flag) {
                        List<String> headerNodesTemp = new ArrayList<>();
                        for (String headerNodeCode : headerScheduleNodes) {
                            for (ScheduleEdge scheduleEdge : scheduleEdgeEnvList) {
                                if (scheduleEdge.getFromNode().equals(headerNodeCode)) {
                                    // 当前头节点的子节点 作为下次执行的头节点
                                    headerNodesTemp.add(scheduleEdge.getToNode());
                                }
                            }
                        }
                        if (CollectionUtil.isNotEmpty(headerNodesTemp)) {
                            // 将节点添加到执行节点
                            headerScheduleNodes = headerScheduleNodes.stream().distinct().collect(Collectors.toList());
                            List<String> exeNodeCodes = getExeNodeCodes(headerScheduleNodes, scheduleEdgeEnvList);
                            if (CollectionUtil.isNotEmpty(exeNodeCodes)) {
                                exeNodeJobsList.addAll(exeNodeCodes);
                            }
                            headerScheduleNodes.clear();
                            headerScheduleNodes.addAll(headerNodesTemp);
                        } else {
                            // 将节点添加到执行节点
                            if (CollectionUtil.isNotEmpty(headerScheduleNodes)) {
                                headerScheduleNodes = headerScheduleNodes.stream().distinct().collect(Collectors.toList());
                                List<String> exeNodeCodes = getExeNodeCodes(headerScheduleNodes, scheduleEdgeEnvList);
                                if (CollectionUtil.isNotEmpty(exeNodeCodes)) {
                                    exeNodeJobsList.addAll(exeNodeCodes);
                                }
                                headerScheduleNodes.clear();
                            }
                            flag = Boolean.FALSE;
                        }
                    }
                }
            }

            if (CollectionUtil.isNotEmpty(exeNodeJobsList)) {
                log.info("exe node jobs info:{}", ObjectMapperUtil.toJson(exeNodeJobsList));
                exeNodeJobsList = exeNodeJobsList.stream().distinct().collect(Collectors.toList());
                exeNodeJobsPort(exeNodeJobsList, exeNodeJobsMap, scheduleEdgeEnvList, saveJobInstanceList, storageBoxList, saveScheduleInstanceList, jobSchedule);
            }

        }

    }

    private void exeNodeJobsPort(List<String> exeNodeJobsList, Map<String, List<NodeJobs>> exeNodeJobsMap, List<ScheduleEdge> scheduleEdgeEnvList, List<JobInstance> saveJobInstanceList, List<StorageBox> storageBoxList, List<ScheduleInstance> saveScheduleInstanceList, JobSchedule jobSchedule) throws DtvsAdminException, InvalidCmdException, NoPortNodeException, InvalidConnException, InterruptedException, PortConnectionException, JsonProcessingException {
        if (CollectionUtil.isNotEmpty(exeNodeJobsList)) {
            // 创建调度实例
            ScheduleInstance scheduleInstance = getScheduleInstance(jobSchedule);
            // scheduleEdgeEnvList 判断调度依赖是否有效 或者执行成功
            for (String nodeCode : exeNodeJobsList) {
                List<NodeJobs> nodeJobsList = exeNodeJobsMap.get(nodeCode);
                if (CollectionUtil.isNotEmpty(nodeJobsList)) {
                    NodeJobs nodeJobs = nodeJobsList.get(0);
                    log.info("exeNodeJobsPort nodeJobs info:{}", ObjectMapperUtil.toJson(nodeJobs));
                    if (Objects.nonNull(nodeJobs)) {
                        if (Objects.nonNull(nodeJobs.getJobType())) {
                            if (nodeJobs.getJobType().intValue() == JobTypeEnum.ETL_JOB.getValue()) {
                                exeEtlJob(nodeJobs, scheduleEdgeEnvList, saveJobInstanceList, storageBoxList, scheduleInstance.getScheduleCode());
                            } else if (nodeJobs.getJobType().intValue() == JobTypeEnum.BDM_JOB.getValue()) {
                                exeBdmJob(nodeJobs, scheduleEdgeEnvList, saveJobInstanceList, storageBoxList, scheduleInstance.getScheduleCode());
                            }
                        }
                    }
                }
            }
            // 编辑调度实例
            scheduleInstance.setOffset(DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_TIME_FORMATTER));
            scheduleInstance.setUpdateTime(LocalDateTime.now());
            if (CollectionUtil.isNotEmpty(saveJobInstanceList)) {
                if (saveJobInstanceList.stream().filter(s -> Objects.nonNull(s.getExeStatus()) && (s.getExeStatus().intValue() == ExeStatusEnum.EXE_FAIL.getValue() || s.getExeStatus().intValue() == ExeStatusEnum.EXE_DONE.getValue())).count() > 0) {
                    scheduleInstance.setExeStatus(ExeStatusEnum.EXE_SUCCESS.getValue());
                }
            }

            scheduleInstance.setScheduleInstanceCode(scheduleInstance.getScheduleInstanceCode() + "_" + DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_DATE_FORMATTER_YYYYMMDDHHMM));
            saveScheduleInstanceList.add(scheduleInstance);
            // 填充任务实例
            saveJobInstanceList.forEach(s -> {
                s.setScheduleInstanceCode(scheduleInstance.getScheduleInstanceCode());
            });
            log.info("save job instance list info:{}", ObjectMapperUtil.toJson(saveJobInstanceList));
        }
    }

    private ScheduleInstance getScheduleInstance(JobSchedule jobSchedule) {
        ScheduleInstance scheduleInstance = mapperFactory.getMapperFacade().map(jobSchedule, ScheduleInstance.class);
        scheduleInstance.setScheduleInstanceCode(UCodeUtil.produce() + "_" + DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_DATE_FORMATTER_YYYYMMDDHHMM));
        scheduleInstance.setExeStatus(ExeStatusEnum.EXE_DONE.getValue());
        scheduleInstance.setCreateTime(LocalDateTime.now());
        return scheduleInstance;
    }

    private void exeEtlJob(NodeJobs nodeJobs, List<ScheduleEdge> scheduleEdgeEnvList, List<JobInstance> saveJobInstanceList, List<StorageBox> storageBoxList, String scheduleCode) throws DtvsAdminException, InterruptedException, NoPortNodeException, PortConnectionException, JsonProcessingException, InvalidCmdException, InvalidConnException {
        checkNodeJobsDependency(nodeJobs, scheduleEdgeEnvList);
        // 保存抽取表统计数据
        List<TableStatistics> saveTableStatisticsList = new ArrayList<>();
        String etlJobCode = nodeJobs.getJobCode();
        Integer env = nodeJobs.getEnv();
        // 任务实例结果保存
        JobInstance jobInstance = getJobInstance(nodeJobs);
        saveJobInstanceList.add(jobInstance);

        // 获取集成任务对应的数据源信息
        EtlJob etlJob = etlJobService.getOne(Wrappers.<EtlJob>lambdaQuery().eq(EtlJob::getEtlJobCode, etlJobCode).eq(EtlJob::getEnv, env).
                eq(EtlJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(etlJob)) {
            throw new DtvsAdminException("集成任务不存在");
        }
        Datasource datasource = datasourceService.getOne(Wrappers.<Datasource>lambdaQuery().eq(Datasource::getDatasourceCode, etlJob.getDatasourceCode()).
                eq(Datasource::getEnv, env).eq(Datasource::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(datasource)) {
            throw new DtvsAdminException("集成任务对应的数据源不存在");
        }
        JdbcSource jdbcSource = jdbcSourceService.getOne(Wrappers.<JdbcSource>lambdaQuery().eq(JdbcSource::getDatasourceCode, datasource.getDatasourceCode()).
                eq(JdbcSource::getEnv, env));
        if (Objects.isNull(jdbcSource)) {
            throw new DtvsAdminException("集成任务对应的数据源JDBC配置信息不存在");
        }
        List<TableExtract> tableExtractList = tableExtractService.list(Wrappers.<TableExtract>lambdaQuery().eq(TableExtract::getEtlJobCode, etlJobCode).eq(TableExtract::getEnv, env));
        List<TableTransform> tableTransformList = tableTransformService.list(Wrappers.<TableTransform>lambdaQuery().eq(TableTransform::getEtlJobCode, etlJobCode).eq(TableTransform::getEnv, env));
        List<TableLoad> tableLoadList = tableLoadService.list(Wrappers.<TableLoad>lambdaQuery().eq(TableLoad::getEtlJobCode, etlJobCode).eq(TableLoad::getEnv, env));
        if (CollectionUtil.isNotEmpty(tableExtractList)) {
            List<String> tableCodes = tableExtractList.stream().map(TableExtract::getTableCode).collect(Collectors.toList());
            List<TableStorage> tableStorageList = tableStorageService.list(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodes).eq(TableStorage::getEnv, env));
            List<TableStatistics> tableStatisticsList = tableStatisticsService.list(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).eq(TableStatistics::getEnv, env));
            int i = 0;
            for (TableExtract tableExtract : tableExtractList) {
                String sql;
                TableLoad tableLoad = null;
                List<TableTransform> tableTransforms = null;
                TableStorage tableStorage;
                TableStatistics tableStatistics = null;
                StorageBox storageBox = null;
                String tableCode = tableExtract.getTableCode();
                if (CollectionUtil.isNotEmpty(tableLoadList)) {
                    tableLoad = tableLoadList.stream().filter(t -> t.getEtlJobCode().equals(etlJobCode) && t.getEnv().intValue() == env.intValue() && t.getTableCode().equals(tableCode)).findFirst().orElse(null);
                }
                if (CollectionUtil.isNotEmpty(tableTransformList)) {
                    tableTransforms = tableTransformList.stream().filter(t -> t.getEtlJobCode().equals(etlJobCode) && t.getEnv().intValue() == env.intValue() && t.getTableCode().equals(tableCode)).collect(Collectors.toList());
                }
                if (CollectionUtil.isNotEmpty(tableStorageList)) {
                    tableStorage = tableStorageList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(tableStorage)) {
                        throw new DtvsAdminException("集成任务对应的存储表不存在");
                    }
                } else {
                    tableStorage = null;
                }
                if (CollectionUtil.isNotEmpty(tableStatisticsList)) {
                    tableStatistics = tableStatisticsList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(tableStatistics)) {
                        tableStatistics = new TableStatistics();
                        tableStatistics.setTableCode(tableCode);
                        tableStatistics.setEnv(env);
                        tableStatistics.setCreateTime(LocalDateTime.now());
                        tableStatistics.setUpdateTime(LocalDateTime.now());
                    }
                }
                if (CollectionUtil.isNotEmpty(storageBoxList)) {
                    storageBox = storageBoxList.stream().filter(s -> s.getBoxCode().equals(tableStorage.getBoxCode()) && s.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(storageBox)) {
                        throw new DtvsAdminException("集成任务抽取表对应的存储桶不存在");
                    }
                }

                sql = getEtlJobSql(tableExtract, tableLoad, tableTransforms);
                if (StringUtils.isNotBlank(sql)) {
                    DiReqPacket diPacket = new DiReqPacket();
                    diPacket.setCommandId(org.apex.dataverse.core.util.UCodeUtil.produce());
                    diPacket.setIncr(false);
                    diPacket.setDriver(DataSourceTypeEnum.getDriverNameByValue(datasource.getDatasourceTypeId()));
                    diPacket.setUser(jdbcSource.getUserName());
                    diPacket.setPassword(jdbcSource.getPassword());
                    diPacket.setQuery(sql);
                    diPacket.setFetchSize(500);
                    diPacket.setNumPartitions((short) 2);
                    diPacket.setPartitionColumn(null);
                    diPacket.setLowerBound(null);
                    diPacket.setUpperBound(null);
                    diPacket.setQueryTimeout(50000);
                    diPacket.setUrl(jdbcSource.getJdbcUrl());
                    // 抽取数据放入ts目录下 ts格式yyyyMMdd
                    diPacket.setOutput(HdfsOutput.newOutput(tableStorage.getStorageName() + SqlGrammarConst.SLASH + ts));
                    diPacket.setSourceStorageId(storageBox.getStorageId());

                    Request<Packet> request = new Request<>();
                    request.setPacket(diPacket);
                    Header header = Header.newHeader(CmdSet.DATA_INTEGRATION.getReq(), ISerialize.JDK_SERIALIZE);
                    request.setHeader(header);

                    // 查找对应的存储区 选择对应的连接器
                    PortConnection conn = portExchanger.getConn(storageBox.getStorageId().toString());
                    Response<Packet> response;
                    try {
                        response = conn.exe(request);
                    } finally {
                        portExchanger.close(conn);
                    }

                    DiRspPacket diRspPacket = (DiRspPacket) response.getPacket();
                    log.info("exe etl job response:{}", ObjectMapperUtil.toJson(diRspPacket));
                    // 将集成任务抽取表进入实例偏移结束节点
                    jobInstance.setEndOffset(DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_TIME_FORMATTER));
                    jobInstance.setUpdateTime(LocalDateTime.now());
                    if (diRspPacket.getState().intValue() == ExeStatusEnum.EXE_SUCCESS.getValue()) {
                        tableStatistics.setCount(diRspPacket.getDiCount());
                        jobInstance.setExeStatus(ExeStatusEnum.EXE_SUCCESS.getValue());
                    } else {
                        // 抽取结果放入统计表中
                        jobInstance.setExeStatus(ExeStatusEnum.EXE_FAIL.getValue());
                        jobInstance.setExeResult("集成任务抽取表任务失败:" + diRspPacket.getMessage());
                    }
                    saveTableStatisticsList.add(tableStatistics);
                    jobInstance.setJobInstanceCode(jobInstance.getJobInstanceCode() + "_" + DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_DATE_FORMATTER_YYYYMMDDHHMM));
                    jobInstance.setScheduleInstanceCode(scheduleCode);
                    if (!jobInstanceService.save(jobInstance)) {
                        throw new DtvsAdminException("保存任务实例失败");
                    }
                }
            }
        }
        // 更新统计表
        if (CollectionUtil.isNotEmpty(saveTableStatisticsList)) {
            if (!tableStatisticsService.saveOrUpdateBatch(saveTableStatisticsList)) {
                log.error("更新统计表失败");
            }
        }
    }

    private JobInstance getJobInstance(NodeJobs nodeJobs) throws DtvsAdminException {
        JobInstance jobInstance = mapperFactory.getMapperFacade().map(nodeJobs, JobInstance.class);
        ScheduleNode scheduleNode = scheduleNodeService.getOne(Wrappers.<ScheduleNode>lambdaQuery().eq(ScheduleNode::getNodeCode, nodeJobs.getNodeCode()).eq(ScheduleNode::getEnv, nodeJobs.getEnv()));
        if (Objects.isNull(scheduleNode)) {
            throw new DtvsAdminException("执行任务节点对应的调度节点不存在");
        }
        jobInstance.setNodeId(scheduleNode.getNodeId());
        jobInstance.setCreateTime(LocalDateTime.now());
        jobInstance.setStartOffset(DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_TIME_FORMATTER));
        jobInstance.setExeStatus(ExeStatusEnum.EXE_DONE.getValue());
        jobInstance.setJobInstanceCode(UCodeUtil.produce() + "_" + DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_DATE_FORMATTER_YYYYMMDDHHMM));
        return jobInstance;
    }

    private void checkNodeJobsDependency(NodeJobs nodeJobs, List<ScheduleEdge> scheduleEdgeEnvList) throws DtvsAdminException, JsonProcessingException {
        log.info("checkNodeJobsDependency nodeJobs:{}", ObjectMapperUtil.toJson(nodeJobs));
        if (CollectionUtil.isNotEmpty(scheduleEdgeEnvList)) {
            List<ScheduleEdge> scheduleEdgeList = scheduleEdgeEnvList.stream().filter(s -> s.getToNode().equals(nodeJobs.getNodeCode())).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(scheduleEdgeList)) {
                for (ScheduleEdge scheduleEdge : scheduleEdgeList) {
                    if (scheduleEdge.getIsValid().intValue() == IsValidStatus.VALID_STATUS.getValue()) {
                        List<JobInstance> jobInstanceList = jobInstanceService.list(Wrappers.<JobInstance>lambdaQuery().eq(JobInstance::getNodeCode, scheduleEdge.getFromNode()).eq(JobInstance::getEnv, nodeJobs.getEnv()).orderByDesc(JobInstance::getCreateTime).last(SqlGrammarConst.LIMIT_ONE));
                        if (CollectionUtil.isNotEmpty(jobInstanceList)) {
                            JobInstance jobInstance = jobInstanceList.get(0);
                            if (Objects.nonNull(jobInstance) && Objects.nonNull(jobInstance.getExeStatus()) && jobInstance.getExeStatus().intValue() == ExeStatusEnum.EXE_FAIL.getValue()) {
                                throw new DtvsAdminException("节点任务前置依赖节点任务失败, 前置失败任务实例:" + ObjectMapperUtil.toJson(jobInstance));
                            }
                        }
                    }
                }
            }
        }
    }

    private String getEtlJobSql(TableExtract tableExtract, TableLoad tableLoad, List<TableTransform> tableTransforms) {
        StringBuilder sql = new StringBuilder();
        if (Objects.nonNull(tableExtract) && Objects.nonNull(tableLoad) && CollectionUtil.isNotEmpty(tableTransforms)) {
            sql.append(SqlGrammarConst.SELECT);
            int i = 0;
            int length = tableTransforms.size();
            for (TableTransform tableTransform : tableTransforms) {
                sql.append(tableTransform.getOutColumn());
                i++;
                if (i < length) {
                    sql.append(SqlGrammarConst.COMMA);
                }
            }
            sql.append(SqlGrammarConst.FROM);
            sql.append(tableExtract.getOriginTableName());
        }

        return sql.toString();
    }

    /**
     * todo 第一版本实现简单两级结构 创建表 写入表语句, 后面根据表输出类型 输出 临时表 事实表
     *
     * @param nodeJobs
     * @param scheduleEdgeEnvList
     * @param saveJobInstanceList
     * @param storageBoxList
     * @param scheduleCode
     * @throws DtvsAdminException
     * @throws InvalidCmdException
     * @throws InvalidConnException
     * @throws InterruptedException
     * @throws NoPortNodeException
     * @throws PortConnectionException
     * @throws JsonProcessingException
     */
    private void exeBdmJob(NodeJobs nodeJobs, List<ScheduleEdge> scheduleEdgeEnvList, List<JobInstance> saveJobInstanceList, List<StorageBox> storageBoxList, String scheduleCode) throws DtvsAdminException, InvalidCmdException, InvalidConnException, InterruptedException, NoPortNodeException, PortConnectionException, JsonProcessingException {
        checkNodeJobsDependency(nodeJobs, scheduleEdgeEnvList);
        // 保存抽取表统计数据
        List<TableStatistics> saveTableStatisticsList = new ArrayList<>();
        // 执行SQL集合
        List<SqlInfo> sqlInfoList = new ArrayList<>();
        String bdmJobCode = nodeJobs.getJobCode();
        Integer env = nodeJobs.getEnv();
        // 任务实例结果保存
        JobInstance jobInstance = getJobInstance(nodeJobs);
        // 开发任务
        BdmJob bdmJob = bdmJobService.getOne(Wrappers.<BdmJob>lambdaQuery().eq(BdmJob::getBdmJobCode, bdmJobCode).eq(BdmJob::getEnv, env).
                eq(BdmJob::getIsDeleted, IsDeletedEnum.NO.getValue()));
        if (Objects.isNull(bdmJob)) {
            throw new DtvsAdminException("开发任务不存在");
        }
        // 开发任务执行SQL语句 对应的SQL语句执行后的表信息 SQL执行依赖的表信息
        List<BdmScript> bdmScriptList = bdmScriptService.list(Wrappers.<BdmScript>lambdaQuery().eq(BdmScript::getBdmJobCode, bdmJobCode).eq(BdmScript::getEnv, env).orderByDesc(BdmScript::getCreateTime));
        BdmScript bdmScript = null;
        if (CollectionUtil.isNotEmpty(bdmScriptList)) {
            bdmScript = bdmScriptService.list(Wrappers.<BdmScript>lambdaQuery().eq(BdmScript::getBdmJobCode, bdmJobCode).eq(BdmScript::getEnv, env).orderByDesc(BdmScript::getCreateTime)).get(0);
        }
        List<BdmOutTable> bdmOutTableList = bdmOutTableService.list(Wrappers.<BdmOutTable>lambdaQuery().eq(BdmOutTable::getBdmJobCode, bdmJobCode).eq(BdmOutTable::getEnv, env));
        List<BdmInTable> bdmInTableList = bdmInTableService.list(Wrappers.<BdmInTable>lambdaQuery().eq(BdmInTable::getBdmJobCode, bdmJobCode).eq(BdmInTable::getEnv, env));
        if (CollectionUtil.isNotEmpty(bdmOutTableList) && Objects.nonNull(bdmScript) && CollectionUtil.isNotEmpty(bdmInTableList)) {
            List<String> tableCodes = bdmOutTableList.stream().map(BdmOutTable::getTableCode).distinct().collect(Collectors.toList());
            List<String> inTableCodes = bdmInTableList.stream().map(BdmInTable::getTableCode).distinct().collect(Collectors.toList());
            List<String> tableCodesInAndOut = new ArrayList<>(tableCodes);
            tableCodesInAndOut.addAll(inTableCodes);
            List<TableStorage> tableStorageList = tableStorageService.list(Wrappers.<TableStorage>lambdaQuery().in(TableStorage::getTableCode, tableCodesInAndOut).eq(TableStorage::getEnv, env));
            List<TableStatistics> tableStatisticsList = tableStatisticsService.list(Wrappers.<TableStatistics>lambdaQuery().in(TableStatistics::getTableCode, tableCodes).eq(TableStatistics::getEnv, env));
            int i = 0;
            // 执行开发任务
            String createTableSql = "";
            String exeSql = "";
            String[] scriptSqlArr = bdmScript.getBdmScript().replace("\n", "").split(SqlGrammarConst.SEMICOLON);
            for (String scriptSql : scriptSqlArr) {
                if (!scriptSql.toLowerCase().startsWith("create")) {
                    exeSql = scriptSql.toLowerCase();
                }
            }
            TableStatistics tableStatistics = null;
            StorageBox storageBox = null;
            for (BdmOutTable bdmOutTable : bdmOutTableList) {
                List<BdmOutTable> outTables = bdmOutTableList.stream().filter(b -> b.getBdmJobCode().equals(bdmJobCode) && b.getEnv().intValue() == env.intValue()).collect(Collectors.toList());
                String tableCode = outTables.get(0).getTableCode();
                TableStorage tableStorage;

                if (CollectionUtil.isNotEmpty(tableStorageList)) {
                    tableStorage = tableStorageList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(tableStorage)) {
                        throw new DtvsAdminException("开发任务对应的存储表不存在");
                    }
                } else {
                    tableStorage = null;
                }
                if (CollectionUtil.isNotEmpty(tableStatisticsList)) {
                    tableStatistics = tableStatisticsList.stream().filter(t -> t.getTableCode().equals(tableCode) && t.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(tableStatistics)) {
                        tableStatistics = new TableStatistics();
                        tableStatistics.setTableCode(tableCode);
                        tableStatistics.setEnv(env);
                        tableStatistics.setCreateTime(LocalDateTime.now());
                        tableStatistics.setUpdateTime(LocalDateTime.now());
                    }
                }
                if (CollectionUtil.isNotEmpty(storageBoxList)) {
                    storageBox = storageBoxList.stream().filter(s -> Objects.nonNull(tableStorage) && s.getBoxCode().equals(tableStorage.getBoxCode()) && s.getEnv().intValue() == env.intValue()).findFirst().orElse(null);
                    if (Objects.isNull(storageBox)) {
                        throw new DtvsAdminException("开发任务抽取表对应的存储桶不存在");
                    }
                }
                if (StringUtils.isNotBlank(exeSql)) {
                    Boolean overwrite = Boolean.TRUE;
                    if (exeSql.contains(SqlGrammarConst.INSERT_OVERWRITE)) {
                        exeSql = exeSql.substring(exeSql.indexOf(SqlGrammarConst.SELECT_WITH_BLANK_LOWER));
                    } else if (exeSql.contains(SqlGrammarConst.INSERT_INTO_LOWER)) {
                        overwrite = Boolean.FALSE;
                        exeSql = exeSql.substring(exeSql.indexOf(SqlGrammarConst.SELECT_WITH_BLANK_LOWER));
                    }
                    List<TableStorage> outTableStorageList = tableStorageList.stream().filter(t -> t.getTableCode().equals(bdmOutTable.getTableCode()) && t.getEnv().intValue() == bdmOutTable.getEnv().intValue()).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(outTableStorageList)) {
                        TableStorage outTableStorage = outTableStorageList.get(0);
                        if (Objects.nonNull(outTableStorage)) {
                            SqlInfo sqlInfo;
                            if (overwrite) {
                                sqlInfo = new SqlInfo(bdmOutTable.getBdmOutTableId().intValue(), exeSql, bdmOutTable.getTableName(), outTableStorage.getStorageName() + SqlGrammarConst.SLASH + ts);
                            } else {
                                sqlInfo = new SqlInfo(bdmOutTable.getBdmOutTableId().intValue(), exeSql, bdmOutTable.getTableName(), outTableStorage.getStorageName() + SqlGrammarConst.SLASH + ts, Boolean.FALSE);
                            }
                            sqlInfoList.add(sqlInfo);
                        }
                    }
                }
            }

            if (CollectionUtil.isNotEmpty(sqlInfoList)) {
                ExeSqlJobReqPacket exeSqlJobReqPacket = new ExeSqlJobReqPacket();
                if (env.intValue() == EnvEnum.BAISC.getValue()) {
                    exeSqlJobReqPacket.setExeEnv((ExeEnv.BASIC.getCode()));
                }
                if (env.intValue() == EnvEnum.DEV.getValue()) {
                    exeSqlJobReqPacket.setExeEnv((ExeEnv.DEV.getCode()));
                }
                if (env.intValue() == EnvEnum.PROD.getValue()) {
                    exeSqlJobReqPacket.setExeEnv((ExeEnv.PROD.getCode()));
                }
                exeSqlJobReqPacket.setStoreInfos(getBdmJobStoreInfo(tableStorageList, bdmInTableList));
                exeSqlJobReqPacket.setCommandId(org.apex.dataverse.core.util.UCodeUtil.produce());
                exeSqlJobReqPacket.setSourceStorageId(storageBox.getStorageId());
                exeSqlJobReqPacket.setSqlInfos(sqlInfoList);

                Request<Packet> request = new Request<>();
                request.setPacket(exeSqlJobReqPacket);

                Header header = Header.newHeader(CmdSet.EXE_SQL_JOB.getReq());

                request.setHeader(header);
                log.info("exe bdm job request info:{}", ObjectMapperUtil.toJson(request));

                // 查找对应的存储区 选择对应的连接器
                PortConnection conn = portExchanger.getConn(storageBox.getStorageId().toString());
                Response<Packet> response;
                try {
                    response = conn.exe(request);
                } finally {
                    portExchanger.close(conn);
                }

                log.info("exe bdm job response:{}", ObjectMapperUtil.toJson(response));
                ExeSqlJobRspPacket exeSqlJobRspPacket = (ExeSqlJobRspPacket) response.getPacket();
                log.info("exe bdm job diRspPacket response:{}", ObjectMapperUtil.toJson(exeSqlJobRspPacket));

                jobInstance.setUpdateTime(LocalDateTime.now());
                if (CollectionUtil.isNotEmpty(exeSqlJobRspPacket.getResults())) {
                    ResultInfo resultInfo = exeSqlJobRspPacket.getResults().stream().filter(r -> r.getTableName().equals(bdmOutTableList.get(0).getTableName())).findFirst().orElse(null);
//                    ResultInfo resultInfo = exeSqlJobRspPacket.getResults().stream().findFirst().orElse(null);
                    if (Objects.nonNull(resultInfo)) {
                        tableStatistics.setCount(resultInfo.getCount());
                    }
                    jobInstance.setExeStatus(ExeStatusEnum.EXE_SUCCESS.getValue());
                } else {
                    // 抽取结果放入统计表中
                    jobInstance.setExeStatus(ExeStatusEnum.EXE_FAIL.getValue());
                    jobInstance.setExeResult("开发任务抽取表任务失败:" + exeSqlJobRspPacket.getMessage());
                }
                saveTableStatisticsList.add(tableStatistics);
                jobInstance.setJobInstanceCode(jobInstance.getJobInstanceCode() + "_" + DateUtils.localDateTimeToStr(LocalDateTime.now(), DateUtils.DATETIME_DATE_FORMATTER_YYYYMMDDHHMM));
                jobInstance.setScheduleInstanceCode(scheduleCode);
                if (!jobInstanceService.save(jobInstance)) {
                    throw new DtvsAdminException("保存任务实例失败");
                }
                saveJobInstanceList.add(jobInstance);
            }
        }

        // 统计表
        if (CollectionUtil.isNotEmpty(saveTableStatisticsList)) {
            if (!tableStatisticsService.saveOrUpdateBatch(saveTableStatisticsList)) {
                log.error("开发任务统计表失败");
            }
        }
    }

    private List<StoreInfo> getBdmJobStoreInfo(List<TableStorage> tableStorages, List<BdmInTable> inTables) throws DtvsAdminException {
        List<StoreInfo> storeInfoList = new ArrayList<>();
        if (CollectionUtil.isEmpty(inTables)) {
            throw new DtvsAdminException("开发任务对应的输出表不存在");
        }
        // 输出表路径
        for (BdmInTable bdmInTable : inTables) {
            TableStorage tableStorage = tableStorages.stream().filter(t -> t.getTableCode().equals(bdmInTable.getTableCode()) && t.getEnv().intValue() == bdmInTable.getEnv().intValue()).findFirst().orElse(null);
            if (Objects.nonNull(tableStorage)) {
                StoreInfo storeInfo = new StoreInfo();
                // 抽取数据放入ts目录下 ts格式yyyyMMdd
                storeInfo.setStorePath(tableStorage.getStorageName() + SqlGrammarConst.SLASH + ts);
                storeInfo.setTableName(bdmInTable.getTableName());
                storeInfoList.add(storeInfo);
            }

        }
        return storeInfoList;
    }

    /**
     * 将同级的节点放到执行节点
     *
     * @param headerNodesTemp
     * @param scheduleEdgeEnvList
     * @return
     */
    public List<String> getExeNodeCodes(List<String> headerNodesTemp, List<ScheduleEdge> scheduleEdgeEnvList) {
        List<ScheduleEdge> scheduleEdgeListTemp = new ArrayList<>(scheduleEdgeEnvList);
        List<String> addExeNodeCodes = new ArrayList<>();
        while (addExeNodeCodes.size() != headerNodesTemp.size()) {
            List<String> exeHeaderNodesTemp;
            if (CollectionUtil.isEmpty(addExeNodeCodes)) {
                exeHeaderNodesTemp = new ArrayList<>(headerNodesTemp);
            } else {
                exeHeaderNodesTemp = headerNodesTemp.stream().filter(h -> addExeNodeCodes.stream().filter(n -> n.equals(h)).count() < 1).collect(Collectors.toList());
            }
            for (String nodeCode : exeHeaderNodesTemp) {
                // 没有下游边
                if (scheduleEdgeListTemp.stream().filter(s -> s.getFromNode().equals(nodeCode)).count() < 1) {
                    addExeNodeCode(nodeCode, addExeNodeCodes);
                }
                // 没有同级依赖
                List<String> finalHeaderNodesTemp = headerNodesTemp;
                List<ScheduleEdge> depScheduleEdgeList = scheduleEdgeListTemp.stream().filter(s -> finalHeaderNodesTemp.contains(s.getFromNode()) && s.getToNode().equals(nodeCode)).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(depScheduleEdgeList)) {
                    addExeNodeCode(nodeCode, addExeNodeCodes);
                } else {
                    // 排除同级依赖 且将对应的边删除
                    ScheduleEdge scheduleEdge = depScheduleEdgeList.get(0);
                    ScheduleEdge depScheduleEdge = getDepHeadNodeCode(scheduleEdge, scheduleEdgeListTemp, finalHeaderNodesTemp);
                    addExeNodeCode(depScheduleEdge.getFromNode(), addExeNodeCodes);
                    scheduleEdgeListTemp = scheduleEdgeListTemp.stream().filter(s -> s.getFromNode().equals(depScheduleEdge.getFromNode()) && s.getToNode().equals(s.getToNode())).collect(Collectors.toList());
                }
            }
        }
        scheduleEdgeListTemp.clear();

        return addExeNodeCodes;
    }

    /**
     * 获取同级依赖的首边
     *
     * @param scheduleEdge
     * @param scheduleEdgeEnvList
     * @param finalHeaderNodesTemp
     * @return
     */
    private ScheduleEdge getDepHeadNodeCode(ScheduleEdge scheduleEdge, List<ScheduleEdge> scheduleEdgeEnvList, List<String> finalHeaderNodesTemp) {
        List<ScheduleEdge> scheduleEdgeList = scheduleEdgeEnvList.stream().filter(s -> s.getToNode().equals(scheduleEdge.getFromNode())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(scheduleEdgeList) && !finalHeaderNodesTemp.contains(scheduleEdgeList.get(0).getFromNode())) {
            return scheduleEdge;
        } else {
            getDepHeadNodeCode(scheduleEdgeList.get(0), scheduleEdgeEnvList, finalHeaderNodesTemp);
        }
        return null;
    }

    private void addExeNodeCode(String nodeCode, List<String> addExeNodeCodes) {
        if (!addExeNodeCodes.contains(nodeCode)) {
            addExeNodeCodes.add(nodeCode);
        }
    }

    private List<JobSchedule> getJobScheduleList(Long jobId) throws DtvsAdminException {
        List<JobSchedule> jobScheduleList = jobScheduleService.list(Wrappers.<JobSchedule>lambdaQuery().eq(JobSchedule::getJobId, jobId));
        if (CollectionUtil.isEmpty(jobScheduleList)) {
            throw new DtvsAdminException("任务调度不存在");
        }
        return jobScheduleList;
    }
}
