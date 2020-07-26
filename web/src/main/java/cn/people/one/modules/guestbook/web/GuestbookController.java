package cn.people.one.modules.guestbook.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.guestbook.model.Guestbook;
import cn.people.one.modules.guestbook.model.front.GuestbookVO;
import cn.people.one.modules.guestbook.service.IGuestbookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 反馈管理
 * Created by sunday on 2017/4/11.
 */
@Api(description = "反馈管理")
@RestController
@RequestMapping("/api/guestbook")
@Slf4j
public class GuestbookController {

    @Autowired
    private IGuestbookService guestbookService;

    @ApiOperation("反馈列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    //@RequiresPermissions("client:guestbook:view")//权限管理;
    public Result<QueryResultVO<Guestbook>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, GuestbookVO guestbookVO) {
        return Result.success(guestbookService.findSearchPage(pageNumber, pageSize, guestbookVO));
    }

    @ApiOperation("反馈详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "反馈ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    //@RequiresPermissions("client:guestbook:view")//权限管理;
    public Result<Guestbook> view(@PathVariable Long id) {
        return Result.success(guestbookService.fetch(id));
    }

    @ApiOperation("保存反馈")
    @RequestMapping(method = RequestMethod.POST)
    //@RequiresPermissions("client:guestbook:edit")//权限管理;
    public Result<Guestbook> save(@RequestBody Guestbook guestbook) {
        guestbookService.save(guestbook);
        return Result.success(guestbook);
    }

    @ApiOperation("更新反馈")
    @RequestMapping(method = RequestMethod.PATCH)
    //@RequiresPermissions("client:guestbook:edit")//权限管理;
    public Result<Guestbook> updateIgnoreNull(@RequestBody Guestbook guestbook) {
        guestbookService.updateIgnoreNull(guestbook);
        return Result.success(guestbook);
    }

    @ApiOperation("删除反馈")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "反馈ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    //@RequiresPermissions("client:guestbook:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(guestbookService.delete(id));
    }

}
