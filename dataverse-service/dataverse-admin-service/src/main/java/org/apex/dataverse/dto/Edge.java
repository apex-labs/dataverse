package org.apex.dataverse.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

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
@ApiModel(value = "画布边")
public class Edge implements Serializable {
    /**
     * 边的ID
     */
    @ApiModelProperty(value = "边ID")
    private Long id;

    /**
     * 是否为否则条件
     */
    @ApiModelProperty(value = "是否为否则条件")
    private boolean isElse;

    /**
     * 开始节点
     */
    @ApiModelProperty(value = "头节点")
    private Long fromNode;

    /**
     * 指向节点
     */
    @ApiModelProperty(value = "尾节点")
    private Long toNode;

    public Edge() {
    }

    public Edge(Long id, Long fromNode, Long toNode) {
        this.id = id;
        this.fromNode = fromNode;
        this.toNode = toNode;
    }
}
