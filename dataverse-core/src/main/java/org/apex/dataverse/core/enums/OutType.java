package org.apex.dataverse.core.enums;

import lombok.Getter;

/**
 * @author : Danny.Huo
 * @version : v1.0
 * @date : 2023/1/12 14:10
 * @since : 0.1.0
 */
@Getter
public enum OutType {

    /**
     * HDFS output
     */
    HDFS,

    /**
     * Mysql output
     */
    MYSQL,

    /**
     * PGSQL output
     */
    PGSQL,

    /**
     * ORACLE output
     */
    ORACLE,

    /**
     * Doris output
     */
    DORIS,

    /**
     * StarRocks output
     */
    STAR_ROCKS,

    /**
     * CLICKHOUSE output
     */
    CLICKHOUSE,

    /**
     * ELASTICSEARCH output
     */
    ELASTICSEARCH,

    /**
     * Hive output
     */
    HIVE

}
