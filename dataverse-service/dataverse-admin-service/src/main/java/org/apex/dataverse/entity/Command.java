package org.apex.dataverse.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Command implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 命令ID
     */
    private String commandId;

    /**
     * 命令，Json格式
     */
    private String command;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
