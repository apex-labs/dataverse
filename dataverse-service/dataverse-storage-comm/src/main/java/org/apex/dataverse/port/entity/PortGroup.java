package org.apex.dataverse.port.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PortGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 组ID
     */
    @TableId(value = "group_id", type = IdType.AUTO)
    private Long groupId;

    /**
     * 组编码，唯一
     */
    private String groupCode;

    /**
     * 组名
     */
    private String groupName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
