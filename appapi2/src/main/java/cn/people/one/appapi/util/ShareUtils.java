package cn.people.one.appapi.util;

import cn.people.one.appapi.vo.ArticleDetailVO;
import cn.people.one.appapi.vo.ArticleVO;
import cn.people.one.modules.cms.model.type.SysCodeType;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by wilson on 2018-10-25.
 */
public class ShareUtils {

    public static void setShareUrl(ArticleVO article, String baseShareUrl) {
        if (article == null) {
            return;
        }

        if (!StringUtils.endsWith(baseShareUrl, "/")) {
            baseShareUrl += "/";
        }

        if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.SUBJECT.value())) {
            article.setShareUrl(baseShareUrl + "thematic/" + article.getArticleId());
        } else if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.LIVE.value())) {
            article.setShareUrl(baseShareUrl + "live/" + article.getArticleId());
        }else if (StringUtils.equalsIgnoreCase(article.getType(), "link")) {
            article.setShareUrl(article.getLink());
        }else if (StringUtils.equalsIgnoreCase(article.getType(), "ask")) {
            article.setShareUrl(baseShareUrl + "detail/question/" + article.getArticleId());
        } else {
            article.setShareUrl(baseShareUrl + "detail/normal/"+ article.getArticleId());
        }
    }

    public static void setShareUrl(List<ArticleVO> articles, String baseShareUrl) {
        if (articles == null || articles.size() < 1) {
            return;
        }

        for (ArticleVO article : articles) {
            setShareUrl(article, baseShareUrl);
        }
    }

    public static void setShareUrl(ArticleDetailVO detail, String baseShareUrl) {
        if (detail == null) {
            return;
        }

        if (!StringUtils.endsWith(baseShareUrl, "/")) {
            baseShareUrl += "/";
        }

        if (StringUtils.equalsIgnoreCase(detail.getSysCode(), SysCodeType.SUBJECT.value())) {
            detail.setShareUrl(baseShareUrl + "thematic/" + detail.getArticleId());
        } else if (StringUtils.equalsIgnoreCase(detail.getSysCode(), SysCodeType.LIVE.value())) {
            detail.setShareUrl(baseShareUrl + "live/" + detail.getArticleId());
        } else if (StringUtils.equalsIgnoreCase(detail.getType(), "link")) {
            detail.setShareUrl(detail.getLink());
        }else if (StringUtils.equalsIgnoreCase(detail.getType(), "ask")) {
            detail.setShareUrl(baseShareUrl + "detail/question/" + detail.getArticleId());
        }else {
            detail.setShareUrl(baseShareUrl + "detail/normal/"+ detail.getArticleId());
        }
    }

}
