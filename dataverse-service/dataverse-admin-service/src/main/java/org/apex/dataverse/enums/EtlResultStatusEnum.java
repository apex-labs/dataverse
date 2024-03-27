package org.apex.dataverse.enums;

public enum EtlResultStatusEnum {

    /**
     * 数据集成ETL任务执行成功标识
     */
    SUCCESS(1, "成功"),
    /**
     * 数据集成ETL任务执行失败标识
     */
    FAILURE(2,"失败"),
    ;

    EtlResultStatusEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    private final int value;
    private final String desc;

    public static String getDescByValue(Integer env) {
        for (EtlResultStatusEnum etlResultStatusEnum : values()) {
            if (etlResultStatusEnum.getValue() == env.intValue()) {
                return etlResultStatusEnum.getDesc();
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }
}
