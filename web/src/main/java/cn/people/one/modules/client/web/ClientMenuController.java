package cn.people.one.modules.client.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.model.front.ClientMenuVO;
import cn.people.one.modules.client.service.IClientMenuService;
import cn.people.one.modules.cms.message.SendMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lml on 17-2-15.
 */
@Api(description = "客户端菜单管理(client模块)")
@RestController
@RequestMapping("/api/client/menu")
public class ClientMenuController {

    @Autowired
    private IClientMenuService clientMenuService;

    @Value("${theone.project.rootId}")
    private Long rootId;

    @ApiOperation("保存客户端菜单")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("client:clientMenu:edit")//权限管理;
    public Result save(@RequestBody ClientMenu clientMenu) {
        clientMenuService.save(clientMenu);
        SendMessage.sendMenu();
        return Result.success(clientMenu);
    }
    @ApiOperation("删除客户端菜单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "菜单ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("client:clientMenu:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        if(id == 1){
            return Result.error("顶级菜单不允许删除");
        }
        int flag = clientMenuService.delete(id);
        SendMessage.sendMenu();
        return Result.success(flag);
    }

    @ApiOperation("获取客户端列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions("client:clientMenu:view")//权限管理;
    public Result<List<ClientMenu>> getMenuList() {
        return Result.success(clientMenuService.getMenuList());
    }

    @ApiOperation("客户端菜单详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "菜单ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("client:clientMenu:view")//权限管理;
    public Result<ClientMenu> view(@PathVariable Long id) {
        return Result.success(clientMenuService.fetch(id));
    }

    /**
     * 获取导航列表
     *
     * @return
     */
    @ApiOperation("获取导航列表")
    @RequestMapping(value = {"/tree"}, method = RequestMethod.GET)
    @RequiresPermissions("client:clientMenu:view")//权限管理;
    public Result<List<ClientMenuVO>> tree() {
        return Result.success(clientMenuService.getTree(rootId));
    }

    /**
     * 部分更新
     *
     * @param clientMenu
     * @return
     */
    @ApiOperation("部分更新")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("client:clientMenu:edit")//权限管理;
    public Result update(@RequestBody ClientMenu clientMenu) {
        clientMenuService.saveClientMenu(clientMenu);
        SendMessage.sendMenu();
        return Result.success();
    }
}
