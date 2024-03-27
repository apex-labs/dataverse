package org.apex.dataverse.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import org.apex.dataverse.enums.DataSourceTypeEnum;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @ClassName SqlUtils
 * @Description TODO
 * @Author wwd
 * @Date 2023/2/23 10:16
 **/
public class SqlUtils {

    public static final String SEMICOLON = ";";

    public static final String PERCENT = "%";

    public static final String BACK_QUOTE = "`";

    public static final String DOUBLE_QUOTE = "\"";

    public static final String DOT_REG = "\\.";

    public static final String BLANK_REG = "\\s+";

    public static final String COMMA_SEPARATOR = ",";


    public static String encloseWithEscapeStr(String encloseStr, String originStr) {
        return encloseStr + originStr + encloseStr;
    }

    public static String encloseWithEscapeStr(String left, String right, String originStr) {
        return left + originStr + right;
    }

    public static String encloseWithDoubleQuotation(String originStr) {
        return encloseWithStr(originStr, "\"");
    }

    private static String encloseWithStr(String originStr, String encloseStr) {
        return encloseStr + originStr + encloseStr;
    }

    public static boolean isPureNumber(String str) {
        Pattern pattern = compile("^[\\d]+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 判断是否时 json 格式的字符串
     *
     * @param str 字符串
     * @return boolean
     */
    public static boolean isJsonFormat(String str) {
        try {
            if (StringUtils.hasLength(str)) {
                JSONObject.parseObject(str);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    public static StringBuilder appendIfPresent(StringBuilder sb, CharSequence detected, CharSequence appended) {
        if (StrUtil.isNotBlank(detected)) {
            sb.append(appended);
        }
        return sb;
    }


    /**
      * @Author wwd
      * @Description 检查jdbcUrl前缀
      * @Date 2023/2/23 11:30
      * @Param [jdbcUrl, datasourceTypeId]
      * @return boolean
     **/
    public static boolean matchJdbcUrl(String jdbcUrl, Integer datasourceTypeId) {
        if (datasourceTypeId.equals(DataSourceTypeEnum.ORACLE.getValue())) {
            if (jdbcUrl.startsWith("jdbc:oracle")) {
                return true;
            }
        } else if (datasourceTypeId.equals(DataSourceTypeEnum.MYSQL.getValue())) {
            if (jdbcUrl.startsWith("jdbc:mysql")) {
                return true;
            }

        } else if (datasourceTypeId.equals(DataSourceTypeEnum.POSTGRESQL.getValue())) {
            if (jdbcUrl.startsWith("jdbc:postgresql")) {
                return true;
            }
        }  else {
            throw new UnsupportedOperationException("暂不支持类型：" + jdbcUrl);
        }
        return false;
    }
}
