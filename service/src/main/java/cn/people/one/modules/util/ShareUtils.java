package cn.people.one.modules.util;


import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by wilson on 2018-10-25.
 */
public class ShareUtils {

    /**
     * 不同类型新闻shareurl  拼接规则
     */
    private static final String SHARE_SUBJECT="thematic/";
    private static final String SHARE_LIVE="live/";
    private static final String SHARE_ASK="detail/question/";
    private static final String SHARE_NORMAL="detail/normal/";


    /**
     * 设置单条记录的shareUrl
     * @param article
     * @param baseShareUrl
     */
    public static void setShareUrl(Article article, String baseShareUrl) {
        if (article == null) {
            return;
        }
        if (!StringUtils.endsWith(baseShareUrl, "/")) {
            baseShareUrl += "/";
        }
        if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.SUBJECT.value())) {
            article.setShareUrl(baseShareUrl + SHARE_SUBJECT + article.getArticleId());
        } else if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.LIVE.value())) {
            article.setShareUrl(baseShareUrl + SHARE_LIVE + article.getArticleId());
        }else if (StringUtils.equalsIgnoreCase(article.getType(), ArticleType.LINK.value())) {
            article.setShareUrl(article.getLink());
        }else if (StringUtils.equalsIgnoreCase(article.getType(), ArticleType.ASK.value())) {
            article.setShareUrl(baseShareUrl + SHARE_ASK + article.getArticleId());
        } else {
            article.setShareUrl(baseShareUrl + SHARE_NORMAL+ article.getArticleId());
        }
    }

    /**
     * 设置多条记录shareUrl
     * @param articles
     * @param baseShareUrl
     */
    public static void setShareUrl(List<Article> articles, String baseShareUrl) {
        if (articles == null || articles.size() < 1) {
            return;
        }
        for (Article article : articles) {
            setShareUrl(article, baseShareUrl);
        }
    }


}
