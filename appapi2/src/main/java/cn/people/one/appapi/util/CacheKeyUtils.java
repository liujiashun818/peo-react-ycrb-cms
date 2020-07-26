package cn.people.one.appapi.util;

import cn.people.one.appapi.constant.CacheConstant;

/**
 * Created by wilson on 2018-11-05.
 */
public class CacheKeyUtils {

    public static String getAiuiSuggestionsKey() {
        return CacheConstant.Key.Aiui.SUGGESTIONS;
    }

    public static String getKeywordsKey(){
        return CacheConstant.Key.Keywords.LIST;
    }

    public static String getLiveServiceKey(){
        return CacheConstant.Key.LifeService.LIST;
    }

    public static String getArticleListKey(Long categoryId, int page, int size) {
        return String.format(CacheConstant.Key.Article.LIST, categoryId, page, size);
    }

    public static String getSubjectListKey(Long articleId, int page, int size) {
        return  String.format(CacheConstant.Key.Subject.LIST, articleId, page , size);
    }

    public static String getArticleDetailKey(Long articleId) {
        return String.format(CacheConstant.Key.Article.DETAIL, articleId);
    }

    public static String getLiveRoomKey(Long liveId) {
        return String.format(CacheConstant.Key.Live.ROOM, liveId);
    }

    public static String getLiveUserCount(Long liveId) {
        return String.format(CacheConstant.Key.Live.USER_COUNT, liveId);
    }

    public static String getPaperDetailKey(String articleId, String paperName, String pjCode) {
        return String.format(CacheConstant.Key.Paper.DETAIL, articleId, paperName, pjCode);
    }

    public static String getPaperLastPathKey(String paperCode, String nsDate) {
        return String.format(CacheConstant.Key.Paper.LAST_PATH, paperCode, nsDate);
    }
    public static String getAskDetailKey(Long askId) {
        return String.format(CacheConstant.Key.Ask.DETAIL, askId);
    }


    public static String getAdsDetailKey(Integer type) {
        return String.format(CacheConstant.Key.Advert.DETAIL, type);
    }

    public static String getAdsListKey(Integer type,Long catId) {
        return String.format(CacheConstant.Key.Advert.LIST, type,catId);
    }
    public static String getPaperDetailKey(String articleId, String paperName, String platform, String pjCode) {
        return String.format(CacheConstant.Key.Paper.DETAIL, articleId, paperName, platform, pjCode);
    }
}
