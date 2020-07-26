package cn.people.one.appapi.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.converter.CommentConverter;
import cn.people.one.appapi.request.CommentRequest;
import cn.people.one.appapi.vo.ArticleGovVO;
import cn.people.one.appapi.vo.CommentVO;
import cn.people.one.appapi.vo.LiveCommentReplyVO;
import cn.people.one.appapi.vo.LiveCommentVO;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO4;
import cn.people.one.appapi.vo.ResultVO5;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.http.IPHelper;
import cn.people.one.core.util.http.IpAreaInfo;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.activitycode.utils.RestTemplateUtil;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.service.ICommentsService;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.service.ILiveRoomService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wilson on 2018-10-09.
 */
@Slf4j
@Service("commentServiceV2")
public class CommentService {

    @Value("${http.vshare}")
    private String url;
    @Value("${http.sxh-api}")
    private String sxhApiUrl;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    /**
     * 获取入住系统文章对象接口
     */
    private static final String INTERFACE_GETARTICLEBYID="/api/articles/getArticleById/";
    /**
     * 更新文章评论数
     */
    private static final String INTERFACE_UPDATEARTICLE="/api/articles/updateArticle/";
    @Autowired
    private ICommentsService commentsService;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ILiveRoomService liveRoomService;
    @Autowired
    private IAskQuestionReplyService askQuestionReplyService;

    public ResultVO4<CommentVO> list(String sysCode, Long articleId, Integer page, Integer size) {
        QueryResult result = commentsService.comments(sysCode, articleId, page, size);
        List<CommentVO> commentVOS = fetchParentComment(result);
        return ResultVO4.success(null,commentVOS,commentVOS.size(),  size, page);
    }

    public ResultVO4<CommentVO> myComments(String openId, Integer page, Integer size) {
        QueryResult result = commentsService.myComments(openId, page, size);
        List<CommentVO> commentVOS = fetchParentComment(result);
        return ResultVO4.success(null,commentVOS, result.getPager().getRecordCount(), size, page);
    }

    public ResultVO4<CommentVO> replyComments(String openId, Integer page, Integer size) {
        List<Comments> result = commentsService.replyComments(openId, page, size);
        long count = commentsService.countReplyComments(openId);
        List<CommentVO> commentVOS = fetchParentComment(result);
        return  ResultVO4.success(null,commentVOS, count, size, page);
    }

    public ResultVO deleteComment(String openId, Long commentId) {
        Comments comments = commentsService.fetch(commentId);
        if (comments == null || comments.getDelFlag() == 3) {
            return ResultVO.result(CodeConstant.COMMENT_NOT_EXIST);
        }

        if (!StringUtils.equals(openId, comments.getUserOpenId())) {
            return ResultVO.result(CodeConstant.UNAUTHORIZED);
        }

        int deleted = commentsService.vDelete(commentId);
        if (deleted > 0) {
            return ResultVO.result("删除成功");
        } else {
            return ResultVO.result(-1, "删除失败");
        }
    }

    /**
     * 获取该评论的原文，并通过syscode字段区分是专题，直播还是普通文章的评论
     * @param comment
     */
    private void fetchArticle(CommentVO comment) {
        if (comment == null) {
            return;
        }

        if (StringUtils.endsWithIgnoreCase(comment.getSysCode(), "live")) {
            comment.setArticleType("live");
        } else if (StringUtils.endsWithIgnoreCase(comment.getSysCode(), "subject")) {
            comment.setArticleType("subject");
        } else {
            List<Article> articleList = articleService.getArticleByArticleId(Long.valueOf(comment.getArticleId()));
            if(null != articleList && articleList.size() >0){
                Article article = articleList.get(0);
                comment.setArticleType(article.getType());
//            Article article = articleService.fetch(comment.getArticleId());
            }else{
                comment.setArticleType("common");
            }
        }
    }

    private List<CommentVO> fetchParentComment(QueryResult result) {
        if (result.getList() != null && result.getList().size() > 0) {
            return fetchParentComment(result.getList(Comments.class));
        }
        return Collections.emptyList();
    }

