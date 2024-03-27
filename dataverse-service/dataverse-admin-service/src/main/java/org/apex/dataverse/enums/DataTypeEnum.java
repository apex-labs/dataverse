package org.apex.dataverse.enums;

public enum DataTypeEnum {
    /**
     * 字符类型
     */
    STRING(1,"STRING"),
    /**
     * 整数类型
     */
    INTEGER(2,"INTEGER"),
    /**
     * 浮点类型
     */
    FLOAT(3,"FLOAT"),
    /**
     * 日期类型
     */
    DATE(4,"DATE"),
    /**
     * 日期时间类型
     */
    DATETIME(5,"DATETIME");

    private final int value;
    private final String desc;

    DataTypeEnum(int value, String desc) {
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
