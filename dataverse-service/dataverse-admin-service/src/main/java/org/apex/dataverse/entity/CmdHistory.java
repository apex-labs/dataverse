package org.apex.dataverse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

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
public class CmdHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cmd_history_id", type = IdType.AUTO)
    private Long cmdHistoryId;

    private Integer dataEngineId;

    private String cmdJson;

    private String clientIp;

    private String channelId;

    private LocalDateTime createTime;


}
