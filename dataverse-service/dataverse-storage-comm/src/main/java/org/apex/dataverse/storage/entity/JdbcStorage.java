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
 * JDBC存储区，如MYSQL, PGSQL, ORACLE, DORIS, STAR_ROCKS, CLICKHOUSE, ELASTICSEARCH
 * </p>
 *
 * @author danny
 * @since 2023-12-20
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
     * JDBC驱动类，如MySQL的com.mysql.jdbc.Driver
     */
    private String driverClass;

    /**
     * 链接配置JSON，如：{"serverTimezone":"Asia/Shanghai","useUnicode":"true"}
     */
    private String connConfig;

    /**
     * 存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH
     */
    private String storageType;

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
