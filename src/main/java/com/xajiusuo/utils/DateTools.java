package com.xajiusuo.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 时间转换工具类
 * Created by xcp on 2017/10/10.
 */
@Slf4j
public class DateTools {
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat dateTimeZoneFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat dateFormatCn = new SimpleDateFormat("yyyy年MM月dd日");
    private static SimpleDateFormat shortDateFormat = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat dateTimeStr17Format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat dateTimeStr14Format = new SimpleDateFormat("yyyyMMddHHmmss");

    public static String getDateTimeString(Date date) {
        return dateTimeFormat.format(date);
    }

    public static String getDateTimeStr17String(Date date) {
        return dateTimeStr17Format.format(date);
    }

    public static String getDateTimeStr14String(Date date) {
        return dateTimeStr14Format.format(date);
    }

    public static Date parseDateTimeString(String datetime) throws ParseException {
        return dateTimeFormat.parse(datetime);
    }

    public static String parseDateTimeZoneString(Date date, String zone) throws ParseException {
        TimeZone timeZone = TimeZone.getTimeZone(zone);
        return dateTimeZoneFormat.format(date) + timeZone.getID().replace("GMT", "");
    }

    public static String getShortDateString(Date date) {
        return shortDateFormat.format(date);
    }

    public static Date shortDateTimeString(String datetime) throws ParseException {
        return shortDateFormat.parse(datetime);
    }

    public static String getDateString(Date date) {
        return dateFormat.format(date);
    }

    public static String getDateStringCn(Date date) {
        return dateFormatCn.format(date);
    }

    public static Date dateFromString(String dateStr) throws ParseException {
        return dateFormat.parse(dateStr);
    }

