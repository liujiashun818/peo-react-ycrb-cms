package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.Subject;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.front.SubjectVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleDataService;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ISubjectService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lml on 17-3-3.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class SubjectService extends BaseService<Subject> implements ISubjectService {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private IArticleDataService articleDataService;
    @Autowired
    private IUserService userService;

    /**
     * 获取区块下文章列表(上下线 标题 ) 需设置categoryId
     */
    @Override
    public QueryResultVO<Article> findArticlesInSubject(Integer pageNumber, Integer pageSize, ArticleVO articleVO) {
        Cnd cnd = Cnd.where(Article.Constant.CATEGORY_ID, "=", articleVO.getCategoryId())
                .and(Article.Constant.IN_SUBJECT, "=", true);
        if (StringUtils.isNotBlank(articleVO.getTitle())) {
            cnd.and(Article.Constant.TITLE, "like", "%" + articleVO.getTitle() + "%");
        }
        if (StringUtils.isNotBlank(articleVO.getAuthors())) {
            cnd.and(Article.Constant.AUTHOR, "like", "%" + articleVO.getAuthors() + "%");
        }
        if (null != articleVO.getDelFlag()) {
            cnd.and(Article.FIELD_STATUS, " = ", articleVO.getDelFlag());
        } else {
            cnd.and(Article.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE);
        }
        cnd.desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE).desc(Article.Constant.ID);
        QueryResultVO<Article> queryResultVO=articleService.listPage(pageNumber, pageSize, cnd);
        List<Article> articleList=queryResultVO.getList();
        if(!Lang.isEmpty(articleList)){
            articleList.stream().forEach(article -> {
                if (article.getCreateBy() != null) {
                    User user = userService.fetch(article.getCreateBy());
                    if (user != null && user.getName() != null) {
                        //发稿人信息
                        article.setContributor(user.getName());
                    }
                }
            });
            queryResultVO.setList(articleList);
        }
        return queryResultVO;
    }

    /**
     * 保存专题到专题模块（区块 需传递parentId）
     */
    @Override
    @Transactional
    public Subject insert(Subject subject) {
        //需传递parentId
        subject.init();
        Subject subject1 = dao.insert(subject);
        return subject1;
    }

    /**
     * 保存专题文章 需设置categoryId为专题或者区块id
     *
     * @param articles
     * @param categoryId
     * @return
     */
    @Override
    @Transactional
    public Boolean saveArticlesToSubject(List<Article> articles, Long categoryId) {
        if (!Lang.isEmpty(articles)) {
            articles.stream().forEach(article -> {
                if ((article.getIsReference() == null || article.getIsReference() == false) && article.getSysCode().equals(SysCodeType.SUBJECT.value())) {
                    //如果是专题，并且是实体专题，则articleID指向包装article的id
                    article.setArticleId(article.getId());
                }
                article.setCategoryId(categoryId);
                article.setIsReference(true);
                article.setId(null);
                article.setType(article.getType());
                article.setSysCode(article.getSysCode());
                article.setPublishDate(null);
                article.setInSubject(true);
                article.setDelFlag(Article.STATUS_AUDIT);
                articleService.save(article);
            });
        }
        return true;
    }

    /**
     * 在栏目中添加专题,需要设置categoryId
     *
     * @param subjectVO
     * @return
     */
    @Override
    @Transactional
    public SubjectVO saveSubjectToCategory(SubjectVO subjectVO) {
        //先保存专题
        Subject _subject = BeanMapper.map(subjectVO, Subject.class);
        _subject.setDescription(subjectVO.getArticle().getDescription());
        _subject.setViewType(subjectVO.getArticle().getViewType()); 
        save(_subject);
        Article article = BeanMapper.map(subjectVO.getArticle(), Article.class);
        //再将专题以文章的形式保存到文章表
        article.setArticleId(_subject.getId());
        if (StringUtils.isBlank(article.getListTitle())) {
            article.setListTitle(subjectVO.getTitle());
        }
        //for founder api
        article.setDoc_editor(UserUtils.getUser().getName());
        article.setDoc_original_flag(1);
        article.setTags("专题");
        article.setType(ArticleType.SUBJECT.value());
        article.setSysCode(SysCodeType.SUBJECT.value());
        //文章图片或音频视频处理
        ArticleData articleData=new ArticleData();
        article.setArticleData(articleData);
        articleService.save(article);
        subjectVO.setArticle(article);
        return subjectVO;
    }

    /**
     * 通过专题Id获取区块列表(权重倒序)
     *
     * @param subjectId
     * @return
     */
    @Override
    public List<Subject> findBlocks(Long subjectId) {
        return dao.query(tClass, getDelFlag(null).and("parent_id", "=", subjectId).desc(Subject.WEIGHT));
    }


    @Override
    public List<Article> findArticlesByApp(Long id) {
        Subject subject = dao.fetch(Subject.class, id);
        if (Lang.isEmpty(subject) || !subject.getDelFlag().equals(0)) {
            return null;
        }
        if (!Lang.isEmpty(subject.getParentId()) && subject.getParentId().equals(0)) {
            return null;
        }
        Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0).and(Article.Constant.CATEGORY_ID, "=", id).and(Article.Constant.IN_SUBJECT, "=", 1).desc(Article.Constant.WEIGHT).desc(Article.Constant.PUBLISH_DATE);
        List<Article> list = dao.query(Article.class, condition);
        list.stream().forEach(a -> dao.fetchLinks(a, "articleData"));
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
        List<Article> listArticle = Lists.newArrayList();
        for (Article article : list) {
            if (article.getSysCode().equals(SysCodeType.SUBJECT.value()) && article.getIsReference().equals(true)) {
                Article articleSubject = articleService.fetch(article.getArticleId());
                if (null != articleSubject) {
                    listArticle.add(articleSubject);
                }
            } else {
                listArticle.add(article);
            }
        }
        return listArticle;
    }

    @Override
    public Subject findSubjectByApp(Long id) {
        Condition articleCondition = Cnd.where(Article.FIELD_STATUS, "=", 0).and("sys_code", "=", "subject").and("article_id", "=", id).and("is_reference", "=", 0);
        List<Article> articleList = dao.query(Article.class, articleCondition);
        if (null == articleList || articleList.size() == 0) {
            return null;
        }
        Subject subject = dao.fetch(Subject.class, id);
        if (Lang.isEmpty(subject) || !subject.getDelFlag().equals(0)) {
            return null;
        }
        if (!Lang.isEmpty(subject.getParentId()) && !subject.getParentId().equals(0)) {
            return null;
        }
        subject.setCategoryId(articleList.get(0).getCategoryId());
        Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0).and("parent_id", "=", id).desc("weight");
        List<Subject> subjects = dao.query(Subject.class, condition);
        if (!Lang.isEmpty(subjects)) {
            for (Subject block : subjects) {
                List<Article> articles = findArticlesByApp(block.getId());
                if (null != articles) {
                    block.setArticles(articles);
                }
                subject.setBlocks(subjects);
            }
        }
        return subject;
    }

    @Override
    public SubjectVO get(Long id) {
        SubjectVO subjectVO = null;
        Article article = dao.fetch(Article.class,Cnd.where(Article.Constant.ID,"=",id).and(Article.Constant.TYPE,"=",ArticleType.SUBJECT));
        if(Lang.isEmpty(article)){
            article=dao.fetch(Article.class,Cnd.where(Article.Constant.ARTICLE_ID,"=",id).and(Article.Constant.TYPE,"=",ArticleType.SUBJECT).and(Article.Constant.IS_REFERENCE,"=",0));
        }
        if(!Lang.isEmpty(article)){
            ArticleData articleData=articleDataService.fetch(article.getId());
            if(articleData!=null){
                article.setArticleData(articleData);
            }
            Subject subject = fetch(article.getArticleId());
            if (subject == null) {
                Article articleSubject=articleService.fetch(article.getArticleId());
                if(articleSubject!=null){
                    subject=fetch(articleSubject.getArticleId());
                }
            }
            if (subject != null) {
                subjectVO = BeanMapper.map(subject, SubjectVO.class);
                subjectVO.setArticle(article);
            }
        }else {
            return null;
        }
        return subjectVO;
    }

    @Override
    @Transactional
    public Object updateSubjectInCategory(SubjectVO subjectVO) {
        Subject subject = BeanMapper.map(subjectVO, Subject.class);
        subject.setDescription(subjectVO.getArticle().getDescription());
        Article article = subjectVO.getArticle();
        article.setTags("专题");
        if (article.getMediaIds()==null &&  article.getArticleData()!=null){
            article.getArticleData().setVideos(null);
        }else if (article.getArticleData()==null){
            ArticleData data=new ArticleData();
            data.setId(article.getId());
            dao.insert(data);
        }
        Object result = articleService.save(article);
        updateIgnoreNull(subject);
        return result;
    }


    @Override
    @Transactional
    public int delSubject(Long id) {
        Article article = articleService.fetch(id);
        if (!Lang.isEmpty(article)) {
            Long subjectId = article.getArticleId();
            if (subjectId != null) {
                vDelete(subjectId);
            }
            articleService.del(id);
            return 1;
        }
        return 0;
    }

    /**
     * 根据专题id查询专题详细信息(包含区块列表信息)
     * @param articleId
     * @return
     */
    @Override
    public Subject getSubjectBlocksByArticleId(Long articleId){
        Condition articleCondition = Cnd.where(Article.FIELD_STATUS, "=", 0).
                and(Article.Constant.SYSCODE, "=", "subject").
                and(Article.Constant.ARTICLE_ID, "=", articleId).
                and(Article.Constant.IS_REFERENCE, "=", 0);
        List<Article> articleList = dao.query(Article.class, articleCondition);
        if (null == articleList || articleList.size() == 0) {
            return null;
        }
        Subject subject = dao.fetch(Subject.class, articleId);
        if(subject!=null){
            subject.setCategoryId(articleList.get(0).getCategoryId());
            //查询区块列表
            Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0).and(Article.Constant.PARENT_ID, "=", articleId).desc(Article.Constant.WEIGHT);
            List<Subject> subjects = dao.query(Subject.class, condition);
            if(subjects!=null){
                subject.setBlocks(subjects);
            }
        }
        return subject;
    }

    public Subject getSubjectBlocksById(Long articleId){
        Condition articleCondition = Cnd.where(Article.FIELD_STATUS, "=", 0).
                and(Article.Constant.ID, "=", articleId).
                and(Article.Constant.IS_REFERENCE, "=", 1);
        List<Article> articleList = dao.query(Article.class, articleCondition);
        if (null == articleList || articleList.size() == 0) {
            return null;
        }
        Subject subject = dao.fetch(Subject.class, articleId);
        subject.setCategoryId(articleList.get(0).getCategoryId());
        //查询区块列表
        Condition condition = Cnd.where(Article.FIELD_STATUS, "=", 0).and(Article.Constant.PARENT_ID, "=", articleId).desc(Article.Constant.WEIGHT);
        List<Subject> subjects = dao.query(Subject.class, condition);
        if(subjects!=null){
            subject.setBlocks(subjects);
        }
        return subject;
    }
}
