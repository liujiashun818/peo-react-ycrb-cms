package cn.people.one.modules.live.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.message.SendMessage;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.service.ILiveRoomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 直播间Controller
 *
 * @author cheng
 */
@Api(description = "直播间管理（live模块）")
@RestController
@RequestMapping("/api/live/room/")
@Slf4j
public class LiveRoomController {

    @Autowired
    private ILiveRoomService liveRoomService;
    @ApiOperation("直播间列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNo", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<LiveRoom>> list(@RequestParam Integer pageNo, @RequestParam Integer pageSize) {
        Condition condition = Cnd.where("del_flag", "<=", BaseEntity.STATUS_AUDIT);
        return Result.success(liveRoomService.listPage(pageNo, pageSize, condition));
    }

    @ApiOperation("直播间详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "直播间ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<LiveRoom> view(@PathVariable Long id) {
        return Result.success(liveRoomService.fetch(id));
    }

    @ApiOperation("保存直播间")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result save(@RequestBody LiveRoom liveRoom) {
        Long id = liveRoomService.insertLiveRoom(liveRoom);
        SendMessage.sendArticle(id);
        return Result.success(liveRoom);
    }

    @ApiOperation("更新直播间")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result update(@RequestBody LiveRoom liveRoom) {
        Long id = liveRoomService.updateLiveRoom(liveRoom);
        SendMessage.sendArticle(id);
        return Result.success(liveRoom);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        int flag = liveRoomService.vDelete(id);
        SendMessage.sendArticle(id);
        return Result.success(flag);
    }

}
