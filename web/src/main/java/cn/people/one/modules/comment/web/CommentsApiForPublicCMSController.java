package cn.people.one.modules.comment.web;


import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.http.MD5Util;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.model.front.CommentsVO;
import cn.people.one.modules.comment.service.ICommentsService;
import cn.people.one.modules.common.CodeConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 问政务系统提供评论管理接口
 *
 * @author 付强
 */
@Api(description = "publiccms评论管理（comment模块）")
@RestController
@RequestMapping("/api/comment/publiccms/")
@Slf4j
public class CommentsApiForPublicCMSController
{
    @Autowired
    private ICommentsService commentsService;
    @Autowired
    private BaseDao dao;
    String publiccmsKey = "5YtxdD7PTjjEoqIatfMVbjGCcmza2Zb2xatvZOMrTSo3ePQ7VWevVHkPHlKsOIej";

    /**
     * 评论管理--列表
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @ApiOperation("评论管理--列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query"),
        @ApiImplicitParam(name = "delFlag", value = "删除标识", required = true, paramType = "query"),
        @ApiImplicitParam(name = "content", value = "内容", required = true, paramType = "query"),
        @ApiImplicitParam(name = "articleId", value = "文章ID", required = false, paramType = "query"),
        @ApiImplicitParam(name = "reqId", value = "请求ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "标识", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sysCode", value = "系统编码", required = false, paramType = "query")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<QueryResultVO<Comments>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize,
                                      @RequestParam String delFlag, @RequestParam String content, @RequestParam(required = false) String articleId,
                                      @RequestParam String reqId, @RequestParam String sign, @RequestParam(required = false) String sysCode )
    {
        try
        {
            if (!checkAuth(reqId, sign))
            {
                return Result.error(CodeConstant.TOKEN_ERROR);
            }
            CommentsVO commentVO = new CommentsVO();
            commentVO.setContent(content);
            if(StringUtils.isNotBlank(delFlag)) {
                commentVO.setDelFlag(Integer.valueOf(delFlag));
            }
            if(StringUtils.isNotBlank(articleId)){
                commentVO.setArticleId(articleId);
            }
            if(StringUtils.isNotBlank(sysCode)){
                commentVO.setSysCode(sysCode);
            }
            log.info("list().req= pageNumber=" + pageNumber.toString() + ",pageSize=" + pageSize
                     + ",articleId="+articleId+",delFlag="+delFlag+",content="+content);
            return Result.success(commentsService.findSearchPageForPublic(pageNumber, pageSize, commentVO));
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    private boolean checkAuth(String reqId, String sign)
    {
        Long reqTime=Long.valueOf(reqId);
        long currentTime=System.currentTimeMillis();
        long timespan=Math.abs(currentTime-reqTime);
        if(timespan>10*60*1000) {
            log.error("reqTime="+reqId+",currentTime="+currentTime+",timespan="+timespan);
            //时间间隔超过10分钟，校验失败
            return false;
        }
        String md5 = MD5Util.string2MD5(publiccmsKey + reqId);
        if (md5.equals(sign))
        {
            return true;
        }
        return false;
    }

    /**
     * 批量上线
     *
     * @param commentIds
     *            评论id数组，逗号分隔
     * @return
     */
    @ApiOperation("批量上线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentIds", value = "评论ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "reqId", value = "请求ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "标识", required = true, paramType = "query")
    })
    @RequestMapping(value = "/batchOn", method = RequestMethod.POST)
    public Result batchOn(@RequestParam String commentIds, @RequestParam String reqId,
                          @RequestParam String sign)
    {
        try
        {
            if (!checkAuth(reqId, sign))
            {
                return Result.error(CodeConstant.TOKEN_ERROR);
            }
            log.info("batchOn.req= commentIds=" + commentIds);
            if (StringUtils.isNotEmpty(commentIds))
            {
                int effectRows=dao.update(Comments.class, Chain.make("delFlag", Comments.STATUS_ONLINE), Cnd.where("id", "in", commentIds.split(",")));
                return Result.success(effectRows);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return Result.error("批量上线失败");
    }

    /**
     * 批量下线
     *
     * @param commentIds
     *            评论id数组，逗号分隔
     * @return
     */
    @ApiOperation("批量下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentIds", value = "评论ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "reqId", value = "请求ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "标识", required = true, paramType = "query")
    })
    @RequestMapping(value = "/batchOff", method = RequestMethod.POST)
    public Result batchOff(@RequestParam String commentIds, @RequestParam String reqId,
                           @RequestParam String sign)
    {
        try
        {
            if (!checkAuth(reqId, sign))
            {
                return Result.error(CodeConstant.TOKEN_ERROR);
            }
            log.info("batchOff.req= commentIds=" + commentIds);
            if (StringUtils.isNotEmpty(commentIds))
            {
                int effectRows=dao.update(Comments.class, Chain.make("delFlag", Comments.STATUS_OFFLINE), Cnd.where("id", "in", commentIds.split(",")));
                return Result.success(effectRows);
            }
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return Result.error("批量下线失败");
    }

    /**
     * 逻辑删除
     *
     * @param commentId
     *            评论id
     * @return
     */
    @ApiOperation("逻辑删除")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentId", value = "评论ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "reqId", value = "请求ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "标识", required = true, paramType = "query")
    })
    @RequestMapping(value = "/vdelete", method = RequestMethod.POST)
    public Result vdelete(@RequestParam String commentId, @RequestParam String reqId,
                          @RequestParam String sign)
    {
        try
        {

            if (!checkAuth(reqId, sign))
            {
                return Result.error(CodeConstant.TOKEN_ERROR);
            }
            log.info("vdelete.req= commentId=" + commentId);
            commentsService.vDelete(Long.valueOf(commentId));
            return Result.success();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return Result.error("逻辑删除失败");
    }

    /**
             * 恢复到待审核状态
     *
     * @param commentId
              *            评论id
     * @return
     */
    @ApiOperation("恢复到待审核状态")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentId", value = "评论ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "reqId", value = "请求ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "sign", value = "标识", required = true, paramType = "query")
    })
    @RequestMapping(value = "/restore", method = RequestMethod.POST)
    public Result restore(@RequestParam String commentId, @RequestParam String reqId,
                          @RequestParam String sign)
    {
        try
        {
            if (!checkAuth(reqId, sign))
            {
                return Result.error(CodeConstant.TOKEN_ERROR);
            }
            //DEL_FLAG_AUDIT=2
           log.info("restore.req= commentId=" + commentId);
           Comments comment= commentsService.fetch(Long.valueOf(commentId));
           comment.setDelFlag(Comments.STATUS_AUDIT);
            commentsService.updateIgnoreNull(comment);
            return Result.success();
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return Result.error("恢复状态失败");
    }

    /**
     * 根据评论ids获取评论列表
     * @param commentIds 评论id数组，逗号分隔
     * @return
     */
    @ApiOperation("根据评论ids获取评论列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "commentIds", value = "评论ID", required = true, paramType = "query")
    })
    @RequestMapping(value = "/getArticleIds", method = RequestMethod.GET)
    public Result<List<Comments>> batchOn(@RequestParam String commentIds) {
        try {
            log.info("getArticleIds.req= commentIds=" + commentIds);
            if (StringUtils.isNotEmpty(commentIds)) {
                List<Comments> commentsList = dao.query(Comments.class, Cnd.where("id", "in", commentIds.split(",")));
                return Result.success(commentsList);
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return Result.error("根据评论ids获取评论列表失败");
    }
}
