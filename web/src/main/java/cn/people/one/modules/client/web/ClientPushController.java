package cn.people.one.modules.client.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.client.service.IClientPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * Created by lml on 17-2-15.
 */
@Api(description = "客户端推送管理(client模块)")
@RestController
@Slf4j
@RequestMapping("/api/client/push")
public class ClientPushController {

    @Autowired
    private IClientPushService clientPushService;

    @Value("${theone.project.rootId}")
    private Long rootId;

    @ApiOperation("保存推送")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("client:clientPush:edit")//权限管理;
    public Result save(@RequestBody ClientPush clientPush) {
        clientPushService.save(clientPush);
        return Result.success(clientPush);
    }

    @ApiOperation("删除")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("client:clientPush:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(clientPushService.delete(id));
    }

    @ApiOperation("推送列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query"),
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("client:clientPush:view")//权限管理;
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(clientPushService.listPage(pageNumber, pageSize, clientPushService.getDelFlag(null), null));
    }

    @ApiOperation("查看推送详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:clientPush:view")//权限管理;
    public Result<ClientPush> view(@PathVariable Long id) {
        return Result.success(clientPushService.fetch(id));
    }

    /**
     * 部分更新
     *
     * @param clientPush
     * @return
     */
    @ApiOperation("部分更新")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("client:clientPush:edit")//权限管理;
    public Result update(@RequestBody ClientPush clientPush) {
        clientPushService.updateIgnoreNull(clientPush);
        return Result.success("success");
    }

    /**
     * 推送管理--根据id查看推送内容
     * @param id
     * @return
     */
    @ApiOperation("根据id查看推送内容")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "query")
    })
    @RequestMapping(value="viewPushById" , method = RequestMethod.GET)
    @RequiresPermissions("client:clientPush:edit")//权限管理;
    public Result<ClientPush> viewPushById(@RequestParam Long id) {
        log.info("【 list 】" + id.toString());
        return Result.success(clientPushService.fetch(id));
    }

    /**
     * 推送管理--保存推送
     * @param clientPush
     * @return
     */
    @ApiOperation("保存推送")
    @RequestMapping(value = "savePush", method = RequestMethod.POST)
    @RequiresPermissions("client:clientPush:edit")//权限管理;
    public Result savePush(@RequestBody ClientPush clientPush){
        log.info("【 list 】" + clientPush.toString());
        return Result.success(clientPushService.save(clientPush));
    }
}
