package org.apex.dataverse.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    private final static SimpleDateFormat SDF = new SimpleDateFormat(
            "yyyyMMdd");

    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public final static DateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public final static DateTimeFormatter DATETIME_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public final static DateTimeFormatter DATETIME_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public final static DateTimeFormatter DATETIME_DATE_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    public final static DateTimeFormatter DATETIME_DATE_YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    public final static DateTimeFormatter DATETIME_DATE_FORMATTER_YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd");


    private static Date getFromToDate(Date date, int option, int k) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        //判断是否为星期天,外国定义星期天是他们的第一天
        if (dayOfWeek <= 0) {
            dayOfWeek = 7;
        }
        int offset = 0 == option ? 1 - dayOfWeek : 7 - dayOfWeek;
        int amount = 0 == option ? offset - k * 7 : offset - k * 7;
        calendar.add(Calendar.DATE, amount);
        return calendar.getTime();
    }

    /**
     * 根据当前日期获得本周的日期区间（本周周一和周日日期）
     *
     */
    public static Map<String,Date> getThisWeekTimeInterval() {
        Map<String,Date> map = new HashMap<>();
        map.put("startDate",getFromToDate(new Date(), 0, 0));
        map.put("endDate",getFromToDate(new Date(), 1, 0));
        return map;
    }

    /**
     * 根据当前日期获得上周的日期区间（上周周一和周日日期）
     *
     */
    public static Map<String,Date> getLastWeekTimeInterval() {
        Map<String,Date> map = new HashMap<>();
        map.put("startDate",getFromToDate(new Date(), 0, 1));
        map.put("endDate",getFromToDate(new Date(), 1, 1));
        return map;
    }

    /**
     * 获取上个月的第一天和最后一天
     *
     */
    public static Map<String,Date> getLastMonthTimeInterval() {
        Map<String,Date> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,-1);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        map.put("startDate",calendar.getTime());
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,0);
        map.put("endDate",calendar.getTime());
        return map;
    }

    /**
     * 获取本月的第一天和最后一天
     *
     */
    public static Map<String,Date> getNowMonthTimeInterval() {
        Map<String,Date> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        map.put("startDate",calendar.getTime());
        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        map.put("endDate",calendar.getTime());
        return map;
    }

    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();
        return currYearLast;
    }

    /**
     * 获取当前年的第一天
     * @return
     */
    public static Date getCurrYearFirst(){
        Calendar currCal=Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR,currCal.get(Calendar.YEAR));
        return calendar.getTime();
    }

    public static int getYearByDate(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    public static Date parse(String dateTime,String dateFormat) {

        // 创建 SimpleDateFormat 对象，指定日期格式为"yyyy-MM-dd'T'HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 将输入字符串转换成 Date 对象
        Date date = null;
        try {
            date = sdf.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 再次创建 SimpleDateFormat 对象，指定日期格式为"HH:mm:ss"
        SimpleDateFormat timeFormat = new SimpleDateFormat(dateFormat);

        // 获取时间部分
        String timeOnly = timeFormat.format(date);

        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss"); // 定义日期格式化模式
        try {
            date = sdf2.parse(timeOnly);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    public static LocalDateTime StringToDate(String dateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = null;
        try {
            Date date = sdf.parse(dateTime);
             localDateTime = date.toInstant().atZone(sdf.getTimeZone().toZoneId()).toLocalDateTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDateTime;
    }

    public static String DateToString(LocalDateTime dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 定义日期格式化模式
        String formattedDate = dateTime.format(formatter); // 将LocalDateTime对象按指定格式转换为字符串
        return formattedDate;
    }
    
    public static String test(String timeVale,String num){
        String[] time = timeVale.split(":");
        StringBuilder sb =  new StringBuilder();
        sb.append("0").append(" ").append(time[1]).append(" ").append(time[0]).append(" ").append("1/").append(num).append(" * ?");
        return sb.toString();
    }

    /**
     * 格式化文本转为日期时间
     * @param dateTime
     * @param dateTimeFormatter
     * @return
     * @throws ParseException
     */
    public static LocalDateTime str2LocalDateTime(String dateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(dateTime, dateTimeFormatter);
    }

    /**
     * 格式化文本转为日期
     * @param dateTime
     * @param dateTimeFormatter
     * @return
     * @throws ParseException
     */
    public static LocalDate str2LocalDate(String dateTime, DateTimeFormatter dateTimeFormatter) {
        return LocalDate.parse(dateTime, dateTimeFormatter);
    }

    /**
     * 日期重新格式成字符串
     * @param dateString
     * @param dateTimeFormatter
     * @param formatter
     * @return
     */
    public static String strDateFormatNewStrDate(String dateString, DateTimeFormatter dateTimeFormatter, DateTimeFormatter formatter) {
        return LocalDate.parse(dateString, dateTimeFormatter).format(formatter);
    }

    /**
     * 日期格式转字符串
     * @param localDate
     * @param dateTimeFormatter
     * @return
     */
    public static String localDateToStr(LocalDate localDate, DateTimeFormatter dateTimeFormatter) {
        return localDate.format(dateTimeFormatter);
    }

    /**
     * 日期时间格式转字符串
     * @param localDateTime
     * @param dateTimeFormatter
     * @return
     */
    public static String localDateTimeToStr(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

//    public static boolean test1(){
//        String dayNum = "11";
//        Pattern pattern = compile("[0-9]*");
//        return pattern.matcher(dayNum).matches();
//    }

//    public static void main(String[] args) {
//        System.out.println(getThisWeekTimeInterval());
//        System.out.println(getLastWeekTimeInterval());
//        System.out.println(getLastMonthTimeInterval());
//        System.out.println(getNowMonthTimeInterval());
//        System.out.println(getCurrYearFirst());
//        System.out.println(getYearFirst(2020));
//        System.out.println(getYearLast(2020)); 0 2 1 1/1 * ?
//        System.out.println(test1());
//        System.out.println(StringToDate("2020-10-01 10:10:09"));

//    }

}
