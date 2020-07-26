package cn.people.one.modules.user.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.service.IRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理下角色管理Controller
 *
 * @author cuiyukun
 */
@Api(description = "角色管理(user模块)")
@RestController
@RequestMapping("/api/users/role/")
@Slf4j
public class RoleController {

	@Autowired
	private IRoleService roleService;


    /**
     * 获取全部角色
     * @return
     */
	@ApiOperation("获取全部角色")
    @RequestMapping(value = {"/listAll"}, method = RequestMethod.GET)
    @RequiresPermissions("users:role:view")//权限管理;
    public Result<List<Role>> listAll(){
        return Result.success(roleService.listAll());
    }

	/**
	 * 分页获取角色列表
	 */
	@ApiOperation("分页获取角色列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
	@RequiresPermissions("users:role:view")//权限管理;
	public Result<QueryResultVO<Role>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
		return Result.success(roleService.listPage(pageNumber, pageSize));
	}

	/**
	 * 根据id获取角色
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("根据id获取角色")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "角色ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@RequiresPermissions("users:role:edit")//权限管理;
	public Result<Role> getRoleById(@PathVariable Long id) {
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		return Result.success(roleService.fetch(id));
	}

	/**
	 * 添加一条角色
	 *
	 * @param role
	 * @return
	 */
	@ApiOperation("添加一条角色")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	@RequiresPermissions("users:role:edit")//权限管理;
	public Result roleAdd(@RequestBody Role role) {
		String name = role.getName();
		if (!Lang.isEmpty(roleService.fetch(name))) {
			return Result.error(-1,"该角色已存在！");
		}
		roleService.save(role);
		return Result.success();
	}

	/**
	 * 修改一条角色信息
	 *
	 * @param role
	 * @return
	 */
	@ApiOperation("修改一条角色信息")
	@RequestMapping(value = "/update", method = RequestMethod.PATCH)
	@RequiresPermissions("users:role:edit")//权限管理;
	public Result roleUpdate(@RequestBody Role role) {
		Long id = role.getId();
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		if (1 == roleService.updateIgnoreNull(role)) {
			return Result.success();
		}
		return Result.error(-2,"更新失败！");
	}

	/**
	 * 伪删除角色
	 * 不删除记录，只修改del_flag字段值
	 *
	 * @param id
	 * @return
	 */
	@ApiOperation("伪删除角色")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "角色ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@RequiresPermissions("users:role:edit")//权限管理;
	public Result roleVDel(@PathVariable Long id) {
		if (Lang.isEmpty(roleService.fetch(id))) {
			return Result.error(-1,"该角色不存在！");
		}
		roleService.vDelete(id);
		return Result.success();
	}
}
