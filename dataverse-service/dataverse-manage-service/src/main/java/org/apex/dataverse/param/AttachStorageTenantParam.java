package org.apex.dataverse.param;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName: AttachStorageTenantParam
 * @Author: wwd
 * @TODO:
 * @Date: 2024/1/12 12:37
 */
@ApiModel
@Data
public class AttachStorageTenantParam {

    @ApiModelProperty("主键")
    private Long authId;

    @ApiModelProperty("存储区ID")
    private Long storageId;

    @ApiModelProperty("租户ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("是否默认存储区，0：否，1：是")
    private Integer isDefault;
}
