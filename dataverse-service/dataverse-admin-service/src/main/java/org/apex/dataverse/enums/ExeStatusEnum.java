package org.apex.dataverse.enums;

public enum ExeStatusEnum {

    /**
     * 执行成功
     */
    EXE_SUCCESS(1, "SUCCESS"),
    /**
     * 执行失败
     */
    EXE_FAIL(0, "FAIL"),
    /**
     * 执行中
     */
    EXE_DONE(2, "DONE");

    private final int value;

    private String desc;

    ExeStatusEnum(int value, String desc) {
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
