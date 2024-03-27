package org.apex.dataverse.core.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局唯一编码生成器
 *
 * @author danny
 * @date 2019-04-04
 * @since 0.1.0
 */
public class UCodeUtil {

    /**
     * 自定义进制字符。
     * 包括：
     * 数字、小写字母、大写字母
     */
    private final static char[] BASE = {
            '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9',

            'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j',
            'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y',
            'z',

            'A', 'B', 'C', 'D', 'E',
            'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O',
            'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y',
            'Z'
    };

    private static int soaId = 0;

    public static void setSoaId(int id) {
        soaId = id;
    }

    /**
     * 1970-01-01距离今毫秒数缩减基数
     * 49 * 365天  ≈ 49年
     */
    private final static long BASE_REDUCED_MS = 1000L * 60 * 60 * 24 * 365 * 49;

    /**
     * 自定义36进制
     * 仅小写字母和数字
     */
    public final static int DECIMAL36 = 36;

    /**
     * 自定义62进制
     */
    public final static int DECIMAL62 = 62;

    /**
     * 自定义进制最大值
     */
    private final static int MAX = 62;

    /**
     * 自定义进制最小值
     */
    private final static int MIN = 2;

    /**
     * 小写字母长度
     */
    private final static int LOWER_CASE_LEN = 26;

    /**
     * 小写字母起始位置
     */
    private final static int LOWER_CASE_START_INDEX = 10;

    /**
     * 自定义字符的10进制含义字典
     */
    private final static Map<Character, Integer> MAP = new HashMap<>(MAX);

    /**
     * 初始化自定义字符10进制值
     */
    static {
        for (int i = 0; i < BASE.length; i++) {
            MAP.put(BASE[i], i);
        }
    }

    /**
     * 每毫秒内计数器
     * key: 毫秒
     * val:当前毫秒下的计数器的值
     */
    private final static Map<Long, Integer> COUNTER_MAP = new ConcurrentHashMap<>(1);

    /**
     * 计数器起始值
     */
    private final static int COUNTER_START = 0;

    /**
     * 自定义进制中的0
     */
    private final static String ZERO = "0";

    /**
     * 获取计数
     *
     * @param timeMs long
     * @return int
     */
    private static synchronized int counter(long timeMs) {
        Integer count = COUNTER_MAP.get(timeMs);
        COUNTER_MAP.clear();
        if (null == count) {
            COUNTER_MAP.put(timeMs, COUNTER_START);
            return COUNTER_START;
        }
        COUNTER_MAP.put(timeMs, ++count);
        return count.intValue();
    }

    /**
     * 数值转换为自定义进制数值
     *
     * @param num long
     * @param x               [2-62]
     * @param startWithLetter startWithLetter
     * @return String
     */
    public static String code(long num, int x, boolean startWithLetter) {
        StringBuffer buffer = new StringBuffer();
        //Start with character
        if (startWithLetter) {
            //First character. Character start at index of 10.
            long fx = Math.min(x, LOWER_CASE_LEN);
            //Add the number base len. Confirm start with character
            int firstBit = LOWER_CASE_START_INDEX + (int) (num % fx);
            buffer.append(BASE[firstBit]);
            num /= fx;
        }

        //Other character
        for (; num > 0; num /= x) {
            buffer.append(BASE[(int) (num % x)]);
        }

        if (startWithLetter) {
            return buffer.toString();
        }

        if (buffer.length() == 0) {
            buffer.append(BASE[0]);
        }
        return buffer.reverse().toString();
    }

    /**
     * 数值转换为自定义进度数值 , 重载
     *
     * @param num int
     * @param x int
     * @param startWithLetter boolean
     * @return String
     */
    public static String code(int num, int x, boolean startWithLetter) {
        return code((long) num, x, startWithLetter);
    }

    /**
     * 根据数值生成唯一字符串编码，小写字母+数字
     * 小写字母开头
     *
     * @param num long
     * @param x   [2-62]
     * @return String
     */
    public static String code(long num, int x) {
        return code(num, x, true);
    }

    /**
     * 根据数值生成唯一字符串编码，小写字母+数字， 重载
     * 小写字母开头
     *
     * @param num long
     * @param x   [2-62]
     * @return String
     */
    public static String code(int num, int x) {
        return code((long) num, x);
    }

    /**
     * 自定义进制字符串转成10进制
     *
     * @param code            自定义进制字符
     * @param x               x进制
     * @param startWithLetter 是否以字母开头的生成法则
     * @return long
     */
    public static long unCode(String code, int x, boolean startWithLetter) {
        int b = code.length();
        long rv = 0;

        if (!startWithLetter) {
            for (int i = 0; i < b; i++) {
                rv += MAP.get(code.charAt(i)).longValue() * (long) Math.pow(x, b - i - 1);
            }
            return rv;
        }

        //Start with letter
        for (int i = 1; i < b; i++) {
            rv += MAP.get(code.charAt(i)).longValue() * (long) Math.pow(x, i - 1);
        }

        rv *= Math.min(x, LOWER_CASE_LEN);

        //Add the first character 10 decimal value.
        return rv + MAP.get(code.charAt(0)).longValue() - LOWER_CASE_START_INDEX;
    }


    /**
     * 自定义进制字符串转成10进制， 重载
     * 默认字母开头的特殊进制规则
     *
     * @param code String
     * @param x int
     * @return long
     */
    public static long unCode(String code, int x) {
        return unCode(code, x, true);
    }

    /**
     * 将时间数值生成的36进制(小写字母和数字)的code
     *
     * @param num long
     * @return String
     */
    public static String code36(long num) {
        return code(num, DECIMAL36);
    }

