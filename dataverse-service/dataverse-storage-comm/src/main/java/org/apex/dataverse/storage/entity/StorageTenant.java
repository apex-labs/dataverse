package org.apex.dataverse.storage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 授权给租户的存储区
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StorageTenant implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "auth_id", type = IdType.AUTO)
    private Long authId;

    /**
     * 存储区ID
     */
    private Long storageId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 是否默认存储区，0：否，1：是
     */
    private Boolean isDefault;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人/授权人ID
     */
    private Long userId;

    /**
     * 创建人/授权人名称【冗余】
     */
    private String userName;


}
