package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: StorageStatisticsVO
 * @Author: wwd
 * @TODO:
 * @Date: 2024/3/12 10:13
 */
@Data
public class StorageStatisticsVO {

    @ApiModelProperty("存储区数量")
    private Integer storageCount;
}
