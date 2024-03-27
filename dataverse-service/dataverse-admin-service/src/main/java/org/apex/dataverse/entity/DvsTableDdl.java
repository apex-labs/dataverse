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
 * 表的建表语句
 * </p>
 *
 * @author danny
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class DvsTableDdl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "table_ddl_id", type = IdType.AUTO)
    private Long tableDdlId;

    /**
     * 表编码。同一Evn下，表编码唯一。table_code+env唯一
     */
    private String tableCode;

    /**
     * 建表语句
     */
    private String ddlText;

    /**
     * 版本号，每更变更一次，版本号自加1
     */
    private Integer version;

    /**
     * 是否删除：0否，1：是
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
