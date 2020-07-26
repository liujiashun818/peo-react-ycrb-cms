package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.ArticleTaskRecord;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.front.ArticleVO;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * 定时发布
 */
public interface IArticleTaskRecordService extends IBaseService<ArticleTaskRecord> {

}
