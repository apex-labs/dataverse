package org.apex.dataverse.entity;

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
 * @since 2024-01-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class KafkaSource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "kafka_source_id", type = IdType.AUTO)
    private Long kafkaSourceId;

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


}
