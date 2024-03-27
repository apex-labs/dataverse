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
 * 调度实例
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ScheduleInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "schedule_instance_id", type = IdType.AUTO)
    private Long scheduleInstanceId;

    /**
     * 调度实例编码，每次调度生成一 个唯一编码schedule_code+start(yyyyMMddHHmm)+end(yyyyMMddHHmm)
     * 如：xxx_202403160000_202403170000
     */
    private String scheduleInstanceCode;

    private String scheduleCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 截止偏移量
     */
    private String offset;

    /**
     * 运行状态
     */
    private Integer exeStatus;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
