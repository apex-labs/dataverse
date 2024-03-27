package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * <p>项目名称: me-router
 *
 * <p>文件名称:
 *
 * <p>描述:
 *
 * <p>创建时间: 2021/6/9 4:07 PM
 *
 * <p>公司信息: 创略科技
 *
 * @author Danny.Huo <br>
 * @version v1.0 <br>
 * @date 2021/6/9 4:07 PM <br>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述] <br>
 */
@Data
@ApiModel(value = "活动画布图")
public class FlowDAG implements Serializable {

    @ApiModelProperty(value = "活动信息")
    private FlowInfo flowInfo;

    @ApiModelProperty(value = "头节点")
    private Node headNode;

    @ApiModelProperty(value = "画布节点集，key : 节点ID")
    private Map<Long, Node> nodes;

    @ApiModelProperty(value = "画布边集，key : 边ID")
    private Map<Long, Edge> edges;
}
