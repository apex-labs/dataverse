//import cn.hutool.core.collection.CollectionUtil;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.apex.dataverse.entity.ScheduleEdge;
//import org.apex.dataverse.jobhandler.DtvsAdminDemoHandler;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @ClassName: Demo
// * @Author: wwd
// * @TODO:
// * @Date: 2024/3/1 11:47
// */
//public class Demo {
//
//    public static void main(String[] args) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        DtvsAdminDemoHandler demoHandler = new DtvsAdminDemoHandler();
//        List<String> exeNodeJobsList = new ArrayList();
//        List<ScheduleEdge> scheduleEdgeEnvList = null;
//        String scheduleEdgeStr = "[{\"edgeId\":45,\"edgeCode\":\"23c8urg6\",\"scheduleCode\":\"23c8u3ap\",\"fromNode\":\"23c8u3ap1\",\"toNode\":\"23c8urfp\",\"env\":0,\"isValid\":1},{\"edgeId\":46,\"edgeCode\":\"23c8weyv\",\"scheduleCode\":\"23c8u3ap\",\"fromNode\":\"23c8u3ap1\",\"toNode\":\"23c8wsyr\",\"env\":0,\"isValid\":1},{\"edgeId\":48,\"edgeCode\":\"23c8zzgw\",\"scheduleCode\":\"23c8u3ap\",\"fromNode\":\"23c8wsyr\",\"toNode\":\"23c8zzgs\",\"env\":0,\"isValid\":1}]";
//        String rootNodeCode = "23c8u3ap1";
//        scheduleEdgeEnvList = objectMapper.readValue(scheduleEdgeStr, new TypeReference<List<ScheduleEdge>>(){});
//        List<String> headerScheduleNodes = scheduleEdgeEnvList.stream().filter(s -> s.getFromNode().equals(rootNodeCode)).map(ScheduleEdge::getToNode).distinct().collect(Collectors.toList());
//        if (CollectionUtil.isNotEmpty(headerScheduleNodes)) {
//            Boolean flag = Boolean.TRUE;
//            while (flag) {
//                List<String> headerNodesTemp = new ArrayList<>();
//                for (String headerNodeCode : headerScheduleNodes) {
//                    for (ScheduleEdge scheduleEdge : scheduleEdgeEnvList) {
//                        if (scheduleEdge.getFromNode().equals(headerNodeCode)) {
//                            // 当前头节点的子节点 作为下次执行的头节点
//                            headerNodesTemp.add(scheduleEdge.getToNode());
//                        }
//                    }
//                }
//                if (CollectionUtil.isNotEmpty(headerNodesTemp)) {
//                    // 将节点添加到执行节点
//                    headerScheduleNodes = headerScheduleNodes.stream().distinct().collect(Collectors.toList());
//                    List<String> exeNodeCodes = demoHandler.getExeNodeCodes(headerScheduleNodes, scheduleEdgeEnvList);
//                    if (CollectionUtil.isNotEmpty(exeNodeCodes)) {
//                        exeNodeJobsList.addAll(exeNodeCodes);
//                    }
//                    headerScheduleNodes.clear();
//                    headerScheduleNodes.addAll(headerNodesTemp);
//                } else {
//                    // 将节点添加到执行节点
//                    if (CollectionUtil.isNotEmpty(headerScheduleNodes)) {
//                        headerScheduleNodes = headerScheduleNodes.stream().distinct().collect(Collectors.toList());
//                        List<String> exeNodeCodes = demoHandler.getExeNodeCodes(headerScheduleNodes, scheduleEdgeEnvList);
//                        if (CollectionUtil.isNotEmpty(exeNodeCodes)) {
//                            exeNodeJobsList.addAll(exeNodeCodes);
//                        }
//                        headerScheduleNodes.clear();
//                    }
//                    flag = Boolean.FALSE;
//                }
//            }
//        }
//        System.out.println(exeNodeJobsList);
//    }
//}
