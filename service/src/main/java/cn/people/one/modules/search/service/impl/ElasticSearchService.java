package cn.people.one.modules.search.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.time.AiuiTimeUtils;
import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.search.model.ArticleIndexData;
import cn.people.one.modules.search.model.AskIndexData;
import cn.people.one.modules.search.model.AskSuggestData;
import cn.people.one.modules.search.repository.ArticleIndexRepository;
import cn.people.one.modules.search.repository.AskRepository;
import cn.people.one.modules.search.repository.AskSuggestRepository;
import cn.people.one.modules.search.service.IElasticSearchService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by lml on 2017/1/17.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class ElasticSearchService implements IElasticSearchService {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ArticleIndexRepository articleIndexRepository;
    @Autowired
    private AskSuggestRepository askSuggestRepository;

    @Autowired
    private AskRepository askRepository;
    @Autowired
    private IArticleService articleService;
    @Autowired
    private IAskQuestionReplyService askQuestionReplyService;

    @Autowired
    private Client client;

    @Value("${theone.elasticsearch.index.name}")
    private String indexName;

    @Value("${theone.elasticsearch.type}")
    private String type;

    @Value("${theone.elasticsearch.ask.index.name}")
    private String askIndexName;

    @Value("${theone.elasticsearch.ask.type}")
    private String askType;
    @Autowired
    private BaseDao dao;

    /**
     * 对查询字段加权重搜索
     *
     * @param keywords
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public List<ArticleIndexData> articleSearch(String keywords, int pageNo, int pageSize) {
        List<ArticleIndexData> list = new ArrayList();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        keywords = keywords.trim();
        try {
            if (StringUtils.isNotBlank(keywords)) {
                if (keywords.length() < 4) {
                    //短字数的关键词查询不做分词处理
                    queryBuilder.should(QueryBuilders.matchPhraseQuery(ArticleIndexData.TITLE, keywords).boost(10f))
                            .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.KEYWORDS, keywords).boost(2f));
//                            .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.CONTENT, keywords).boost(1f));
                } else {
                    //分词并且匹配条件
                    queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.TITLE, keywords).boost(10.0f));
                    queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.KEYWORDS, keywords).boost(2.0f));
//                    queryBuilder.should(QueryBuilders.wildcardQuery(ArticleIndexData.CONTENT, "*" + keywords + "*").boost(1f));
                }
            }
            queryBuilder.minimumNumberShouldMatch(1);
            search(list, queryBuilder, pageNo, pageSize);
        } catch (Exception e) {
            log.error("从ES查询失败", e);
        }
        return list;
    }

    /**
     * 拼接检索条件
     *
     * @param list
     * @param queryBuilder
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public long search(List list, QueryBuilder queryBuilder, int pageNo, int pageSize) {
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName)  //检索的目录
                .setTypes(type)  //检索的索引
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom((pageNo - 1) * pageSize)
                .setSize(pageSize)
                .addSort("_score", SortOrder.DESC)
                .addSort("publishDate", SortOrder.DESC);
        searchRequestBuilder.setQuery(queryBuilder);
        return getDynamicResults(list, searchRequestBuilder);
    }

    @Override
    public List<String> askSuggest(String keyword) {
        List<String> words = Lists.newArrayList();
        CompletionSuggestionBuilder suggest = new CompletionSuggestionBuilder("suggest").field("title").text(keyword).size(10);
        SearchRequestBuilder request = client.prepareSearch("ask_suggest_index").setTypes("ask_suggest").setSize(0).addSuggestion(suggest).addSort("date", SortOrder.DESC);
        SearchResponse response = request.get();
        try {
            JSONObject jo = JSONObject.parseObject(response.toString());
            JSONArray ary= jo.getJSONObject("suggest").getJSONArray("suggest").getJSONObject(0).getJSONArray("options");
            for (int i=0;i<ary.size();i++) {
                JSONObject json = ary.getJSONObject(i);
                words.add(json.getString("text"));
            }

        } catch (Exception e) {
            log.error("问政联想失败",e.getMessage());
        }

        return words;
    }
    /**
     * 获取结果（分数过滤匹配结果）
     *
     * @param results
     * @param searchRequestBuilder
     * @return
     */
    public long getDynamicResults(List results, SearchRequestBuilder searchRequestBuilder) {
        searchRequestBuilder.addHighlightedField(ArticleIndexData.LIST_TITLE);
//        searchRequestBuilder.addHighlightedField(ArticleIndexData.KEYWORDS);
//        searchRequestBuilder.addHighlightedField(ArticleIndexData.LIST_TITLE);
//        searchRequestBuilder.addHighlightedField(ArticleIndexData.DESCRIPTION);
//        searchRequestBuilder.addHighlightedField(ArticleIndexData.CONTENT_TEXT);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        long count = response.getHits().getTotalHits();
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit hit : searchHits) {
            ArticleIndexData entity = objectMapper.convertValue(hit.getSource(), ArticleIndexData.class);
            // 获取对应的hit域
            Map<String, HighlightField> result = hit.highlightFields();
            Set<String> keys = result.keySet();
            for (String fieldName : keys) {
//                if (!ArticleIndexData.CONTENT_TEXT.equalsIgnoreCase(fieldName) && hit.getScore() < 0.1) {
//                    continue;
//                }
                if(ArticleIndexData.LIST_TITLE.equals(fieldName)) {
                    // 标题高亮
                    HighlightField f=result.get(fieldName);
                    if(f.getFragments().length>0) {
                        entity.setListTitle(f.getFragments()[0].string());
                    }
                }
            }
            results.add(entity);
        }

        return count;
    }

