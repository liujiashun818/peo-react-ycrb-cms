package cn.people.one.modules.sys.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.sys.model.front.NavMenuVO;
import cn.people.one.modules.sys.service.IMenuService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 菜单管理Controller
 *
 * @author cuiyukun
 */
@Api(description = "菜单管理(sys模块)")
@RestController
@RequestMapping("/api/sys/menu/")
@Slf4j
public class MenuController {

	@Autowired
	private IMenuService menuService;

	@Value("${theone.project.rootId}")
	private Long rootId;


	/**
	 * 获取菜单列表
	 *
	 * @return
	 */
    @ApiOperation("获取菜单列表")
	@RequestMapping(value = "/tree", method = RequestMethod.GET)
//    @RequiresPermissions("sys:menu:view")//权限管理;
	public Result<List<NavMenuVO>> getTree() {
        User user = UserUtils.getUser();
        if (Lang.isEmpty(user)){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆");
        }
	    return Result.success(menuService.getMenuTree(rootId,user.getId()));
	}

	/**
	 * 获取菜单树
	 *
	 * @return
	 */
    @ApiOperation("获取菜单树")
	@RequestMapping(value = "/treeView", method = RequestMethod.GET)
//    @RequiresPermissions("sys:menu:view")//权限管理;
	public Result<List<NavMenuVO>> getTreeView() {
        User user = UserUtils.getUser();
        if (Lang.isEmpty(user)){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆");
        }
        return Result.success(menuService.getMenuTree(rootId,true,user.getId()));
	}

	/**
	 * 根据id获取菜单
	 *
	 * @param id
	 * @return
	 */
    @ApiOperation("根据id获取菜单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "菜单ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@RequiresPermissions("sys:menu:view")//权限管理;
	public Result<Menu> getMenuById(@PathVariable Long id) {
		if (Lang.isEmpty(menuService.fetch(id))) {
			return Result.error("菜单不存在");
		}
		return Result.success(menuService.fetch(id));
	}

	/**
	 * 添加一条菜单
	 *
	 * @param menu
	 * @return
	 */
    @ApiOperation("添加一条菜单")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result menuAdd(@RequestBody Menu menu) {
		if (!Lang.isEmpty(menuService.queryCode(menu.getCode()))) {
			return Result.error("菜单编号不能重复");
		}
		menuService.save(menu);
		return Result.success();
	}

	/**
	 * 修改菜单信息
	 * create_at,create_by，del_flag不可修改
	 *
	 * @param menu
	 * @return
	 */
    @ApiOperation("修改菜单信息")
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result menuUpdate(@RequestBody Menu menu) {
		Long id = menu.getId();
		if (Lang.isEmpty(menuService.fetch(id))) {
			return Result.error("菜单不存在！");
		}
		if (1 == menuService.update(menu, "createAt|createBy|delFlag")) {
			return Result.success();
		}
		return Result.error("更新失败！");
	}

	/**
	 * 伪删除菜单
	 * 不删除记录，只修改del_flag字段值
	 *
	 * @param id
	 * @return
	 */
    @ApiOperation("伪删除菜单")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "菜单ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result menuVDel(@PathVariable Long id) {
        if(id == 1){
            return Result.error("顶级菜单不允许删除");
        }
		if (Lang.isEmpty(menuService.fetch(id))) {
			return Result.error("菜单不存在！");
		}
		if (menuService.vDelete(id) > 0) {
			return Result.success();
		}
        return Result.error("删除失败");
	}


	/**
	 * 部分更新
	 *
	 * @param menu
	 * @return
	 */
    @ApiOperation("部分更新")
	@RequestMapping(method = RequestMethod.PATCH)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result update(@RequestBody Menu menu) {
		menuService.upadte(menu);
		return Result.success("success");
	}

	/**
	 * 上下线
	 *
	 * @param id
	 * @return
	 */
    @ApiOperation("上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "菜单ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/onOff/{id}", method = RequestMethod.GET)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result OnOff(@PathVariable Long id) {
		menuService.updateIgnoreNull(menuService.changeOnlineStatus(id));
		return Result.success();
	}

	/**
	 * 批量切换上下线
	 *
	 * @param menuIds
	 * @return
	 */
    @ApiOperation("批量切换上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "menuIds", value = "菜单ID", required = true, paramType = "query")
    })
	@RequestMapping(value = "/batchOnOff", method = RequestMethod.POST)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	@ResponseBody
	public Result batchOnOff(@RequestParam String menuIds) {
		List<String> ids = null;
		if (StringUtils.isNotEmpty(menuIds)) {
			ids = Arrays.asList(menuIds.split(","));
		}
		if (null != ids && ids.size() > 0) {
			ids.forEach(id -> {
				menuService.updateIgnoreNull(menuService.changeOnlineStatus(Long.valueOf(id)));
			});
			return Result.success();
		}
		return null;
	}

    @ApiOperation("批量更新")
	@RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
	@RequiresPermissions("sys:menu:edit")//权限管理;
	public Result batchUpdate(@RequestBody List<Menu> list) {
		menuService.batchUpdate(list);
		return Result.success();
	}

    /**
     * 菜单管理--批量更新排序
     * @param ids
     * @param sorts
     * @return
     */
    @ApiOperation("批量更新排序")
    @RequestMapping(value = "/updateSort",method = RequestMethod.POST)
    @RequiresPermissions("sys:menu:edit")//权限管理;
	public Result updateSort(Long[] ids, Integer[] sorts){
        log.info("【 updateSort 】" + Arrays.toString(ids)+Arrays.toString(sorts));
        return Result.success( menuService.updateSort(ids,sorts));
    }

}
