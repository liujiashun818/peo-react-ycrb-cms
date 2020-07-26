package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Tags;
import cn.people.one.modules.cms.model.front.TagsVO;
import org.nutz.dao.QueryResult;

import java.util.List;

/**
 * Created by sunday on 2017/4/12.
 */
public interface ITagsService extends IBaseService<Tags> {

    QueryResultVO<Tags> findSearchPage(Integer pageNumber, Integer pageSize);

    List<TagsVO> queryCount();

    List<Article> getArticlesByTags(Tags tags);

    List<Tags> queryAll();
}
