package org.apex.dataverse.enums;

public enum ExecuteResultEnum {

    /**
     * 任务执行结果 成功
     */
    EXECUTE_RESULT_SUCCESS(1, "SUCCESS"),
    /**
     * 任务执行结果 失败
     */
    EXECUTE_RESULT_FAIL(2, "FAIL");

    private final int value;
    private final String desc;

    ExecuteResultEnum(int value, String desc) {
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
