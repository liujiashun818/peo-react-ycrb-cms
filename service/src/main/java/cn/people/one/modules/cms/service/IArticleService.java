package cn.people.one.modules.cms.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.front.ArticleVO;
import io.swagger.models.auth.In;
import org.nutz.dao.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lml on 2016/12/22.
 */
public interface IArticleService extends IBaseService<Article> {

    Article getArticleDetails(Long id);

    Article changeOnlineStatus(Long id);

    void addMetaData(Long articleId, String fieldCode, String fieldValue);

    QueryResultVO<Article> findSearchPage(ArticleVO articleVO);

    List<Article> getAll(int page, int size);

    Article del(Long id);

    void batchInsert(List<Article> list, Long categoryId, Integer block);

    void clientPush(ClientPush clientPush);

    List<Article> findByDocId(Long docId);

    List<Article> findListByApp(Integer pageSize, ArticleVO articleVO);

    List<Article> findListByApp(Integer pageSize, Long categoryId, Integer block);

    Article findArticleByApp(Long articleId,Integer delFlag);

    QueryResultVO<Article> findReferPage(ArticleVO article);

    void saveMediaInfo(ArticleData articleData, List<Long> mediaIds, String audioUrl, String audioCover, String videoUrl, String videoCover);

    int setPosition(Long id, Integer position);

    QueryResult getArticlePageByBlockId(Long blockId, Integer size, Integer page);

    QueryResult getArticlePageByCategoryIdAndBlockId(Long categoryId, Long blockId, Integer size, Integer page);

    List<Article> findArticlePageByArticleVO(Integer pageNumber, Integer pageSize, ArticleVO focusVO);

    String[] categoryIds(Long categoryId);


    List<Category> getPublicCmsCategoryList();

    String syncArticle2PublicCms(Long articleId, Integer syncCategoryId);

    QueryResult getPubliccmsArticle(Map paramMap);


    List<Article> getArticleByArticleId(Long articleId);

    QueryResult getArticleByCategoryId(Long categoryId);

    QueryResult getArticleById(List<Article> list);

    QueryResult getArticleByIds(List<Long> list,Integer pageSize,Integer pageNo);

    QueryResult getSearchArticleById(String str,Integer pageSize,Integer pageNo);

    void savePubliccmsArticle(Long categoryId, String articleIds,String block);

    int getCount(String str);

    Article update(Article article);

    /**
     * 查询第一个articleId的稿件
     * @param articleId
     * @return
     */
    Article findArticleByArticleId(Long articleId,String sysCode,String type);

    /**
     * 根据凡闻id和栏目id查询文章
     * @param docid
     * @param aLong
     * @return
     */
    List<Article> findByFanewsArticleIdAndCategoryId(String docid, Long aLong);

	String getArticlePreviewUrl(Long id);

	Article changeOffLineStatus(Long id);

    /**
     * 提前发布
     * @param id
     */
    void advancePublish(Long id);

    /**
     * 取消发布
     * @param id
     */

    void cancelPublish(Long id);
    /**
     * 设置定时发布
     */
    void fixedPublish(ArticleVO articleVO);

    /**
     * recommondPage
     * @param articleVO
     * @return
     */
    QueryResultVO<Article> recommondPage(ArticleVO articleVO);

    /**
     * 根据ucenterf返回的文章id查询稿件
     * @param size
     * @param pageToken
     * @param newIdArry
     * @return
     */
    QueryResult myHelpList(Integer size, Integer pageToken, String[] newIdArry);
    /**
     * 如果ucenter没有我的爱心数据，查询最新公益栏目下的稿件，取前3条
     * @param size
     * @param pageToken
     * @return
     */
    QueryResult myLastHelpList(Integer size, Integer pageToken,Long id);
}
