package cn.people.one.modules.client.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.client.model.LoadingImgs;
import cn.people.one.modules.client.model.front.LoadingImgsVO;
import cn.people.one.modules.client.service.ILoadingImgsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * loading图管理
 * Created by sunday on 2017/4/12.
 */
@Api(description = "loading图管理")
@RestController
@RequestMapping("/api/client/loadingImgs")
@Slf4j
public class LoadingImgsController {

    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Autowired
    private ILoadingImgsService loadingImgsService;

    @ApiOperation("loading图片列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query"),
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("client:loadingImgs:view")//权限管理;
    public Result<QueryResultVO<LoadingImgs>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, LoadingImgsVO loadingImgs) {
        QueryResultVO<LoadingImgs> queryResultVO=loadingImgsService.findSearchPage(pageNumber, pageSize, loadingImgs);
        return Result.success(queryResultVO);
    }

    @ApiOperation("查看图片详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "图片ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:loadingImgs:view")//权限管理;
    public Result<LoadingImgs> view(@PathVariable Long id) {
        return Result.success(loadingImgsService.fetch(id));
    }

    @ApiOperation("保存")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("client:loadingImgs:edit")//权限管理;
    public Result save(@RequestBody LoadingImgs loadingImgs) {
        loadingImgsService.insert(loadingImgs);
        //将新加的开屏图url推送到mq
        loadingImgsService.sendLoadingImgs();
        return Result.success(loadingImgs);
    }

    @ApiOperation("更新")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("client:loadingImgs:edit")//权限管理;
    public Result updateIgnoreNull(@RequestBody LoadingImgs loadingImgs) {
        loadingImgsService.updateIgnoreNull(loadingImgs);
        //更新推送消息到mq
        loadingImgsService.sendLoadingImgs();
        return Result.success(loadingImgs);
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "图片ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("client:loadingImgs:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        int flag = loadingImgsService.delete(id);
        loadingImgsService.sendLoadingImgs();
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
    @RequiresPermissions("client:loadingImgs:edit")//权限管理;
    public Result OnOff(@PathVariable Long id) {
        loadingImgsService.changeOnlineStatus(id);
        loadingImgsService.sendLoadingImgs();
        return Result.success();
    }
}
