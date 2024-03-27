package org.apex.dataverse.util;

/**
 * @version : v1.0
 * @projectName : dynamic-rule-engine
 * @package : com.chinapex.fdre.util
 * @className : StringUtil
 * @description :
 * @Author : Danny.Huo
 * @createDate : 2022/10/14 10:30
 * @updateUser :
 * @updateDate :
 * @updateRemark :
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
