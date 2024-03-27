package org.apex.dataverse.utils;

/**
 * category code生成工具类
 *
 * @author danny
 *
 * @date 2019/7/10 4:02 PM
 *
 */
public class CategoryCodeUtil {

    /**
     * 一级类别的最小长度
     */
    private final static int TOP_LEVEL_LEN = 8;

    /**
     * 除一级类别外，后每级的增加长度
     */
    private final static int LEVEL_LEN = 2;

    /**
     * 每级从几开始
     */
    private final static int LEVEL_START = 10;

    /**
     * 生成一级code
     * @param soaId
     * @return
     */
    public static String levelOne (int soaId) {
        return UCodeUtil.rpad(UCodeUtil.produce(soaId), TOP_LEVEL_LEN);
    }

    /**
     * 生成下一个code
     * @param code
     * @return
     */
    public static String nextCode (String code) {
        int len;
        if ((len = code.length()) < TOP_LEVEL_LEN) {
            throw new IllegalArgumentException("The code is invalid : " + code);
        }
        int idx = len - LEVEL_LEN;
        String tLevel = code.substring(idx);
        long nextNum = UCodeUtil.unCode(tLevel, UCodeUtil.DECIMAL36, false) + 1;
        String nLevel = UCodeUtil.lpad(UCodeUtil.code(nextNum, UCodeUtil.DECIMAL36, false), 2);
        return code.substring(0, idx) + nLevel;
    }

    /**
     * 生成下一级code
     * @param code
     * @return
     */
    public static String nextLevel (String code) {
        if (code.length() < TOP_LEVEL_LEN) {
            throw new IllegalArgumentException("The code is invalid : " + code);
        }
        return code + UCodeUtil.lpad(UCodeUtil.code(LEVEL_START, UCodeUtil.DECIMAL36, false), 2);
    }
}
