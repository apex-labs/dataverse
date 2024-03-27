package org.apex.dataverse.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel("分页结果模型")
@Data
public class PageResult<T> {

    @ApiModelProperty(value = "当前页码")
    private long current = 1;

    @ApiModelProperty(value = "总记录数")
    private long total;

    @ApiModelProperty(value = "单页大小")
    private long size = 10;

    @ApiModelProperty(value = "最后一页面页码")
    private long lastPageNo = total % size == 0 ? total / size : total / size + 1;

    @ApiModelProperty(value = "页面数据")
    private List<T> list;
}
