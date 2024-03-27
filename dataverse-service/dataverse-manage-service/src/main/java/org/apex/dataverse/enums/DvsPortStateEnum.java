package org.apex.dataverse.enums;

public enum DvsPortStateEnum {

    /**
     * Dvs port state build
     */
    BUILD(1, "BUILD"),
    /**
     * Dvs port state online
     */
    ONLINE(2, "ONLINE"),
    /**
     * Dvs port state pause
     */
    PAUSE(3, "PAUSE"),
    /**
     * Dvs port state offline
     */
    OFFLINE(4, "OFFLINE");

    private final int value;
    private String desc;

    DvsPortStateEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean exists(String value) {
        for (DvsPortStateEnum dvsPortStateEnum : values()) {
            if (value.equals(dvsPortStateEnum.getDesc())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
}