    private List<CommentVO> fetchParentComment(List<Comments> comments) {
        List<CommentVO> commentVOS = new ArrayList<>();
        if (comments != null && comments.size() > 0) {
            for (Comments comment : comments) {
                CommentVO commentVO = CommentConverter.toVO(comment);
                if (commentVO == null || commentVO.getArticleId() == null || StringUtils.isBlank(commentVO.getSysCode())) {
                    continue;
                }

                fetchArticle(commentVO);
                commentVO.setDeleted(false);
                if (comment.getParentId() != null && comment.getParentId() != 0) {
                    Comments parent = commentsService.fetch(comment.getParentId());
                    if (parent != null) {
                        CommentVO parentVO = CommentConverter.toVO(parent);
                        if (parent.getDelFlag() == BaseEntity.STATUS_ONLINE) {
                            parentVO.setDeleted(false);
                        } else {
                            parentVO.setContent("原评论已删除");
                            parentVO.setDeleted(true);
                        }
                        fetchArticle(parentVO);
                        commentVO.setParent(parentVO);
                    }
                }
                commentVOS.add(commentVO);
            }
        }
        return commentVOS;
    }

    public ResultVO like(Long commentId, Integer likeOrNot) {
        Comments comments = commentsService.fetch(commentId);
        if (comments == null || (comments.getDelFlag() != BaseEntity.STATUS_ONLINE
                && comments.getDelFlag() != BaseEntity.STATUS_AUDIT)) {
            return ResultVO.result(-1, "评论不存在或已删除");
        }

        if (comments.getLikes() == null || comments.getLikes() < 0) {
            comments.setLikes(0);
        }

        int likes = comments.getLikes() + likeOrNot;
        comments.setLikes(0 < likes ? likes : 0);
        commentsService.updateIgnoreNull(comments);
        return ResultVO.result(CodeConstant.SUCCESS);
    }

    /**
     * 直播评论(网友互动)
     *
     * @param articleId
     * @param sysCode
     * @param order
     * @param size
     * @param page
     * @return
     */
    public ResultVO5<Map,CommentVO> getLiveRoomComment(Long articleId, String sysCode, String order, Integer size, Integer page) {
        Map resultMap = new HashMap();
        //获取直播间信息
        LiveRoom liveRoom = liveRoomService.fetch(articleId);
        if (liveRoom == null) {
            return ResultVO5.result(CodeConstant.LIVEROOM_NOT_EXIST);
        }
        resultMap.put("status", liveRoom.getStatus());
        resultMap.put("time", 0);
        //原逻辑结构封装作废
        /*Cnd condition = Cnd.where("article_id", "=", articleId)
                .and("sys_code", "=", sysCode)
                .and("parent_id", "=", 0)
                .and("del_flag", "=", 0);
        //按点赞数或创建时间排序
        if (StringUtils.isNotBlank(order) && order.equals("likes")) {
            condition.desc("likes");
        } else {
            condition.desc("create_at");
        }
        QueryResult queryResult = commentsService.listPage(page, size, condition);*/
        QueryResult queryResult = commentsService.comments(sysCode, articleId, page, size);
        List<Comments> commentsList = (List<Comments>) queryResult.getList();
        if (commentsList == null || commentsList.size() == 0) {
            commentsList = new ArrayList<>();
            return ResultVO5.success(resultMap, new  ArrayList<CommentVO>(),0,size,page);
        }
        List<Long> times = Lists.newArrayList();
        //提取最新时间
        for (Comments comments : commentsList) {
            if(comments.getUpdateAt()!=null){
                times.add(comments.getUpdateAt());
            }
        }
        //转换实体
        List<CommentVO> commentVOS = fetchParentComment(queryResult);
        if(times.size()>0){
            resultMap.put("time", Collections.max(times));
        }
        return ResultVO5.success(resultMap, commentVOS, queryResult.getPager().getRecordCount(), size, page);
    }

    /**
     * 获取评论分享地址
     *
     * @param commentId
     * @return
     */
    private String getCommentShareUrl(Long commentId,String type) {
        String shareUrl = null;
        if (!Lang.isEmpty(commentId)) {
            if (!url.endsWith("/")) {
                url += "/";
            }
            shareUrl = url + "detail/" + type+"/"+commentId;
        }
        return shareUrl;
    }

