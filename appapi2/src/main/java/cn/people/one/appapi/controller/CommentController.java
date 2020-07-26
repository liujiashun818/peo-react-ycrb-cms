package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.request.CommentRequest;
import cn.people.one.appapi.service.CommentService;
import cn.people.one.appapi.util.IpUtils;
import cn.people.one.appapi.vo.CommentVO;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.ResultVO3;
import cn.people.one.appapi.vo.ResultVO4;
import cn.people.one.modules.comment.model.Comments;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by wilson on 2018-10-16.
 */
@Slf4j
@Api(value = "评论API", tags = {"评论相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    @Qualifier("commentServiceV2")
    private CommentService commentService;

    @ApiOperation("获取评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sysCode", value = "sysCode", required = true, paramType = "path"),
            @ApiImplicitParam(name = "articleId", value = "articleId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/{sysCode}/{articleId}")
    public ResultVO4<CommentVO> list(@PathVariable("sysCode") String sysCode,
                         @PathVariable("articleId") Long articleId,
                         @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                         @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {

        if (StringUtils.isBlank(sysCode)) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (articleId == null || articleId < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (size == null || size < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        int page;
        try {
            page = Integer.valueOf(pageToken);
        } catch (Exception e) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (page < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        return commentService.list(sysCode, articleId, page, size);
    }
    /**
     * 我的>我的评论，获取我的评论列表
     * @param userOpenId
     * @param size
     * @param pageToken
     * @return
     */
    @ApiOperation("获取我的评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userOpenId", value = "userOpenId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/{userOpenId}/myComments")
    public ResultVO4<CommentVO> myComments(@PathVariable("userOpenId") String userOpenId,
                               @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                               @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {

        if (StringUtils.isBlank(userOpenId)) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (size == null || size < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        int page;
        try {
            page = Integer.valueOf(pageToken);
        } catch (Exception e) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (page < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        return commentService.myComments(userOpenId, page, size);
    }


    @ApiOperation("获取评论我的评论列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userOpenId", value = "userOpenId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/{userOpenId}/reply_comments")
    public ResultVO4<CommentVO> replyComments(@PathVariable("userOpenId") String userOpenId,
                                  @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                  @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {

        if (StringUtils.isBlank(userOpenId)) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (size == null || size < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        int page;
        try {
            page = Integer.valueOf(pageToken);
        } catch (Exception e) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        if (page < 1) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }

        return commentService.replyComments(userOpenId, page, size);
    }

    /**
     * 山西号>评论,添加评论（和web中的添加评论接口保存在同一个库中）
     * @param comment
     * @param request
     * @return
     */
    @ApiOperation("添加评论")
    @PostMapping
    public ResultVO addComment(@RequestBody CommentRequest comment, HttpServletRequest request) {
        try {
            comment.setUserIp(IpUtils.getIp(request));
            return commentService.addComment(comment);
        } catch (Exception e) {
            log.error("添加评论失败",e);
            return ResultVO.result(CodeConstant.ERROR);
        }
    }

    @ApiOperation("删除评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "commentId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "openId", value = "openId", required = true, paramType = "query")
    })
    @DeleteMapping("/{commentId}")
    public ResultVO delete(@PathVariable("commentId") Long commentId,
                           @RequestParam("openId") String openId) {
        if (StringUtils.isBlank(openId)) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }

        if (commentId == null || commentId < 1) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }

        return commentService.deleteComment(openId, commentId);
    }

    @ApiOperation("点赞OR取消点赞")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commentId", value = "评论ID", required = true),
            @ApiImplicitParam(name = "likeOrNot", value = "点赞OR取消点赞(1:点赞，-1：取消点赞)", required = true)
    })
    @PutMapping("/{commentId}/like")
    public ResultVO like(@PathVariable("commentId") Long commentId,
                         @RequestParam("likeOrNot") String slikeOrNot) {

        int likeOrNot;
        try {
            likeOrNot = Integer.parseInt(slikeOrNot);
        } catch (Exception e) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }

        if (commentId == null || commentId < 1) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }

        if (likeOrNot != 1 && likeOrNot != -1) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }

        return commentService.like(commentId, likeOrNot);
    }

}
