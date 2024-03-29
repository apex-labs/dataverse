package org.apex.dataverse.util;

/**
 * @version : v0.1.0
 * @author : Danny.Huo
 * @date : 2022/10/14 10:30
 */
public class StringUtil {

    /**
     * 判断字符串是否为空或者空白
     * @param s
     * @return
     */
    public static boolean isBlank (String s) {
        if (null == s || s.trim().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串非空
     * @param s
     * @return
     */
    public static boolean isNotBlank (String s) {
        return !isBlank(s);
    }

}
