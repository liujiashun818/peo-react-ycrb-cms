package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Subject;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.SubjectVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by lml on 17-3-3.
 */
public interface ISubjectService extends IBaseService<Subject> {

    QueryResultVO<Article> findArticlesInSubject(Integer pageNumber, Integer pageSize, ArticleVO articleVO);

    List<Subject> findBlocks(Long subjectId);

    Boolean saveArticlesToSubject(List<Article> articles, Long categoryId);

    SubjectVO saveSubjectToCategory(SubjectVO subjectVO);

    List<Article> findArticlesByApp(Long id);

    Subject findSubjectByApp(Long id);

    SubjectVO get(Long id);

    Subject insert(Subject subject);

    Object updateSubjectInCategory(SubjectVO subjectVO);

    int delSubject(Long id);

    Subject getSubjectBlocksByArticleId(Long articleId);

    Subject getSubjectBlocksById(Long articleId);
}
