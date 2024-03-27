package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apex.dataverse.entity.NodeJobs;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>项目名称: me-router
 *
 * <p>文件名称:
 *
 * <p>描述:
 *
 * <p>创建时间: 2021/6/9 4:08 PM
 *
 * <p>公司信息: 创略科技
 *
 * @author Danny.Huo <br>
 * @version v1.0 <br>
 * @date 2021/6/9 4:08 PM <br>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述] <br>
 */
@Data
@ApiModel(value = "画布节点")
public class Node implements Serializable {

    @ApiModelProperty(value = "节点ID")
    private Long nodeId;

    @ApiModelProperty(value = "节点名称")
    private String nodeName;

    @ApiModelProperty(value = "配置实例, key为属性字段名")
    private Map<String, ConfigInstance> confInstances;

    @ApiModelProperty(value = "节点下的边，ID， 为空表示末尾节点")
    private Long[] nextEdge;

    @ApiModelProperty(value = "配置实例, key为属性字段名")
    private Map<String, NodeJobs> nodeJobsMap;

    public Node(Long nodeId, String nodeName, Map<String, ConfigInstance> confInstances, Map<String, NodeJobs> nodeJobsMap) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.confInstances = confInstances;
        this.nodeJobsMap = nodeJobsMap;
    }

    public Node() {
    }
}
