package org.apex.dataverse.enums;

public enum TriggerStatusEnum {

    /**
     * 触发器状态 停止
     */
    STOP(0, "STOP"),
    /**
     * 触发器状态 运行
     */
    RUN(1, "RUN");

    private final int value;

    private String desc;

    TriggerStatusEnum(int value, String desc) {
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
