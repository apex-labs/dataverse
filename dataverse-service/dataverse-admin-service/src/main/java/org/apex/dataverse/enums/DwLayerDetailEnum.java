package org.apex.dataverse.enums;

public enum DwLayerDetailEnum {
    /**
     * ODS层
     */
    ODS(11, "ODS", "ODS"),
    /**
     * DWD层
     */
    DWD(21, "DWD", "DW"),
    /**
     * DWS层
     */
    DWS(22, "DWS", "DW"),
    /**
     * DIM层
     */
    DIM(23, "DIM", "DW"),
    /**
     * MASTER层
     */
    MASTER(31, "MASTER", "ADS"),
    /**
     * MODEL层
     */
    MODEL(32, "MODEL", "ADS"),
    /**
     * LABEL层
     */
    LABEL(33, "LABEL", "ADS"),
    /**
     * DM层
     */
    DM(34, "DM", "ADS"),
    ;

    private final int value;

    private final String desc;

    private final String layer;

    DwLayerDetailEnum(int value, String desc, String layer) {
        this.value = value;
        this.desc = desc;
        this.layer = layer;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public String getLayer() {
        return layer;
    }

    public static String getDescByValue(int value) {
        for (DwLayerDetailEnum dwLayerDetailEnum : values()) {
            if (dwLayerDetailEnum.getValue() == value) {
                return dwLayerDetailEnum.getDesc();
            }
        }
        return null;
    }

    public static Integer getValueByDesc(String desc) {
        for (DwLayerDetailEnum dwLayerDetailEnum : values()) {
            if (dwLayerDetailEnum.getDesc().equals(desc)) {
                return dwLayerDetailEnum.getValue();
            }
        }
        return null;
    }
}