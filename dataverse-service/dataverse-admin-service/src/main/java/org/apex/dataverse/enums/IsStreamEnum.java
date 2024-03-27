package org.apex.dataverse.enums;

public enum IsStreamEnum {

    NO_STREAM(0, "否"),
    YES_STREAM(1, "是");

    private final int value;
    private String desc;

    IsStreamEnum(int value, String desc) {
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
        for (IsStreamEnum isStreamEnum :values()) {
            if (isStreamEnum.getValue() == value) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
