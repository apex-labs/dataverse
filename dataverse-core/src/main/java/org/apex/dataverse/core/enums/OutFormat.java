package org.apex.dataverse.core.enums;

import lombok.Getter;

import java.io.Serializable;

/**
 * @author : Danny.Huo
 * @version : v1.0
 * @date : 2023/1/12 14:10
 * @since : 0.1.0
 */
@Getter
public enum OutFormat implements Serializable {

    /**
     * Orc format
     */
    ORC,

    /**
     * Parquet format
     */
    PARQUET,

    /**
     * Avro format
     */
    AVRO,

    /**
     * Hudi format
     */
    HUDI,

    /**
     * Iceberg format
     */
    ICEBERG,

    /**
     * Delta lake format
     */
    DELTA
}
