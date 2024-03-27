package org.apex.dataverse.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @version : v1.0
 * @author : Danny.Huo
 * @date : 2023/4/27 19:25
 */
public class DateUtil {

    /**
     * 默认SimpleDateFormat
     */
    private final static SimpleDateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");

    /**
     * 默认时间格式化
     * @param date
     * @return
     */
    public static String defaultFormat(Date date) {
        return defaultDateFormat.format(date);
    }

    /**
     * 默认时间戳格式化
     * @param timestamp
     * @return
     */
    public static String defaultFormat(Long timestamp) {
        return defaultDateFormat.format(new Date(timestamp));
    }
}
