package org.apex.dataverse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-02-13
 */
@Data
public class TableStatisticsVO {


    @ApiModelProperty("tableStatisticsId")
    private Long tableStatisticsId;

    @ApiModelProperty("数据表编码，同一个数据表在不同环境下编码相同，tableCode+env可唯一确认一个数据表")
    private String tableCode;

    @ApiModelProperty("表中记录数，行数")
    private Integer count;

    @ApiModelProperty("表所占空间大小")
    private Double size;

    @ApiModelProperty("文件个数")
    private String fileCount;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
