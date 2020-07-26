package cn.people.one.core.util.time;

import cn.people.one.core.util.time.DateFormatUtil;
import cn.people.one.core.util.time.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sunday on 2018/10/30.
 */
@Slf4j
public class AiuiTimeUtils {

    /**
     * 获取AIUI语音识别的时间值
     *
     * @param time：时间槽位，未传值是默认为当天，格式有具体和区间两种，如具体：2018-10-29、2018-10、2018； 区间：2018-10-01/2018-10-07、2018-08/2018-10、2016/2018
     * @return
     */
    public static Map<String, String> getAiuiTime(String time) {
        Map<String, String> map = new HashMap<>();
        String startTime;
        String endTime;
        if (Lang.isEmpty(time) || "".equalsIgnoreCase(time.trim())) {
            startTime = endTime = null;
        } else {
            if (time.contains("/")) {
                //如果包含/代表语音识别的时间为一个区间
                String[] times = time.split("/");
                startTime = getRealDay(times[0], "start");
                endTime = getRealDay(times[1], "end");
            } else {
                startTime = getRealDay(time, "start");
                endTime = getRealDay(time, "end");
            }

            try {
                Date end = DateUtil.addDays(DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, endTime), 1);
                endTime = DateFormatUtil.formatDate(DateFormatUtil.PATTERN_ISO_ON_DATE, end);
            } catch (Exception e) {
                log.error("AiuiTimeUtils getAiuiTime method pareDate error!!!", e);
            }

        }
        log.info("getAiuiTime time:" + time + " startTime:" + startTime + " endTime:" + endTime);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }

    /**
     * AIUI获取真正的时间格式 yyyy-mm-dd
     *
     * @param time
     * @param flag
     * @return
     */
    private static String getRealDay(String time, String flag) {
        String realTime;
        String times[] = time.split("-");
        String year = times[0];
        String month;
        if (times.length >= 2) {
            //日期中包含月份
            month = times[1];
        } else {
            if (flag.equals("start")) {
                month = "01";
            } else {
                month = "12";
            }
        }
        String day;
        if (times.length >= 3) {
            //日期中包含天
            day = times[2];

        } else {
            if (flag.equals("start")) {
                day = "01";
            } else {
                day = String.valueOf(DateUtil.getMonthLength(Integer.valueOf(year), Integer.valueOf(month)));
            }
        }
        realTime = year + "-" + month + "-" + day;
        return realTime;
    }

    public static void main(String[] args) {
        getAiuiTime("2018-10-29");
        getAiuiTime("2018-10");
        getAiuiTime("2018");
        getAiuiTime("2018-10-01/2018-10-07");
        getAiuiTime("2018-08/2018-10");
        getAiuiTime("2016/2018");
        getAiuiTime(null);
    }

}
