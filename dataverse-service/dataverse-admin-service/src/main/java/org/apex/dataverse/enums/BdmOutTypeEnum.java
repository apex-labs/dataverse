package org.apex.dataverse.enums;

public enum BdmOutTypeEnum {
    /**
     * 开发任务输出表类型 临时表
     */
    TEMP_TABLE(1, "临时表"),
    /**
     * 开发任务输出表类型 正式表-逻辑
     */
    LOGIC_TABLE(2, "正式表-逻辑"),
    /**
     * 开发任务输出表类型 正式表
     */
    TABLE(3, "正式表");

    private final int value;
    private String desc;

    BdmOutTypeEnum(int value, String desc) {
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
