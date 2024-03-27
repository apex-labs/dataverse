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
public class JdbcSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JDBC实例ID
     */
    @TableId(value = "jdbc_source_id", type = IdType.AUTO)
    private Long jdbcSourceId;

    /**
     * 数据源父编码，同一编码在不同环境（DEV/PROD)中相同
     */
    private String datasourceCode;

    /**
     * 环境，0：BASIC、1：DEV、2：PROD
     */
    private Integer env;

    /**
     * 数据源名称，命名规则可为datasource_name + env（BASIC，DEV，TEST，PROD）
     */
    private String datasourceName;

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
