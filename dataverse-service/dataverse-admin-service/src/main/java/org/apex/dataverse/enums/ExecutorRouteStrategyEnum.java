package org.apex.dataverse.enums;

public enum ExecutorRouteStrategyEnum {

    /**
     * 路由策略 第一个
     */
    FIRST(1, "FIRST"),
    /**
     * 路由策略 最后一个
     */
    LAST(2, "LAST"),
    /**
     * 路由策略 轮询
     */
    ROUND(3, "ROUND"),
    /**
     * 路由策略 随机
     */
    RANDOM(4, "RANDOM"),
    /**
     * 路由策略 一致性HASH
     */
    CONSISTENT_HASH(5, "CONSISTENT_HASH"),
    /**
     * 路由策略 最不经常使用
     */
    LEAST_FREQUENTLY_USED(6, "LEAST_FREQUENTLY_USED"),
    /**
     * 路由策略 最近最久未使用
     */
    LEAST_RECENTLY_USED(7, "LEAST_RECENTLY_USED"),
    /**
     * 路由策略 故障转移
     */
    FAILOVER(8, "FAILOVER"),
    /**
     * 路由策略 忙碌转移
     */
    BUSYOVER(9, "BUSYOVER"),
    /**
     * 路由策略 分片广播
     */
    SHARDING_BROADCAST(10, "SHARDING_BROADCAST");

    private final int value;

    private String desc;

    ExecutorRouteStrategyEnum(int value, String desc) {
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
