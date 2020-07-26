package cn.people.one.modules.cms.service.impl;

import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleRelation;
import cn.people.one.modules.cms.service.IArticleRelationService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ArticleRelationService extends BaseService<ArticleRelation> implements IArticleRelationService{
    @Override
    public List<ArticleRelation> findRecommondArticle(Long articleId) {
        Condition condition = Cnd.where("article_id","=",articleId).and(Article.FIELD_STATUS,"=",0);
        QueryResult queryResult = this.listPage(1, 5, condition);
        List<ArticleRelation> list = queryResult.getList(ArticleRelation.class);
        return list;
    }
}
