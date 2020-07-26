package cn.people.one.core.util.time;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YX
 * @date 2018/10/9
 * @comment
 */
@Slf4j
public class ASRUtils {
    //正则表达式
    static final Pattern PATTERN_DATE = Pattern.compile("(\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?(\\d{1,2}月\\d{1,2}(日|号))?", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_DATE_INTERVAL = Pattern.compile("((\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?)+(到|至)((\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?)+", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_DATE_CHAR = Pattern.compile("(去年)?(前年)?(今年)?(今天)?(刚才)?(刚刚)?(不久前)?(昨天)?(前天)?(本周)?(这周)?(最近)?(近期)?(本月)?(这月)?(上周)?", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_DATE_ALL = Pattern.compile("(((\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?)+(到|至)((\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?)+)?((\\d{4}年)?(\\d{1,2}月)?(\\d{1,2}(日|号))?(\\d{1,2}月\\d{1,2}(日|号))?)?(去年)?(前年)?(今年)?(今天)?(刚才)?(刚刚)?(不久前)?(昨天)?(前天)?(本周)?(这周)?(最近)?(近期)?(本月)?(这月)?(上周)?", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_YEAR = Pattern.compile("\\d{4}年", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_MONTH = Pattern.compile("\\d{1,2}月", Pattern.CASE_INSENSITIVE);
    static final Pattern PATTERN_DAY = Pattern.compile("\\d{1,2}(日|号)", Pattern.CASE_INSENSITIVE);
    //表示日期的常量
    static final String DATE_TODAY = "今天,刚才,刚刚,不久前";
    static final String DATE_THIS_WEEK = "本周,这周,最近,近期";
    static final String DATE_THIS_MONTH = "本月,这月";
    static final String DATE_LAST_YEAR = "去年";
    static final String DATE_BEFORE_YEAR = "前年";
    static final String DATE_THIS_YEAR = "今年";
    static final String DATE_LAST_DAY = "昨天";
    static final String DATE_BEFORE_DAY = "前天";
    static final String DATE_LAST_WEEK = "上周";
    static final String FULL_TIME_START = "00:00:00";
    static final String FULL_TIME_END = "23:59:59";
    static final String DATE_FORMATE_ONE = "yyyy年MM月dd日";
    static final String DATE_FORMATE_TWO = "yyyy-MM-dd";
    static final String DATE_FORMATE_THREE = "yyyy-MM-dd hh:mm:ss";
    static final String DATE_START = "1-1";
    static final String DATE_END = "12-31";
    static final String[] DATE_CHAR_ARRY={"去年","前年","今年","今天","刚才","刚刚","不久前","昨天","前天","本周","这周","最近","近期","本月","这月","上周"};
    //表示消息的常量
    static final String PUSH_MESSAGE = "Push,推送,消息";
    static final String REGEX_MESSAGE = "(Push)?(推送)?(消息)?";
    public static void main(String[] args) {
        String dateMatch_test = "2018年";
        String dateIntervalMatch_test = "2017年5月24日至2018年9月26日";
        String dateByChar = "2018年今天hfashfaksh刚刚sahfjashfj去年ah副驾驶飞机开始的减肥前年ahfah今天上周";
        String dateMatchAll = "2018年今天hfashfaksh刚刚sahfjashfj去年ah副驾驶飞机开始的减肥前年ahfah2017年5月24日至2018年9月26日2018年上周";
        String categoryMatch = "附件案件发评论金风科技文热点push";
//        ASRUtils.dateByChar(dateByChar);
//        ASRUtils.dateMatch(dateMatch_test);
//        ASRUtils.dateIntervalMatch(dateIntervalMatch_test,2,"-");
//        ASRUtils.categoryMatch(categoryMatch,null);
//        ASRUtils.getCategorysRegex(categoryListMap);
//        ASRUtils.dateMatchAll(dateMatchAll, 2);
    }

    /**
     * ORDER 1
     * 匹配汉字格式日期
     * @param chineseChar
     * @return
     */
    public static String[] dateByChar(String chineseChar) {
        Matcher matcher = PATTERN_DATE_CHAR.matcher(chineseChar);
        String dateChar = "";
        while (matcher.find()) {
            dateChar = matcher.group(0);
            if (StringUtils.isEmpty(dateChar)) {
                continue;
            }
        }
        String[] dateArry = getFormateDateByChar(dateChar, true);
        return dateArry;
    }

    /**
     * ORDER 2
     * 匹配日期格式
     * XX年XX月XX日;XX年;XX月;XX日;XX年XX月;XX月XX日
     * @return
     */
    public static String[] dateMatch(String strSource) {
        String year = "";
        String month = "";
        String day = "";
        String strMatcher = "";
        Matcher matcher = PATTERN_DATE.matcher(strSource);
        //获取匹配值
        while (matcher.find()) {
            strMatcher = matcher.group(0);
            if (StringUtils.isEmpty(strMatcher)) {
                continue;
            }
        }
        year = getSingleDate(year, strMatcher, PATTERN_YEAR);
        month = getSingleDate(month, strMatcher, PATTERN_MONTH);
        day = getSingleDate(day, strMatcher, PATTERN_DAY);
        String[] dateArry = getFinalDate(year, month, day, true);
        return dateArry;
    }

    /**
     * ORDER 3
     * 匹配日期间隔格式
     * XX年XX月XX日"至/到"XX年XX月XX日
     * @return
     */
    public static String[] dateIntervalMatch(String strSource, int arryLen) {
        String[] yearArry = new String[arryLen];
        String[] monthArry = new String[arryLen];
        String[] dayArry = new String[arryLen];
        String[] dateArry = new String[arryLen];
        String strMatcher = "";
        Matcher matcher = PATTERN_DATE_INTERVAL.matcher(strSource);
        //获取匹配值
        while (matcher.find()) {
            strMatcher = matcher.group(0);
            if (StringUtils.isEmpty(strMatcher)) {
                continue;
            }
        }
        yearArry = getSinglDateArry(strMatcher, PATTERN_YEAR, arryLen);
        monthArry = getSinglDateArry(strMatcher, PATTERN_MONTH, arryLen);
        dayArry = getSinglDateArry(strMatcher, PATTERN_DAY, arryLen);
        for (int i = 0; i < arryLen; i++) {
            if (i == 0) {
                dateArry[i] = getFinalDate(yearArry[i], monthArry[i], dayArry[i], FULL_TIME_START);
            } else {
                dateArry[i] = getFinalDate(yearArry[i], monthArry[i], dayArry[i], FULL_TIME_END);
            }
        }
        return dateArry;
    }

    /**
     * 获取日期中的单个年/月/日
     * @param strMatcher 需要匹配的字符串
     * @param pattern    正则规则表达式
     * @param strOrigin  原日期
     * @return
     */
    public static String getSingleDate(String strOrigin, String strMatcher, Pattern pattern) {
        String date = "";
        Matcher matcher_date = pattern.matcher(strMatcher);
        if (matcher_date.find()) {
            date = matcher_date.group(0).substring(0, matcher_date.group(0).length() - 1);
        }
        return StringUtils.isEmpty(date) ? strOrigin : date;
    }

    /**
     * 获取单个日期的数组
     * @param strMatcher
     * @param pattern
     * @param arryLen
     * @return
     */
    public static String[] getSinglDateArry(String strMatcher, Pattern pattern, int arryLen) {
        String date[] = new String[arryLen];
        int count = 0;
        Matcher matcher_date = pattern.matcher(strMatcher);
        while (matcher_date.find()) {
            date[count] = matcher_date.group(0).substring(0, matcher_date.group(0).length() - 1);
            count++;
            if (count == arryLen) {
                break;
            }
        }
        return date;
    }

    /**
     * 根据汉字日期转化成常规日期查询范围
     *
     * @param dateChar
     * @return
     */
    public static String[] getFormateDateByChar(String dateChar, boolean isFullTime) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat f = null;
        f = new SimpleDateFormat(DATE_FORMATE_TWO);
        StringBuilder dateStart = new StringBuilder("");
        StringBuilder dateEnd = new StringBuilder("");
        String[] dateArry = new String[2];
        if (DATE_TODAY.contains(dateChar)) {
            //今天
            dateStart.append(f.format(calendar.getTime()));
            dateEnd = new StringBuilder(dateStart);
        } else if (DATE_LAST_YEAR.contains(dateChar)) {
            //去年
            dateStart.append(String.valueOf(calendar.get(Calendar.YEAR) - 1));
            dateStart.append("-");
            dateEnd = new StringBuilder(dateStart);
            dateStart.append(DATE_START);
            dateEnd.append(DATE_END);
        } else if (DATE_BEFORE_YEAR.contains(dateChar)) {
            //前年
            dateStart.append(String.valueOf(calendar.get(Calendar.YEAR) - 2));
            dateStart.append("-");
            dateEnd = new StringBuilder(dateStart);
            dateStart.append(DATE_START);
            dateEnd.append(DATE_END);
        } else if (DATE_THIS_YEAR.contains(dateChar)) {
            //今年
            dateStart.append(String.valueOf(calendar.get(Calendar.YEAR)));
            dateStart.append("-");
            dateEnd = new StringBuilder(dateStart);
            dateStart.append(DATE_START);
            dateEnd.append(DATE_END);
        } else if (DATE_LAST_DAY.contains(dateChar)) {
            //昨天
            calendar.add(Calendar.DATE, -1);
            dateStart.append(f.format(calendar.getTime()));
            dateEnd = new StringBuilder(dateStart);
        } else if (DATE_BEFORE_DAY.contains(dateChar)) {
            //前天
            calendar.add(Calendar.DATE, -2);
            dateStart.append(f.format(calendar.getTime()));
            dateEnd = new StringBuilder(dateStart);
        } else if (DATE_THIS_WEEK.contains(dateChar)) {
            //本周
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            dateStart.append(f.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 6);
            dateEnd.append(f.format(calendar.getTime()));
        } else if (DATE_THIS_MONTH.contains(dateChar)) {
            //本月
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            dateStart.append(f.format(calendar.getTime()));
            int dayCount = DateHelper.getDaysByYearMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH + 1));
            calendar.add(Calendar.DATE, dayCount - 1);
            dateEnd.append(f.format(calendar.getTime()));
        } else if (DATE_LAST_WEEK.contains(dateChar)) {
            //上周
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            dateStart.append(f.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 6);
            dateEnd.append(f.format(calendar.getTime()));
        }
        if (isFullTime && !StringUtils.isEmpty(dateStart.toString()) && !StringUtils.isEmpty(dateEnd.toString())) {
            dateStart.append(" " + FULL_TIME_START);
            dateEnd.append(" " + FULL_TIME_END);
        }
        dateArry[0] = dateStart.toString();
        dateArry[1] = dateEnd.toString();
        return dateArry;
    }

    /**
     * 根据分散的年月日组装成可以查询的时间范围
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static String[] getFinalDate(String year, String month, String day, boolean isFullTime) {
        //组装日期
        String[] dateArry = new String[2];
        StringBuilder dateStart = new StringBuilder("");
        StringBuilder dateEnd = new StringBuilder("");
        if (StringUtils.isEmpty(day)) {
            if (StringUtils.isEmpty(month)) {
                if (!StringUtils.isEmpty(year)) {
                    dateStart.append(year);
                    dateStart.append("-");
                    dateEnd = new StringBuilder(dateStart);
                    dateStart.append(DATE_START);
                    dateEnd.append(DATE_END);
                } else {
                    return null;
                }
            } else {
                dateStart.append(StringUtils.isEmpty(year) ? DateHelper.getSysDate(DateHelper.YEAR) : year);
                dateStart.append("-");
                dateStart.append(month);
                dateStart.append("-");
                dateEnd = new StringBuilder(dateStart);
                dateStart.append("1");
                dateEnd.append(DateHelper.getDaysByYearMonth(Integer.parseInt(DateHelper.getSysDate(DateHelper.YEAR)), Integer.parseInt(month)));
            }
        } else {
            dateStart.append(StringUtils.isEmpty(year) ? DateHelper.getSysDate(DateHelper.YEAR) : year);
            dateStart.append("-");
            dateStart.append(StringUtils.isEmpty(month) ? DateHelper.getSysDate(DateHelper.MONTH) : month);
            dateStart.append("-");
            dateStart.append(day);
            dateEnd = new StringBuilder(dateStart);
        }
        if (isFullTime && !StringUtils.isBlank(dateStart.toString()) && !StringUtils.isEmpty(dateEnd.toString())) {
            dateStart.append(" " + FULL_TIME_START);
            dateEnd.append(" " + FULL_TIME_END);
        }
        dateArry[0] = dateStart.toString();
        dateArry[1] = dateEnd.toString();
        return dateArry;
    }

    /**
     * 根据分散的年月日组装成完整时间
     * @param year
     * @param month
     * @param day
     * @param fullTime
     * @return
     */
    public static String getFinalDate(String year, String month, String day, String fullTime) {
        //组装日期
        StringBuilder dateStr = new StringBuilder("");
        if (StringUtils.isEmpty(day)) {
            if (StringUtils.isEmpty(month)) {
                if (!StringUtils.isEmpty(year)) {
                    dateStr.append(year);
                    dateStr.append("-");
                    dateStr.append(DATE_START);
                } else {
                    return null;
                }
            } else {
                dateStr.append(StringUtils.isEmpty(year) ? DateHelper.getSysDate(DateHelper.YEAR) : year);
                dateStr.append("-");
                dateStr.append(month);
                dateStr.append("-");
                dateStr.append("1");
            }
        } else {
            dateStr.append(StringUtils.isEmpty(year) ? DateHelper.getSysDate(DateHelper.YEAR) : year);
            dateStr.append("-");
            dateStr.append(StringUtils.isEmpty(month) ? DateHelper.getSysDate(DateHelper.MONTH) : month);
            dateStr.append("-");
            dateStr.append(day);
        }
        if (!StringUtils.isEmpty(fullTime)) {
            dateStr.append(" " + fullTime);
        }
        return dateStr.toString();
    }

    /**
     * 正则过滤所有日期
     * @param strSource
     * @return
     */
    public static String[] dateMatchAll(String strSource, int arryLen) {
        String[] dateArry = new String[arryLen];
        Matcher matcher = PATTERN_DATE_ALL.matcher(strSource);
        String strMatcher = "";
        while (matcher.find()) {
            if (StringUtils.isEmpty(matcher.group(0))) {
                continue;
            }
            strMatcher = matcher.group(0);
        }
        log.info(strMatcher);
        for (String s : DATE_CHAR_ARRY) {
            if(strMatcher.lastIndexOf(s)>0){
                dateArry = dateByChar(strMatcher);
                return dateArry;
            }
        }
        if (PATTERN_DATE.matcher(strMatcher).find()) {
            dateArry = dateMatch(strMatcher);
        }
        if (PATTERN_DATE_INTERVAL.matcher(strMatcher).find()) {
            dateArry = dateIntervalMatch(strMatcher, arryLen);
        }
        log.info(dateArry[0] + "=============" + dateArry[1]);
        return dateArry;
    }
}
