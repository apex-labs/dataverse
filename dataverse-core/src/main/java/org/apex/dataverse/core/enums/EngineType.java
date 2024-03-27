package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * Engine type
 * @author : Danny.Huo
 * @date : 2023/1/12 14:10
 * @version : v1.0
 * @since : 0.1.0
 */
@Getter
public enum EngineType {

    /**
     * Spark engine
     */
    SPARK_ENGINE ((byte) 1, "SPARK"),

    /**
     * Flink engine
     */
    FLINK_ENGINE ((byte) 2, "FLINK"),

    /**
     * Others engine
     * Mysql, doris, clickhouse ...
     */
    OTHERS ((byte) 3, "OTHERS");

    /**
     * Code
     */
    private final Byte code;

    private final String name;

    EngineType(Byte code, String name) {
        this.code = code;
        this.name = name;
    }
}
