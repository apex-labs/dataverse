package org.apex.dataverse.enums;

public enum IsPkEnum {

    /**
     * 是主键
     */
    PK_YES(1, "YES"),
    /**
     * 不是主键
     */
    PK_NO(2, "NO");

    private final int value;
    private final String desc;

    IsPkEnum(int value, String desc) {
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
