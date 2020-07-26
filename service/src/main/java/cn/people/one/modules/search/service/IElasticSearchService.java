package cn.people.one.modules.search.service;

import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.search.model.ArticleIndexData;
import cn.people.one.modules.search.model.AskIndexData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;

import java.util.List;

/**
 * Created by lml on 2017/1/19.
 */
public interface IElasticSearchService {

    List<ArticleIndexData> articleSearch(String keywords, int pageNo, int pageSize);

    long search(List list, QueryBuilder queryBuilder, int pageNo, int pageSize);

    void saveHandle(Article article);

    void saveAll(int startPage);

    /**
     * 保存问政title
     * @param startPage
     */
    void saveAllAsk(int startPage);

    void delete(Long id);

    void deleteAll();

    /**
     * 清空所有问政索引
     */
    void deleteAllAsk();

    ArticleIndexData findOne(final Long id);

//    void processHandler(Article article);

    void saveArticle(Article article);

    /**
     * 保存模糊搜索问政到es
     * @param ask
     */
    void saveSuggestAsk(AskQuestionReply ask);

    /**
     * 保存问政到es
     * @param askQuestionReply
     */
    void saveAsk(AskQuestionReply askQuestionReply);

    void removeIndex();

    BoolQueryBuilder getQueryBuilder(String keywords, String time, String categoryName, int pageNo, int pageSize);

    /**
     * 问政标题联想
     * @param keywords
     * @return
     */
    List<String> askSuggest(String keywords);

    /**
     * 问政搜索
     * @param title
     * @param pageNumber
     * @param pageSize
     * @return
     */
    List<AskIndexData> askSearch(String title, Integer pageNumber, Integer pageSize);
}
