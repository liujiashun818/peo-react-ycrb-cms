package cn.people.one.appapi.util;

/**
 * Created by wilson on 2018-11-06.
 */
public class PageUtils {

    public static int toPage(String pageToken) {
        int page = 0;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception ignored) {
        }
        return page;
    }

}
