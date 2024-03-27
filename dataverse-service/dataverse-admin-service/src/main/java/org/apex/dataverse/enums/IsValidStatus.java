package org.apex.dataverse.enums;

public enum IsValidStatus {
    /**
     * 数据空间状态 草稿/规划
     */
    VALID_STATUS(1, "生效"),
    /**
     * 数据空间状态 开发
     */
    NOT_VALID_STATUS(0, "失效");

    private final int value;
    private final String desc;

    IsValidStatus(int value, String desc) {
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
