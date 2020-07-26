package cn.people.one.modules.client.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.client.model.FloatingImgs;
import cn.people.one.modules.client.model.front.FloatingImgsVO;
import cn.people.one.modules.client.service.IFloatingImgsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sunday on 2018/9/25.
 */
@Api(description = "浮标图管理")
@RestController
@RequestMapping("/api/client/floatingImgs")
@Slf4j
public class FloatingImgsController {

    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Autowired
    private IFloatingImgsService floatingImgsService;

    @ApiOperation("floating图片列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query"),
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("client:floatingImgs:view")//权限管理;
    public Result<QueryResultVO<FloatingImgs>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, FloatingImgsVO floatingImgs) {
        QueryResultVO<FloatingImgs> queryResultVO=floatingImgsService.findSearchPage(pageNumber, pageSize, floatingImgs);
        return Result.success(queryResultVO);
    }

    @ApiOperation("查看图片详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "图片ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:floatingImgs:view")//权限管理;
    public Result<FloatingImgs> view(@PathVariable Long id) {
        return Result.success(floatingImgsService.fetch(id));
    }

    @ApiOperation("保存图片")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("client:floatingImgs:edit")//权限管理;
    public Result save(@RequestBody FloatingImgs floatingImgs) {
        floatingImgsService.insert(floatingImgs);
        return Result.success(floatingImgs);
    }

    @ApiOperation("更新图片")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("client:floatingImgs:edit")//权限管理;
    public Result updateIgnoreNull(@RequestBody FloatingImgs floatingImgs) {
        floatingImgsService.updateIgnoreNull(floatingImgs);
        return Result.success(floatingImgs);
    }

    @ApiOperation("删除图片")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "图片ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("client:floatingImgs:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        int flag = floatingImgsService.delete(id);
        return Result.success(flag);
    }

    /**
     * 上下线
     *
     * @param id
     * @return
     */
    @ApiOperation("上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "图片ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/onOff/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:floatingImgs:edit")//权限管理;
    public Result OnOff(@PathVariable Long id) {
        floatingImgsService.changeOnlineStatus(id);
        return Result.success();
    }
}
