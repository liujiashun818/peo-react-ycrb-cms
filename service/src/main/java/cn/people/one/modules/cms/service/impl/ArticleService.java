package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.aop.annotation.TimeMonitor;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.time.ClockUtil;
import cn.people.one.core.util.time.DateUtil;
import cn.people.one.modules.activitycode.utils.RestTemplateUtil;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.client.service.IClientPushService;
import cn.people.one.modules.cms.model.*;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.front.SxrbAppcmsCategoryMenuVO;
import cn.people.one.modules.cms.model.gov.Article2PubliccmsVO;
import cn.people.one.modules.cms.model.gov.ArticlePubiccmsVO;
import cn.people.one.modules.cms.model.gov.MediaForSxrbCms;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.CategoryModelEnum;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.*;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.file.service.IMediaInfoService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import cn.people.one.modules.util.ShareUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.dao.util.cri.Static;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by lml on 2016/12/10.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@EnableAsync
public class ArticleService extends BaseService<Article> implements IArticleService {

    @Autowired
    private BaseDao dao;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ICategoryModelService categoryModelService;
    @Autowired
    private IArticleDataService articleDataService;
    @Autowired
    private IClientPushService clientPushService;
    @Autowired
    private IMediaInfoService mediaInfoService;
    @Autowired
    private IUserService userService;
    @Autowired
    private  IArticleTaskRecordService iArticleTaskRecordService;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private IArticleRelationService iArticleRelationService;
    /**
     * 获取政务系统栏目接口
     */
    private static final String INTERFACE_GETPUBLICCMSCATEGORYLIST="api/category/getCategoryList";
    /**
     * 同步文章到政务系统
     */
    private static final String INTERFACE_SYNCARTICLE2PUBLICCMS="api/article/syncArticle";
    /**
     * 获取政务系统文章列表
     */
    private static final String INTERFACE_GETPUBLICCMSARTICLE="/api/v1/gov/getArticleListForAppcms";
    /**
     * 根据id获取政务文章
     */
    private static final String ITERFACE_getArticleForSxrbCmsById="/api/v1/gov/getArticleForSxrbCmsById";
    /**
     * 文本编辑器图片地址前缀
     */
    private static final String CONTENT_SRC="/publiccms";
    /**
     * 文本编辑器图片地址新前缀
     */
    private static final String CONTENT_NEW_SRC="/data";
    @Value("${http.publiccms}")
    private String publiccmsUrl;
    @Value("${http.vshare}")
    private String shareUrl;
    /**
     * 图片地址前缀
     */
    @Value("${upload.domain}")
    private String uploadDomain;
    /**
     * 文章列表页面搜索查询
     */
    @Override
    @Transactional
    public QueryResultVO<Article> findSearchPage(ArticleVO articleVO) {
        Date updateExpiredWeightDate=(Date) redisTemplate.opsForValue().get("updateExpiredWeightDateByArticle");
        if (updateExpiredWeightDate == null || (updateExpiredWeightDate != null && updateExpiredWeightDate.getTime() < System.currentTimeMillis())) {
            Sql sql=Sqls.create("update cms_article set weight=0,weight_date=null where weight>0 and weight_date<current_timestamp()");
            dao.execute(sql);
            redisTemplate.opsForValue().set("updateExpiredWeightDateByArticle", DateUtil.addHours(new Date(),1));
        }
        QueryResultVO<Article> result = getSearchResult(articleVO);
        List<Article> list = (List<Article>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(article -> {
//                dao.fetchLinks(article, "articleData");
                Category category = dao.fetch(Category.class, article.getCategoryId());
                if (category != null) {
                    article.setCategoryName(category.getName());
                }
                if (article.getCreateBy() != null) {
                    User user = userService.fetch(article.getCreateBy());
                    if (user != null && user.getName() != null) {
                        //发稿人信息
                        article.setContributor(user.getName());
                    }
                }
                if (article.getDeleteBy() != null) {
                    User user = userService.fetch(article.getDeleteBy());
                    if (user != null && user.getName() != null) {
                        //发稿人信息
                        article.setDeletOr(user.getName());
                    }
                }
            });
            //设置上线文章的预览地址
            if(Article.STATUS_ONLINE==articleVO.getDelFlag()){
                ShareUtils.setShareUrl(list,shareUrl);
            }
            if (articleVO.getPageNumber() == 1 && 1!=articleVO.getCategoryId()) {
                result.setList(handleListByPosition(list));
            }
        }
        return result;
    }

