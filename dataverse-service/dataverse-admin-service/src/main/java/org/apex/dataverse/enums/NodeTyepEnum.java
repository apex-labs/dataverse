package org.apex.dataverse.enums;

public enum NodeTyepEnum {

    START_NODE(1, "开始节点"),
    DO_NODE(2, "作业节点"),
    END_NODE(3, "结束节点");

    private final int value;
    private String desc;

    NodeTyepEnum(int value, String desc) {
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
        for (NodeTyepEnum nodeTyepEnum : values()) {
            if (value == nodeTyepEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
