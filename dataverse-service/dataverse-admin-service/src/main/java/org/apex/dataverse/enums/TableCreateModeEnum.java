package org.apex.dataverse.enums;

public enum TableCreateModeEnum {

    AUTO_CREATE_TABLE(1, "自动创建表"),
    HAND_CREATE_TABLE(2, "手动创建表");

    private final int value;
    private String desc;

    TableCreateModeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsValue(int value) {
        for (TableCreateModeEnum tableCreateModeEnum :values()) {
            if (tableCreateModeEnum.getValue() == value) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
