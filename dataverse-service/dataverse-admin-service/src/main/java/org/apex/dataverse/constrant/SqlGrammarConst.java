package org.apex.dataverse.constrant;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * SQL语法关键字常量
 *
 * @Author danny
 * @date 2019-03-21
 */
public class SqlGrammarConst {

    public final static String SELECT = "SELECT ";
    public final static String FROM = " FROM ";
    public final static String WHERE = " WHERE ";
    public final static String AND = " AND ";
    public final static String AND_WITH_OUT_BANK = "AND";
    public final static String CASE = " CASE ";
    public final static String WHEN = " WHEN ";
    public final static String AS = " AS ";
    public static final String AS_WITH_OUT_BLANK = "AS";
    public final static String THEN = " THEN ";
    public final static String END = " END ";
    public final static String JOIN = " JOIN ";
    public final static String LEFT_JOIN = " LEFT JOIN ";
    public final static String INNER_JOIN = " INNER JOIN ";
    public final static String ON = " ON ";
    public final static String GROUP_BY = " GROUP BY ";
    public final static String ORDER_BY = " ORDER BY ";
    public final static String ELSE = " ELSE ";

    public final static String EQUE = " = ";
    public final static String NOT_EQUE = " != ";
    public final static String GREATER = " > ";
    public final static String LESS = " < ";
    public final static String GREATER_EQUE = " >= ";
    public final static String LESS_EQUE = " <= ";
    public final static String BETWEEN = " BETWEEN ";
    public final static String NULL = " NULL";
    public final static String SQL_BLANK = "''";
    public final static String IS = " IS ";
    public final static String IS_NOT = " IS NOT ";
    public final static String IN = " in ";
    public final static String NOT_IN = " not in ";
    public final static String IS_NULL = " is null ";
    public final static String IS_NOT_NULL = " is not null ";

    public final static char CHAR_COMMA = ',';
    public final static char CHAR_LEFT_PARENTHESIS = '(';
    public final static char CHAR_RIGHT_PARENTHESIS = ')';
    public final static char CHAR_SINGLE_QUOTES = '\'';
    public final static char CHAR_SLASH = '\\';
    public final static char CHAR_ZERO = '0';
    public final static char CHAR_A = 'A';
    public final static char CHAR_S = 'S';
    public final static char CHAR_BLANK = ' ';

    public final static List<Integer> DATA_TYPE_NEED_SINGLE_QUOTES = Lists.newArrayList(
            2, 3, 4
    );

    /**
     * 数字类型
     */
    public final static Integer DATA_TYPE_NUMBER = 1;
    /**
     * 文本类型
     */
    public final static Integer DATA_TYPE_TEXT = 2;
    /**
     * 时间（年月日时分秒）
     */
    public final static Integer DATA_TYPE_DATETIME = 3;

    /**
     * 日期(年月日)
     */
    public final static Integer DATA_TYPE_DATE = 4;

    /**
     * 年月
     */
    public final static Integer DATA_TYPE_YEAR_MONTH = 5;

    /**
     * 过滤中所有支持的数据类型
     */
    public final static List<Integer> FILTER_DATA_TYPE = Lists.newArrayList(DATA_TYPE_NUMBER, DATA_TYPE_TEXT, DATA_TYPE_DATETIME, DATA_TYPE_DATE, DATA_TYPE_YEAR_MONTH);

    public final static String COMMA = ", ";
    public final static String COMMA_WITHOUT_BLANK = ",";
    public final static String SINGLE_QUOTES = "'";
    public final static String DOUBLE_QUOTES = "\"";
    public final static String DOT = ".";
    public final static String ASTERISK = "*";

    public final static String TRUE_CONDITION = "1 = 1";
    public final static String FALSE_CONDITION = "1 = 2";

    public final static String ELSE_DEFAULT = "0";
    public final static int NOT_EXIST = -1;

    public final static String BRACKET_LEFT = "(";
    public final static String BRACKET_RIGHT = ")";
    public final static String SEMICOLON = ";";

    //聚合函数
    public final static String COUNT = " COUNT ";
    public final static String SUM = " SUM ";
    public final static String AVG = " AVG ";
    public final static String MAX = " MAX ";
    public final static String MIN = " MIN ";
    public final static String DISTINCT = " DISTINCT ";
    public final static String DISTINCT_WITHOUT_BLANK = "DISTINCT";

    public final static String LOWER_DISTINCT_WITHOUT_BLANK = "distinct";

    public final static String LIMIT = " LIMIT ";

    public final static String LIMIT_ONE = " LIMIT 1 ";

    public final static String INT = " INT ";

    public final static String COLUMN_FAMILY = "cf";

    public final static String BLANK = " ";

    public final static String EMPTY_STR = "";

    public final static String SYMBOLS = "`";

    // DDL 部分
    /**
     * hive外表语句头部
     */
    public final static String CREATE_EXTERNAL_TABLE = "CREATE EXTERNAL TABLE ";

    /**
     * hive 内部表语句头部
     */
    public final static String CREATE_TABLE = "CREATE TABLE ";

    /**
     * 删除hive外表语句头部
     */
    public final static String DROP_EXTERNAL_TABLE = "DROP TABLE IF EXISTS ";
    public final static String INSERT_INTO = "INSERT INTO TABLE ";
    public final static String COMMENT = " COMMENT ";
    public final static String COLON = ":";
    public final static String HBASE_STORAGE_HANDLER = " STORED BY 'org.apache.hadoop.hive.hbase.HBaseStorageHandler'" +
            " WITH SERDEPROPERTIES ('hbase.columns.mapping'=" +
            "':key%s') TBLPROPERTIES ('hbase.table.name' = '%s')";


    public static void main(String[] args) {
        System.out.println(CASE.length());
    }

    /**
     * hive 日期格式函数
     */
    public final static String HIVE_DATE_FORMAT = "date_format";

    public final static String NOT_WITHOUT_BLANK = "!";

    public final static String ORDER_DESC = " desc ";
    public final static String HAVING = " having ";

    public final static String OR = " or ";

    /**
     * 每日时间字符串开始时间 00:00:00
     */
    public final static String DAY_TIME_STR_START = "00:00:00";

    /**
     * 每日时间字符串最后时间 00:00:00
     */
    public final static String DAY_TIME_STR_END = "23:59:59";

    public final static String NOT = " not ";

    public final static String LIKE = " like ";

    public final static String SYMBOL_WILDCARD = "%";

    public final static String FUNC_SIZE = "size";

    public final static String FUNC_SPLIT = "split";

    public static final String EQUE_WITHOUT_BLANK = "=";

    public static final String SLASH = "/";

    public static final String OVERWRITE = "overwrite";
    public static final String INSERT_OVERWRITE = "insert overwrite";
    public static final String INSERT_INTO_LOWER = "insert into";

    public static final String SELECT_WITH_BLANK_LOWER = "select";

    public static final String UPPER_BOUND = "300000";

}
