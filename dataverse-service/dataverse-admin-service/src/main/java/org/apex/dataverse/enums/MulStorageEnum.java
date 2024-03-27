package org.apex.dataverse.enums;

public enum MulStorageEnum {

    /**
     * 单存储区
     */
    SIGNAL_STORAGE(0, "单存储"),
    /**
     * 多存储区
     */
    MULTI_STORAGE(1, "多存储");

    private final int value;
    private String desc;

    MulStorageEnum(int value, String desc) {
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
