package org.apex.dataverse.constrant;

/**
 * @author danny
 * @date 2020/7/24 2:29 PM
 */
public class HiveDataTypeName {

    /**
     * TINYINT
     */
    public final static String TINYINT = "TINYINT";

    /**
     * BOOLEAN
     */
    public final static String BOOLEAN = "BOOLEAN";

    /**
     * SMALLINT
     */
    public final static String SMALLINT = "SMALLINT";

    /**
     * INT / INTEGER
     */
    public final static String INT = "INT";

    /**
     * BIGINT
     */
    public final static String BIGINT = "BIGINT";

    /**
     * DECIMAL
     */
    public final static String DECIMAL = "DECIMAL";

    /**
     * DOUBLE
     */
    public final static String DOUBLE = "DOUBLE";

    /**
     * TIMESTAMP
     */
    public final static String TIMESTAMP = "TIMESTAMP";

    /**
     * DATE
     */
    public final static String DATE = "DATE";

    /**
     * STRING
     */
    public final static String STRING = "STRING";

    /**
     * 无符号标志
     */
    public final static String UNSIGNED_FLAG = "UNSIGNED";

    /**
     * 转换为带精度的类型
     * @param precision 精度
     * @param scale 小数点后精度
     * @return
     */
    public static String withPrecision(String type, int precision, int scale) {

        if(precision <= 0 || scale < 0) {
           return type;
        }
        return type + "(" + precision + "," + scale + ")";
    }

}
