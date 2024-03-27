package org.apex.dataverse.enums;

public enum EnvEnum {

    /**
     * BASIC环境
     */
    BAISC(0, "BASIC"),
    /**
     * DEV环境
     */
    DEV(1,"DEV"),
    /**
     * PROD环境
     */
    PROD(2,"PROD");

    private final int value;
    private final String desc;

    EnvEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static int getEnvValueByModel(int modelValue) {
        if (modelValue == EnvModelEnum.BAISC.getValue()) {
            return BAISC.getValue();
        } else {
            return DEV.getValue();
        }
    }

    public static String getEnvDescByValue(int value) {
        if (value == EnvEnum.BAISC.getValue()) {
            return BAISC.getDesc();
        }
        if (value == EnvEnum.PROD.getValue()) {
            return PROD.getDesc();
        } else {
            return DEV.getDesc();
        }
    }

    public static Boolean existsValue(int value) {
        for (EnvEnum envEnum : values()) {
            if (value == envEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
