package org.apex.dataverse.enums;

public enum TestDatasourceConnResultEnum {

    /**
      * 测试数据源连接成功
     **/
    SUCCESS(1, "SUCCESS"),
    /**
     * 测试数据源连接失败
     **/
    FAILURE(2, "FAIL");

    private final int value;

    private final String desc;

    TestDatasourceConnResultEnum(int value, String desc) {
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