    /**
     * 拼接搜索条件
     *
     * @param article
     * @return
     */
    @TimeMonitor
    private QueryResultVO<Article> getSearchResult(ArticleVO article) {
        Boolean exSearch = StringUtils.isNotBlank(article.getContent()) ? true : false;
        Sql sql;
        Long categoryId = article.getCategoryId() == null ? 0 : article.getCategoryId();
        String[] categoryIds = categoryIds(categoryId);
        sql = exSearch ? Sqls.create("SELECT a.*,d.content FROM cms_article a,cms_article_data d $condition") : null;
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where().andIn("category_id", categoryIds);
        if (exSearch) {
            cnd = cnd.and(new Static("a.article_id = d.id "));
            cnd = article.getDelFlag() == null ? cnd.and("a.del_flag", "<", BaseEntity.STATUS_DELETE).and("d.del_flag", "<", BaseEntity.STATUS_DELETE) :
                    article.getDelFlag()==5?cnd.andNotIsNull(Article.Constant.FIXED_PUBLISH_DATE)
                            .and("a.del_flag","=",Article.STATUS_AUDIT):cnd.and("a.del_flag", "=", article.getDelFlag()).andIsNull(Article.Constant.FIXED_PUBLISH_DATE);
            cnd = cnd.and("d.content","like", "%" + article.getContent().trim() + "%");
        } else {
                if(article.getDelFlag()==null){
                    cnd=cnd.and(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE);
                }else if(article.getDelFlag()==5){
                    cnd= cnd.andNotIsNull(Article.Constant.FIXED_PUBLISH_DATE)
                            .and(BaseEntity.FIELD_STATUS,"=",Article.STATUS_AUDIT);
                }else{
                    cnd= cnd.and(BaseEntity.FIELD_STATUS, " = ", article.getDelFlag()).andIsNull(Article.Constant.FIXED_PUBLISH_DATE);
                }
			}
        cnd = article.getBlock() != null ? cnd.and(Article.Constant.BLOCK, "=", article.getBlock()) : cnd;
        cnd = article.getInSubject() != null ? cnd.and(Article.Constant.IN_SUBJECT, "=", article.getInSubject()) : cnd;
        cnd = article.getBeginTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, ">=", article.getBeginTime()) : cnd;
        cnd = article.getEndTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, "<=", article.getEndTime()) : cnd;
        cnd = article.getDeleteBeginTime() != null ? cnd.and(Article.Constant.DELETE_DATE, ">=", article.getDeleteBeginTime()) : cnd;
        cnd = article.getDeleteEndTime() != null ? cnd.and(Article.Constant.DELETE_DATE, "<=", article.getDeleteEndTime()) : cnd;
        cnd = StringUtils.isNotBlank(article.getType()) ? cnd.and(Article.Constant.TYPE, "=", article.getType()) : cnd;
        cnd = StringUtils.isNotBlank(article.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + article.getTitle().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(article.getAuthors()) ? cnd.and(Article.Constant.AUTHOR, "like", "%" + article.getAuthors().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(article.getAuthors()) ? cnd.and(Article.Constant.AUTHOR, "=", article.getAuthors()) : cnd;
        cnd = StringUtils.isNotBlank(article.getSysCode()) ? cnd.and(Article.Constant.SYSCODE, "=", article.getSysCode()) : cnd;
        cnd = article.getFixedBeginTime() != null ? cnd.and(Article.Constant.FIXED_PUBLISH_DATE, ">=", article.getFixedBeginTime()) : cnd;
        cnd = article.getFixedEndTime() != null ? cnd.and(Article.Constant.FIXED_PUBLISH_DATE, "<=", article.getFixedEndTime()) : cnd;

        OrderBy orderBy = criteria.getOrderBy();
        if (categoryId != 1) {
            orderBy.asc(Article.Constant.POSITION + " is null");
            orderBy.asc(Article.Constant.POSITION);
        }
        if (StringUtils.isBlank(article.getDesc()) && StringUtils.isBlank(article.getAsc())) {
            // 待审核查询时，不按发布时间倒序
            if(article.getDelFlag() ==null || 2 != article.getDelFlag()) {
                //增加默认排序（如果是回收站的数据，只按照发布时间排序）
                if(Article.STATUS_DELETE==article.getDelFlag()){
                    orderBy.desc(Article.Constant.PUBLISH_DATE);
                }else{
                    orderBy.desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE);
                }
            }
        } else {
            if (StringUtils.isNotBlank(article.getDesc())) {
                orderBy.desc(article.getDesc());
            }
            if (StringUtils.isNotBlank(article.getAsc())) {
                orderBy.asc(article.getAsc());
            }
        }
        orderBy.desc(Article.Constant.ID);
        if (exSearch) {
            sql.setCondition(criteria);
            sql.setEntity(dao.getEntity(Article.class));
            System.out.println(sql);
            return listPage(article.getPageNumber(), article.getPageSize(), sql);
        }
        System.out.println(criteria);
        return listPage(article.getPageNumber(), article.getPageSize(), criteria);
    }

    /**
     * 根据position设置list排序
     *
     * @param listSource
     */
    private List<Article> handleListByPosition(List<Article> listSource) {
        int size = listSource.size();
        int i, j, k;
        List<Article> listTarget = new ArrayList<>();
        for (i = 0; i < size; i++) {
            Article articleTarget = null;
            for (j = 0; j < listSource.size(); j++) {
                if (listSource.get(j).getPosition() != null && listSource.get(j).getPosition() == (i + 1)) {
                    articleTarget = listSource.get(j);
                    listTarget.add(listSource.get(j));
                    listSource.remove(listSource.get(j));
                    j--;
                    break;
                }
            }
            if (articleTarget == null) {
                for (k = 0; k < listSource.size(); k++) {
                    if (listSource.get(k).getPosition() == null) {
                        listTarget.add(listSource.get(k));
                        listSource.remove(listSource.get(k));
                        break;
                    }
                }
            }
        }
        return listTarget;
    }
    
    
    /**
     * 回收站列表拼接搜索条件
     */
    @TimeMonitor
    private QueryResultVO<Article> getSearchResultRecycleBin(ArticleVO article) {
        Boolean exSearch = StringUtils.isNotBlank(article.getFieldName()) ? true : false;
        Sql sql;
        sql = exSearch ? Sqls.create("SELECT a.*,m.field_code,m.field_value FROM cms_article a,cms_article_meta m $condition") : null;
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where();
        if (exSearch) {
            cnd = exSearch ? cnd.and(new Static("a.id = m.article_id ")).and("field_code", "=", article.getFieldName()) : cnd;
            cnd = StringUtils.isNotBlank(article.getFieldValue()) ? cnd.and("field_value", "=", Strings.sNull(article.getFieldValue())) : cnd;
            cnd = article.getDelFlag() == null ? cnd.and("a.del_flag", "=", BaseEntity.STATUS_DELETE).and("m.del_flag", "=", BaseEntity.STATUS_DELETE) :
                    cnd.and("a.del_flag", "=", article.getDelFlag()).and("m.del_flag", "=", BaseEntity.STATUS_DELETE);
        } else {
            cnd = article.getDelFlag() == null ? cnd.and(BaseEntity.FIELD_STATUS, "=", BaseEntity.STATUS_DELETE) : cnd.and(BaseEntity.FIELD_STATUS, " = ", article.getDelFlag());
        }
        cnd = article.getBlock() != null ? cnd.and(Article.Constant.BLOCK, "=", article.getBlock()) : cnd;
        cnd = article.getInSubject() != null ? cnd.and(Article.Constant.IN_SUBJECT, "=", article.getInSubject()) : cnd;
        cnd = article.getBeginTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, ">=", article.getBeginTime()) : cnd;
        cnd = article.getEndTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, "<=", article.getEndTime()) : cnd;
        cnd = article.getBeginTime() != null ? cnd.and(Article.Constant.DELETE_DATE, ">=", article.getDeleteBeginTime()) : cnd;
        cnd = article.getEndTime() != null ? cnd.and(Article.Constant.DELETE_DATE, "<=", article.getDeleteEndTime()) : cnd;
        cnd = StringUtils.isNotBlank(article.getType()) ? cnd.and(Article.Constant.TYPE, "=", article.getType()) : cnd;
        cnd = StringUtils.isNotBlank(article.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + article.getTitle().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(article.getAuthors()) ? cnd.and(Article.Constant.AUTHOR, "like", "%" + article.getAuthors().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(article.getAuthors()) ? cnd.and(Article.Constant.AUTHOR, "=", article.getAuthors()) : cnd;
        cnd = StringUtils.isNotBlank(article.getSysCode()) ? cnd.and(Article.Constant.SYSCODE, "=", article.getSysCode()) : cnd;

        OrderBy orderBy = criteria.getOrderBy();
        if (StringUtils.isBlank(article.getDesc()) && StringUtils.isBlank(article.getAsc())) {
            // 待审核查询时，不按发布时间倒序
            if(article.getDelFlag() ==null || 2 != article.getDelFlag()) {
                //增加默认排序
                orderBy.desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE);
            }
        } else {
            if (StringUtils.isNotBlank(article.getDesc())) {
                orderBy.desc(article.getDesc());
            }
            if (StringUtils.isNotBlank(article.getAsc())) {
                orderBy.asc(article.getAsc());
            }
        }
        orderBy.desc(Article.Constant.ID);
        if (exSearch) {
            sql.setCondition(criteria);
            sql.setEntity(dao.getEntity(Article.class));
            return listPage(article.getPageNumber(), article.getPageSize(), sql);
        }
        return listPage(article.getPageNumber(), article.getPageSize(), criteria);
    }

    @Override
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

    /**
     * TODO (建议添加缓存)获取山西日报入住系统栏目列表
     * @return
     */
    @Override
    public List<Category> getPublicCmsCategoryList() {
        RestTemplate restTemplate = new RestTemplate();
        List<Category> categoryList = new ArrayList<>();
        log.info("》山西日报入住系统栏目树接口地址："+publiccmsUrl+INTERFACE_GETPUBLICCMSCATEGORYLIST);
        String result = restTemplate.getForObject(publiccmsUrl+INTERFACE_GETPUBLICCMSCATEGORYLIST, String.class);
        //判空接口结果
        if(StringUtils.isBlank(result)){
            log.error("》山西日报入住系统栏目树接口数据："+result);
            return categoryList;
        }
        List<SxrbAppcmsCategoryMenuVO> menuList = new Gson().fromJson(result,new TypeToken<List<SxrbAppcmsCategoryMenuVO>>(){}.getType());
        //将得到的publiccms系统的栏目结构转换成Category形式的栏目结构用来显示
        for(SxrbAppcmsCategoryMenuVO sxrbAppcmsCategoryMenuVO:menuList){
            getPublicCmsCategory(menuList,sxrbAppcmsCategoryMenuVO,categoryList,null);
        }
        return categoryList;
    }

    /**
     * 同步文章到山西日报入住系统
     * @param articleId  要同步文章id
     * @param syncCategoryId 山西日报入住系统栏目id
     */
    @Override
    public String syncArticle2PublicCms(Long articleId, Integer syncCategoryId) {
        Article article = getArticleDetails(articleId);
        if(article != null){
            if(article.getIsReference()){
                //如果是引用类型文章则查询原文章的articleData
                ArticleData articleData=dao.fetch(ArticleData.class,article.getArticleId());
                article.setArticleData(articleData);
            }
            article.setSyncCategoryId(syncCategoryId);
            Article2PubliccmsVO article2PubliccmsVO = new Article2PubliccmsVO(article);
            //处理媒体文件
            List<ArticleMediaVO> articleMediaVOList=null;
            if("image".equals(article.getType())){
                articleMediaVOList=article.getArticleData().getImageJson();
            }else if("audio".equals(article.getType())){
                articleMediaVOList=article.getArticleData().getAudioJson();
            }else if("video".equals(article.getType())){
                articleMediaVOList=article.getArticleData().getVideoJson();
            }
            if(articleMediaVOList!=null){
                List<MediaForSxrbCms> mediaForSxrbCmsList=new ArrayList<>();
                for (ArticleMediaVO articleMediaVO : articleMediaVOList) {
                    MediaForSxrbCms mediaForSxrbCms=new MediaForSxrbCms();
                    BeanUtils.copyProperties(articleMediaVO,mediaForSxrbCms);
                    mediaForSxrbCms.setId(null);
                    if(StringUtils.isNotBlank(articleMediaVO.getIndex())){
                        mediaForSxrbCms.setSort(Integer.valueOf(articleMediaVO.getIndex()));
                    }else{
                        mediaForSxrbCms.setSort(1);
                    }
                    if("image".equals(article.getType())){
                        mediaForSxrbCms.setFileUrl(articleMediaVO.getImage());
                    }else if("audio".equals(article.getType())|| "video".equals(article.getType())){
                        mediaForSxrbCms.setFileUrl(articleMediaVO.getResources().get(0).getUrl());
                        mediaForSxrbCms.setEncodeType(articleMediaVO.getResources().get(0).getEnctype());
                        mediaForSxrbCms.setFileSize(articleMediaVO.getResources().get(0).getSize()+"");
                    }
                    mediaForSxrbCmsList.add(mediaForSxrbCms);
                }
                article2PubliccmsVO.setMedias(mediaForSxrbCmsList);
            }
            JsonMapper jsonMapper=new JsonMapper();
            String jsonStr = jsonMapper.toJson(article2PubliccmsVO);
            log.info("》同步文章到山西日报入住系统接口地址："+publiccmsUrl+INTERFACE_SYNCARTICLE2PUBLICCMS);
            RestTemplate restTemplate=new RestTemplate();
            HttpEntity<Map<String,Object>> entity=new HttpEntity(jsonStr,RestTemplateUtil.getHeaders());
            ResponseEntity<String> result = restTemplate.postForEntity(publiccmsUrl+INTERFACE_SYNCARTICLE2PUBLICCMS, entity, String.class);
            log.info("》同步文章到山西日报入住系统接口结果："+result.getBody());
            return result.getBody();
        }
        return null;
    }

    /**
     * 获取入住系统文章列表
     * @param paramMap 参数封装map
     * @return
     */
    @Override
    public QueryResult getPubliccmsArticle(Map paramMap) {
        QueryResult queryResult=null;
        RestTemplate restTemplate = new RestTemplate();
        JsonMapper jsonMapper=new JsonMapper();
        String pramJson=jsonMapper.toJson(paramMap);
        log.info("》山西日报获取入住系统文章列表接口地址："+publiccmsUrl+INTERFACE_GETPUBLICCMSARTICLE);
        ResponseEntity<QueryResult> responseEntity= restTemplate.getForEntity(publiccmsUrl+INTERFACE_GETPUBLICCMSARTICLE+"?paramJson={pramJson}", QueryResult.class,pramJson);
        queryResult=responseEntity.getBody();
        return queryResult;
    }

    @Override
    public QueryResultVO<Article> findReferPage(ArticleVO articleVO) {
        //过滤专题下的文章
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where();
        cnd = StringUtils.isNotBlank(articleVO.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + articleVO.getTitle().trim() + "%") : cnd;
        cnd = StringUtils.isNotBlank(articleVO.getAuthors()) ? cnd.and(Article.Constant.AUTHOR, "like", "%" + articleVO.getAuthors().trim() + "%") : cnd;
        cnd.and(Article.FIELD_STATUS, "=", Article.STATUS_ONLINE).and(Article.Constant.IN_SUBJECT, "=", false).and(Article.Constant.TYPE, "<>", "live");
        //普通文章过滤没权限的栏目
        Long categoryId = articleVO.getCategoryId() == null ? 0 : articleVO.getCategoryId();
        cnd.andIn(Article.Constant.CATEGORY_ID, categoryIds(categoryId));
        criteria.getOrderBy().desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID);
        QueryResultVO result = listPage(articleVO.getPageNumber(), articleVO.getPageSize(), criteria);
        List<Article> list = (List<Article>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.forEach(item -> {
                //普通文章
                if (item.getCategoryId() != null) {
                    Category category = dao.fetch(Category.class, item.getCategoryId());
                    if (null != category) {
                        item.setCategoryName(category.getName());
                    }
                }
            });
        }
        return result;
    }

    @Override
    public Article getArticleDetails(Long id) {
        //普通字段 关联调查和扩展字段
        Article article = dao.fetchLinks(dao.fetchLinks(fetch(id), "votes"), Article.Constant.MATAS);
        if (Lang.isEmpty(article)) {
            return null;
        }

        if (!article.getInSubject()) {
            //字段定义
            Category category = categoryService.fetch(article.getCategoryId());
            if (category.getModelId() != null) {
                CategoryModel model = categoryModelService.fetch(category.getModelId());
                if (model != null) {
                    article.setFieldGroups(model.getFieldGroups());
                }
            }
        }

        if (!Lang.isEmpty(article.getVotes())) {
            article.getVotes().stream().forEach(vote -> dao.fetchLinks(vote, "options", Cnd.where(BaseEntity.FIELD_STATUS, "<", VoteOption.STATUS_DELETE)));
        }
        ArticleData articleData=articleDataService.fetch(id);
        //处理导入的线上老数据
        if((ArticleType.AUDIO.value().equals(article.getType()) || ArticleType.VIDEO.value().equals(article.getType()))
                && StringUtils.isNotBlank(article.getImage())){
            handleArticleDataForOldData(articleData,article.getImage());
        }
        if(articleData!=null){
            article.setArticleData(articleData);
        }
        //相关阅读文章
        List<ArticleRelation> list=iArticleRelationService.findRecommondArticle(id);
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
            article.setRelationArticles(articleList);
        }
        return article;
    }



    /**
     * 批量保存(引用文章开放)
     *
     * @param list
     */
    @Transactional
    public void batchInsert(List<Article> list, Long categoryId, Integer block) {
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(article -> {
                //if ((article.getIsReference() == null || !article.getIsReference()) && article.getSysCode().equals(SysCodeType.SUBJECT.value())) {
                //如果是专题，并且是实体专题，则articleID指向包装article的id
                article.setArticleId(article.getArticleId());
                // }
                if (null != block) {
                    article.setBlock(block);
                }
                article.setCategoryId(categoryId);
                article.setId(null);
                article.setType(article.getType());
                article.setSysCode(article.getSysCode());
                article.setIsReference(true);
                article.setDelFlag(Article.STATUS_AUDIT);
                article.setInSubject(false);
                article.setWeight(0);
                article.setPublishDate(null);
                save(article);
            });
        }
    }

    /**
     * 保存（文章 详情 扩展字段）
     *
     * @param article
     */
    @Override
    @Transactional
    public Object save(Article article) {
        //添加专题(栏目下的专题)的时候，article inSubject=true,categoryId 正常栏目id,articleId=专题id,sysCode=article
        //添加引用的时候 articleId为引用文章的id
        if (Lang.isEmpty(article.getCategoryId())) {
            return null;
        }
        //处理文章内容和媒体资源
        ArticleData articleData = article.getArticleData();
        if (!Lang.isEmpty(articleData)) {
            //文章内容处理
            if (!Lang.isEmpty(articleData.getContent())) {
                articleData.setContent(StringEscapeUtils.unescapeHtml4(articleData.getContent()));
            }
            //文章图片或音频视频处理
            saveMediaInfo(articleData, article.getMediaIds(), article.getAudioUrl(), article.getAudioCover(), article.getVideoUrl(), article.getVideoCover());
        }
        // /设置默认标题
        if (StringUtils.isBlank(article.getListTitle())) {
            article.setListTitle(article.getTitle());
        }
        if (null != article.getDelFlag() && article.getDelFlag().equals(Article.STATUS_ONLINE) && null == article.getPublishDate()) {
            //第一次上线则设置发布时间
            article.setPublishDate(ClockUtil.currentDate());
        }
        if (Lang.isEmpty(article.getId())) {
            article.init();
            Category category=dao.fetch(Category.class,article.getCategoryId());
            if(category!=null){
                if(CategoryModelEnum.RECOMMEND.value().equals(category.getModelId())){
                    article.setRecomdFlag(1);
                }
            }
            dao.insert(article);
            //实体文章更新articleID 引用文章则在对应的接口修改好
            if (article.getIsReference() == null || article.getIsReference().equals(false)) {
                //普通文章设置默认articleId，引用文章则找到原始的id
                if (article.getArticleId() == null) {
                    article.setArticleId(article.getId());
                    dao.updateIgnoreNull(article);
                }
            }
            if (!Lang.isEmpty(articleData)) {
                articleData.setId(article.getId());
                dao.insert(articleData);
            }
            if(article.getRelationIds()!=null && article.getRelationIds().size()>0){
                for(int i=0;i<article.getRelationIds().size();i++){
                    saveRelation(article,article.getRelationIds().get(i));
                }
            }
        } else {
            //更新文章
            return update(article);
        }
        return article;
    }



    @Override
    @Transactional
    public Article update(Article article) {
        //判断文章是否更新了状态或者修改了栏目
        //如果有以上任何一点修改则需要清空当前文章位置
        boolean clear = isNeedClearPosition(article);
        //插入扩展字段
        dao.clearLinks(article, Article.Constant.MATAS);
        dao.insertLinks(article, Article.Constant.MATAS);

        ArticleData articleData = article.getArticleData();
        if (!Lang.isEmpty(articleData) && !Lang.isEmpty(articleData.getId())) {
            dao.update(articleData);
        } else if (!Lang.isEmpty(articleData)) {
            ArticleData articleData1=dao.fetch(ArticleData.class,article.getId());
            if(articleData1==null){
                dao.insert(articleData);
            }else{
                articleData.setId(article.getId());
                dao.update(articleData);
            }
        }

        //获取原有对象，清除关联关系再插入
        Article oriArt = dao.fetchLinks(fetch(article.getId()), "votes");
        if (!Lang.isEmpty(oriArt.getVotes())) {
            oriArt.getVotes().stream().forEach(vote -> {
                dao.clearLinks(vote, "options");
            });
        }

        dao.clearLinks(oriArt, "votes");

        if (!Lang.isEmpty(article.getVotes())) {
            for (Vote vote : article.getVotes()) {
                //调查插入
                vote.setArticleId(article.getId());
                dao.insertWith(vote, "options");
            }
        }
        dao.updateIgnoreNull(article);
        if(article.getTags()==null){
            article.setTags("");
            Sql sql=Sqls.create("update cms_article set tags=null where id="+article.getId());
            dao.execute(sql);
        }

        if (clear) {
            clearPositon(article.getId());
        }

        if(article.getRelationIds()==null || article.getRelationIds().size()<=0){
            Sql sql=Sqls.create("update cms_article_relation set del_flag=3 where  article_id="+article.getId());
            dao.execute(sql);
        }
        if(article.getRelationIds()!=null && article.getRelationIds().size()>0){
            String idsStr = Joiner.on(",").join(article.getRelationIds());
            Sql sql=Sqls.create("update cms_article_relation set del_flag=3 where reference_id not in("+idsStr+") and article_id="+article.getId());
            dao.execute(sql);
            for(int i=0;i<article.getRelationIds().size();i++){
                ArticleRelation articleRelation=dao.fetch(ArticleRelation.class,
                        Cnd.where("reference_id","=",article.getRelationIds().get(i)).and("article_id","=",article.getId()));
                if(articleRelation!=null){
                    articleRelation.setDelFlag(ArticleRelation.STATUS_ONLINE);
                    dao.update(articleRelation);
                }else {
                    saveRelation(article,article.getRelationIds().get(i));
                }
            }
        }
        return article;
    }

    /**
     * 保存相关阅读文章
     * @param article
     * @param referenceId
     */
    private void saveRelation(Article article,Long referenceId){
        ArticleRelation relation=new ArticleRelation();
        Article articleCommond=dao.fetch(Article.class,referenceId);
        relation.setCategoryId(articleCommond.getCategoryId());
        relation.setPublishDate(articleCommond.getPublishDate());
        relation.setTitle(articleCommond.getTitle());
        relation.setReferenceId(referenceId);
        relation.setArticleId(article.getId());
        relation.setDelFlag(ArticleRelation.STATUS_ONLINE);
        dao.insert(relation);
    }
    /**
     * 根据文章判断是否需要清空文章的位置
     *
     * @param articleUpdate
     * @return
     */
    private boolean isNeedClearPosition(Article articleUpdate) {
        Boolean clear = false;
        Article articleOrigin = fetch(articleUpdate.getId());
        if (articleOrigin != null &&
                (!articleOrigin.getCategoryId().equals(articleUpdate.getCategoryId()) ||
                        !articleOrigin.getDelFlag().equals(articleUpdate.getDelFlag()))) {
            clear = true;
        }
        return clear;
    }

    @Override
    @Transactional
    public void batchUpdate(List<Article> list) {
        if (!Lang.isEmpty(list)) {
            list.stream().forEach((t) -> {
                updateIgnoreNull(t);
            });
        }
    }

    /**
     * 伪删除
     */
    @Override
    @Transactional
    public Article del(Long id) {
        Article article = fetch(id);
        article.setDelFlag(Article.STATUS_DELETE);
        article.setDeleteBy(UserUtils.getUser().getId());
        article.setDeleteDate(ClockUtil.currentDate());
        dao.updateIgnoreNull(article);
        return article;
    }

    /**
     * 反转文章上线状态
     *
     * @param id
     */
    @Override
    @Transactional
    public Article changeOnlineStatus(Long id) {
        Article article = dao.fetch(tClass, id);
        if (article.getDelFlag().equals(Article.STATUS_OFFLINE) || article.getDelFlag().equals(Article.STATUS_AUDIT)) {
            article.setDelFlag(Article.STATUS_ONLINE);
//            if (null == article.getPublishDate()) {
            //如果没有添加发布时间，则自动填补
            article.setPublishDate(ClockUtil.currentDate());
//            }
            article.setCreateBy(UserUtils.getUser().getId());
            dao.updateIgnoreNull(article);
        } else {
            article.setDelFlag(Article.STATUS_OFFLINE);
            dao.updateIgnoreNull(article);
        }
        //清空文章位置
        clearPositon(id);
        return article;
    }

    /**
     * 可扩展字段保存
     *
     * @param articleId
     * @param fieldCode
     * @param fieldValue
     */
    @Override
    @Transactional
    public void addMetaData(Long articleId, String fieldCode, String fieldValue) {
        ArticleMeta articleMeta = new ArticleMeta();
        articleMeta.setArticleId(articleId);
        articleMeta.setFieldCode(fieldCode);
        articleMeta.setFieldValue(fieldValue);
        dao.insert(articleMeta);
    }

    /**
     * 为检索区域提供获取所有上线文章的接口（包含文章详情）
     *
     * @return
     */
    public List<Article> getAll(int page, int size) {
        List<Article> articles = query(null,
                Cnd.where(Article.Constant.IS_REFERENCE, "=", false)
                        .limit(page, size));
        articles.forEach(article -> dao.fetchLinks(article, "articleData"));
        return articles;
    }


    /**
     * 客户端推送
     */
    @Override
    @Transactional
    public void clientPush(ClientPush clientPush) {
        clientPushService.save(clientPush);
    }

    public void saveMediaInfo(ArticleData articleData, List<Long> mediaIds, String audioUrl, String audioCover, String videoUrl, String videoCover) {
        List<ArticleMediaVO> videos = Lists.newArrayList();
        List<ArticleMediaVO> audios = Lists.newArrayList();
        if (!Lang.isEmpty(mediaIds)) {
            for (Long mediaId : mediaIds) {
                MediaInfo mediaInfo = mediaInfoService.fetch(mediaId);
                if (Lang.isEmpty(mediaInfo)) {
                    continue;
                }
                if (Lang.isEmpty(mediaInfo.getType())) {
                    continue;
                }
                if (mediaInfo.getType().equals("video")) {
                    ArticleMediaVO mediaVO = new ArticleMediaVO();
                    mediaVO.setId(mediaId);
                    mediaVO.setTitle(mediaInfo.getName());
                    if (StringUtils.isNotBlank(videoCover)) {
                        mediaVO.setImage(videoCover);
                    } else {
                        mediaVO.setImage(mediaInfo.getCover());
                    }
                    mediaVO.setType(mediaInfo.getType());
                    mediaVO.setTimes(mediaInfo.getDuration());
                    List<MediaResourceVO> resources = Lists.newArrayList();
                    MediaResourceVO ld = new MediaResourceVO();
                    MediaResourceVO sd = new MediaResourceVO();
                    MediaResourceVO hd = new MediaResourceVO();
                    ld.setEnctype("ld");
                    ld.setSize(mediaInfo.getLdSize());
                    ld.setUrl(mediaInfo.getLdUrl());
                    sd.setEnctype("sd");
                    sd.setSize(mediaInfo.getSdSize());
                    sd.setUrl(mediaInfo.getSdUrl());
                    hd.setEnctype("hd");
                    hd.setSize(mediaInfo.getHdSize());
                    hd.setUrl(mediaInfo.getHdUrl());
                    resources.add(ld);
                    resources.add(sd);
                    resources.add(hd);
                    mediaVO.setResources(resources);
                    videos.add(mediaVO);
                }
                if (mediaInfo.getType().equals("audio")) {
                    ArticleMediaVO mediaVO = new ArticleMediaVO();
                    mediaVO.setId(mediaId);
                    mediaVO.setTitle(mediaInfo.getName());
                    if (StringUtils.isNotBlank(audioCover)) {
                        mediaVO.setImage(audioCover);
                    }
                    mediaVO.setType(mediaInfo.getType());
                    mediaVO.setTimes(mediaInfo.getDuration());
                    List<MediaResourceVO> resources = Lists.newArrayList();
                    MediaResourceVO mp3Small = new MediaResourceVO();
                    MediaResourceVO mp3Big = new MediaResourceVO();
                    mp3Small.setEnctype("64");
                    mp3Small.setSize(mediaInfo.getMp3SmallSize());
                    mp3Small.setUrl(mediaInfo.getMp3SmallUrl());
                    mp3Big.setEnctype("128");
                    mp3Big.setSize(mediaInfo.getMp3BigSize());
                    mp3Big.setUrl(mediaInfo.getMp3BigUrl());
                    resources.add(mp3Small);
                    resources.add(mp3Big);
                    mediaVO.setResources(resources);
                    audios.add(mediaVO);
                }
            }
        } else if (StringUtils.isNotBlank(audioUrl)) {
            ArticleMediaVO mediaVO = new ArticleMediaVO();
            if (StringUtils.isNotBlank(audioCover)) {
                mediaVO.setImage(audioCover);
            }
            mediaVO.setType("audio");
            List<MediaResourceVO> resources = Lists.newArrayList();
            MediaResourceVO mp3Small = new MediaResourceVO();
            MediaResourceVO mp3Big = new MediaResourceVO();
            mp3Small.setEnctype("64");
            mp3Small.setUrl(audioUrl);
            mp3Big.setEnctype("128");
            mp3Big.setUrl(audioUrl);
            resources.add(mp3Small);
            resources.add(mp3Big);
            mediaVO.setResources(resources);
            audios.add(mediaVO);
        } else if (StringUtils.isNotBlank(videoUrl)) {
            ArticleMediaVO mediaVO = new ArticleMediaVO();
            if (StringUtils.isNotBlank(videoCover)) {
                mediaVO.setImage(videoCover);
            }
            mediaVO.setType("video");
            List<MediaResourceVO> resources = Lists.newArrayList();
            MediaResourceVO ld = new MediaResourceVO();
            MediaResourceVO sd = new MediaResourceVO();
            MediaResourceVO hd = new MediaResourceVO();
            ld.setEnctype("ld");
            ld.setUrl(videoUrl);
            sd.setEnctype("sd");
            sd.setUrl(videoUrl);
            hd.setEnctype("hd");
            hd.setUrl(videoUrl);
            resources.add(ld);
            resources.add(sd);
            resources.add(hd);
            mediaVO.setResources(resources);
            videos.add(mediaVO);
        }
        if (!Lang.isEmpty(videos)) {
            articleData.setVideos(JSONArray.toJSONString(videos));
        }
        if (!Lang.isEmpty(audios)) {
            articleData.setAudios(JSONArray.toJSONString(audios));
        }
        if (!Lang.isEmpty(articleData.getImageJson())) {
            articleData.setImages(JSONArray.toJSONString(articleData.getImageJson()));
        }
    }

    /**
     * 给文章设置固定位置
     *
     * @param id
     * @param position
     */
    @Override
    @Transactional
    public int setPosition(Long id, Integer position) {
        //查询出预修改位置的当前文章记录
        System.out.println("设置固定位置开始"+System.currentTimeMillis());
        Article article = super.fetch(id);
        //判断文章所属栏目下文章数===》判断文章可以设置的位置
        Sql sql=Sqls.create("SELECT COUNT(id) FROM cms_article WHERE cms_article.category_id=@categoryId and cms_article.del_flag=@delFlag");
        sql.setParam("categoryId",article.getCategoryId());
        sql.setParam("delFlag",Article.STATUS_ONLINE);
        Integer articleCount=count(sql);
        //如果当前栏目下上线文章数小于预设置的position值则不能设置
        if(articleCount==null||articleCount<position){
            return 0;
        }
        //查询处于当前位置的文章
        Article articleOrigin = dao.fetch(Article.class, Cnd.where(Article.Constant.POSITION, "=", position)
                .and(Article.Constant.CATEGORY_ID, "=", article.getCategoryId()));
        if (articleOrigin != null) {
            //清空文章位置属性
            clearPositon(articleOrigin.getId());
        }
        //修改保存设置位置的文章记录
        if (position == null || position.equals(0) || position < 0) {
            article.setPosition(null);
        } else {
            article.setPosition(position);
        }
        return dao.update(article);
    }

    /**
     * 清空文章位置
     *
     * @param id
     */
    private void clearPositon(Long id) {
        Sql sql = Sqls.create("UPDATE cms_article SET position=NULL WHERE id=@id");
        sql.params().set("id", id);
        dao.execute(sql);
    }


    @Override
    public List<Article> findByDocId(Long docId) {
        Condition condition = Cnd.where("sys_documentid", "=", docId).and("is_reference", "!=", 1).and(BaseEntity.FIELD_STATUS, "!=", 3);
        return dao.query(Article.class, condition);
    }

    @Override
    public List<Article> findListByApp(Integer pageSize, ArticleVO articleVO) {
        Condition condition = Cnd.where("category_id", "=", articleVO.getCategoryId()).and("block", "=", articleVO.getBlock()).and(BaseEntity.FIELD_STATUS, "=", 0).and("in_subject", "=", 0).desc("weight").desc("publish_date");
        List<Article> list = dao.query(Article.class, condition);
        if (Lang.isEmpty(list)) {
            return null;
        }
        if (pageSize < list.size()) {
            list = list.subList(0, pageSize);
        }
        list.stream().forEach(a -> dao.fetchLinks(dao.fetchLinks(a, "articleData"), Article.Constant.MATAS));
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));//将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));//将Article中的imageUrl转化为客户端的list<image>
        for (Article article : list) {
            if (Lang.isEmpty(article.getType())) {
                continue;
            }
            if (null == article.getIsReference() || !article.getIsReference()) {
                if (Lang.isEmpty(article.getArticleData())) {
                    continue;
                }
            }
            if (null != article.getIsReference() && article.getIsReference()) {//引用的文章
                //如果是音频视频类型的文章需要将其articleData信息拼接过去
                if (article.getType().equals("audio") || article.getType().equals("video") || article.getType().equals("image")) {
                    ArticleData articleData = articleDataService.fetch(article.getArticleId());
                    if (null != articleData) {
                        article.setArticleData(articleData);
                    }
                }
            }
            if (article.getType().equals("audio") && !Lang.isEmpty(article.getArticleData().getAudios())) {
                List<ArticleMediaVO> audios = JSONArray.parseArray(article.getArticleData().getAudios(), ArticleMediaVO.class);
                if (Lang.isEmpty(audios)) {
                    continue;
                }
                List<MediaResourceVO> resources = audios.get(0).getResources();
                article.setMedias(BeanMapper.mapList(resources, MediaResourceVO.class));
                article.setMediaTime(audios.get(0).getTimes());
            }
            if (article.getType().equals("video") && !Lang.isEmpty(article.getArticleData().getVideos())) {
                List<ArticleMediaVO> videos = JSONArray.parseArray(article.getArticleData().getVideos(), ArticleMediaVO.class);
                if (Lang.isEmpty(videos)) {
                    continue;
                }
                List<MediaResourceVO> resources = videos.get(0).getResources();
                article.setMedias(BeanMapper.mapList(resources, MediaResourceVO.class));
                article.setMediaTime(videos.get(0).getTimes());
            }
            if ("image".equals(article.getType()) && !Lang.isEmpty(article.getArticleData().getImages())) {
                List<ArticleMediaVO> images = JSONArray.parseArray(article.getArticleData().getImages(), ArticleMediaVO.class);
                if (null != images) {
                    article.setImageNum(images.size());
                }
            }
        }
        return list;
    }

    @Override
    public List<Article> findListByApp(Integer pageSize, Long categoryId, Integer block) {
        Condition condition = Cnd.where("category_id", "=", categoryId).and("block", "=", block).and(BaseEntity.FIELD_STATUS, "=", 0).and("in_subject", "=", 0).desc("weight").desc("publish_date");
        List<Article> list = dao.query(Article.class, condition);
        if (Lang.isEmpty(list)) {
            return null;
        }
        if (pageSize < list.size()) {
            list = list.subList(0, pageSize);
        }
        list.stream().forEach(a -> dao.fetchLinks(dao.fetchLinks(a, "articleData"), Article.Constant.MATAS));
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));//将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));//将Article中的imageUrl转化为客户端的list<image>
        for (Article article : list) {
            if (Lang.isEmpty(article.getType())) {
                continue;
            }
            if (null == article.getIsReference() || !article.getIsReference()) {
                if (Lang.isEmpty(article.getArticleData())) {
                    continue;
                }
            }
            if (null != article.getIsReference() && article.getIsReference()) {//引用的文章
                //如果是音频视频类型的文章需要将其articleData信息拼接过去
                if (article.getType().equals("audio") || article.getType().equals("video") || article.getType().equals("image")) {
                    ArticleData articleData = articleDataService.fetch(article.getArticleId());
                    if (null != articleData) {
                        article.setArticleData(articleData);
                    }
                }
            }
            if (article.getType().equals("audio") && !Lang.isEmpty(article.getArticleData().getAudios())) {
                List<ArticleMediaVO> audios = JSONArray.parseArray(article.getArticleData().getAudios(), ArticleMediaVO.class);
                if (Lang.isEmpty(audios)) {
                    continue;
                }
                List<MediaResourceVO> resources = audios.get(0).getResources();
                article.setMedias(BeanMapper.mapList(resources, MediaResourceVO.class));
                article.setMediaTime(audios.get(0).getTimes());
            }
            if (article.getType().equals("video") && !Lang.isEmpty(article.getArticleData().getVideos())) {
                List<ArticleMediaVO> videos = JSONArray.parseArray(article.getArticleData().getVideos(), ArticleMediaVO.class);
                if (Lang.isEmpty(videos)) {
                    continue;
                }
                List<MediaResourceVO> resources = videos.get(0).getResources();
                article.setMedias(BeanMapper.mapList(resources, MediaResourceVO.class));
                article.setMediaTime(videos.get(0).getTimes());
            }
            if ("image".equals(article.getType()) && !Lang.isEmpty(article.getArticleData().getImages())) {
                List<ArticleMediaVO> images = JSONArray.parseArray(article.getArticleData().getImages(), ArticleMediaVO.class);
                if (null != images) {
                    article.setImageNum(images.size());
                }
            }
        }
        return list;
    }

    @Override
    public Article findArticleByApp(Long articleId,Integer delFlag) {
        Article article = getArticleDetails(articleId);
        if(article!=null){
            if(delFlag==null && !article.getDelFlag().equals(0)){
                return null;
            }else if(delFlag!=null && article.getDelFlag()>delFlag){
                return null;
            }
        }else{
            return null;
        }
        if (!Lang.isEmpty(article.getPublishDate())) {
            article.setDate(article.getPublishDate().getTime());
        }
        if (StringUtils.isNotBlank(article.getImageUrl())) {
            article.setImageApp(Arrays.asList(article.getImageUrl().split(",")));
        }
        List<ArticleMediaVO> medias = Lists.newArrayList();
        if (null != article.getArticleData()) {
            if (StringUtils.isNotBlank(article.getArticleData().getAudios())) {
                List<ArticleMediaVO> audios = JSONArray.parseArray(article.getArticleData().getAudios(), ArticleMediaVO.class);
                medias.addAll(audios);
            }
            if (StringUtils.isNotBlank(article.getArticleData().getVideos())) {
                List<ArticleMediaVO> videos = JSONArray.parseArray(article.getArticleData().getVideos(), ArticleMediaVO.class);
                medias.addAll(videos);
            }
            if (StringUtils.isNotBlank(article.getArticleData().getImages())) {
                List<ArticleMediaVO> images = JSONArray.parseArray(article.getArticleData().getImages(), ArticleMediaVO.class);
                medias.addAll(images);
            }
        }
        article.setDetailMedias(medias);
        if (!Lang.isEmpty(article.getVotes())) {
            article.getVotes().stream().filter(vote -> vote.getDelFlag() < Vote.STATUS_DELETE)
                    .forEach(vote -> dao.fetchLinks(vote, "options", Cnd.where(BaseEntity.FIELD_STATUS, "<", VoteOption.STATUS_DELETE)));
        }
        return article;
    }

    /**
     * 根据区块id查询文章分页列表
     *
     * @param blockId
     * @param size
     * @param page
     * @return
     */
    @Override
    public QueryResult getArticlePageByBlockId(Long blockId, Integer size, Integer page) {
        Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0).
                and(Article.Constant.CATEGORY_ID, "=", blockId).
                and(Article.Constant.IN_SUBJECT, "=", 1).
                desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE);
        QueryResult queryResult = this.listPage(page, size, condition);

        List<Article> list = queryResult.getList(Article.class);
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        queryResult.setList(list);
        return queryResult;
    }

    //TODO 查询某个时间段，某个栏目下的文章内容

    @Override
    public QueryResult getArticlePageByCategoryIdAndBlockId(Long categoryId, Long blockId, Integer size, Integer page) {
        Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0)
                .and(Article.Constant.CATEGORY_ID, "=", categoryId)
                .and(Article.Constant.BLOCK, "=", blockId)
                .and(Article.Constant.IN_SUBJECT, "=", 0)
                .asc(Article.Constant.POSITION + " is null")
                .asc(Article.Constant.POSITION)
                .desc(Article.Constant.WEIGHT)
                .desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID);

        QueryResult queryResult = this.listPage(page, size, condition);

        List<Article> list = queryResult.getList(Article.class);
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        //兼容老数据对articleData做特殊处理
        list.stream().filter(article -> (ArticleType.AUDIO.value().equals(article.getType()) || ArticleType.VIDEO.value().equals(article.getType()))
                && StringUtils.isNotBlank(article.getImage())).forEach(article-> handleArticleDataForOldData(article.getArticleData(),article.getImage()));
        queryResult.setList(handleListByPosition(list));
        return queryResult;
    }

    /**
     * 根据articleVO定义的查询条件分页查询文章列表
     *
     * @param pageSize
     * @param articleVO
     * @return
     */
    @Override
    public List<Article> findArticlePageByArticleVO(Integer pageNumber, Integer pageSize, ArticleVO articleVO) {
        Cnd cnd = Cnd.where("1", "=", "1");
        if (articleVO.getCategoryId() != null) {
            cnd.and(Article.Constant.CATEGORY_ID, "=", articleVO.getCategoryId());
        }
        if (articleVO.getDelFlag() != null) {
            cnd.and(BaseEntity.FIELD_STATUS, "=", articleVO.getDelFlag());
        } else {
            cnd.and(BaseEntity.FIELD_STATUS, "!=", 3);
        }
        if (articleVO.getBlock() != null) {
            cnd.and(Article.Constant.BLOCK, "=", articleVO.getBlock());
        }
        cnd.and(Article.Constant.IN_SUBJECT, "=", articleVO.getInSubject() ? 1 : 0);
        if (StringUtils.isNotBlank(articleVO.getDesc())) {
            cnd.desc(articleVO.getDesc());
        } else {
            cnd.desc(Article.Constant.WEIGHT);
            cnd.desc(Article.Constant.PUBLISH_DATE);
        }
        if (StringUtils.isNotBlank(articleVO.getAsc())) {
            cnd.asc(articleVO.getAsc());
        }
        List<Article> articleList=dao.query(Article.class,cnd);
        return articleList;
    }


    /**
     * 递归获取每一个山西日报入住系统的栏目
     * @param list
     * @return
     */
    private void getPublicCmsCategory(List<SxrbAppcmsCategoryMenuVO> list,
                                      SxrbAppcmsCategoryMenuVO sxrbAppcmsCategoryMenuVO,
                                      List<Category> categoryList,Category parent){
        Category category = new Category();
        category.setId(Long.valueOf(sxrbAppcmsCategoryMenuVO.getId()+""));
        category.setParentIds(sxrbAppcmsCategoryMenuVO.getParentId());
        category.setName(sxrbAppcmsCategoryMenuVO.getName());
        if(parent != null){
            category.setParent(parent);
        }
        categoryList.add(category);
        if(sxrbAppcmsCategoryMenuVO.getChild() != null && sxrbAppcmsCategoryMenuVO.getChild().size() > 0){
            for (SxrbAppcmsCategoryMenuVO sxrbAppcmsCategoryMenuVO1 :sxrbAppcmsCategoryMenuVO.getChild()) {
                getPublicCmsCategory(sxrbAppcmsCategoryMenuVO1.getChild(), sxrbAppcmsCategoryMenuVO1, categoryList,category);
            }
        }
    }

    /**
     * 获取文章类型
     * @param type
     * @return
     */
    private String getArticleType(String type){
        String result = "普通新闻";
        if(Article.ARTICLE_TYPE_NORMAL.equals(type)){
            result = "普通新闻";
        }else if(Article.ARTICLE_TYPE_IMG.equals(type)){
            result = "图片新闻";
        }else if(Article.ARTICLE_TYPE_AUDIO.equals(type)){
            result = "音频新闻";
        }else if(Article.ARTICLE_TYPE_VIDEO.equals(type)){
            result = "视频新闻";
        }else if(Article.ARTICLE_TYPE_PAPER.equals(type)){
            result = "报纸";
        }else if(Article.ARTICLE_TYPE_GOV.equals(type)){
            result = "政务";
        }else if(Article.ARTICLE_TYPE_ACT.equals(type)){
            result = "活动";
        }else if(Article.ARTICLE_TYPE_SUBJECT.equals(type)){
            result = "普通专题";
        }else if(Article.ARTICLE_TYPE_ALBUM.equals(type)){
            result = "音频专题";
        }else if(Article.ARTICLE_TYPE_HELP.equals(type)){
            result = "公益新闻";
        }else if(Article.ARTICLE_TYPE_INTERVIEW.equals(type)){
            result = "访谈";
        }else if(Article.ARTICLE_TYPE_LINK.equals(type)){
            result = "链接";
        }else if(Article.ARTICLE_TYPE_COLLECTION.equals(type)){
            result = "征集类型";
        }else if(Article.ARTICLE_TYPE_VRVIDEO.equals(type)){
            result = "VR视频";
        }else if(Article.ARTICLE_TYPE_ASK.equals(type)){
            result = "问类型";
        }else if(Article.ARTICLE_TYPE_ASK_TOPIC.equals(type)){
            result = "问话题";
        }
        return result;
    }

    @Override
    public List<Article> getArticleByArticleId(Long articleId){
        Sql sql = Sqls.create("SELECT type FROM cms_article WHERE article_id = @article_id and del_flag=0");
        sql.params().set("article_id", articleId);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> list = sql.getList(Article.class);
        return list;
    }

    @Override
    public QueryResult getArticleByCategoryId(Long categoryId) {
        Sql sql=Sqls.create("select * from cms_article where category_id="+categoryId+" and del_flag=0");
        sql.setCallback(Sqls.callback.entities());
        sql.setCondition(Cnd.orderBy().asc(Article.Constant.POSITION).desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE));
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> list = sql.getList(Article.class);
        QueryResult queryResult = new QueryResult();
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        queryResult.setList(handleListByPosition(list));
        return queryResult;
    }

    @Override
    public QueryResult getArticleById(List<Article> list) {
        QueryResult queryResult = new QueryResult();
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        queryResult.setList(handleListByPosition(list));
        return queryResult;
    }

    @Override
    public QueryResult getArticleByIds(List<Long> list,Integer pageSize,Integer pageNo) {
        String str = Joiner.on(",").join(list);
        Sql sql=Sqls.create("select * from cms_article where id in("+str+") order by recomd_flag desc,weight desc,publish_date desc");
        Pager pager = new Pager();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> listResult = sql.getList(Article.class);
        QueryResult queryResult = new QueryResult();
        listResult.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        listResult.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        listResult.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        queryResult.setList(handleListByPosition(listResult));
        return queryResult;
    }

    @Override
    public QueryResult getSearchArticleById(String str,Integer pageSize,Integer pageNo) {
        Sql sql = Sqls.create("select * from cms_article where id in ("+str+") order by publish_date desc");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Article.class));
        dao.execute(sql);
        List<Article> listResult = sql.getList(Article.class);
        QueryResult queryResult = new QueryResult();
        listResult.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        listResult.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        listResult.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        queryResult.setList(handleListByPosition(listResult));
        return queryResult;
    }

    /**
     * 保存政务文章到数据库
     * @param categoryId  栏目id(政务栏目)
     * @param articleIds  文章id
     * @param block  显示为位置：区块 1. 头图 2.列表 3.待选
     */
    @Override
    @Transactional
    public void savePubliccmsArticle(Long categoryId, String articleIds,String block) {
        //根据文章id获取政务文章对象
        String[] articleArry=articleIds.split(",");
        if(articleArry!=null){
            for (String s : articleArry) {
                Long articleId=Long.valueOf(s);
                RestTemplate restTemplate=new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                //设置请求头部：token,编码
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
                ResponseEntity<Result> responseEntity= restTemplate.getForEntity(publiccmsUrl+ITERFACE_getArticleForSxrbCmsById+"?articleId="+articleId, Result.class);
                Result result=responseEntity.getBody();
                ArticlePubiccmsVO articlePubiccmsVO= JSONObject.parseObject(JSONObject.toJSONString(result.getData()),ArticlePubiccmsVO.class);
                Article article=new Article();
                BeanUtils.copyProperties(articlePubiccmsVO,article);
                article.setCategoryId(categoryId);
                article.setId(null);
                article.setBlock(StringUtils.isBlank(block)?2:Integer.valueOf(block));
                //评论数至成0
                article.setComments(0);
                //展示类型为左图右文
                article.setViewType(articlePubiccmsVO.getDisplayType());
                //设置自动剪裁
                article.setIsTailor(true);
                if(ArticleType.NORMAL_.value().equals(articlePubiccmsVO.getType())){
                    article.setType(ArticleType.NORMAL.value());
                }
                if(StringUtils.isNotBlank(articlePubiccmsVO.getImage())){
                    article.setImageUrl(formatImageSrcToWeb(articlePubiccmsVO.getImage()));
                }
                //处理文章内容
                ArticleData articleData=new ArticleData();
                if(StringUtils.isNotBlank(articlePubiccmsVO.getContent())){
                    articleData.setContent(handleContent(articlePubiccmsVO.getContent()));
                }
                //处理媒体资源
                if(articlePubiccmsVO.getMedias()!=null){
                    List<ArticleMediaVO> articleMediaVOList=new ArrayList<>();
                    List<MediaForSxrbCms> mediaForSxrbCmsList=articlePubiccmsVO.getMedias();
                    for (MediaForSxrbCms mediaForSxrbCms : mediaForSxrbCmsList) {
                        ArticleMediaVO articleMediaVO=new ArticleMediaVO();
                        BeanUtils.copyProperties(mediaForSxrbCms,articleMediaVO);
                        articleMediaVO.setIndex(mediaForSxrbCms.getSort()+"");
                        articleMediaVO.setImage(formatImageSrcToWeb(mediaForSxrbCms.getFileUrl()));
                        if(ArticleType.AUDIO.value().equals(articlePubiccmsVO.getType())){
                            article.setAudioUrl(formatImageSrcToWeb(mediaForSxrbCms.getFileUrl()));
                            articleMediaVO.setImage(formatImageSrcToWeb(articlePubiccmsVO.getImage()));
                            break;
                        }else if(ArticleType.VIDEO.value().equals(articlePubiccmsVO.getType())){
                            article.setVideoUrl(formatImageSrcToWeb(mediaForSxrbCms.getFileUrl()));
                            articleMediaVO.setImage(formatImageSrcToWeb(articlePubiccmsVO.getImage()));
                            break;
                        }
                        articleMediaVOList.add(articleMediaVO);
                    }
                    if(ArticleType.AUDIO.value().equals(articlePubiccmsVO.getType())){
                        articleData.setAudioJson(articleMediaVOList);
                    }else if(ArticleType.VIDEO.value().equals(articlePubiccmsVO.getType())){
                        articleData.setVideoJson(articleMediaVOList);
                    }else if(ArticleType.IMAGE.value().equals(articlePubiccmsVO.getType()) || Article.PUBLICIMAGE.equals(articlePubiccmsVO.getType())){
                        articleData.setImageJson(articleMediaVOList);
                    }

                }
                article.setArticleData(articleData);
                save(article);
            }
        }
    }

    @Override
    public int getCount(String str) {
        Cnd cnd=Cnd.where("id","in",str);
        return dao.count(Article.class,cnd);
    }

    /**
     * 格式化图片地址
     * @param src
     * @return
     */
    private String formatImageSrcToWeb(String src) {
        if (StringUtils.isBlank(src)){
            return src;
        }
        if (src.startsWith("http://")) {
            return src;
        }
        return uploadDomain+src;
    }



    /**
     * 处理文章内容
     * @param content
     */
    private String handleContent(String content){
        if(StringUtils.isBlank(content)){
            return content;
        }
        content=content.replaceAll(CONTENT_SRC,uploadDomain+CONTENT_NEW_SRC);
        return HtmlUtils.htmlUnescape(content);
    }

    @Override
    public Article findArticleByArticleId(Long articleId,String sysCode,String type) {
        Condition condition = Cnd.where("article_id", "=", articleId)
                .and("sys_code", "=", sysCode)
                .and("type", "=", type)
                .and("is_reference", "=", 0);
        List<Article> articleList = dao.query(Article.class, condition);
        if (articleList != null && articleList.size() > 0) {
            return articleList.get(0);
        }
        return null;
    }

    /**
     * 根据凡闻id和栏目id查询文章
     * @param docid
     * @param aLong
     * @return
     */
    @Override
    public List<Article> findByFanewsArticleIdAndCategoryId(String docid, Long aLong) {
        List<Article> articleList=dao.query(Article.class,Cnd.where(Article.Constant.FANEWSARTICLEID,"=",docid).and(Article.Constant.CATEGORY_ID,"=",aLong));
        return articleList;
    }

    /**
     * 兼容老数据对articleData做特殊处理
     * 取article中image字段作为音视频封面图
     * @param articleData
     * @param image
     */
    private void handleArticleDataForOldData(ArticleData articleData, String image) {
        if(articleData==null){
            return;
        }
        List<ArticleMediaVO> audioJson=articleData.getAudioJson()==null?JSONArray.parseArray(articleData.getAudios(), ArticleMediaVO.class):articleData.getAudioJson();
        List<ArticleMediaVO> videoJson=articleData.getVideoJson()==null?JSONArray.parseArray(articleData.getVideos(), ArticleMediaVO.class):articleData.getVideoJson();
        if(CollectionUtils.isNotEmpty(audioJson)){
            for (ArticleMediaVO articleMediaVO : audioJson) {
                if(StringUtils.isBlank(articleMediaVO.getImage())){
                    articleMediaVO.setImage(image);
                }
            }
            articleData.setAudios(JSONArray.toJSONString(audioJson));
        }
        if(CollectionUtils.isNotEmpty(videoJson)){
            for (ArticleMediaVO articleMediaVO : videoJson) {
                if(StringUtils.isBlank(articleMediaVO.getImage())){
                    articleMediaVO.setImage(image);
                }
            }
            articleData.setVideos(JSONArray.toJSONString(videoJson));
        }
    }
    
    /**
     * 文章预览分享地址
    */
    @Override
	public String getArticlePreviewUrl(Long id) {
        //1、生成时间戳
        String timeStampSec  =  String.valueOf((ClockUtil.currentTimeMillis()/1000));
    	//2、拼接URL
        String previewUrl=shareUrl+"detail/normal/"+timeStampSec+"?isTimeLimit=true";
        //3、存放缓存
        if (!Lang.isEmpty(id)) {
            redisTemplate.opsForValue().set(timeStampSec, id, 1, TimeUnit.DAYS);
        }
        return previewUrl;
	}
    
    /**
     * 回收站还原至下线
     */
	@Transactional
	public Article changeOffLineStatus(Long id) {
		 Article article = dao.fetch(tClass, id);
	        if (article.getDelFlag().equals(Article.STATUS_DELETE)) {
	            article.setDelFlag(Article.STATUS_OFFLINE);
	            //如果没有添加发布时间，则自动填补
	            article.setPublishDate(ClockUtil.currentDate());
	            article.setCreateBy(UserUtils.getUser().getId());
	            dao.updateIgnoreNull(article);
	        } 
	        //清空文章位置
	        clearPositon(id);
	        return article;
	}


    /**
     * 提前发布
     * @param id
     */
    @Override
    @Transactional
    public void advancePublish(Long id) {
        Article article=dao.fetch(Article.class,id);
        article.setDelFlag(Article.STATUS_ONLINE);
        article.setFixedPublishDate(null);
        article.setPublishDate(new Date());
        ArticleTaskRecord articleTaskRecord=dao.fetch(ArticleTaskRecord.class,id);
        articleTaskRecord.setDelFlag(ArticleTaskRecord.STATUS_DELETE);
        dao.update(article);
        dao.updateIgnoreNull(articleTaskRecord);
    }

    /**
     * 取消发布
     * @param id
     */
    @Override
    @Transactional
    public void cancelPublish(Long id) {
        Article article=dao.fetch(Article.class,id);
        article.setFixedPublishDate(null);
        article.setDelFlag(Article.STATUS_AUDIT);
        ArticleTaskRecord articleTaskRecord=dao.fetch(ArticleTaskRecord.class,id);
        articleTaskRecord.setDelFlag(ArticleTaskRecord.STATUS_DELETE);
        dao.update(article);
        dao.updateIgnoreNull(articleTaskRecord);
    }

    /**
     * 设置定时发布
     * @param articleVO
     */
    @Override
    @Transactional
    public void fixedPublish(ArticleVO articleVO) {
        Article article=dao.fetch(Article.class,articleVO.getArticleId());
        article.setFixedPublishDate(articleVO.getFixedPubTime());

        ArticleTaskRecord articleTaskRecord1=dao.fetch(ArticleTaskRecord.class,articleVO.getArticleId());
        if(articleTaskRecord1!=null){
            articleTaskRecord1.setFixedPublishDate(articleVO.getFixedPubTime());
            articleTaskRecord1.setDelFlag(ArticleTaskRecord.STATUS_ONLINE);
            dao.update(articleTaskRecord1);
        }else{
            ArticleTaskRecord articleTaskRecord=new ArticleTaskRecord();
            articleTaskRecord.setId(articleVO.getArticleId());
            articleTaskRecord.setDelFlag(ArticleTaskRecord.STATUS_ONLINE);
            articleTaskRecord.setFixedPublishDate(articleVO.getFixedPubTime());
            iArticleTaskRecordService.insert(articleTaskRecord);
        }

        dao.update(article);
    }

    /**
     * 相关推荐文章添加列表
     * @param article
     * @return
     */
    @Override
    public QueryResultVO<Article> recommondPage(ArticleVO article) {
        Long categoryId = article.getCategoryId() == null ? 0 : article.getCategoryId();
        String[] categoryIds = categoryIds(categoryId);
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where().andIn("category_id", categoryIds);

        cnd=cnd.and(BaseEntity.FIELD_STATUS, "=", BaseEntity.STATUS_ONLINE);
        cnd = article.getInSubject() != null ? cnd.and(Article.Constant.IN_SUBJECT, "=", article.getInSubject()) : cnd;
        cnd = article.getBeginTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, ">=", article.getBeginTime()) : cnd;
        cnd = article.getEndTime() != null ? cnd.and(Article.Constant.PUBLISH_DATE, "<=", article.getEndTime()) : cnd;
        cnd = StringUtils.isNotBlank(article.getType()) ? cnd.and(Article.Constant.TYPE, "=", article.getType()) : cnd;
        cnd = StringUtils.isNotBlank(article.getTitle()) ? cnd.and(Article.Constant.TITLE, "like", "%" + article.getTitle().trim() + "%") : cnd;

        OrderBy orderBy = criteria.getOrderBy();
        orderBy.desc(Article.Constant.PUBLISH_DATE);
        return listPage(article.getPageNumber(), article.getPageSize(), criteria);
    }


    /**
     * 组装文章预览地址(24小时过期)
     * @param article
     * @param baseShareUrl
     * @return
     */
    private  String setPreviewUrl(Article article, String baseShareUrl) {
        if (article == null) {
            return null;
        }
        String date = new Date().toString();
        if (!StringUtils.endsWith(baseShareUrl, "/")) {
            baseShareUrl += "/";
        }
        if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.SUBJECT.value())) {
            return baseShareUrl + "thematic/" + article.getArticleId() + "/" + date;
        } else if (StringUtils.equalsIgnoreCase(article.getSysCode(), SysCodeType.LIVE.value())) {
            return baseShareUrl + "live/" + article.getArticleId() + "/" + date;
        }else if (StringUtils.equalsIgnoreCase(article.getType(), "link")) {
            return article.getLink() + "/" + date;
        }else if (StringUtils.equalsIgnoreCase(article.getType(), "ask")) {
            return baseShareUrl + "detail/question/" + article.getArticleId() + "/" + date;
        } else {
            return baseShareUrl + "detail/normal/" + article.getArticleId() + "/" + date;
        }
    }

    /**
     * 根据ucenterf返回的文章id查询稿件
     * @param size
     * @param pageToken
     * @param newIdArry
     * @return
     */
    @Override
    public QueryResult myHelpList(Integer size, Integer pageToken, String[] newIdArry) {
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where().andIn("id", newIdArry);
        cnd=cnd.and(BaseEntity.FIELD_STATUS, "=", BaseEntity.STATUS_ONLINE);
        OrderBy orderBy = criteria.getOrderBy();
        orderBy.desc(Article.Constant.PUBLISH_DATE);
        QueryResult queryResult = this.listPage(pageToken, size, criteria);

        List<Article> list = queryResult.getList(Article.class);
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        //兼容老数据对articleData做特殊处理
        list.stream().filter(article -> (ArticleType.AUDIO.value().equals(article.getType()) || ArticleType.VIDEO.value().equals(article.getType()))
                && StringUtils.isNotBlank(article.getImage())).forEach(article-> handleArticleDataForOldData(article.getArticleData(),article.getImage()));
        list.stream().forEach(a -> a.setHelpFlag(0));
        queryResult.setList(list);
        return queryResult;
    }

    /**
     * 如果ucenter没有我的爱心数据，查询最新公益栏目下的稿件，取前3条
     * @param size
     * @param pageToken
     * @param id
     * @return
     */
    @Override
    public QueryResult myLastHelpList(Integer size, Integer pageToken,Long id) {
        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup cnd = criteria.where();
        cnd=cnd.and(BaseEntity.FIELD_STATUS, "=", BaseEntity.STATUS_ONLINE);
        cnd=cnd.and("category_id","=",id);
        OrderBy orderBy = criteria.getOrderBy();
        orderBy.desc(Article.Constant.PUBLISH_DATE);
        QueryResult queryResult = this.listPage(pageToken, size, criteria);

        List<Article> list = queryResult.getList(Article.class);
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
        //将Article中的publishDate转化客户端的date
        list.stream().filter(a -> null != a.getPublishDate()).forEach(a -> a.setDate(a.getPublishDate().getTime()));
        //将Article中的imageUrl转化为客户端的list<image>
        list.stream().filter(a -> null != a.getImageUrl()).forEach(a -> a.setImageApp(Arrays.asList(a.getImageUrl().split(","))));
        //兼容老数据对articleData做特殊处理
        list.stream().filter(article -> (ArticleType.AUDIO.value().equals(article.getType()) || ArticleType.VIDEO.value().equals(article.getType()))
                && StringUtils.isNotBlank(article.getImage())).forEach(article-> handleArticleDataForOldData(article.getArticleData(),article.getImage()));
        list.stream().forEach(a -> a.setHelpFlag(1));
        queryResult.setList(list);
        return queryResult;
    }
}
