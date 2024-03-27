package org.apex.dataverse.enums;

public enum DataSourceTypeEnum {
    /**
     * 关系型数据库mysql
     */
    MYSQL(1, "Mysql", "com.mysql.cj.jdbc.Driver"),
    /**
     * 关系型数据库oracle
     */
    ORACLE(2, "Oracle", "Oracle.jdbc.driver.OracleDriver"),
    /**
     * 关系型数据库postgresql
     */
    POSTGRESQL(3, "PostgreSQL", "org.postgresql.Driver"),
    /**
     * 消息中间件kafka
     */
    KAFKA(31, "Kafka", "Kafka");


    private final int value;

    private final String desc;

    private final String driverName;

    DataSourceTypeEnum(int value, String desc, String driverName) {
        this.value = value;
        this.desc = desc;
        this.driverName = driverName;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getDriverName() {
        return driverName;
    }

    public static String getDriverNameByValue(int value) {
        for (DataSourceTypeEnum dataSourceTypeEnum : values()) {
            if (dataSourceTypeEnum.getValue() == value) {
                return dataSourceTypeEnum.getDriverName();
            }
        }
        return null;
    }
}
