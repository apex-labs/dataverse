package org.apex.dataverse.enums;

public enum StorageFormatEnum {
    /**
     * 存储区hdfs文件保存为 orc类型
     */
    ORC(1, "orc"),
    /**
     * 存储区hdfs文件保存为 parquet类型
     */
    PARQUET(2, "parquet");

    private final int value;
    private final String desc;

    StorageFormatEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public String getDesc() {
        return desc;
    }

    public static Boolean existsValue(int value) {
        for (StorageFormatEnum storageFormatEnum : values()) {
            if (value == storageFormatEnum.getValue()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public static String getDescByValue(int value) {
        for (StorageFormatEnum storageFormatEnum : values()) {
            if (value == storageFormatEnum.getValue()) {
                return storageFormatEnum.getDesc();
            }
        }
        return null;
    }
}
