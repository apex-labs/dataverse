package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * @author : Danny.Huo
 * @version : v1.0
 * @date : 2023/1/12 14:10
 * @since : 0.1.0
 */
@Getter
public enum StorageType {

    /**
     * HDFS storage
     */
    HDFS((byte) 1, "HDFS", "ODPC", false),

    /**
     * Mysql storage
     */
    MYSQL((byte) 2, "MYSQL", "JDBC", true),

    /**
     * PGSQL storage
     */
    PGSQL((byte) 3, "PGSQL", "JDBC", true),

    /**
     * ORACLE storage
     */
    ORACLE((byte) 4, "ORACLE", "JDBC", true),

    /**
     * Doris storage
     */
    DORIS((byte) 5, "DORIS", "JDBC", true),

    /**
     * StarRocks storage
     */
    STAR_ROCKS((byte) 6, "STAR_ROCKS", "JDBC", true),

    /**
     * CLICKHOUSE storage
     */
    CLICKHOUSE((byte) 7, "CLICKHOUSE", "JDBC", true),

    /**
     * ELASTICSEARCH storage
     */
    ELASTICSEARCH((byte) 8, "ELASTICSEARCH", "JDBC", true);

    /**
     * Code
     */
    private final Byte code;

    /**
     * Storage name
     */
    private final String name;

    /**
     * Connection type
     */
    private final String connType;

    /**
     * Adhoc support or not
     */
    private final boolean adhoc;

    /**
     * Storage type enum constructor
     *
     * @param code code
     * @param name name
     * @param connType connection type
     * @param adhoc adhoc support or not
     */
    StorageType(Byte code, String name, String connType, boolean adhoc) {
        this.code = code;
        this.name = name;
        this.connType = connType;
        this.adhoc = adhoc;
    }

}