    /**
     * 处理网友互动数据，组装返回客户端数据
     *
     * @param commentsList
     */
    private Long handleCommentsList(List<Comments> commentsList, List<LiveCommentVO> liveCommentVOList) {
        List<Long> times = Lists.newArrayList();
        commentsList.stream().forEach(comments -> {
            times.add(comments.getUpdateAt());
            comments.setTime(comments.getUpdateAt());
            comments.setShareUrl(getCommentShareUrl(comments.getId(),comments.getType()));
            comments.setAdminReply(null == comments.getAdminReply() ? "" : comments.getAdminReply());
            comments.setDate(new Date(comments.getUpdateAt()));
            LiveCommentVO liveCommentVO = BeanMapper.map(comments, LiveCommentVO.class);
            List<Comments> commentsReply = commentsService.query(null, Cnd.where("parent_id", "=", comments.getId()).
                    and("del_flag", "=", 0).
                    asc("create_by"));
            for (int i = 0; i < commentsReply.size(); i++) {
                Comments comment = commentsReply.get(i);
                comment.setTime(comment.getUpdateAt());
                comment.setFloor(i + 1);//将跟帖按照时间升序排序，则跟帖的楼层为跟帖排序之后的索引值+1
                comment.setAdminReply(null == comment.getAdminReply() ? "" : comment.getAdminReply());
                comment.setDate(new Date(comment.getUpdateAt()));
            }
            List<LiveCommentReplyVO> replies = BeanMapper.mapList(commentsReply, LiveCommentReplyVO.class);
            liveCommentVO.setReplies(replies);
            liveCommentVOList.add(liveCommentVO);
        });
        return Collections.max(times);
    }

    /**
     * 获取最新网友互动数据
     *
     * @param articleId
     * @param time
     * @return
     */
    public ResultVO getNewLiveRoomComment(Long articleId, Long time, String sysCode) {
        //获取直播间信息
        Map resultMap = new HashMap();
        Long timeNew = time==null?0:time;
        LiveRoom liveRoom = liveRoomService.fetch(articleId);
        if (liveRoom == null) {
            return new ResultVO(CodeConstant.LIVEROOM_NOT_EXIST);
        }
        resultMap.put("status", liveRoom.getStatus());
        resultMap.put("time", timeNew);
        Cnd condition = Cnd.where("sys_code", "=", sysCode)
                .and("article_id", "=", articleId)
                .and(BaseEntity.FIELD_STATUS, "=", 0)
                .and("update_at", ">", time);
        //排序
        condition.desc("likes").desc("create_at");
        List<Comments> commentsList = commentsService.queryByCnd(condition);
        if (commentsList == null || commentsList.size() == 0) {
            commentsList = new ArrayList<>();
            return new ResultVO(resultMap, commentsList);
        }
        List<Long> times = Lists.newArrayList();
        //提取最新时间
        for (Comments comments : commentsList) {
            if(comments.getUpdateAt()!=null){
                times.add(comments.getUpdateAt());
            }
        }
        if(times.size()>0){
            timeNew=Collections.max(times);
        }
        //转换实体
        List<CommentVO> commentVOS = fetchParentComment(commentsList);
        resultMap.put("time", timeNew >= time ? timeNew : time);
        return new ResultVO(resultMap, commentVOS);
    }

    /**
     * 新增评论
     * @param comment
     */
    public ResultVO addComment(CommentRequest comment) {
        Comments comments = new Comments();
        comments.setArticleId(comment.getArticleId());
        comments.setCategoryId(comment.getCategoryId());
        comments.setTitle(comment.getTitle());
        comments.setContent(comment.getContent());
        comments.setSysCode(comment.getSysCode());
        comments.setUserName(comment.getUserName());
        comments.setUserIp(comment.getUserIp());
        comments.setUserOpenId(comment.getUserOpenId());
        comments.setUserIcon(comment.getUserIcon());
        comments.setUserType(comment.getUserType());
        comments.setReplyCommentId(comment.getReplyCommentId() == null ? 0L : comment.getReplyCommentId());
        comments.setReplyUserName(comment.getReplyUserName());
        comments.setReplyUserSysType(comment.getReplyUserSysType());
        comments.setParentId(comment.getReplyCommentId() == null ? 0L : comment.getReplyCommentId());
        if(comment.getImageUrls()!=null && comment.getImageUrls().size()>0){
            comments.setImage(comment.getImageUrls().get(0));
        }
        comments.setLikes(0);
        //默认为待审核状态
        comments.setDelFlag(Comments.STATUS_AUDIT);
        //从IP地址中解析出地域，解析采用http://www.ipip.net/的免费库
        if (StringUtils.isNotBlank(comments.getUserIp())) {
            IpAreaInfo area=IPHelper.getAreaInfo(comments.getUserIp());
            if(area!=null){
                if (area.getProvince().equals(area.getCity())) {
                    comments.setArea(area.getProvince());
                } else {
                    comments.setArea(area.getProvince() + area.getCity());
                }
            }
        }
        //正常显示我的评论
        comments.setMyCommentStatus(Comments.STATUS_ONLINE);
        //1、如果是问政评论
        if(SysCodeType.ASK.value().equals(comment.getSysCode())){
            return saveAskComment(comments);
        }
        //2、如果是政务的评论
        if(SysCodeType.GOV.value().equals(comment.getSysCode())){
            return saveGovComment(comments);
        }
        else{
            commentsService.saveArticleComment(comments);
            if(comments.getId()!=null){
                try {
                    if(Comments.STATUS_ONLINE==comments.getDelFlag()){
                        if (comment.getSysCode().equals(SysCodeType.LIVE.value())) {
                            Map<String, Object> map = Maps.newHashMap();
                            map.put("method", "comment");
                            map.put("comment", comments);
                            redisTemplate.convertAndSend(ILiveRoomService.REDIS_CHANNEL_LIVE_ROOM_COMMENT,new Gson().toJson(map));
                        }
                    }
                } catch (Exception e) {
                    log.error("WEBSOCKET推送评论到客户端失败",e);
                }
                return ResultVO.result(CodeConstant.SUCCESS);
            }else{
                return ResultVO.result(CodeConstant.ERROR);
            }
        }
    }

