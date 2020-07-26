package cn.people.one.appapi.constant;

import cn.people.one.appapi.model.ErrorMessage;

/**
 * Created by wilson on 2018-10-11.
 */
public class CodeConstant {

    public static final ErrorMessage ERROR = ErrorMessage.error(-1, "请求出错");
    public static final ErrorMessage SYSTEM_ERROR = ErrorMessage.error(-1, "系统异常");
    public static final ErrorMessage PARAM_ERROR = ErrorMessage.error(-1, "请求参数错误");

    public static final ErrorMessage SUCCESS = ErrorMessage.error(0, "success");
    public static final ErrorMessage TOKEN_ERROR = ErrorMessage.error(2, "token失效");

    public static final ErrorMessage EMPTY_RESULT = ErrorMessage.error(-1, "查询结果为空");
    public static final ErrorMessage ARTICLE_NOT_EXIST = ErrorMessage.error(-1, "请求文章不存在或已下线");
    public static final ErrorMessage LIVE_NOT_EXIST = ErrorMessage.error(-1, "请求直播不存在或已下线");
    public static final ErrorMessage SUBJECT_NOT_EXIST = ErrorMessage.error(-1, "请求专题不存在或已下线");
    public static final ErrorMessage COMMENT_NOT_EXIST = ErrorMessage.error(-1, "评论不存在或已删除");
    public static final ErrorMessage LOADINGIMGS_NOT_EXIST = ErrorMessage.error(-1, "开屏图不存在或已删除");
    public static final ErrorMessage FLOATINGIMGS_NOT_EXIST = ErrorMessage.error(-1, "浮标图不存在或已删除");
    public static final ErrorMessage UNAUTHORIZED = ErrorMessage.error(-1, "无操作权限");

    /**
     * 圈子数据相关
     */
    public static final ErrorMessage RECOMMEND_DATA_ERROR = ErrorMessage.error(-1, "获取推荐数据异常"); 
    public static final ErrorMessage SEARCH_DATA_ERROR = ErrorMessage.error(-1, "获取搜索数据异常"); 
    public static final ErrorMessage AD_STATISTICS_ERROR = ErrorMessage.error(-1, "广告统计异常"); 
    
    /**
     * 直播相关
     */
    public static final ErrorMessage LIVEROOM_NOT_EXIST = ErrorMessage.error(-1, "请求直播不存在");
    /**
     * 问政相关
     */
    public static final ErrorMessage ASK_NOT_EXIST = ErrorMessage.error(-1, "请求问政不存在");

    /**
     * 政务相关
     */
    public static final ErrorMessage SAVE_COMMENT_ERROR = ErrorMessage.error(-1, "保存政务评论失败");

}
