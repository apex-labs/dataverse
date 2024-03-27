package org.apex.dataverse.enums;

public enum IsDeletedEnum {
    NO(0, "否"),
    YES(1, "是");

    private final int value;
    private String desc;

    IsDeletedEnum(int value, String desc) {
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
