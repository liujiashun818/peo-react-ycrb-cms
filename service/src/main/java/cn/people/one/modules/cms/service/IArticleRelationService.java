package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.ArticleRelation;
import io.swagger.models.auth.In;
import org.nutz.dao.QueryResult;

import java.util.List;

public interface IArticleRelationService extends IBaseService<ArticleRelation>{
    List<ArticleRelation> findRecommondArticle(Long articleId);
}
