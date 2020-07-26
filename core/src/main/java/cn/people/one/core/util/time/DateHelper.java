package cn.people.one.core.util.time;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 */
@Slf4j
public class DateHelper {
    public static final String DEFAULT_TIME_FORMAT="yyyy-MM-dd hh:mm:ss";
    public static final String DEFAULT_TIME_24_FORMAT="yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String YEAR="YEAR";
    public static final String MONTH="MONTH";
    public static final String DAY="DAY";

    /**
     * 获取一天的开始
     * @param date
     * @return 00:00:00.000
     */
    public static Date getBeginOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天的结束
     * @param date
     * @return 23:59:59.999
     */
    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 根据字符串日期获得一天的开始时间
     * @param dateStr
     * @param pattern
     */
    public static Date getBeginOfDay(String dateStr, String pattern) {
        return getBeginOfDay(parseDate(dateStr, pattern));
    }

    /**
     * 根据字符串日期获得一天的结束时间
     * @param dateStr
     * @param pattern
     */
    public static Date getEndOfDay(String dateStr, String pattern) {
        return getEndOfDay(parseDate(dateStr, pattern));
    }

    /**
     * 根据字符串日期获得一天的开始时间
     * @param dateStr
     */
    public static Date getBeginOfDay(String dateStr) {
        return getBeginOfDay(parseDate(dateStr, DEFAULT_DATE_FORMAT));
    }

    /**
     * 根据字符串日期获得一天的结束时间
     * @param dateStr
     */
    public static Date getEndOfDay(String dateStr) {
        return getEndOfDay(parseDate(dateStr, DEFAULT_DATE_FORMAT));
    }

    /**
     * 将指定格式的日期字符串转换成日期
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date parseDate(String dateStr, String pattern) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 将日期格式化输出
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }


    public static Date getBeginOfMonth(String month) {
        return getBeginOfMonth(parseDate(month, "yyyy-MM"));
    }


    public static Date getEndOfMonth(String month) {
        return getEndOfMonth(parseDate(month, "yyyy-MM"));
    }

    public static Date getBeginOfMonth(Date month) {
        return getBeginOfDay(month);
    }

    public static Date getEndOfMonth(Date month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        return cal.getTime();
    }

    /**
     * 获取当前月份的上个月的最后一天
     * @return
     */
    public static Date getLastDayForPrevMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, 1);
        cal.add(Calendar.DATE, -1);
        return getEndOfDay(cal.getTime());
    }

    /**
     * 获取日期的第一天
     * @param month
     * @return
     */
    public static Date getBeginDayOfMonth(Date month) {
        Calendar c = Calendar.getInstance();
        c.setTime(month);
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    /**
     * 将字符串日期转换成long型
     * @param strDate
     * @param pattern
     * @return
     */
    public static Long getLongByString(String strDate,String pattern){
        Long longDate=null;
        try {
            if(StringUtils.isBlank(pattern)){
                pattern="yyyy-MM-dd HH:mm:ss";
            }
            Date date=new SimpleDateFormat(pattern).parse(strDate);
            longDate=date.getTime();
        } catch (ParseException e) {
            log.error("解析日期出错--->DateHelper.getLongByString",e);
        }
        return longDate;
    }

    /**
     * 获取系统当前年份/月份
     * @param sysCode
     * @return
     */
    public static String getSysDate(String sysCode) {
        Calendar date = Calendar.getInstance();
        String strDate="";
        if(YEAR.equals(sysCode)){
            strDate= String.valueOf(date.get(Calendar.YEAR));
        }else if(MONTH.equals(sysCode)){
            strDate= String.valueOf(date.get(Calendar.MONTH)+1);
        }else if(DAY.equals(sysCode)){
            strDate= String.valueOf(date.get(Calendar.DAY_OF_MONTH));
        }
        return strDate;
    }
    /**
     * 根据年 月 获取对应的月份 天数
     */
    public static int getDaysByYearMonth(int year, int month) {
        Calendar a = Calendar.getInstance();
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);
        a.roll(Calendar.DATE, -1);
        int maxDate = a.get(Calendar.DATE);
        return maxDate;
    }

    /**
     * 将long类型转换成固定格式的日期字符串
     * @param pattern
     * @param time
     * @return
     */
    public static String getFormatByLong(String pattern,Long time){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            Date date = new Date(time);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将格式化的日期字符串二次格式化
     * @param patternDest
     * @param dateStr
     * @param patternOri
     * @return
     */
    public static String secondFormate(String patternDest,String dateStr,String patternOri){
        String dateDest= null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(patternOri);
            Date date = sdf.parse(dateStr);
            SimpleDateFormat sdfDest = new SimpleDateFormat(patternDest);
            dateDest = sdfDest.format(date);
        } catch (ParseException e) {
            log.info("日期转换出错",e);
            return null;
        }
        return dateDest;
    }

    /**
     * 转换时分秒为秒数
     * @param time
     * @param split
     * @return
     */
    public static Long getSeconds(String time,String split){
        Long secondCount=0L;
        String splitStr=split==null?":":split;
        try {
            if(StringUtils.isBlank(time)){
                return null;
            }
            String[] timeArry=time.split(splitStr);
            for (int i = 0; i < timeArry.length; i++) {
                double senconds=Math.pow(60,(timeArry.length-1-i));
                secondCount+=new Double(senconds).longValue()*Long.valueOf(timeArry[i]);
            }
        } catch (NumberFormatException e) {
            log.error("转换时分秒为秒数出错===DateHelper/getSeconds",e);
            return secondCount;
        }
        return secondCount;
    }

    /**
     * 格式话时间
     * @param time
     * @param split
     * @return
     */
    public static String getFormateTime(String time,String split){
        String splitStr=split==null?":":split;
        if(StringUtils.isBlank(time)){
            return null;
        }
        String[] timeArry=time.split(splitStr);
        for (int i = 0; i < timeArry.length; i++) {
            String timeStr=timeArry[i];
            if(!timeStr.contains("0")){
                timeArry[i]=Integer.valueOf(timeStr)<10?"0"+timeStr:timeStr;
            }
        }
        return StringUtils.join(timeArry,":");
    }

    /**
     * 秒转成hh:MM:ss
     * @param seconds
     * @return
     */
    public static String secToTime(String seconds) {
        String timeStr = null;
        try {
            if(StringUtils.isBlank(seconds)){
                return null;
            }
            int time=new BigDecimal(seconds).intValue();
            timeStr = null;
            int hour = 0;
            int minute = 0;
            int second = 0;
            if (time <= 0){
                return "00:00";
            }
            else {
                minute = time / 60;
                if (minute < 60) {
                    second = time % 60;
                    timeStr = unitFormat(minute) + ":" + unitFormat(second);
                } else {
                    hour = minute / 60;
                    if (hour > 99) {
                        return "99:59:59";
                    }
                    minute = minute % 60;
                    second = time - hour * 3600 - minute * 60;
                    timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
                }
            }
        } catch (Exception e) {
            return seconds;
        }
        return timeStr;
    }

    /**
     * 时间补零格式化
     * @param i
     * @return
     */
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static void main(String[] args) {
        /*final Date date = DateHelper.getBeginDayOfMonth(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf.format(date));
        System.out.println(new java.sql.Date(new Date().getTime()));*/
//        System.out.println(getLongByString("2018-09-09 09:19:20",null));
//        System.out.println(secondFormate("yyyy-MM-dd","20181010","yyyyMMdd"));
        System.out.println(secToTime("459.141256"));
    }
}
