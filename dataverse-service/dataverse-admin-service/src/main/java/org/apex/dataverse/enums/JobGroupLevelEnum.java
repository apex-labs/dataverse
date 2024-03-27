package org.apex.dataverse.enums;

public enum JobGroupLevelEnum {
    /**
     * 1级
     */
    ONE(1, "1级"),
    /**
     * 2级
     */
    TWO(2, "2级"),
    /**
     * 3级
     */
    THREE(3, "3级"),
    /**
     * 4级
     */
    FOUR(4, "4级"),
    /**
     * 5级
     */
    FIVE(5, "5级");

    /**
     * 值
     */
    private int value;

    /**
     * 描述
     */
    private String desc;

    JobGroupLevelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public final int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
