package org.apex.dataverse.enums;

public enum DwLayerEnum {
    /**
     * ODS层
     */
    ODS(1, "ODS"),
    /**
     * DW层
     */
    DW(2, "DW"),
    /**
     * ADS层
     */
    ADS(3, "ADS"),
    ;

    private final int value;

    private final String desc;

    DwLayerEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static String getDwLayerDescByModel(int modelValue) {
        if (modelValue == DwLayerEnum.ODS.getValue()) {
            return ODS.getDesc();
        }
        if (modelValue == DwLayerEnum.DW.getValue()) {
            return DW.getDesc();
        }
        if (modelValue == DwLayerEnum.ADS.getValue()) {
            return ADS.getDesc();
        } else {
            return null;
        }
    }

    public static Boolean existsValue(int value) {
        for (DwLayerEnum dwLayerEnum : values()) {
            if (value == dwLayerEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}