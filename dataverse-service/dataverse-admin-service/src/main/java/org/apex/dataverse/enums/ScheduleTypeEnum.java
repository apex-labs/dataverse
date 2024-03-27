package org.apex.dataverse.enums;

public enum ScheduleTypeEnum {

    /**
     * 调度类型-CRON
     */
    CRON(1, "CRON"),
    /**
     * 调度类型-无
     */
    NONE(2, "无"),
    /**
     * 调度类型-固定速度
     */
    FIX_RATE(3, "固定速度");

    private final int value;

    private String desc;

    ScheduleTypeEnum(int value, String desc) {
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