    /**
     * 保存
     * @param comments
     */
    @Transactional
    public ResultVO saveGovComment(Comments comments) {
        //调用入住系统接口查询新闻数据
        RestTemplate restTemplate = new RestTemplate();
        log.info("》调用山西日报sxh-api获取文章接口地址："+sxhApiUrl+INTERFACE_GETARTICLEBYID);
        ResultVO resultVO = restTemplate.getForObject(sxhApiUrl+INTERFACE_GETARTICLEBYID+comments.getArticleId(), ResultVO.class);
        ArticleGovVO articleGovVO= JSONObject.parseObject(JSONObject.toJSONString(resultVO.getItem()),ArticleGovVO.class);
        comments.setTitle(articleGovVO.getTitle());
        //如果栏目设置为先发后审,该栏目下的文章评论直接上线，不用审核
        if(ArticleGovVO.CATEGORYISAUDIT.equals(articleGovVO.getCateGoryIsAudit())){
            comments.setDelFlag(Comments.STATUS_ONLINE);
            commentsService.insert(comments);
            if(comments.getId()==null){
                return ResultVO.result(CodeConstant.SAVE_COMMENT_ERROR);
            }else {
                //调用入住系统更新新闻数据评论数
                RestTemplate restTemplate1 = new RestTemplate();
                log.info("》调用山西日报sxh-api更新文章评论数接口地址："+sxhApiUrl+INTERFACE_UPDATEARTICLE);
                Map paramMap = new HashMap<>();
                paramMap.put("articleId",comments.getArticleId());
                paramMap.put("commentsChange",1);
                String jsonStr = JSON.toJSONString(paramMap);
                HttpEntity<Result> entity=new HttpEntity(jsonStr,RestTemplateUtil.getHeaders());
                ResponseEntity<ResultVO> result = restTemplate1.postForEntity(sxhApiUrl+INTERFACE_UPDATEARTICLE, entity, ResultVO.class);
                return result.getBody();
            }
        }else {
            comments.setDelFlag(Comments.STATUS_AUDIT);
            commentsService.insert(comments);
            if(comments.getId()==null){
                return ResultVO.result(CodeConstant.SAVE_COMMENT_ERROR);
            }else {
                return ResultVO.result(CodeConstant.SUCCESS);
            }
        }
    }

    /**
     * 保存问政评论
     * @param comments
     */
    @Transactional(rollbackFor = Exception.class)
    public ResultVO saveAskComment(Comments comments) {
        AskQuestionReply askQuestionReply=askQuestionReplyService.fetch(Long.valueOf(comments.getArticleId()));
        comments.setTitle(askQuestionReply.getTitle());
        commentsService.insert(comments);
        int updateRow=askQuestionReplyService.updateIgnoreNull(askQuestionReply);
        if(updateRow==1){
            return ResultVO.result(CodeConstant.SUCCESS);
        }else{
            return ResultVO.result(CodeConstant.ERROR);
        }
    }
}
