package org.apex.dataverse.enums;

public enum ScheduleLifecycleEnum {
    /**
     * 调度编排中
     */
    SCHEDULE_ARRANGE(31, "调度编排中"),
    /**
     * 调度编排完成
     */
    SCHEDULE_ARRANGE_END(32, "调度编排完成"),
    /**
     * 调度测试中
     */
    SCHEDULE_TEST(41, "调度测试中"),
    /**
     * 调度测试完成(通过)
     */
    SCHEDULE_TEST_END(42, "调度测试完成(通过)"),
    /**
     * 已发布生产
     */
    DEPLOY_PROD(51, "已发布生产"),
    /**
     * 生产测试中
     */
    PROD_TEST(61, "生产测试中    "),
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
    DOWN_LINE(72, "已下线");

    private final int value;
    private final String desc;

    ScheduleLifecycleEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsByValue(int value) {
        for (ScheduleLifecycleEnum scheduleLifecycleEnum : values()) {
            if (scheduleLifecycleEnum.getValue() == value) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
