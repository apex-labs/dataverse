package org.apex.dataverse.enums;

public enum DataStorageTypeEnum {

    /**
      * HDFS存储类型
     **/
    HDFS(0,"HDFS"),
    /**
     * MYSQL存储类型
     **/
    MYSQL(1,"MYSQL");

    private final int value;
    private final String desc;

    DataStorageTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