    public static String getDateStringToString(String dateStr) {
        String str = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6, 8);
        return str;
    }

    public static String getMonthDateFromString(String dateStr){
        String str = dateStr.substring(5).replace("-","月")+"日";
        return str;
    }

    public static Date parseDateTimeOrDateString(String dateStr) throws ParseException {
        Date date = null;
        try{
            date = dateTimeFormat.parse(dateStr);
        }catch (Exception e){
            date = dateFormat.parse(dateStr);
        }
        return date;
    }

    /******************************************日期转换中文数字******************************************************/
    /**
     * 判断某个日期是否在某个日期范围
     * @param beginDate 开始时间
     * @param endDate 结束时间
     * @param src 待判断时间
     * @return
     */
    public static boolean between(Date beginDate, Date endDate, Date src) {
        return beginDate.before(src) && endDate.after(src);
    }

    /**
     * 获得当前时间的<code>java.util.Date</code>对象
     * @return
     */
    public static Date now() {
        return new Date();
    }

    /**
     * 格式化日期时间
     * @param date
     * @param pattern
     *            格式化模式，详见{@link SimpleDateFormat}构造器
     *            <code>SimpleDateFormat(String pattern)</code>
     * @return
     */
    public static String formatDatetime(Date date, String pattern) {
        SimpleDateFormat customFormat = (SimpleDateFormat) dateTimeFormat.clone();
        customFormat.applyPattern(pattern);
        return customFormat.format(date);
    }

    /**
     * @Author shirenjing
     * @Description 时间格式字符串转换
     * @Date 9:58 2019/6/17
     * @Param [time, pattern]
     * @return
     **/
    public static String formatDatetime(String time, String pattern){
        SimpleDateFormat customFormat = (SimpleDateFormat) dateTimeFormat.clone();
        customFormat.applyPattern(pattern);
        try {
            Date dateTime = dateTimeFormat.parse(time);
            return customFormat.format(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 描述：将日期转换为指定格式字符串
     * @param date 日期
     * @return
     */
    public static String getDateStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datestr = sdf.format(date);
        return datestr;
    }

    /**
     * 描述：取出日期字符串中的年份字符串
     * @param str 日期字符串
     * @return
     */
    public static String getYearStr(String str) {
        String yearStr = "";
        yearStr = str.substring(0, 4);
        return yearStr;
    }

    /**
     * 描述：取出日期字符串中的月份字符串
     * @param str 日期字符串
     * @return
     */
    public static String getMonthStr(String str) {
        String monthStr;
        int startIndex = str.indexOf("年");
        int endIndex = str.indexOf("月");
        monthStr = str.substring(startIndex + 1, endIndex);
        return monthStr;
    }

    /**
     * 描述：将源字符串中的阿拉伯数字格式化为汉字
     * @param sign 源字符串中的字符
     * @return
     */
    public static char formatDigit(char sign) {
        if (sign == '0')
            sign = '0';
        if (sign == '1')
            sign = '一';
        if (sign == '2')
            sign = '二';
        if (sign == '3')
            sign = '三';
        if (sign == '4')
            sign = '四';
        if (sign == '5')
            sign = '五';
        if (sign == '6')
            sign = '六';
        if (sign == '7')
            sign = '七';
        if (sign == '8')
            sign = '八';
        if (sign == '9')
            sign = '九';
        return sign;
    }

    /**
     * 描述： 获得月份字符串的长度
     * @param str 待转换的源字符串
     * @param pos1 第一个'-'的位置
     * @param pos2 第二个'-'的位置
     * @return
     */
    public static int getMidLen(String str, int pos1, int pos2) {
        return str.substring(pos1 + 1, pos2).length();
    }

    /**
     * 描述：获得日期字符串的长度
     * @param str 待转换的源字符串
     * @param pos2 第二个'-'的位置
     * @return
     */
    public static int getLastLen(String str, int pos2) {
        return str.substring(pos2 + 1).length();
    }

    /**
     * 描述：取出日期字符串中的日字符串
     * @param str 日期字符串
     * @return
     */
    public static String getDayStr(String str) {
        String dayStr = "";
        int startIndex = str.indexOf("月");
        int endIndex = str.indexOf("日");
        dayStr = str.substring(startIndex + 1, endIndex);
        return dayStr;
    }

    /**
     * 描述：格式化日期
     * @param str 源字符串中的字符
     * @return
     */
    public static String formatStr(String str) {
        StringBuffer sb = new StringBuffer();
        int pos1 = str.indexOf("-");
        int pos2 = str.lastIndexOf("-");
        for (int i = 0; i < 4; i++) {
            sb.append(formatDigit(str.charAt(i)));
        }
        sb.append('年');
        if (getMidLen(str, pos1, pos2) == 1) {
            sb.append(formatDigit(str.charAt(5)) + "月");
            if (str.charAt(7) != '0') {
                if (getLastLen(str, pos2) == 1) {
                    sb.append(formatDigit(str.charAt(7)) + "日");
                }
                if (getLastLen(str, pos2) == 2) {
                    if (str.charAt(7) != '1' && str.charAt(8) != '0') {
                        sb.append(formatDigit(str.charAt(7)) + "十" + formatDigit(str.charAt(8)) + "日");
                    } else if (str.charAt(7) != '1' && str.charAt(8) == '0') {
                        sb.append(formatDigit(str.charAt(7)) + "十日");
                    } else if (str.charAt(7) == '1' && str.charAt(8) != '0') {
                        sb.append("十" + formatDigit(str.charAt(8)) + "日");
                    } else {
                        sb.append("十日");
                    }
                }
            } else {
                sb.append(formatDigit(str.charAt(8)) + "日");
            }
        }
        if (getMidLen(str, pos1, pos2) == 2) {
            if (str.charAt(5) != '0' && str.charAt(6) != '0') {
                sb.append("十" + formatDigit(str.charAt(6)) + "月");
                if (getLastLen(str, pos2) == 1) {
                    sb.append(formatDigit(str.charAt(8)) + "日");
                }
                if (getLastLen(str, pos2) == 2) {
                    if (str.charAt(8) != '0') {
                        if (str.charAt(8) != '1' && str.charAt(9) != '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");
                        } else if (str.charAt(8) != '1' && str.charAt(9) == '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十日");
                        } else if (str.charAt(8) == '1' && str.charAt(9) != '0') {
                            sb.append("十" + formatDigit(str.charAt(9)) + "日");
                        } else {
                            sb.append("十日");
                        }
                    } else {
                        sb.append(formatDigit(str.charAt(9)) + "日");
                    }
                }
            } else if (str.charAt(5) != '0' && str.charAt(6) == '0') {
                sb.append("十月");
                if (getLastLen(str, pos2) == 1) {
                    sb.append(formatDigit(str.charAt(8)) + "日");
                }
                if (getLastLen(str, pos2) == 2) {
                    if (str.charAt(8) != '0') {
                        if (str.charAt(8) != '1' && str.charAt(9) != '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");
                        } else if (str.charAt(8) != '1' && str.charAt(9) == '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十日");
                        } else if (str.charAt(8) == '1' && str.charAt(9) != '0') {
                            sb.append("十" + formatDigit(str.charAt(9)) + "日");
                        } else {
                            sb.append("十日");
                        }
                    } else {
                        sb.append(formatDigit(str.charAt(9)) + "日");
                    }
                }
            } else {
                sb.append(formatDigit(str.charAt(6)) + "月");
                if (getLastLen(str, pos2) == 1) {
                    sb.append(formatDigit(str.charAt(8)) + "日");
                }
                if (getLastLen(str, pos2) == 2) {
                    if (str.charAt(8) != '0') {
                        if (str.charAt(8) != '1' && str.charAt(9) != '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十" + formatDigit(str.charAt(9)) + "日");
                        } else if (str.charAt(8) != '1' && str.charAt(9) == '0') {
                            sb.append(formatDigit(str.charAt(8)) + "十日");
                        } else if (str.charAt(8) == '1' && str.charAt(9) != '0') {
                            sb.append("十" + formatDigit(str.charAt(9)) + "日");
                        } else {
                            sb.append("十日");
                        }
                    } else {
                        sb.append(formatDigit(str.charAt(9)) + "日");
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String dateAddMonth(String date,int month){
        SimpleDateFormat customFormat = (SimpleDateFormat) dateTimeFormat.clone();
        customFormat.applyPattern("yyyy-MM");
        Calendar calendar = Calendar.getInstance();
        try {
            Date mDate = customFormat.parse(date);
            calendar.setTime(mDate);
            calendar.add(Calendar.MONTH,month);
            return customFormat.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @Author shirenjing
     * @Description 时间加减小时
     * @Date 16:52 2019/6/26
     * @Param [date, lon]
     * @return
     **/
    public static Date currentDateAddSubHH(Date date, Integer lon) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(11, lon.intValue());
        return cal.getTime();
    }

    /**
     * @Author shirenjing
     * @Description 获取时间段内的所有日期
     * @Date 18:12 2019/6/28
     * @Param [startDate, endDate]
     * @return
     **/
    public static List<String> getDateStrs(Date startDate,Date endDate){
        List<String> dates = new ArrayList<>();
        while (endDate.before(startDate)){
            dates.add(dateFormat.format(startDate));
            try {
                startDate = currentDateAddSubHH(startDate,24);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }
    
    /**
     * @Author shirenjing
     * @Description 获取时间段内的所有日期
     * @Date 18:12 2019/6/28
     * @Param [startDate, endDate]
     * @return
     **/
    public static List<Date> getDates(Date startDate,Date endDate){
        List<Date> dates = new ArrayList<>();
        while (startDate.before(endDate) || startDate.equals(endDate)){
            dates.add(startDate);
            try {
                startDate = currentDateAddSubHH(startDate,24);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dates;
    }

    /**
     * @Author shirenjing
     * @Description 获取时间段内的所有周
     * @Date 18:13 2019/6/28
     * @Param [startDate, endDate]
     * @return
     **/
    public static List<String> getWeeks(Date startDate,Date endDate){
        List<Date> dates = getDates(startDate, endDate);
        List<String> weeks = new ArrayList<>();
        for (Date d:dates) {
            String w = getWeek(d);
            if(!weeks.contains(w)){
                weeks.add(w);
            }
        }
        return weeks;
    }

    /**
     * @Author shirenjing
     * @Description 获取日期对应的周
     * @Date 18:13 2019/6/28
     * @Param [date]
     * @return
     **/
    public static String getWeek(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        String w = calendar.get(Calendar.WEEK_OF_YEAR)<10? "0"+calendar.get(Calendar.WEEK_OF_YEAR) : calendar.get(Calendar.WEEK_OF_YEAR)+"";
        return calendar.get(Calendar.YEAR)+"-"+w;
    }

    /**
     * @Author shirenjing
     * @Description 获取年
     * @Date 18:16 2019/6/28
     * @Param [year]
     * @return
     **/
    public static Calendar getCalendarFormYear(int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
        cal.set(Calendar.YEAR,year);
        return cal;
    }
    
    /**
     * @Author shirenjing
     * @Description 获取第几周对应的开始时间
     * @Date 11:06 2019/6/29
     * @Param [year, weekNo]
     * @return
     **/
    public static String getStartDayOfWeekNo(int year,int weekNo){
        Calendar cal = getCalendarFormYear(year);
        cal.set(Calendar.WEEK_OF_YEAR,weekNo);
        return cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @Author shirenjing
     * @Description 获取第几周对应的结束时间
     * @Date 11:06 2019/6/29
     * @Param [year, weekNo]
     * @return
     **/
    public static String getEndDayOfWeekNo(int year,int weekNo){
        Calendar cal = getCalendarFormYear(year);
        cal.set(Calendar.WEEK_OF_YEAR,weekNo);
        cal.add(Calendar.DAY_OF_WEEK,6);
        return cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH);
    }



    public static void main(String[] args) throws ParseException {
//        String a = parseDateTimeZoneString(new Date(), "GMT+0");
//            String a = getMonthDateFromString("2017-09-08");
//        System.out.println(a);
//        System.out.println(formatDatetime("2019-01-01 12:22:32","yyyy-MM-dd HH"));
//        System.out.println(dateAddMonth("2019-01" ,-12));
//
//        System.out.println(currentDateAddSubHH(new Date(),-24));

        List<String> weeks = getWeeks(dateFormat.parse("2019-01-01"),dateFormat.parse("2019-02-01"));
        for (String s:weeks) {
            System.out.println(s);
        }

        System.out.println(getStartDayOfWeekNo(2019,1));
        System.out.println(getEndDayOfWeekNo(2019,1));
    }

}
