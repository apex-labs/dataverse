package org.apex.dataverse.enums;

public enum ConnTypeEnum {

    /**
     * ODBC
     */
    ODPC(1, "ODPC"),
    /**
     * JDBC
     */
    JDBC(2, "JDBC");

    private final int value;
    private String desc;

    ConnTypeEnum(int value, String desc) {
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
        for (ConnTypeEnum connTypeEnum : values()) {
            if (connTypeEnum.getDesc().equals(value)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
