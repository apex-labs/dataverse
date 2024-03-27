package org.apex.dataverse.enums;

public enum EnvModelEnum {

    /**
     * BASIC模式
     */
    BAISC(1, "BASIC"),
    /**
     * DEV-PROD模式
     */
    DEV_PROD(2,"DEV-PROD");

    private final int value;
    private final String desc;

    EnvModelEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsValue(int value) {
        for (EnvModelEnum envModelEnum: values()) {
            if (value == envModelEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
