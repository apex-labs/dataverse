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
public class JdbcStorage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "jdbc_storage_id", type = IdType.AUTO)
    private Long jdbcStorageId;

    /**
     * 数据存储区ID
     */
    private Long storageId;

    /**
     * JDBC链接URL
     */
    private String jdbcUrl;

    /**
     * JDBC链接用户名
     */
    private String userName;

    /**
     * JDBC链接密码
     */
    private String password;

    /**
     * 链接配置JSON，如：{"serverTimezone":"Asia/Shanghai","useUnicode":"true"}
     */
    private String connConfig;

    /**
     * 存储区类型，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle
     */
    private Integer storageType;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
