package org.apex.dataverse.enums;

public enum IncrTypeEnum {

    NO_INCR(0, "非增量抽取"),
    DATE_INCR(1, "日期增量抽取"),
    NUNBER_INCR(2, "数值增量抽取");

    private final int value;
    private String desc;

    IncrTypeEnum(int value, String desc) {
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
        for (IncrTypeEnum incrTypeEnum :values()) {
            if (incrTypeEnum.getValue() == value) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
