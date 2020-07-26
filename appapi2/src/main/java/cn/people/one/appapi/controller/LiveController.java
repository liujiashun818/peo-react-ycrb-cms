package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.CommentService;
import cn.people.one.appapi.service.LiveService;
import cn.people.one.appapi.vo.*;
import cn.people.one.modules.live.service.ILiveRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author YX
 * @date 2018/10/15
 * @comment
 */
@Slf4j
@Api(value = "直播API", tags = {"直播相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/live")
public class LiveController {

    @Autowired
    private LiveService liveService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @ApiOperation("直播详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "直播id", required = true, paramType = "path")
    })
    @GetMapping("/detail/{articleId}")
    public ResultVO2<LiveRoomVO> detail(@PathVariable Long articleId) {
        if (articleId == null) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        try {
            LiveRoomVO liveRoomVO = liveService.getLiveRoomDetail(articleId);
            if (liveRoomVO != null) {
                return ResultVO2.success(liveRoomVO);
            } else {
                return ResultVO2.result(CodeConstant.LIVE_NOT_EXIST);
            }
        } catch (Exception e) {
            log.error("获取直播详情出错===》/api/v2/live/detail/" + articleId, e);
            return ResultVO2.result(CodeConstant.SYSTEM_ERROR);
        }
    }

    @ApiOperation("直播间")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "直播id", required = true),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/liveRoomTalk")
    public ResultVO liveRoomTalk(@RequestParam Long articleId,
                                 @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                 @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        /**
         * 参数处理====start
         */
        Integer page;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO.result(CodeConstant.PARAM_ERROR);
        }
        /**
         * 参数处理====end
         */
        try {
            return liveService.getLiveRoomTalk(articleId, size, page);
        } catch (Exception e) {
            log.error("获取直播间聊天出错===》/api/v2/live/liveRoomTalk", e);
            return ResultVO.result(CodeConstant.ERROR);
        }
    }

    @ApiOperation("网友互动 result.item = {status:直播状态 1预告 2直播中 3已结束,time:最后更新时间}" )
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "直播间id", required = true),
            @ApiImplicitParam(name = "sysCode", value = "系统编码", required = false, defaultValue = "live"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/liveRoomComment")
    public ResultVO5<Map, CommentVO> liveRoomComment(@RequestParam Long articleId,
                                                     @RequestParam(defaultValue = "live") String sysCode,
                                                     @RequestParam(required = false) String order,
                                                     @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                                     @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        /**
         * 处理参数
         */
        Integer page;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO5.result(CodeConstant.PARAM_ERROR);
        }
        /**
         * 处理参数结束
         */
        try {
            return commentService.getLiveRoomComment(articleId, sysCode, order, size, page);
        } catch (Exception e) {
            log.error("获取直播网友互动信息出错===》/api/v2/live/commentReplyForLive", e);
            return ResultVO5.result(CodeConstant.ERROR);
        }
    }

    @ApiOperation("最新直播间列表数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "直播间id", required = true),
            @ApiImplicitParam(name = "time", value = "最新时间戳", required = true)
    })
    @GetMapping("/newLiveRoomTalk")
    public  ResultVO5<Map, LiveTalkVO> newLiveRoomTalk(@RequestParam Long articleId, @RequestParam Long time) {
        try {
            if(Lang.isEmpty(time)){
                time=0L;
            }
            return this.liveService.getNewLiveRoomTalk(articleId, time);
        } catch (Exception e) {
            log.error("获取最新直播间列表数据出错===》/api/v2/live/newLiveRoomTalk", e);
            return ResultVO5.result(CodeConstant.ERROR);
        }
    }

    @ApiOperation("最新网友互动数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "直播间id", required = true),
            @ApiImplicitParam(name = "time", value = "最新时间戳", required = true),
            @ApiImplicitParam(name = "sysCode", value = "系统编码", required = false, defaultValue = "live")
    })
    @GetMapping("/newLiveRoomComment")
    public ResultVO newLiveRoomComment(@RequestParam Long articleId, @RequestParam Long time, @RequestParam String sysCode) {
        try {
            if(Lang.isEmpty(time)){
                time=0L;
            }
            return this.commentService.getNewLiveRoomComment(articleId, time, sysCode);
        } catch (Exception e) {
            log.error("获取最新直播间列表数据出错===》/api/v2/live/newLiveRoomTalk", e);
            return new ResultVO(CodeConstant.ERROR);
        }
    }


    @Deprecated
    @GetMapping("/convertAndSend")
    public ResultVO convertAndSend() {
        redisTemplate.convertAndSend(ILiveRoomService.REDIS_CHANNEL_LIVE_ROOM_COMMENT,String.valueOf(Math.random()));
        return ResultVO.result("ok");
    }
}
