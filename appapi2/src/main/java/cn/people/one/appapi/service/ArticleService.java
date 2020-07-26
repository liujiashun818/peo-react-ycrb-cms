package cn.people.one.appapi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.people.one.appapi.vo.*;
import cn.people.one.core.util.time.AiuiTimeUtils;
import cn.people.one.modules.cms.model.*;
import cn.people.one.modules.cms.service.*;
import io.swagger.models.auth.In;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.converter.ArticleConverter;
import cn.people.one.appapi.converter.PageConverter;
import cn.people.one.appapi.provider.ad.AdProvider;
import cn.people.one.appapi.provider.ad.model.AdVo;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.util.PageUtils;
import cn.people.one.appapi.util.ShareUtils;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.activitycode.utils.RestTemplateUtil;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.service.ILiveRoomService;
import cn.people.one.modules.search.model.ArticleIndexData;
import cn.people.one.modules.search.service.IElasticSearchService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wilson on 2018-10-11.
 */
@Slf4j
@Service("articleServiceV2")
@EnableAsync
public class ArticleService {

    @Value("${http.vshare}")
    private String url;

    @Value("${theone.adStats.url}")
    private String adStatsUrl;

    @Value("${theone.ad.url}")
    private String adUrl;

    @Value("${http.ucenter}")
    private String ucenterUrl;

    @Autowired
    private ILiveRoomService roomService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ISubjectService subjectService;

    @Autowired
    private IElasticSearchService elasticSearchService;

    @Autowired
    private AdProvider adProvider;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private IArticleDataService articleDataService;

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    BaseDao dao;
    @Autowired
    private IUserService userService;
    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IArticleRelationService iArticleRelationService;

    private static final String MYHELP_URL = "user/myhelp/getMyHelpList";

    public ResultVO3<ArticleDetailVO> detail(Long articleId,Integer delFlag) {
        if (articleId == null || articleId < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }

        String key = CacheKeyUtils.getArticleDetailKey(articleId);
        Cache<ArticleDetailVO> cache = cacheRepository.getObject(key, ArticleDetailVO.class);
        if (cache.getStatus() == CacheStatus.NOT_CACHING) {
            ArticleDetailVO detail = fetchDetail(articleId,delFlag);
            cacheRepository.cache(key, detail, CacheConstant.Time.TEN_SECOND);
            cache.setObject(detail);
        }

        if (cache.getObject() != null) {
            return ResultVO3.success(cache.getObject());
        } else {
            return ResultVO3.result(CodeConstant.ARTICLE_NOT_EXIST);
        }
    }

