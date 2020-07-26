package cn.people.one.modules.live.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.live.model.LiveUser;
import cn.people.one.modules.live.service.ILiveUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* 直播嘉宾/主持人Controller
* @author cheng
*/
@Api(description = "主持人嘉宾（live模块）")
@RestController
@RequestMapping("/api/live/user/")
@Slf4j
public class LiveUserController {

    @Autowired
    private ILiveUserService liveUserService;

    @ApiOperation("直播用户列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "role", value = "角色", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<List<LiveUser>> list(@RequestParam String role) {
        return Result.success(liveUserService.list(role));
    }

    @ApiOperation("直播用户详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<LiveUser> view(@PathVariable Long id) {
        return Result.success(liveUserService.fetch(id));
    }

    @ApiOperation("保存直播用户")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result save(@RequestBody LiveUser liveUser) {
        liveUserService.save(liveUser);
        return Result.success(liveUser);
    }
    @ApiOperation("更新直播用户")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result update(@RequestBody LiveUser liveUser) {
        liveUserService.save(liveUser);
        return Result.success(liveUser);
    }

    @ApiOperation("删除直播用户")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(liveUserService.delete(id));
    }

}
