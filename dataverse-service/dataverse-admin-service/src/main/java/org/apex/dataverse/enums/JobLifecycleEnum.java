package org.apex.dataverse.enums;

public enum JobLifecycleEnum {

    /**
     * 作业开发中(草稿)
     */
    ETL_DEVELOP(11, "作业开发中(草稿)"),
    /**
     * 作业开发中完成
     */
    ETL_DEVELOP_END(12, "作业开发中完成"),
    /**
     * 作业测试中
     */
    ETL_TEST(21, "作业测试中"),
    /**
     * 作业测试完成(通过)
     */
    ETL_TEST_END(22, "作业测试完成(通过)"),
    /**
     * 调度编排中
     */
    BDM_DEVELOP(31, "调度编排中"),
    /**
     * 调度编排完成
     */
    BDM_DEVELOP_END(32, "调度编排完成"),
    /**
     * 调度测试中
     */
    BDM_TEST(41, "调度测试中"),
    /**
     * 调度测试完成(通过)
     */
    BDM_TEST_END(42, "调度测试完成(通过)"),
    /**
     * 已发布生产
     */
    DEPLOY_PROD(51, "已发布生产"),
    /**
     * 生产测试中
     */
    PROD_TEST(61, "生产测试中"),
    /**
     * 生产测试完成(通过)
     */
    PROD_TEST_END(62, "生产测试完成(通过)"),
    /**
     * 已上线
     */
    ONLINE(71, "已上线"),
    /**
     * 已下线
     */
    DOWN_LINE(72, "已下线"),
    /**
     * 失败
     */
    FAIL(0, "失败");

    private final int value;
    private final String desc;

    JobLifecycleEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean exists(int value) {
        for (JobLifecycleEnum jobLifecycleEnum : values()) {
            if (value == jobLifecycleEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
