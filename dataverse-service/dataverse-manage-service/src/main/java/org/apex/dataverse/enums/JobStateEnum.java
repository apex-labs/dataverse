package org.apex.dataverse.enums;

public enum JobStateEnum {

    SUCCESS(0, "执行成功"),
    FAIL(1, "执行失败"),
    DONE(2, "执行中");

    private final int value;

    private final String desc;

    JobStateEnum(int value, String desc) {
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
