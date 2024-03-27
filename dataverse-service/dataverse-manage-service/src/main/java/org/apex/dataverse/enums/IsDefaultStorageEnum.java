package org.apex.dataverse.enums;

public enum IsDefaultStorageEnum {

    /**
     * NO
     */
    NO(0, "不是"),
    /**
     * YES
     */
    YES(1, "是");

    private final int value;
    private String desc;

    IsDefaultStorageEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsValue(String value) {
        for (IsDefaultStorageEnum connTypeEnum : values()) {
            if (connTypeEnum.getDesc().equals(value)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
