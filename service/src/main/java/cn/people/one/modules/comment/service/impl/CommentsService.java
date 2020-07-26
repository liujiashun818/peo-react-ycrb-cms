package cn.people.one.modules.comment.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.http.IPHelper;
import cn.people.one.core.util.http.IpAreaInfo;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.Subject;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ISubjectService;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.model.front.CommentsParam;
import cn.people.one.modules.comment.model.front.CommentsVO;
import cn.people.one.modules.comment.service.ICommentsService;
import cn.people.one.modules.comment.service.ISensitiveWordsService;
import cn.people.one.modules.live.socket.LiveTalkWebSocket;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 评论Service
 *
 * @author 周欣
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class CommentsService extends BaseService<Comments> implements ICommentsService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private ISensitiveWordsService sensitiveWordsService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ISubjectService subjectService;

    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired
    private IAskQuestionReplyService askQuestionReplyService;

    /**
     * 评论列表页面搜索查询
     *
     * @param pageNumber
     * @param pageSize
     * @param commentsVO
     * @return
     */
    @Override
    public QueryResultVO<Comments> findSearchPage(Integer pageNumber, Integer pageSize, CommentsVO commentsVO) {
        Criteria cri = Cnd.cri();
        if (null != commentsVO.getDelFlag()) {
            cri.where().and(BaseEntity.FIELD_STATUS, " = ", commentsVO.getDelFlag());
        } else {
            cri.where().and(BaseEntity.FIELD_STATUS, "<", BaseEntity.STATUS_DELETE);
        }
        if (StringUtils.isNotBlank(commentsVO.getContent())) {
            cri.where().and("content", "like", "%" + commentsVO.getContent() + "%");
        }
        if (StringUtils.isNotBlank(commentsVO.getArticleId())) {
            cri.where().andEquals("article_id",commentsVO.getArticleId());
        }
        if(StringUtils.isNotBlank(commentsVO.getCategoryIds())){
            cri.where().and(Comments.Constant.CATEGORY_ID,"in",commentsVO.getCategoryIds().split(","));
        }
        //过滤政务评论
        cri.where().and(Comments.Constant.SYS_CODE,"!=",SysCodeType.GOV.value());
        cri.getOrderBy().desc("id");
        QueryResultVO<Comments> result = listPage(pageNumber, pageSize, cri);

        //含有敏感词的评论对敏感词进行加红色处理
        List<Comments> list = (List<Comments>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(comment -> {
                if (!Lang.isEmpty(comment)) {
                    comment.setContent(sensitiveWordsService.checkSensitiveWordContent(comment.getContent()));
                    comment.setTitle(sensitiveWordsService.checkSensitiveWordContent(comment.getTitle()));
                }
            });
        }

        return result;
    }
    @Override
    public QueryResultVO<Comments> findSearchPageForPublic(Integer pageNumber, Integer pageSize, CommentsVO commentsVO) {
        Criteria cri = Cnd.cri();
//        cri.where().andEquals("sys_code","gov");
        if (null != commentsVO.getDelFlag()) {
            cri.where().and(BaseEntity.FIELD_STATUS, " = ", commentsVO.getDelFlag());
        } 
        if (StringUtils.isNotBlank(commentsVO.getContent())) {
            cri.where().and("content", "like", "%" + commentsVO.getContent() + "%");
        }
        if (StringUtils.isNotBlank(commentsVO.getArticleId())) {
            cri.where().andEquals("article_id",commentsVO.getArticleId());
        }
        if(StringUtils.isNotBlank(commentsVO.getSysCode())){
            cri.where().andEquals(Comments.Constant.SYS_CODE,commentsVO.getSysCode());
        }
        cri.getOrderBy().desc("id");
        QueryResultVO<Comments>  result = listPage(pageNumber, pageSize, cri);

        //含有敏感词的评论对敏感词进行加红色处理
        List<Comments> list = (List<Comments>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(comment -> {
                if (!Lang.isEmpty(comment)) {
                    comment.setContent(sensitiveWordsService.checkSensitiveWordContent(comment.getContent()));
                    comment.setTitle(sensitiveWordsService.checkSensitiveWordContent(comment.getTitle()));
                }
            });
        }

        return result;
    }

    @Override
    public Comments query(Condition cnd) {
        Comments comments = null;
        List<Comments> list = dao.query(tClass, cnd);
        if (!Lang.isEmpty(list)) {
            comments = list.get(0);
        }
        return comments;
    }

    /**
     * 反转评论上线状态
     *
     * @param id
     */
    @Override
    @Transactional
    public void changeOnlineStatus(Long id) {
        Comments comments = dao.fetch(tClass, id);
        if (comments.getDelFlag().equals(Comments.STATUS_OFFLINE) || comments.getDelFlag().equals(Comments.STATUS_AUDIT) || comments.getDelFlag().equals(Comments.STATUS_NO_AUDIT)) {
            comments.setDelFlag(Comments.STATUS_ONLINE);
            updateArticleCommentsNumber(comments.getSysCode(), comments.getArticleId(), 1);
        } else {
            comments.setDelFlag(Comments.STATUS_OFFLINE);
            updateArticleCommentsNumber(comments.getSysCode(), comments.getArticleId(), -1);
        }
        comments.setUpdateAt(System.currentTimeMillis());
        updateIgnoreNull(comments);
    }
    
    @Override
    @Transactional
    public void changeOnlineOffStatus(Long id,boolean on) {
        Comments comments = dao.fetch(tClass, id);
        if(on) {
            comments.setDelFlag(Comments.STATUS_ONLINE);
            updateArticleCommentsNumber(comments.getSysCode(), comments.getArticleId(), 1);
        }else {
            comments.setDelFlag(Comments.STATUS_OFFLINE);
            updateArticleCommentsNumber(comments.getSysCode(), comments.getArticleId(), -1);
        }
        comments.setUpdateAt(System.currentTimeMillis());
        updateIgnoreNull(comments);
    }

    private void updateArticleCommentsNumber(String sysCode, String articleId, int operate) {
        if(SysCodeType.ASK.value().equals(sysCode)){
            //如果是问政的评论
            AskQuestionReply askQuestionReply=askQuestionReplyService.fetch(Long.valueOf(articleId));
            int oriComments = askQuestionReply.getCommentsNum() == null ? 0 : askQuestionReply.getCommentsNum();
            int newComments = (oriComments + operate) >= 0 ? (oriComments + operate) : 0;
            askQuestionReply.setCommentsNum(newComments);
            dao.updateIgnoreNull(askQuestionReply);
            if(newComments==0){
                Sql sql=Sqls.create("update ask_question_reply set comments_num=null where id ="+articleId);
                dao.execute(sql);
            }
        }else if(!SysCodeType.GOV.value().equals(sysCode)){
            Condition condition = Cnd.where(BaseEntity.FIELD_STATUS, "=", 0)
                    .and("article_id", "=", articleId)
                    .and("sys_code", "=", sysCode)
                    .and("is_reference", "=", 0);
            List<Article> articles = dao.query(Article.class, condition);
            if (articles != null && articles.size() > 0) {
                Article article = articles.get(0);
                int oriComments = article.getComments() == null ? 0 : article.getComments();
                int newComments = (oriComments + operate) >= 0 ? (oriComments + operate) : 0;
                article.setComments(newComments);
                dao.updateIgnoreNull(article);
            }
        }
    }

    /**
     * 组装评论数据
     *
     * @param comments
     */
    @Override
    @Transactional
    public Comments saveArticleComment(Comments comments) {
        //新的评论默认都是待审核状态，除非栏目设置为直接上线，则是上线状态
        if (null == comments) {
            return null;
        }
        //判断是否开启了栏目先发后审
        Category category = dao.fetch(Category.class, Cnd.where("id", "=", comments.getCategoryId()));
        if (!Lang.isEmpty(category) && null != category.getIsAutoOnline() && category.getIsAutoOnline()) {
            comments.setDelFlag(Category.STATUS_ONLINE);
        }
//        //实现敏感词管理，在评论入库的时候根据是否含有敏感词来判断默认上下线 敏感词匹配可以采用DFA算法实现
//        String content = comments.getContent();
//        if (!Lang.isEmpty(content)) {
//            boolean sensitiveWords = sensitiveWordsService.isIncludeSensitiveWord(content);
//            if (sensitiveWords) {
//                for (String str : sensitiveWordsService.getSensitiveWord(content)) {
//                    content = content.replaceAll(str, "<font color=\"#FF0000\">" + str + "</font>");
//                }
//                comments.setContent(content);
//                comments.setDelFlag(Category.STATUS_AUDIT);//为待审核状态
//            }
//            comments.setContent(EmojiParser.parseToAliases(content));
//        }
        if (SysCodeType.LIVE.value().equals(comments.getSysCode())) {
            //直播
            Condition condition = Cnd.where(BaseEntity.FIELD_STATUS, "=", 0)
                    .and("article_id", "=", comments.getArticleId())
                    .and("sys_code", "=", "live")
                    .and("is_reference", "=", 0);
            List<Article> articles = dao.query(Article.class, condition);
            if (!Lang.isEmpty(articles)) {
                Article liveArticle = articles.get(0);
                //查询直播对应文章下栏目是否设置了先发后审
                if(category==null && liveArticle!=null){
                    Category categoryLive=dao.fetch(Category.class,liveArticle.getCategoryId());
                    if (!Lang.isEmpty(categoryLive) && null != categoryLive.getIsAutoOnline() && categoryLive.getIsAutoOnline()) {
                        comments.setDelFlag(Category.STATUS_ONLINE);
                    }
                }
                comments.setTitle(liveArticle.getTitle());
                //将文章的评论数+1
                if ((new Integer(Category.STATUS_ONLINE).equals(comments.getDelFlag()))) {
                    liveArticle.setComments(liveArticle.getComments() == null ? 1 : liveArticle.getComments() + 1);
                    dao.updateIgnoreNull(liveArticle);
                }
            }
        } else if (SysCodeType.ARTICLE.value().equals(comments.getSysCode()) || SysCodeType.SUBJECT.value().equals(comments.getSysCode())) {
            //普通文章
            Article article = articleService.fetch(Long.valueOf(comments.getArticleId()));
            if (!Lang.isEmpty(article)) {
                //如果是专题下文章
                if(article.getInSubject() && category==null){
                    Subject subject=dao.fetch(Subject.class,article.getCategoryId());
                    if(subject!=null && subject.getParentId()!=null){
                        Subject subjectPar=dao.fetch(Subject.class,subject.getParentId());
                        if(subjectPar!=null){
                            Article articleSublect=dao.fetch(Article.class,Cnd.where(Article.Constant.ARTICLE_ID,"=",subjectPar.getId())
                                    .and(Article.Constant.TYPE,"=",ArticleType.SUBJECT).and(Article.FIELD_STATUS,"=",Article.STATUS_ONLINE).and(Article.Constant.IS_REFERENCE,"=",0));
                            if(articleSublect!=null){
                                Category categorySubject=dao.fetch(Category.class,articleSublect.getCategoryId());
                                if (!Lang.isEmpty(categorySubject) && null != categorySubject.getIsAutoOnline() && categorySubject.getIsAutoOnline()) {
                                    comments.setDelFlag(Category.STATUS_ONLINE);
                                }
                            }
                        }
                    }
                }
                comments.setTitle(article.getTitle());
                if (SysCodeType.SUBJECT.value().equals(comments.getSysCode())) {
                    //专题评论的标题为专题标题
                    Subject subject = subjectService.fetch(comments.getArticleId());
                    if (!Lang.isEmpty(subject)) {
                        comments.setTitle(subject.getTitle());
                    }
                }
                if (null != article.getIsReference() && article.getIsReference().equals(true)) {//引用文章
                    Article original = articleService.fetch(article.getArticleId());//原文
                    if (!Lang.isEmpty(original)) {
                        if ((new Integer(Category.STATUS_ONLINE).equals(comments.getDelFlag()))) {
                            original.setComments(original.getComments() == null ? 1 : original.getComments() + 1);
                            dao.updateIgnoreNull(original);
                        }
                    }
                } else {
                    if ((new Integer(Category.STATUS_ONLINE).equals(comments.getDelFlag()))) {
                        article.setComments(article.getComments() == null ? 1 : article.getComments() + 1);
                        dao.updateIgnoreNull(article);
                    }
                }
            }
        }
        return super.insert(comments);
    }

    /**
     * 评论审核
     * @param commentId 评论id
     * @param actionStatus 动作状态0:审核不通过，1:审核通过
     */
    @Override
    @Transactional
    public void auditComment(Long commentId, Integer actionStatus) {
        Comments comments=fetch(commentId);
        if(comments!=null && Comments.STATUS_AUDIT==comments.getDelFlag() && actionStatus==0){
            comments.setDelFlag(Comments.STATUS_NO_AUDIT);
            save(comments);
        }
    }

    @Override
    public List<Comments> comments(String sysCode, Long articleId) {
        Condition condition = Cnd.where("sys_code", "=", sysCode)
                .and("article_id", "=", articleId)
                .and(BaseEntity.FIELD_STATUS, "<", 3)
                .desc("create_at");
        return dao.query(Comments.class, condition);
    }

    @Override
    public QueryResult comments(String sysCode, Long articleId, Integer page, Integer size) {
        Condition condition = Cnd.where("sys_code", "=", sysCode)
                .and("article_id", "=", articleId)
                .and(BaseEntity.FIELD_STATUS, "=", 0)
                .desc("likes")
                .desc("create_at");
        return this.listPage(page, size, condition);
    }

    @Override
    public QueryResult myComments(String openId, Integer page, Integer size) {
        Condition condition = Cnd.where("user_open_id", "=", openId)
                .and(BaseEntity.FIELD_STATUS, "<", 3)
                .desc("create_at")
                .desc("likes");
        return this.listPage(Comments.class, page, size, condition);
    }

    @Override
    public long countReplyComments(String openId) {
        Sql sql = Sqls.fetchLong("" +
                "SELECT                                     " +
                "   count(*)                                " +
                "FROM                                       " +
                "   comment_comments                        " +
                "WHERE                                      " +
                "   parent_id IN (                          " +
                "       SELECT                              " +
                "           id                              " +
                "       FROM                                " +
                "           comment_comments                " +
                "       WHERE                               " +
                "           user_open_id=@openId            " +
                "           AND del_flag=0                  " +
                "   )                                       " +
                "   AND del_flag=0                          ");
        sql.params().set("openId", openId);
        dao.execute(sql);
        return sql.getLong();
    }

    @Override
    public List<Comments> queryByCnd(Cnd condition) {
        return dao.query(Comments.class, condition);
    }

    @Override
    public List<Comments> replyComments(String openId, Integer page, Integer size) {
        Sql sql = Sqls.queryRecord("" +
                "SELECT                                     " +
                "   *                                       " +
                "FROM                                       " +
                "   comment_comments                        " +
                "WHERE                                      " +
                "   parent_id IN (                          " +
                "       SELECT                              " +
                "           id                              " +
                "       FROM                                " +
                "           comment_comments                " +
                "       WHERE                               " +
                "           user_open_id=@openId            " +
                "           AND del_flag=0                  " +
                "   )                                       " +
                "   AND del_flag=0                          " +
                "$condition                                 ");
        Pager pager = new Pager();
        pager.setPageNumber(page);
        pager.setPageSize(size);
        sql.params().set("openId", openId);
        sql.setPager(pager);
        sql.setCondition(Cnd.orderBy().desc("create_at").desc("likes"));
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Comments.class));
        dao.execute(sql);
        return sql.getList(Comments.class);
    }

    /**
     * 回复/管理员回复评论（web）
     * @param requestComment
     * @param originalComment
     * @return
     */
    @Override
    @Transactional
    public Comments saveReplay(CommentsParam requestComment, Comments originalComment) {
        Comments comment = new Comments();
        comment.setContent(requestComment.getContent());
        comment.setUserName(requestComment.getUserName());
        comment.setUserIp(requestComment.getIp());
        comment.setCategoryId(originalComment.getCategoryId());
        //取上级评论的sysCode作为本级别sysCode
        comment.setSysCode(originalComment.getSysCode());
        //回复帖子的时候 无论是回复主贴还是盖楼，parentid都是主贴的id
        comment.setParentId(originalComment.getId());
        /*if(originalComment.getParentId()==0){
            comment.setParentId(originalComment.getId());
        }else{
            comment.setParentId(originalComment.getParentId());
        }*/
        comment.setReplyCommentId(originalComment.getId());
        comment.setReplyUserName(requestComment.getUserName());
        comment.setArticleId(originalComment.getArticleId());
        comment.setTitle(originalComment.getTitle());
        comment.setCreateAt(System.currentTimeMillis());
        comment.setDelFlag(Comments.STATUS_ONLINE);
        //真正的文章id
//        comment.setInteriorArticleId(originalComment.getInteriorArticleId());
        //评论所属文章的type
//        comment.setArticleType(originalComment.getArticleType());
        save(comment);
        if(SysCodeType.ASK.value().equals(requestComment.getSysCode())){
            //1、问政评论
            AskQuestionReply askQuestionReply=askQuestionReplyService.fetch(Long.valueOf(requestComment.getArticleId()));
            Integer commentNum=askQuestionReply.getCommentsNum()==null?0:askQuestionReply.getCommentsNum();
            askQuestionReply.setCommentsNum(commentNum+1);
            askQuestionReplyService.updateIgnoreNull(askQuestionReply);
        }else{
            //增加文章评论数
            Article article=articleService.fetch(Long.valueOf(comment.getArticleId()));
            if(article==null){
                 article=dao.fetch(Article.class,Cnd.where(Article.Constant.ARTICLE_ID,"=",comment.getArticleId())
                        .and(Article.Constant.TYPE,"=",comment.getSysCode()).and(Article.FIELD_STATUS,"=",Article.STATUS_ONLINE).and(Article.Constant.IS_REFERENCE,"=",0));
            }
            article.setComments(article.getComments()==null?0+1:article.getComments()+1);
            articleService.updateIgnoreNull(article);
        }
        return comment;
    }

    /**
     * 内容管理---》添加评论
     * @param commentsParam
     * @return
     */
    @Override
    @Transactional
    public Comments addComment(CommentsParam commentsParam){
        //查询原文
        AskQuestionReply askQuestionReply=null;
        Article article=null;
        Comments comment=new Comments();
        comment.setContent(commentsParam.getContent());
        comment.setUserName(commentsParam.getUserName());
        comment.setArticleId(commentsParam.getArticleId());
        comment.setDelFlag(Comments.STATUS_ONLINE);
        comment.setSysCode(commentsParam.getSysCode());
        comment.setParentId(0L);
        if(SysCodeType.ASK.value().equals(commentsParam.getSysCode())){
            //1、问政评论
            askQuestionReply=askQuestionReplyService.fetch(Long.valueOf(commentsParam.getArticleId()));
            comment.setTitle(askQuestionReply.getTitle());
            Integer commentNum=askQuestionReply.getCommentsNum()==null?0:askQuestionReply.getCommentsNum();
            askQuestionReply.setCommentsNum(commentNum+1);
            save(comment);
            askQuestionReplyService.save(askQuestionReply);
        }else{
            if(SysCodeType.LIVE.value().equals(commentsParam.getSysCode())||SysCodeType.SUBJECT.equals(commentsParam.getSysCode())){
                article=dao.fetch(Article.class,Cnd.where(Article.Constant.ARTICLE_ID,"=",commentsParam.getArticleId())
                        .and(Article.Constant.TYPE,"=",commentsParam.getSysCode()).and(Article.Constant.IS_REFERENCE,"=",0));
            }else{
                article=articleService.fetch(Long.valueOf(commentsParam.getArticleId()));
            }
            if(article!=null){
                comment.setTitle(article.getTitle());
                comment.setCategoryId(article.getCategoryId());
                Integer comments=article.getComments()==null?0:article.getComments();
                article.setComments(comments+1);
                save(comment);
                articleService.save(article);
            }
        }
        return comment;
    }

    @Override
    public QueryResultVO<Comments> listOfArticle(Map<String,String> params) {
        QueryResultVO<Comments> result = null;
        Integer pageNumber = Integer.valueOf(params.get("pageNumber"));
        Integer pageSize = Integer.valueOf(params.get("pageSize"));
        String article_id = params.get("article_id");

        Criteria cri = Cnd.cri();
        if (null != params.get("delFlag")) {
            cri.where().and(BaseEntity.FIELD_STATUS, " = ", params.get("delFlag"));
        }
        if (null != article_id) {
            cri.where().and("article_id", "like", "%" + article_id + "%");
        }
        cri.getOrderBy().desc("id");
        try {
            log.info("CommentsService/listOfArticle article_id:" + article_id.toString()+"delFlag:"+params.get("delFlag")+"pageNumber:"+pageNumber+"pageSize:"+pageSize);
            result = listPage(pageNumber, pageSize, cri);
            return result;
        }catch (Exception e){
            log.error(e.getMessage());
            return result;
        }

    }

    /**
     * index 从 0 开始 为数组索引
     * @param str
     * @param index
     * @return
     */
    public static String getStrArrValueByIndex(String[] str,int index){
        if(str == null || str.length < 1 || index < 0 || (index >(str.length -1))){
            return null;
        }
        return str[index];
    }

    /**
     * ip处理
     * @param comment
     * @return
     */
    private  Comments resolveip(Comments comment){
        //从IP地址中解析出地域，解析采用http://www.ipip.net/的免费库
        if (cn.people.one.core.util.text.StringUtils.isNotBlank(comment.getUserIp())) {
            File file = new File("ip.tmp");
            try {
                InputStream in = resourceLoader.getResource("classpath:17monipdb.dat").getInputStream();
                file.deleteOnExit();
                OutputStream out = new FileOutputStream(file);
                IOUtils.copy(in, out);
                out.close();
                in.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            String[] ipArr = IPHelper.findIp(comment.getUserIp(), file);
            IpAreaInfo area = IPHelper.transformInfo(ipArr);
            if (area.getProvince().equals(area.getCity())) {
                comment.setArea(area.getProvince());
            } else {
                comment.setArea(area.getProvince() + area.getCity());
            }
        }
        return comment;
    }

    /**
     * 将评论内容推送到客户端
     * @param comment
     * @throws IOException
     */
    @Override
    public void pushComment(Comments comment) throws IOException {
        //推送数据到直播房间
        log.info("推送评论数据到直播房间");
        CopyOnWriteArraySet<LiveTalkWebSocket> sockets = LiveTalkWebSocket.getWebSocketSet();
        if (null != sockets && sockets.size() > 0) {
            for (LiveTalkWebSocket socket : sockets) {
                if (comment.getSysCode().equals(SysCodeType.LIVE.value()) && socket.getRoomId().equals(comment.getArticleId())) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("method", "comment");
                    map.put("comment", comment);
                    log.info("评论发送直播的房间id为" + socket.getRoomId());
                    socket.sendMessage(new Gson().toJson(map));
                }
            }
        }
    }
}