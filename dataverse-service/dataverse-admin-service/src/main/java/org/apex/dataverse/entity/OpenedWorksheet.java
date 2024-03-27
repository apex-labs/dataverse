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
 * 打开的工作表
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class OpenedWorksheet implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "worksheet_id", type = IdType.AUTO)
    private Long worksheetId;

    /**
     * 作业Code，BDM作业或ETL作业
     */
    private String jobCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 作业类型，1：ETL Job, 2:BDM Job
     */
    private Integer jobType;

    /**
     * Work Sheet名称(作业名称）
     */
    private String jobName;

    /**
     * 是否关闭 ，0：否，1：是（当sheet关闭时，置为1，表示sheet已关闭）
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 关闭时间
     */
    private LocalDateTime closedTime;

    /**
     * 创建人ID
     */
    private Long userId;

    /**
     * 创建人名称【冗余】
     */
    private String userName;


}
