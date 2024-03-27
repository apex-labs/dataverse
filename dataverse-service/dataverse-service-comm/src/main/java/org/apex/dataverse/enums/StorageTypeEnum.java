package org.apex.dataverse.enums;

public enum StorageTypeEnum {

    /**
     * HDFS
     */
    HDFS(0, "HDFS"),
    /**
     * MYSQL
     */
    MYSQL(1, "MYSQL"),
    /**
     * MARIADB
     */
    MARIADB(2, "MARIADB"),
    /**
     * DORIS
     */
    DORIS(3, "DORIS"),
    /**
     * CLICKHOUSE
     */
    CLICKHOUSE(4, "CLICKHOUSE"),
    /**
     * ORACLE
     */
    ORACLE(5, "ORACLE");

    private final int value;
    private String desc;

    StorageTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsValue(String value) {
        for (StorageTypeEnum storageTypeEnum : values()) {
            if (storageTypeEnum.getDesc().equals(value)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
