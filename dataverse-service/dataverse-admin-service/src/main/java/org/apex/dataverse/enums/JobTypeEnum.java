package org.apex.dataverse.enums;

public enum JobTypeEnum {

    /**
     * ETL数据集成作业
     */
    ETL_JOB(1, "ETL数据集成作业"),
    /**
     * BDM数据开发作业
     */
    BDM_JOB(2, "BDM数据开发作业");

    private final int value;
    private final String desc;

    JobTypeEnum(int value, String desc) {
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
