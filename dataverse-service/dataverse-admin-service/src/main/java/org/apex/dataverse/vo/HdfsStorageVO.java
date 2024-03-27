package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class HdfsStorageVO {


    @ApiModelProperty("hdfs数据存储ID")
    private Long hdfsStorageId;

    @ApiModelProperty("数据存储ID")
    private Long storageId;


}
