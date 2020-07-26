package cn.people.one.modules.comment.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.model.front.CommentsParam;
import cn.people.one.modules.comment.model.front.CommentsVO;
import cn.people.one.modules.comment.service.ICommentsService;
import cn.people.one.modules.common.CodeConstant;
import cn.people.one.modules.live.socket.LiveTalkWebSocket;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 评论Controller
 *
 * @author 周欣
 */
@Api(description = "评论管理（comment模块）")
@RestController
@RequestMapping("/api/comment/comments/")
@Slf4j
public class CommentsController {
    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Autowired
    private ICommentsService commentsService;

    /**
     * 评论管理--列表
     * @param pageNumber
     * @param pageSize
     * @param comments
     * @return
     */
    @ApiOperation("根据评论ids获取评论列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("cms:comment:view")//权限管理;
    public Result<QueryResultVO<Comments>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, CommentsVO comments) {
        try {
            log.info("【 pageNumber 】" + pageNumber.toString()+"pageSize:"+pageSize+"comments:"+comments);
            return Result.success(commentsService.findSearchPage(pageNumber, pageSize, comments));
        }catch (Exception e){
            log.error("论管理--列表出错===》 /api/comment/comments", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 评论管理--查看原文
     * @param id
     * @return
     */
    @ApiOperation("评论管理--查看原文")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "评论ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:comment:view")//权限管理;
    public Result<Comments> view(@PathVariable Long id) {
        try {
            log.info("【 id 】" + id.toString());
            return Result.success(commentsService.fetch(id));
        }catch (Exception e){
            log.error("评论管理--查看原文===》 /api/comment/comments/list/id", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 评论管理--添加评论
     * @param comments
     * @return
     */
    @ApiOperation("评论管理--添加评论")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result<Comments> save(@RequestBody Comments comments) {
        commentsService.save(comments);
        return Result.success(comments);
    }

    @ApiOperation("评论管理--更新评论")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result<Comments> updateIgnoreNull(@RequestBody Comments comments) {
        commentsService.updateIgnoreNull(comments);
        return Result.success(comments);
    }

    @ApiOperation("评论管理--删除评论")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "评论ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(commentsService.delete(id));
    }

    /**
     * 批量切换上下线
     *
     * @param commentIds
     * @return
     */
    @ApiOperation("批量切换上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentIds", value = "评论ID", required = true, paramType = "query")
    })
    @RequestMapping(value = "/batchOnOff", method = RequestMethod.POST)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result batchOnOff(@RequestParam String commentIds) {
        List<String> ids = null;
        try {
            if (StringUtils.isNotEmpty(commentIds)) {
                ids = Arrays.asList(commentIds.split(","));
            }
            if (null != ids && ids.size() > 0) {
                ids.forEach(id -> {
                    commentsService.changeOnlineStatus(Long.valueOf(id));
                });
                return Result.success();
            }
        } catch (Exception e) {
            log.error("批量切换上下线出错===/api/comment/comments/batchOnOff",e);
            return Result.error("批量切换上下线失败");
        }
        return null;
    }

    //
//    @RabbitListener(queues = "${theone.project.code}_${theone.rabbitmq.queue.comment}")
    public void processMessage(String commentStr) throws IOException{
        log.info("开始消费theone_comment_qu队列"+commentStr+"消息");
        if(StringUtils.isNotBlank(commentStr)){
            CommentDTO comment = null;
            try {
                comment = jsonMapper.fromJson(commentStr, CommentDTO.class);
            }catch (Exception e){
                log.error("消息转换错误");
            }
            if(null == comment){
                return;
            }
            Comments comments = convertToModel(comment);
            if(null == comments){
                return;
            }
            try {
                commentsService.insert(comments);
            }catch (Exception e){
                log.error("评论入库错误");
            }
            pushComment(comments);
        }
        log.info("评论消息消费完毕");
    }

    private Comments convertToModel(CommentDTO comment){
        Comments comments = new Comments();
        try {
            if(StringUtils.isNotBlank(comment.getParentId())){
                comments.setParentId(Long.valueOf(comment.getParentId()));
            }
            if(StringUtils.isNotBlank(comment.getCategoryId())){
                comments.setCategoryId(Long.valueOf(comment.getCategoryId()));
            }
            if(StringUtils.isNotBlank(comment.getContent())){
                comments.setContent(comment.getContent());
            }
            if(StringUtils.isNotBlank(comment.getImage())){
                comments.setImage(comment.getImage());
            }
            if(StringUtils.isNotBlank(comment.getSysCode())){
                comments.setSysCode(comment.getSysCode());
            }
            if(StringUtils.isNotBlank(comment.getArticleId())){
                comments.setArticleId(comment.getArticleId());
            }
            if(StringUtils.isNotBlank(comment.getTitle())){
                comments.setTitle(comment.getTitle());
            }
            if(StringUtils.isNotBlank(comment.getUserType())){
                comments.setUserType(comment.getUserType());
            }
            if(StringUtils.isNotBlank(comment.getUserOpenId())){
                comments.setUserOpenId(comment.getUserOpenId());
            }
            if(StringUtils.isNotBlank(comment.getUserSysType())){
                comments.setUserSysType(Integer.valueOf(comment.getUserSysType()));
            }
            if(StringUtils.isNotBlank(comment.getReplyUserOpenId())){
                comments.setReplyUserOpenId(comment.getReplyUserOpenId());
            }
            if(StringUtils.isNotBlank(comment.getReplyUserSysType())){
                comments.setReplyUserSysType(Integer.valueOf(comment.getReplyUserSysType()));
            }
            if(StringUtils.isNotBlank(comment.getUserName())){
                comments.setUserName(comment.getUserName());
            }
            if(StringUtils.isNotBlank(comment.getUserIcon())){
                comments.setUserIcon(comment.getUserIcon());
            }
            if(StringUtils.isNotBlank(comment.getUserIp())){
                comments.setUserIp(comment.getUserIp());
            }
            if(StringUtils.isNotBlank(comment.getArea())){
                comments.setArea(comment.getArea());
            }
            if(StringUtils.isNotBlank(comment.getLikes())){
                comments.setLikes(Integer.valueOf(comment.getLikes()));
            }
            if(StringUtils.isNotBlank(comment.getFloor())){
                comments.setFloor(Integer.valueOf(comment.getFloor()));
            }
            if(StringUtils.isNotBlank(comment.getMyCommentStatus())){
                comments.setMyCommentStatus(Integer.valueOf(comment.getMyCommentStatus()));
            }
            if(StringUtils.isNotBlank(comment.getReplyUserName())){
                comments.setReplyUserName(comment.getReplyUserName());
            }
            if(StringUtils.isNotBlank(comment.getReplyCommentId())){
                comments.setReplyCommentId(Long.valueOf(comment.getReplyCommentId()));
            }
        }catch (Exception e){
            comments = null;
            log.error("对象转换错误");
        }
        return comments;
    }

    //将评论内容推送到客户端
    private void pushComment(Comments comment) throws IOException {
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

    /**
     * 评论管理--回复/管理员回复
     * @param requestComment
     * @return
     */
    @ApiOperation("评论管理--回复/管理员回复")
    @RequestMapping(value = "/addReplyComment" ,method = RequestMethod.POST)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result addReplyComment(@RequestBody CommentsParam requestComment){
        try {
            //原始评论
            Comments originalComment = commentsService.fetch(requestComment.getParentId());
            //插入编辑产生评论
            commentsService.saveReplay(requestComment,originalComment);
            return Result.success(CodeConstant.SUCCESS);
        }catch (Exception e){
            log.error("回复/管理员回复出错===》 /api/comment/comments/list/addReplyComment", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 内容管理--添加评论
     * @param requestComment 接收评论参数
     * @return
     */
    @ApiOperation("内容管理--添加评论")
    @RequestMapping(value="/addComment" ,method = RequestMethod.POST)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result addComment(@RequestBody CommentsParam requestComment){
        try {
            Comments comments=commentsService.addComment(requestComment);
            if(comments!=null && null!=comments.getId()){
                return Result.success();
            }else {
                return Result.error("添加评论失败!");
            }
        }catch (Exception e){
            log.error("添加评论出错===》 /api/comment/comments/list/addComment", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 内容管理--根据文章查看评论列表
     * @param params
     * @return
     */
    @ApiOperation("内容管理--根据文章查看评论列表")
    @RequestMapping(value = "/listOfArticle", method = RequestMethod.GET)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result<QueryResultVO<Comments>> listOfArticle(@RequestParam Map<String ,String> params){
        try {
            log.info("【 params 】" + params.toString());
            return Result.success(commentsService.listOfArticle(params));
        }catch (Exception e){
            log.error("根据文章查看评论列表出错===》 /api/comment/comments/list/listOfArticle", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 评论审核不通过
     * @return
     */
    @ApiOperation("评论审核不通过")
    @RequestMapping(value = "/auditComment",method = RequestMethod.POST)
    public Result auditComment(@RequestBody Map paramMap){
        try {
            Long commentId= Long.valueOf(paramMap.get("commentId")+"");
            Integer actionStatus= (Integer) paramMap.get("actionStatus");
            commentsService.auditComment(commentId,actionStatus);
            return Result.success();
        } catch (Exception e) {
            log.error("评论审核不通过出错!",e);
            return Result.error("操作失败");
        }
    }
}
