package org.apex.dataverse.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: zoubin
 * @date: 11/8/18 14:00
 * @description: 分页参数
 */
@ApiModel("分页查询参数")
@Getter
@Setter
public class PageQueryParam {
    /**
     * 每页显示条数
     */
    @ApiModelProperty(value = "每页显示条数", example = "10")
    private Long size;
    /**
     * 页码
     */
    @ApiModelProperty(value = "页码，从1开始", example = "1")
    private Long pageNo;


    /**
     * 降序字段
     */
    @ApiModelProperty(value = "降序字段集合")
    private List<String> descs;

    /**
     * 升序字段
     */
    @ApiModelProperty("升序字段集合")
    private List<String> ascs;
}
