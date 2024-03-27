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
public class SparkEngineConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long configId;

    private String key;

    private String subKey;

    private String value;

    private LocalDateTime createTime;


}