//    @Override
//    @RabbitListener(queues = "${theone.project.code}_article_index_queue")
//    @RabbitHandler
//    public void processHandler(Article article) {
//        if (article == null || article.getIsReference() != null && article.getIsReference().equals(true)) {
//            return;
//        }
//        if (article.getDelFlag() == Article.STATUS_ONLINE) {
//            saveHandle(article);
//            log.info("es同步更新保存消息");
//        } else if (article.getDelFlag() == Article.STATUS_OFFLINE || article.getDelFlag() == Article.STATUS_DELETE) {
//            ArticleIndexData articleIndexData = get(article.getId());
//            if (!Lang.isEmpty(articleIndexData)) {
//                delete(article.getId());
//                log.info("es成功删除一条消息");
//            }
//        }
//    }

    /**
     * 將文章保存到es中
     *
     * @param article
     */
    @Override
    public void saveArticle(Article article) {
        if (article == null || article.getIsReference() != null && article.getIsReference().equals(true)) {
            return;
        }
        if (article.getDelFlag() == Article.STATUS_ONLINE) {
            saveHandle(article);
            log.info("es同步更新保存消息");
        } else if (article.getDelFlag() == Article.STATUS_OFFLINE || article.getDelFlag() == Article.STATUS_DELETE) {
            ArticleIndexData articleIndexData = get(article.getId());
            if (!Lang.isEmpty(articleIndexData)) {
                delete(article.getId());
                log.info("es成功删除一条消息");
            }
        }
    }

    /**
     * 保存模糊搜索问政数据到ES
     * @param ask
     */
    @Override
    public void saveSuggestAsk(AskQuestionReply ask) {
        log.info("Build suggestAsk index ({}, {})", ask.getId(), ask.getTitle());
        try {
            if(ask!=null){
                askSuggestRepository.delete(ask.getId());
            }
            if (ask.isNormal()) {
                // 先去重，删除所有旧数据 ,暂定一千，因数据较少，可按此方案执行
                log.info("满足建索引条件");
                QueryResult r = askQuestionReplyService.queryForCheck(ask.getTitle(),1000);
                if(r !=null && r.getList().isEmpty()) {
                    for (int i=0; i< r.getList().size();i++) {
                        AskQuestionReply q = (AskQuestionReply)r.getList().get(i);
                        askSuggestRepository.delete(q.getId());
                    }
                }

                // 将新数据插入，（可能不是最新数据，但名称一致，获取时会取最新数据）
                askSuggestRepository.save(new AskSuggestData(ask));
            }
        } catch (Exception e) {
            log.error("ask " + ask.getId() + " build index error", e);
        }
    }

    /**
     * 消息类型为Article
     *
     * @param article
     */
    @Override
    public void saveHandle(Article article) {
        try {
            if (null != article) {
                articleIndexRepository.save(new ArticleIndexData(article));
            }
        } catch (Exception e) {
            log.error("从ES存储失败", e);
        }
    }

    public ArticleIndexData get(Long id) {
        return articleIndexRepository.findOne(id);
    }

    //同步数据库
    @Override
    public void saveAll(int startPage) {
        int page = startPage > 0 ? startPage : 1;
        int size = 2000;
        articleIndexRepository.deleteAll();
        while (true) {
            List<Article> list = articleService.getAll(page, size);
            if (list == null) {
                log.info("Rebuild index success.");
                break;
            }

            log.info("Start rebuild index: page {}, size {}", page, size);
            List<ArticleIndexData> indexList=new ArrayList<>();
            for (Article article : list) {
//                log.info("Build index ({}, {})", article.getId(), article.getTitle());
            	if (article.getDelFlag().equals(BaseEntity.STATUS_ONLINE)) {
            		indexList.add(new ArticleIndexData(article));
                }
            }
            try {
            	 articleIndexRepository.save(indexList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
           
            page++;
            if (list.size() < size) {
                log.info("Rebuild index success.");
                break;
            }
        }
    }

    //同步数据库
    @Override
    public void saveAllAsk(int startPage) {
        int page = startPage > 0 ? startPage : 1;
        int size = 2000;
        while (true) {
            List<AskQuestionReply> list = askQuestionReplyService.getAll(page, size);
            if (list == null) {
                log.info("Rebuild index success.");
                break;
            }

            log.info("Start rebuild index: page {}, size {}", page, size);
            List<AskIndexData> askIndexList=new ArrayList<>();
            List<AskSuggestData> askSuggestDataList=new ArrayList<>();
            for (AskQuestionReply ask : list) {
            	askIndexList.add(new AskIndexData(ask));
            	askSuggestDataList.add(new AskSuggestData(ask));
            }
            try {
            	askRepository.save(askIndexList);
            	askSuggestRepository.save(askSuggestDataList);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}

            page++;

            if (list.size() < size) {
                log.info("Rebuild index success.");
                break;
            }
        }
    }

    /**
     * 保存搜索问政数据到es
     * @param ask
     */
    @Override
    public void saveAsk(AskQuestionReply ask) {
        log.info("Build ask index ({}, {})", ask.getId(), ask.getTitle());
        try {
                askRepository.save(new AskIndexData(ask));
            }
         catch (Exception e) {
            log.error("ask " + ask.getId() + " build index error", e);
        }
    }

    /**
     * @param id
     */
    @Override
    public void delete(Long id) {
        try {
            articleIndexRepository.delete(id);
        } catch (Exception e) {
            log.error("从ES删除失败", e);
        }
    }

    /**
     * 清空数据
     */
    @Override
    public void deleteAll() {
        try {
            articleIndexRepository.deleteAll();
            askRepository.deleteAll();
            askSuggestRepository.deleteAll();
        } catch (Exception e) {
            log.error("从ES删除全部数据失败", e);
        }
    }

    /**
     * 清空所有问政索引
     */
    @Override
    public void deleteAllAsk() {
        try {
            askRepository.deleteAll();
            askSuggestRepository.deleteAll();
        } catch (Exception e) {
            log.error("从ES删除全部问政数据失败", e);
        }
    }

    /**
     * 根据id检索
     */
    @Override
    public ArticleIndexData findOne(final Long id) {
        ArticleIndexData articleIndexData = null;
        try {
            articleIndexData = articleIndexRepository.findOne(id);
        } catch (Exception e) {
            log.error("从ES根据id查找失败", e);
        }
        return articleIndexData;
    }

    /**
     * 删除整个索引
     */
    @Override
    public void removeIndex() {
        try {
            client.admin().indices().prepareDelete(indexName)
                    .execute().actionGet();
        } catch (Exception e) {
            log.error("从ES删除索引失败", e);
        }
    }

    /**
     * 组装builder
     *
     * @param keywords
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public BoolQueryBuilder getQueryBuilder(String keywords, String time, String categoryName, int pageNo, int pageSize) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        keywords = keywords.trim();
        if (StringUtils.isNotBlank(keywords)) {
            if(keywords.length()<4){
                //短字数的关键词查询分词后全匹配
                queryBuilder.must(QueryBuilders.matchPhraseQuery(ArticleIndexData.LIST_TITLE, keywords));
            }else{
                queryBuilder.must(QueryBuilders.matchQuery(ArticleIndexData.LIST_TITLE, keywords));
            }
            /*if (keywords.length() < 4) {
                //短字数的关键词查询不做分词处理
                queryBuilder.should(QueryBuilders.matchPhraseQuery(ArticleIndexData.TITLE, keywords).boost(10f));
//                        .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.LIST_TITLE, keywords).boost(5f))
//                        .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.KEYWORDS, keywords).boost(2f));
//                        .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.DESCRIPTION, keywords).boost(5f))
//                        .should(QueryBuilders.matchPhraseQuery(ArticleIndexData.CONTENT_TEXT, keywords).boost(1f));
            } else {
                //分词并且匹配条件
                queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.TITLE, keywords).boost(10.0f));
//                queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.LIST_TITLE, keywords).boost(5.0f));
//                queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.KEYWORDS, keywords).boost(2.0f));
//                queryBuilder.should(QueryBuilders.matchQuery(ArticleIndexData.DESCRIPTION, keywords).boost(5.0f));
//                queryBuilder.should(QueryBuilders.wildcardQuery(ArticleIndexData.CONTENT_TEXT, "*" + keywords + "*").boost(1f));
            }*/
        }

        //时间过滤
        if (StringUtils.isNotBlank(time)) {
            Map<String, String> maps = AiuiTimeUtils.getAiuiTime(time);
            Long startTime = DateHelper.getLongByString(maps.get("startTime"), "yyyy-MM-dd");
            Long endtime = DateHelper.getLongByString(maps.get("endTime"), "yyyy-MM-dd");
            queryBuilder.filter(QueryBuilders.rangeQuery("publishDate").gte(startTime).lt(endtime));
        }

        //栏目过滤
        if (StringUtils.isNotBlank(categoryName)) {
            Category category = dao.fetch(Category.class, Cnd.where(Category.Constant.NAME, "=", categoryName));
            if (!Lang.isEmpty(category)) {
                Long categoryId = category.getId();
                String[] categoryIds = articleService.categoryIds(categoryId);
                if (categoryIds != null && categoryIds.length > 0) {
                    queryBuilder.must(QueryBuilders.termsQuery("categoryId", categoryIds));
                }
            }
        }

        queryBuilder.minimumNumberShouldMatch(1);

        return queryBuilder;
    }

    /**
     * 问政检索
     * @param title
     * @return
     */
    @Override
    public List<AskIndexData> askSearch(String title,Integer pageNumber,Integer pageSize){
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        List<AskIndexData> askIndexDataList=new ArrayList<>();
        queryBuilder.must(QueryBuilders.rangeQuery(AskIndexData.Constant.STATUS).lt(4));
        queryBuilder.must(QueryBuilders.rangeQuery(AskIndexData.Constant.STATUS).gt(0));
        queryBuilder.must(QueryBuilders.matchQuery(AskIndexData.Constant.TITLE, title));
        /*if(title.length()<4){
            //短字数的关键词查询分词后全匹配
            queryBuilder.must(QueryBuilders.matchPhraseQuery(AskIndexData.Constant.TITLE, title));
        }else{
            queryBuilder.must(QueryBuilders.matchQuery(AskIndexData.Constant.TITLE, title));
        }*/
        queryBuilder.minimumNumberShouldMatch(1);
        //检索的目录
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(askIndexName)
                //检索的索引
                .setTypes(askType)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setFrom((pageNumber - 1) * pageSize)
                .setSize(pageSize)
                .addSort("_score", SortOrder.DESC)
                .addSort("publishTime", SortOrder.DESC);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.addHighlightedField(AskIndexData.Constant.TITLE);
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        long count = response.getHits().getTotalHits();
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit hit : searchHits) {
            AskIndexData entity = objectMapper.convertValue(hit.getSource(), AskIndexData.class);
            // 获取对应的hit域
            Map<String, HighlightField> result = hit.highlightFields();
            Set<String> keys = result.keySet();
            for (String fieldName : keys) {
                if("title".equals(fieldName)) {
                    // 标题高亮
                    HighlightField f=result.get(fieldName);
                    if(f.getFragments().length>0) {
                        entity.setTitle(f.getFragments()[0].string());
                    }
                }
            }
            askIndexDataList.add(entity);
        }
        return askIndexDataList;
    }
}
