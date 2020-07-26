package cn.people.one.modules.user.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Controller
 *
 * @author cuiyukun
 */
@Api(description = "用户管理(users模块)")
@RestController
@RequestMapping("/api/users/user")
@Slf4j
public class UserController {

	@Autowired
	private IUserService userService;

    @Value("${cas.server}")
    private String casServer;

    @Value("${cas.client}")
    private String casClient;

    @Value("${theone.sso}")
    private Boolean sso;

	/**
	 * 用户列表获取
	 */
    @ApiOperation("用户列表获取")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
	@RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    @RequiresPermissions("users:user:view")//权限管理;
	public Result<QueryResultVO<User>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        QueryResultVO<User> users = userService.listPage(pageNumber, pageSize);
        List list = users.getList();
        User currentUser = UserUtils.getUser();
        if (!Lang.isEmpty(currentUser)) {
            if (!currentUser.isAdmin()) {
                list.remove(0);
                users.setList(list);
            }
        }
		return Result.success(users);
	}

	/**
	 * 根据id获取用户信息
	 */
    @ApiOperation("根据id获取用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("users:user:view")//权限管理;
	public Result<User> getUserById(@PathVariable Long id) {
		if (Lang.isEmpty(userService.fetch(id))) {
			return Result.error(-1,"该账号不存在！");
		}
		return Result.success(userService.fetch(id));
	}

    /**
     * 获取当前用户的菜单permission
     * @return
     */
    @ApiOperation("获取当前用户的菜单permission")
	@RequestMapping(value = "/currentPermission", method = RequestMethod.GET)
    @RequiresPermissions("users:user:view")//权限管理;
    public Result getCurrentPermission() {
        User user = UserUtils.getUser();
        if (Lang.isEmpty(user.getUsername())){
            return Result.error(-1, "当前用户为空");
        }
        UserUtils.getCurrentPermission(user);
        return Result.success(user.getPermissions());
    }

	/**
	 * 根据username登录名获取用户信息
	 */
    @ApiOperation("根据username登录名获取用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "username", value = "username登录名", required = true, paramType = "query")
    })
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	@RequiresPermissions("users:user:view")//权限管理;
	public Result<User> getUserByUsername(@RequestParam  String username) {
		if (Lang.isEmpty(userService.fetch(username))) {
			return Result.error(-1,"该账号不存在！");
		}
		return Result.success(userService.fetch(username));
	}

    @ApiOperation("获取当前用户")
	@RequestMapping(value = "/current", method = RequestMethod.GET)
    public Result<User> getCurrentUser() {
        User user = null;
        try {
            user = UserUtils.getUser();
        } catch (UnavailableSecurityManagerException e) {
            log.warn("用户未登陆");
        }
        if (!Lang.isEmpty(user)){
            if (!Lang.isEmpty(user.getUsername())) {
                user.setRoleList(null);
                return Result.success(user);
            }
        }
        //单点登录使用下列返回
        if(sso){
            Map<String, String> map = new HashMap<>();
            map.put("direct", casServer + "/login?service=" + casClient + "/shiro-cas");
            return new Result(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆", map);
        }else {
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR, "用户未登陆");
        }

    }

	/**
	 * 添加用户
	 */
    @ApiOperation("添加用户")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("users:user:edit")//权限管理;
	public Result userAdd(@RequestBody User user) {
		if(!StringUtils.isNotBlank(user.getUsername())){
            return Result.error(-1,"登录名不能为空！");
        }
		if (!Lang.isEmpty(userService.fetch(user.getUsername()))) {
			return Result.error(-2,"该账号已存在！");
		}
        if (!UserUtils.passwordFormat(user.getPassword())) {
            return Result.error(-3, "密码必须包含字母和数字并大于8位！");
        }
        if (!Lang.isEmpty(userService.save(user))) {
            return Result.success();
        }
        return Result.error(-3, "添加失败！");
	}

	/**
	 * 管理员修改用户信息
	 */
    @ApiOperation("管理员修改用户信息")
	@RequestMapping(value = "/update", method = RequestMethod.PATCH)
    @RequiresPermissions("users:user:edit")//权限管理;
	public Result userUpdate(@RequestBody User user) {
        if(!StringUtils.isNotBlank(user.getUsername())){
            return Result.error(-1,"登录名不能为空！");
        }
		if (Lang.isEmpty(userService.fetch(user.getId()))) {
			return Result.error(-2,"该账号不存在！");
		}
        if (!UserUtils.passwordFormat(user.getPassword())) {
            return Result.error(-3, "密码必须包含字母和数字并大于8位！");
        }
		if (1 == userService.updateUser(user)) {
			return Result.success();
		}
		return Result.error(-3,"更新失败！");
	}

    /**
     * 个人中心修改密码
     * @param user
     * @return
     */
    @ApiOperation("个人中心修改密码")
    @Transactional
    @RequestMapping(value = "/currentUpdate", method = RequestMethod.PATCH)
//    @RequiresPermissions("users:user:edit")//权限管理;
    public Result currentUpdate(@RequestBody User user) {
        User oriUser = userService.fetch(userService.fetch(user.getId()).getUsername());
        if (Lang.isEmpty(oriUser)) {
            return Result.error(-2,"该账号不存在！");
        }
        Integer match = UserUtils.validatePassword(user.getPassword(), oriUser.getPassword());
        if (match != 0) {
            return Result.error(-1, "原密码有误！");
        }
        if (!UserUtils.passwordFormat(user.getNewPassword())) {
            return Result.error(-3, "新密码必须包含字母和数字并大于8位！");
        }
        user.setPassword(UserUtils.encodePassword(user.getNewPassword()));
        user.setNewPassword(null);
        if (1 == userService.updateIgnoreNull(user)) {
            return Result.success();
        }
        return Result.error(-3,"更新失败！");
    }

	/**
	 * 伪删除用户
	 * 不删除记录，只修改del_flag字段值
	 */
    @ApiOperation("伪删除用户")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "用户ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("users:user:edit")//权限管理;
	public Result userVDel(@PathVariable Long id) {
		if (Lang.isEmpty(userService.fetch(id))) {
			return Result.error(-1,"该账号不存在！");
		}
		userService.vDelete(id);
		return Result.success();
	}

    @RequestMapping(value = "/all",method = RequestMethod.GET)
    @RequiresPermissions("users:user:view")//权限管理;
	public Result<List<User>> findAll(){
	     return Result.success(userService.findAll());
    }
}