    /**
     * 将时间数值生成的36进制(小写字母和数字)的code, 重载
     *
     * @param num int
     * @return String
     */
    public static String code36(int num) {
        return code(num, DECIMAL36);
    }

    /**
     * 将时间数值生成的36进制(小写字母和数字)的code
     *
     * @param num  long
     * @param startWithLetter boolean
     * @return String
     */
    public static String code36(long num, boolean startWithLetter) {
        return code(num, DECIMAL36, startWithLetter);
    }

    /**
     * 将时间数值生成的36进制(小写字母和数字)的code, 重载
     *
     * @param num int
     * @param startWithLetter boolean
     * @return String
     */
    public static String code36(int num, boolean startWithLetter) {
        return code(num, DECIMAL36, startWithLetter);
    }

    /**
     * 将生成的36进制(小写字母和数字)的code解析成对应的long数值
     *
     * @param code String
     * @return long
     */
    public static long unCode36(String code) {
        return unCode(code, DECIMAL36);
    }

    /**
     * 将生成的36进制(小写字母和数字)的code解析成对应的long数值
     *
     * @param code String
     * @param startWithLetter boolean
     * @return long
     */
    public static long unCode36(String code, boolean startWithLetter) {
        return unCode(code, DECIMAL36, startWithLetter);
    }


    /**
     * 将long数值生成对应的62进制的编码
     *
     * @param num long
     * @return String
     */
    public static String code62(long num) {
        return code(num, DECIMAL62);
    }

    /**
     * 将时间数值生成的62进制的code, 重载
     *
     * @param num long
     * @param startWithLetter boolean
     * @return String
     */
    public static String code62(long num, boolean startWithLetter) {
        return code(num, DECIMAL62, startWithLetter);
    }

    /**
     * 将long数值生成对应的62进制的编码, 重载
     *
     * @param num int
     * @return String
     */
    public static String code62(int num) {
        return code(num, DECIMAL62);
    }

    /**
     * 将时间数值生成的62进制的code, 重载
     *
     * @param num int
     * @param startWithLetter boolean
     * @return String
     */
    public static String code62(int num, boolean startWithLetter) {
        return code(num, DECIMAL62, startWithLetter);
    }

    /**
     * 将生成的62进制的code解析成对应的long数值
     *
     * @param code String
     * @return long
     */
    public static long unCode62(String code) {
        return unCode(code, DECIMAL62);
    }


    /**
     * 将生成的62进制的code解析成对应的long数值
     *
     * @param code String
     * @return long
     */
    public static long unCode62(String code, boolean startWithLetter) {
        return unCode(code, DECIMAL62, startWithLetter);
    }

    /**
     * 指定长度字符，不够长度首位补0
     *
     * @param str String
     * @param len int
     * @return String
     */
    public static String lpad(String str, int len) {
        str = str == null ? "" : str;
        int realLen = str.length();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < len - realLen; i++) {
            buffer.append(BASE[0]);
        }
        buffer.append(str);
        return buffer.toString();
    }

    /**
     * 指定长度字符，不够长度首位补0
     *
     * @param str String
     * @param len int
     * @return String
     */
    public static String rpad(String str, int len) {
        str = str == null ? "" : str;
        int realLen = str.length();
        StringBuffer buffer = new StringBuffer();
        buffer.append(str);
        for (int i = 0; i < len - realLen; i++) {
            buffer.append(BASE[0]);
        }
        return buffer.toString();
    }


    /**
     * 编码生成器
     *
     * @return String
     */
    private static String produce(boolean startWithLetter) {
        long ms = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        //timestamp
        buffer.append(code36(ms - BASE_REDUCED_MS, startWithLetter));
        //counter, length = 2
        String counter = code36(counter(ms), false);
        if (ZERO.equals(counter)) {
            return buffer.toString();
        }
        buffer.append(counter);

        return buffer.toString();
    }

    /**
     * 编码生成器
     *
     * @return String
     */
    private static String produce62(boolean startWithLetter) {
        long ms = System.currentTimeMillis();
        StringBuffer buffer = new StringBuffer();
        //timestamp
        buffer.append(code62(ms - BASE_REDUCED_MS, startWithLetter));
        //counter, length = 2
        String counter = code62(counter(ms), false);
        if (ZERO.equals(counter)) {
            return buffer.toString();
        }
        buffer.append(counter);

        return buffer.toString();
    }

    /**
     * 编码生成器，单服务唯一
     *
     * @return String
     */
    public static String produce() {
        return produce(soaId);
    }

    /**
     * ID生成器，单服务唯一
     *
     * @return long
     */
    public static long produceID() {
        return produceID(soaId);
    }

    /**
     * 编码生成器，全局唯一
     *
     * @param soaId 服务ID
     * @return String
     */
    public static String produce(int soaId) {
        if (soaId > 0) {
            return code36(soaId) + produce(false);
        }
        return produce(false);
    }

    /**
     * ID生成器，全局唯一
     *
     * @param soaId 服务ID
     * @return long
     */
    public static long produceID(int soaId) {
        if(soaId > 0) {
            return unCode36(code36(soaId) + produce(false));
        }
        return unCode36(produce(true));
    }

    /**
     * 编码生成器，单服务唯一
     *
     * @return String
     */
    public static String produce62() {
        return produce62(true);
    }

    /**
     * 编码生成器，全局唯一
     *
     * @param soaId 服务ID
     * @return String
     */
    public static String produce62(int soaId) {
        return code62(soaId) + produce62(false);
    }

    public static void main(String[] args) {
        Set<String> set = new HashSet<>();
        int max = 1000000;
        for (int i = 0; i < max; i++) {
            setSoaId(1000);
            set.add(produce());
        }
        System.out.println(max - set.size());
    }
}
