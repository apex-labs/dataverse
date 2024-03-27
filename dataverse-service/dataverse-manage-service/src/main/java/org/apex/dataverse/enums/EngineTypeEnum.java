package org.apex.dataverse.enums;

public enum EngineTypeEnum {

    /**
     * SPARK
     */
    SPARK(1, "SPARK"),
    /**
     * FLINK
     */
    FLINK(2, "FLINK"),
    /**
     * OTHER
     */
    OTHER(3, "OTHER");

    private final int value;
    private String desc;

    EngineTypeEnum(int value, String desc) {
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
        for (EngineTypeEnum engineTypeEnum : values()) {
            if (engineTypeEnum.getDesc().equals(value)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
