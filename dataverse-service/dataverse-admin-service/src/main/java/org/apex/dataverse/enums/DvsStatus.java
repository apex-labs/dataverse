package org.apex.dataverse.enums;

public enum DvsStatus {
    /**
     * 数据空间状态 草稿/规划
     */
    DRAFT(1, "draft"),
    /**
     * 数据空间状态 开发
     */
    DEVELOP(2, "develop"),
    /**
     * 数据空间状态 上线
     */
    ONLINE(3, "online");

    private final int value;
    private final String desc;

    DvsStatus(int value, String desc) {
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
