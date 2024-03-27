package org.apex.dataverse.port.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 存储区和Port映射。指定存储区映射到哪些Port，对应存储区上的命令会提交到对应的Port上进行执行。
 * </p>
 *
 * @author danny
 * @since 2023-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StoragePort implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "storage_port_id", type = IdType.AUTO)
    private Long storagePortId;

    /**
     * storage_id
     */
    private Long storageId;

    /**
     * Storage's connection type
     * JDBC,ODPC
     */
    private String connType;

    /**
     * Engine type，1：Spark，2：Flink，3：非自研引擎（如doris, mysql)
     */
    private String engineType;

    /**
     * port_id
     */
    private Long portId;

    /**
     * port_code
     */
    private String portCode;

    /**
     * port_name
     */
    private String portName;

    /**
     * Port和JDBC存储区间允许创建的最小链接数。
     */
    private Integer minJdbcConns;

    /**
     * Port和JDBC存储区间允许创建的最大链接数。
     */
    private Integer maxJdbcConns;

    /**
     * ODPC存储区允许创建的最小引擎数
     */
    private Integer minOdpcEngines;

    /**
     * ODPC存储区允许创建的最大引擎数
     */
    private Integer maxOdpcEngines;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;


}