    private ArticleDetailVO fetchDetail(Long articleId,Integer delFlag) {
        Article article = articleService.findArticleByApp(articleId,delFlag);
        if (article == null) {
            return null;
        }

        ArticleDetailVO detail = new ArticleDetailVO();
        BeanUtils.copyProperties(article, detail, "date", "image", "shareUrl", "content", "metas", "votes");
        detail.setDate(article.getPublishDate());

        if (SysCodeType.ARTICLE.value().equalsIgnoreCase(detail.getSysCode())) {//文章类型
            if ("image".equals(detail.getType())) {//图集
                detail.setMedias(ArticleConverter.toMediaVO(article.getDetailMedias()));
            }
        }

        if (SysCodeType.SUBJECT.value().equalsIgnoreCase(detail.getSysCode())) {//主题类型
            cn.people.one.modules.cms.model.front.SubjectVO subject = subjectService.get(article.getArticleId());
            if (subject != null && StringUtils.isNotBlank(subject.getBannerUrl())) {
                detail.setBannerUrl(JSON.parseArray(subject.getBannerUrl()));
            }
        }

        detail.setMedias(BeanMapper.mapList(article.getDetailMedias(), ArticleMediaVO.class));
        if (StringUtils.isNotBlank(article.getImageUrl())) {
            detail.setImages(Arrays.asList(StringUtils.split(article.getImageUrl(), ",")));
        }

        if (StringUtils.isNotBlank(article.getMoreVideos())) {
            detail.setMoreVideos(JSON.parseArray(article.getMoreVideos(), ArticleMoreVideoVO.class));
        }

        ArticleData articleData = article.getArticleData();
        if (articleData != null && StringUtils.isNotBlank(articleData.getContent())) {
            detail.setContent(article.getArticleData().getContent());
        }

        if (article.getMetas() != null && article.getMetas().size() > 0) {
            List<ArticleMetaVO> metas = new ArrayList<>();
            for (ArticleMeta articleMeta : article.getMetas()) {
                ArticleMetaVO meta = new ArticleMetaVO();
                meta.setFieldCode(articleMeta.getFieldCode());
                meta.setArticleId(articleMeta.getArticleId());
                meta.setFieldValue(articleMeta.getFieldValue());
                metas.add(meta);
            }
            detail.setMetas(metas);
        }

        if (article.getVotes() != null && article.getVotes().size() > 0) {
            List<VoteVO> votes = new ArrayList<>(article.getVotes().size());
            for (Vote v : article.getVotes()) {
                VoteVO vote = new VoteVO();
                vote.setId(v.getId());
                if (StringUtils.isNotBlank(v.getTitle())) {
                    vote.setTitle(v.getTitle());
                }
                if (null != v.getDate()) {
                    vote.setDate(v.getDate());
                }
                if (null != v.getType()) {
                    vote.setType(v.getType());
                }
                if (null != v.getIsShowResult()) {
                    vote.setIsShowResult(v.getIsShowResult());
                }

                if (v.getOptions() != null && v.getOptions().size() > 0) {
                    List<VoteOptionVO> options = new ArrayList<>(v.getOptions().size());
                    for (VoteOption o : v.getOptions()) {
                        VoteOptionVO option = new VoteOptionVO();
                        option.setId(o.getId());
                        if (StringUtils.isNotBlank(o.getTitle())) {
                            option.setTitle(o.getTitle());
                        }
                        if (null != o.getHits()) {
                            option.setHits(o.getHits());
                        }
                        options.add(option);
                    }
                    vote.setOptions(options);
                }
                votes.add(vote);
            }

            if (votes.size() > 0) {
                detail.setVotes(votes);
            }
        }

        if (StringUtils.isNotBlank(article.getRealImageUrl())) {
            detail.setRealImages(Arrays.asList(StringUtils.split(article.getRealImageUrl(), ",")));
        }

        ShareUtils.setShareUrl(detail, url);

        // 广告信息
        if(StringUtils.isNotBlank(adUrl)){
            detail.setAds(adProvider.getDetailAds());
            if(detail.getAds()==null || detail.getAds().isEmpty()) {
                log.info("广告信息不存在，异步获取");
                adProvider.cacheDetailAds();
            }
        }
        List<ArticleRelation> list=iArticleRelationService.findRecommondArticle(articleId);
        List<Long> ids=new ArrayList<>();
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                ids.add(list.get(i).getReferenceId());
            }
        }
        String idsStr;
        if(ids!=null && ids.size()>0){
            idsStr = Joiner.on(",").join(ids);
            List<Article> articleList=dao.query(Article.class,Cnd.where("id","in",idsStr).desc("publish_date"));
            List<ArticleVO> articleVOList=items(articleList);
            ShareUtils.setShareUrl(articleVOList, url);
            detail.setRecommondArticle(articleVOList);
        }
        return detail;
    }

    /**
     * 获取指定分类下的新闻列表
     *
     * @param categoryId
     * @param size
     * @param pageToken
     * @return
     */
    public ResultVO3<ArticleListVO> articleList(Long categoryId, Integer size, String pageToken,Integer headPageSize) {
        if (categoryId == null || categoryId < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }

        if (size == null || size < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        if (headPageSize == null || headPageSize == 0) {
            headPageSize=6;
        }
        int page = PageUtils.toPage(pageToken);
        if (page < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }

        String key = CacheKeyUtils.getArticleListKey(categoryId, page, size);
        Cache<ArticleListVO> cache = cacheRepository.getObject(key, ArticleListVO.class);
        if (cache.getStatus() == CacheStatus.NOT_CACHING) {
            ArticleListVO articleList = fetchArticle(categoryId, page, size,headPageSize);
            cacheRepository.cache(key, articleList, CacheConstant.Time.TEN_SECOND);
            cache.setObject(articleList);
        }

        if (cache.getObject() != null) {
            return ResultVO3.success(cache.getObject(), cache.getObject().getCount(), size, page);
        } else {
            return ResultVO3.success(cache.getObject(), 0, size, page);
        }
    }

    private ArticleListVO fetchArticle(Long categoryId, int page, int size,int headPageSize) {
        ArticleListVO articleListVO = new ArticleListVO();
        Category category=dao.fetch(Category.class,categoryId);
        if(category!=null && !Category.DEFAULT_HELP_CATEGORY_MODELID.equals(category.getModelId())){
            if (page == 1) {
                List<ArticleVO> headResult = getHeadArticleList(categoryId,headPageSize,page);
                articleListVO.setHead(headResult);
            }
        }
        QueryResult list = articleService.getArticlePageByCategoryIdAndBlockId(categoryId, 2L, size, page);//列表区
        List<ArticleVO> listResult = items(list.getList(Article.class));
        ShareUtils.setShareUrl(listResult, url);

        //添加广告地址判断，如果未配置广告地址，不查询广告相关
        if (page == 1 && StringUtils.isNotBlank(adUrl)) {
            List<ArticleVO> ads = adProvider.getListAds(categoryId);
            if (ads != null && ads.size() > 0) {
                List<ArticleVO> newResultList = new ArrayList<>();
                for (int i = 0, j = 0; i < listResult.size(); i++) {
                    ArticleVO article = listResult.get(i);
                    ArticleVO ad = ads.get(j);
                    int articlePlace = article.getPlace() == null ? 0 : article.getPlace();
                    int adPlace = ad.getPlace();

                    // 判断是不是到了广告位置
                    if ((i + 1) == adPlace) {
                        if (articlePlace == adPlace) {
                            newResultList.add(article);
                            newResultList.add(ad);
                        } else {
                            newResultList.add(ad);
                            newResultList.add(article);
                        }
                        j++;
                    } else {
                        newResultList.add(article);
                        continue;
                    }

                    if (j >= ads.size()) {
                        newResultList.addAll(listResult.subList(i + 1, listResult.size()));
                        break;
                    }
                }
                listResult = newResultList;
            } else {
                // 取得广告失败，异步重取
                log.info("取得广告失败，异步重取");
                adProvider.cacheListAds(categoryId);
            }
        }

        articleListVO.setList(listResult);
        articleListVO.setCount(list.getPager().getRecordCount());
        return articleListVO;
    }

    private List<ArticleVO> items(List<Article> list) {
        if (list == null || list.size() < 1) {
            return Collections.emptyList();
        }

        if (!url.endsWith("/")) {
            url += "/";
        }

        List<ArticleVO> articleVOS = new ArrayList<>(list.size());
        for (Article article : list) {
            //引用专题的articleId为专题id，不需要重新赋值
            /*if ("subject".equals(article.getSysCode()) && article.getIsReference()) {
                Article ori = articleService.fetch(article.getArticleId());
                if (null != ori && null != ori.getArticleId()) {
                    article.setArticleId(ori.getArticleId());
                }
            }*/

            ArticleVO item = new ArticleVO();
            if(article.getIsReference()) {
                // 引用文章需要获取原文data
                article.setArticleData(articleDataService.fetch(article.getArticleId()));
            }
            BeanUtils.copyProperties(article, item, "image", "medias", "date");
            ArticleData data = article.getArticleData();
            item.setDate(article.getPublishDate());
            item.setPlace(article.getPosition());

            //视频需要分享地址
            if (SysCodeType.ARTICLE.value().equalsIgnoreCase(item.getSysCode())
                    && "video".equalsIgnoreCase(item.getType())) {  //视频在普通文章下存储
                item.setShareUrl(url + "normal/article/" + item.getCategoryId() + "/" + item.getArticleId());
                if (data != null && StringUtils.isNotBlank(data.getVideos())) {
                    item.setMedias(JSON.parseArray(data.getVideos(), ArticleMediaVO.class));
                }
            }

            if (SysCodeType.ARTICLE.value().equalsIgnoreCase(item.getSysCode())
                    && "image".equalsIgnoreCase(item.getType())) {
                if (data != null && StringUtils.isNotBlank(data.getImages())) {
                    item.setMedias(JSON.parseArray(data.getImages(), ArticleMediaVO.class));
                }
            }

            if (SysCodeType.ARTICLE.value().equalsIgnoreCase(item.getSysCode())
                    && "audio".equalsIgnoreCase(item.getType())) {
                if (data != null && StringUtils.isNotBlank(data.getAudios())) {
                    item.setMedias(JSON.parseArray(data.getAudios(), ArticleMediaVO.class));
                }
            }

            if (SysCodeType.SUBJECT.value().equalsIgnoreCase(item.getSysCode())) {
                cn.people.one.modules.cms.model.front.SubjectVO subject = subjectService.get(item.getId());
                if (subject != null && StringUtils.isNotBlank(subject.getBannerUrl())) {
                    item.setBannerUrl(JSON.parseArray(subject.getBannerUrl()));
                }
            }

            if (StringUtils.isNotBlank(article.getMoreVideos())) {
                item.setMoreVideos(JSON.parseArray(article.getMoreVideos(), ArticleMoreVideoVO.class));
            }

            //直播类型
            if (SysCodeType.LIVE.value().equalsIgnoreCase(article.getSysCode())
                    && ArticleType.LIVE.value().equalsIgnoreCase(article.getType())) {
                if (!Lang.isEmpty(article.getArticleId())) {
                    LiveRoom room = roomService.fetch(article.getArticleId());
                    if (null != room) {
                        //直播需要分享地址
                        item.setShareUrl(url + "live/live/" + item.getCategoryId() + "/" + item.getArticleId());
                        if (cn.people.one.core.util.text.StringUtils.isNotBlank(room.getGuest())) {
                            item.setLiveGuests(Arrays.asList(room.getGuest()));
                        }
                        if (cn.people.one.core.util.text.StringUtils.isNotBlank(room.getStatus())) {
                            item.setLiveStatus(room.getStatus());
                        }
                        if (null != room.getLiveTime()) {
                            item.setLiveTime(room.getLiveTime());
                        }
                        if (cn.people.one.core.util.text.StringUtils.isNotBlank(room.getStatus())) {
                            if ((room.getStatus().equals("1") || room.getStatus().equals("2")) && cn.people.one.core.util.text.StringUtils.isNotBlank(room.getVideo())) {
                                item.setLiveVideo(room.getVideo());
                            }
                            if (room.getStatus().equals("3") && cn.people.one.core.util.text.StringUtils.isNotBlank(room.getPlayback())) {
                                item.setLiveVideo(room.getPlayback());
                            }
                            if (room.getStatus().equals("3") && cn.people.one.core.util.text.StringUtils.isBlank(room.getPlayback()) && cn.people.one.core.util.text.StringUtils.isNotBlank(room.getVideo())) {
                                item.setLiveVideo(room.getVideo());
                            }
                        }
                        if (cn.people.one.core.util.text.StringUtils.isNotBlank(room.getImage())) {
                            item.setLiveImage(room.getImage());
                            //如果头图没有值、直播封面有值，则将直播封面赋值给 image
                            if (cn.people.one.core.util.text.StringUtils.isBlank(article.getImageUrl()) && (article.getBlock().equals(1) || room.getLiveType().equals(1))) {
                                item.setImages(Lists.newArrayList(room.getImage()));
                            }
                        }
                        if (room.getLiveType() != null) {
                            item.setLiveType(room.getLiveType());
                        }

                        item.setHits(article.getHits());
                    }
                }
            }

            if (StringUtils.isNotBlank(article.getRealImageUrl())) {
                item.setRealImages(Arrays.asList(StringUtils.split(article.getRealImageUrl(), ",")));
            }

            if (null != article.getImageApp() && article.getImageApp().size() > 0) {
                if(article.getImageApp().size()==1 && "".equals(article.getImageApp().get(0))) {
                    // 如果仅有一个“” 也不要返回该属性，否则会一直loading
                } else {
                    item.setImages(article.getImageApp());
                }
            }

            articleVOS.add(item);
        }
        return articleVOS;
    }

    /**
     * 根据区块id获取文章分页列表
     *
     * @param blockId 模块ID
     * @param size    每页显示条数
     * @param page    当前页
     * @return
     */
    public ResultVO4<ArticleVO> getArticlePageByBlockId(Long blockId, Integer size, Integer page) {
        List<ArticleVO> articleVOList = new ArrayList<>();
        QueryResult queryResult = articleService.getArticlePageByBlockId(blockId, size, page);
        transArticle(queryResult.getList(), articleVOList);
        ShareUtils.setShareUrl(articleVOList, url);
        queryResult.setList(articleVOList);
        return ResultVO4.success(null,articleVOList,queryResult.getPager().getRecordCount(),  size, page);
    }

    /**
     * 根据专题id获取区块列表以及区块下文章
     *
     * @param articleId  专题id
     * @param pageNumber 当前页
     * @param pageSize   每页显示条数
     * @return
     */
    public SubjectVO getArticleBlockBySubject(Long articleId, Integer pageNumber, Integer pageSize) {
        SubjectVO subjectVO = new SubjectVO();
        List<BlockVO> blockVOs = new ArrayList<>();
        //查询专题信息
        Subject subject = subjectService.getSubjectBlocksByArticleId(articleId);
        Article article=dao.fetch(Article.class,Cnd.where(Article.Constant.ARTICLE_ID,"=",articleId).and(Article.Constant.TYPE,"=",ArticleType.SUBJECT)
                .and(Article.FIELD_STATUS,"=",Article.STATUS_ONLINE).and(Article.Constant.IS_REFERENCE,"=",0));
        if(subject==null){
            //如果查询的专题为null则判断为引用专题，查询引用专题对应文章
            if(article==null){
                article=articleService.fetch(articleId);
            }
            if(article!=null){
                subject=subjectService.getSubjectBlocksByArticleId(article.getArticleId());
            }
        }
        //组装专题数据
        if(subject!=null){
            subjectVO.setId(subject.getId());
            subjectVO.setTitle(subject.getTitle());
            subjectVO.setDescription(subject.getDescription());
            subjectVO.setShowTitle(subject.getShowTitle());
            subjectVO.setImage(subject.getImage());
            subjectVO.setShareUrl(url + "thematic/" + subject.getId());
            subjectVO.setBannerUrl(JSON.parseArray(subject.getBannerUrl()));
            subjectVO.setDate(new Date(subject.getCreateAt()));
            subjectVO.setPageType(subject.getPageType());
            subjectVO.setShowTop(subject.getShowTop());
            ArticleData articleData  = dao.fetch(ArticleData.class, Cnd.where(ArticleData.ID,"=",article.getId()));
            if (articleData !=null && articleData.getVideos() !=null) {
                subjectVO.setMedias(JSON.parseArray(articleData.getVideos(), ArticleMediaVO.class));
            }
            //查询板块列表
            if (subject.getBlocks() != null && subject.getBlocks().size() > 0) {
                for (Subject block : subject.getBlocks()) {
                    BlockVO blockVO = new BlockVO();
                    blockVO.setId(block.getId());
                    blockVO.setTitle(block.getTitle());
                    blockVO.setViewType(block.getViewType());
                    List<ArticleVO> articleVOList = new ArrayList<>();
                    QueryResult queryResult = articleService.getArticlePageByBlockId(block.getId(), pageSize, pageNumber);
                    transArticle(queryResult.getList(), articleVOList);
                    ShareUtils.setShareUrl(articleVOList, url);
                    blockVO.setArticles(articleVOList);
                    blockVO.setPage(PageConverter.toPageVO(queryResult.getPager()));
                    blockVOs.add(blockVO);
                }
                subjectVO.setBlocks(blockVOs);
            }
        } else {
            return null;
        }
        return subjectVO;
    }


	

	/**
     * 转换Article为ArticleVO
     *
     * @param list          articleList
     * @param articleVOList
     */
    private  void transArticle(List list, List<ArticleVO> articleVOList) {
        List<Article> articleList = (List<Article>) list;
        for (Article article : articleList) {
            ArticleVO articleVO = ArticleVO.transArticle(article);
            articleVOList.add(articleVO);
        }
    }

    /**
     * 文章点赞OR取消点赞接口
     *
     * @param articleId
     * @param likeOrNot
     */
    public void likeArticleOrNot(Long articleId, Integer likeOrNot) {
        //根据id查询文章对象
        Article article = this.articleService.fetch(articleId);
        //更新文章点赞数并保存
        article.setLikes(article.getLikes() + likeOrNot);
        articleService.updateIgnoreNull(article);
    }

    /**
     * 如果是图文时，放入图文内容
     */
    private void putCommonContent() {

    }

    /**
     * 如果是直播时，放入直播内容
     */
    private void putLiveContent() {

    }

    /**
     * 如果是图集时，放入图集内容
     */
    private void putImageContent() {

    }

    /**
     * 根据关键字搜索文章
     *
     * @param keyWord
     * @param page
     * @param size
     * @return
     */
    public ResultVO3<List<ArticleVO>> searchArticle(String keyWord, String time, String categoryName, Integer page, Integer size) {
        List<ArticleIndexData> articleIndexDataList = Lists.newArrayList();
        BoolQueryBuilder queryBuilder = elasticSearchService.getQueryBuilder(keyWord, time, categoryName, page, size);
        long count = elasticSearchService.search(articleIndexDataList, queryBuilder, page, size);
        if (articleIndexDataList == null) {
            return ResultVO3.success(null, 0, size, page);
        }

        List<ArticleVO> articleVOList = Lists.newArrayList();
        for (ArticleIndexData articleIndexData : articleIndexDataList) {
            if (null == articleIndexData) {
                continue;
            }
            ArticleVO articleVO = BeanMapper.map(articleIndexData, ArticleVO.class);
            if (null != articleIndexData.getPublishDate()) {
                articleVO.setDate(articleIndexData.getPublishDate());
            }
            if (cn.people.one.core.util.text.StringUtils.isNotBlank(articleIndexData.getImageUrl())) {
                articleVO.setImages(Arrays.asList(articleIndexData.getImageUrl().split(",")));
            }
            articleVOList.add(articleVO);
        }
        ShareUtils.setShareUrl(articleVOList, url);
        return ResultVO3.success(articleVOList, count, size, page);
    }


    public String getFilterStr(String time,String category) throws ParseException {
        StringBuilder filter=new StringBuilder();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;
        Date endDate;
        String startTime;
        String endtime;
        int start=0;
        int end=0;
        if(StringUtils.isNotBlank(time)){
            Map<String, String> maps = AiuiTimeUtils.getAiuiTime(time);
            startTime = maps.get("startTime");
            endtime = maps.get("endTime");
            startDate = sdf.parse(startTime);
            endDate = sdf.parse(endtime);
            start=(int)(startDate.getTime()/1000);
            end=(int)(endDate.getTime()/1000);
        }
        if (StringUtils.isNotBlank(category) && StringUtils.isNotBlank(time)) {
            Category categoryResult = dao.fetch(Category.class, Cnd.where(Category.Constant.NAME, "=", category));
            if (!Lang.isEmpty(categoryResult)) {
                Long categoryId = categoryResult.getId();
                String[] categoryIds = this.categoryIds(categoryId);
                String strCat = Joiner.on(",").join(categoryIds);
                if (categoryIds != null && categoryIds.length > 0) {
                    filter.append("column:"+strCat).append("*item_publish_date:["+start+","+end+"]");
                }
            }
        }else if(!StringUtils.isNotBlank(time) && StringUtils.isNotBlank(category)){
            Category categoryResult = dao.fetch(Category.class, Cnd.where(Category.Constant.NAME, "=", category));
            if (!Lang.isEmpty(categoryResult)) {
                Long categoryId = categoryResult.getId();
                String[] categoryIds = this.categoryIds(categoryId);
                String strCat = Joiner.on(",").join(categoryIds);
                if (categoryIds != null && categoryIds.length > 0) {
                    filter.append("column:"+strCat);
                }
            }
        }else if(!StringUtils.isNotBlank(category) && StringUtils.isNotBlank(time)){
            filter.append("item_publish_date:["+start+","+end+"]");
        }
        System.out.println(filter.toString());
        return filter.toString();
    }
    public String[] categoryIds(Long categoryId) {
        List<Category> categories = new ArrayList<>();
        String[] categoryIds;
        User user = UserUtils.getUser();
        if (user == null) {
            //如果为空则用户为默认系统管理员
            user = dao.fetch(User.class, 1);
        }
        Set<Long> set = userService.getCategoryIdsInOffices(user);
        categories = categoryService.getAllChildrenList(set, categories, categoryId);
        if (categoryId != 0) {
            Category category = dao.fetch(Category.class, categoryId);
            if (category != null) {
                categories.add(category);//categories包含当前的栏目id及栏目下所有的子栏目
            }
        }
        categoryIds = new String[categories.size()];
        if (!Lang.isEmpty(categories)) {
            for (int i = 0; i < categories.size(); i++) {
                categoryIds[i] = categories.get(i).getId().toString();
            }
        } else {
            return null;
        }
        return categoryIds;
    }
    public Result advStatus(AdVo adVo){
        System.out.println(adVo);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        HttpEntity<AdVo> entity=new HttpEntity(adVo, RestTemplateUtil.getHeaders());
        //设置请求头部：token,编码
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> result = restTemplate.postForEntity(adStatsUrl,entity,String.class);
        if(result.getStatusCodeValue()==200){
            return Result.success();
        }
        return Result.error("统计失败");
    }

    /**
     * 根据栏目id查询头图区列表
     * @param categoryId 栏目id
     * @param pageSize 显示条数
     * @param pageNumber 当前页
     * @return
     */
    public List<ArticleVO> getHeadArticleList(Long categoryId,Integer pageSize,Integer pageNumber){
        //获取头图区列表
        QueryResult head = articleService.getArticlePageByCategoryIdAndBlockId(categoryId, 1L, pageSize, pageNumber);
        List<ArticleVO> headResult = items(head.getList(Article.class));
        ShareUtils.setShareUrl(headResult, url);
        return headResult;
    }

    /**
     * 我的爱心列表
     * @param userId
     * @param size
     * @param page
     * @return
     */
    public ResultVO3<ArticleListVO> myHelpList(String userId, Integer size, Integer page) {
        ArticleListVO articleListVO = new ArticleListVO();
        String newsIds=getMyLikeOffices(userId);
        String newIdArry[]=null;
        if(StringUtils.isNotBlank(newsIds)){
            newIdArry=newsIds.replace("\"", "").split(",");
        }
        QueryResult  queryResult = articleService.myHelpList(size,page,newIdArry);
        List<ArticleVO> listResult= items(queryResult.getList(Article.class));
        //如果没有查询公益栏目最新的3条数据
        if(listResult==null || listResult.size()<=0){
            Category category=dao.fetch(Category.class,Cnd.where("model_id","=",8));
            if(category!=null){
                queryResult = articleService.myLastHelpList(3,1,category.getId());
                listResult = items(queryResult.getList(Article.class));
                articleListVO.setList(listResult);
                articleListVO.setCount(queryResult.getList().size());
            }
        }else{
            articleListVO.setList(listResult);
            articleListVO.setCount(queryResult.getPager().getRecordCount());
        }
        ShareUtils.setShareUrl(listResult, url);
        return ResultVO3.success(articleListVO, articleListVO.getCount(), size, page);
    }

    /**
     * 调用ucenter的我的爱心接口获取文章id
     * @param userId
     * @return
     */
    private String getMyLikeOffices(String userId){
        RestTemplate restTemplate = new RestTemplate();
        log.info("调用ucenter获取当前用户我的爱心列表："+ucenterUrl+MYHELP_URL+"?userId="+userId);
        ResponseEntity<Result> responseEntity= restTemplate.getForEntity(ucenterUrl+MYHELP_URL+"?userId="+userId, Result.class);
        Result result=responseEntity.getBody();
        String itemJson=null;
        if(result.getCode()==Result.DEFAULT_CODE_SUCCESS){
            itemJson = JSONArray.toJSONString(result.getData());
        }
        return itemJson;
    }
}
