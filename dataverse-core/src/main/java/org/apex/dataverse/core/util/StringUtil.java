package org.apex.dataverse.core.util;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/04/28 10:30
 */
public class StringUtil {

    /**
     * Is bland string
     * @param s string
     * @return boolean
     */
    public static boolean isBlank (String s) {
        return null == s || s.trim().isEmpty();
    }

    /**
     * Is not bland string
     * @param s string
     * @return boolean
     */
    public static boolean isNotBlank (String s) {
        return !isBlank(s);
    }
}
