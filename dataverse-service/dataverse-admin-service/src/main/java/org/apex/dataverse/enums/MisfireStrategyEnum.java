package org.apex.dataverse.enums;

public enum MisfireStrategyEnum {
    /**
     * 调度过期策略-忽略
     */
    DO_NOTHING(1, "DO_NOTHING"),
    /**
     * 调度过期策略-立即执行一次
     */
    FIRE_ONCE_NOW(2, "FIRE_ONCE_NOW");

    private final int value;

    private String desc;

    MisfireStrategyEnum(int value, String desc) {
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
