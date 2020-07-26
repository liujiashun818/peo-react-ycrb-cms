package cn.people.one.modules.user.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.front.NavOfficeVO;
import cn.people.one.modules.user.service.IOfficeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理下机构管理Controller
 *
 * @author cuiyukun
 */
@Api(description = "机构管理(user模块)")
@RestController
@RequestMapping("/api/users/office/")
@Slf4j
public class OfficeController {

    @Autowired
    private IOfficeService officeService;

    @Value("${theone.project.rootId}")
    private Long rootId;

    /**
     * 获取机构树
     *
     * @return
     */
    @ApiOperation("获取所有机构树")
    @RequestMapping(value = {"/tree"}, method = RequestMethod.GET)
//    @RequiresPermissions("users:office:view")//权限管理;
    public Result<List<NavOfficeVO>> tree() {
        return Result.success(officeService.getOfficeTree(rootId));
    }


    /**
     * 获取机构树
     *
     * @return
     */
    @ApiOperation("获取所有可用机构树")
    @RequestMapping(value = {"/treeView"}, method = RequestMethod.GET)
//    @RequiresPermissions("users:office:view")//权限管理;
    public Result<List<NavOfficeVO>> treeView() {
        return Result.success(officeService.getOfficeTree(rootId, Office.STATUS_ONLINE));
    }

    @ApiOperation("根据机构ID获取机构下机构树")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "机构ID", required = true, paramType = "path"),
    })
    @RequestMapping(value = {"/treeView/{id}"}, method = RequestMethod.GET)
    @RequiresPermissions("users:office:view")//权限管理;
    public Result<List<NavOfficeVO>> treeView(@PathVariable Long id) {
        return Result.success(officeService.getOfficeTree(id, Office.STATUS_ONLINE));
    }

    /**
     * 获取机构列表
     *
     * @param pageSize
     * @return
     */
    @ApiOperation("获取机构列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = {"/list"}, method = RequestMethod.GET)
    @RequiresPermissions("users:office:view")//权限管理;
    public Result<QueryResultVO<Office>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(officeService.listPage(pageNumber, pageSize));
    }

    /**
     * 根据id获取机构
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id获取机构")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "机构ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("users:office:edit")//权限管理;
    public Result getOfficeById(@PathVariable Long id) {
        if (Lang.isEmpty(officeService.fetch(id))) {
            return Result.error(-1,"该机构不存在！");
        }
        return Result.success(officeService.fetch(id));
    }

    /**
     * 添加一条机构
     *
     * @param office
     * @return
     */
    @ApiOperation("添加一条机构记录")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @RequiresPermissions("users:office:edit")//权限管理;
    public Result officeAdd(@RequestBody Office office) {
        String name = office.getName();
        if (!Lang.isEmpty(officeService.fetch(name))) {
            return Result.error(-1,"该机构已存在！");
        }
        officeService.save(office);
        return Result.success();
    }

    /**
     * 伪删除机构
     * 不删除记录，只修改del_flag字段值
     *
     * @param id
     * @return
     */
    @ApiOperation("伪删除机构")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "机构ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("users:office:edit")//权限管理;
    public Result officeVDel(@PathVariable Long id) {
        if (Lang.isEmpty(officeService.fetch(id))) {
            return Result.error(-1,"该机构不存在！");
        }
        officeService.vDelete(id);
        return Result.success();
    }

    /**
     * 部分更新
     *
     * @param office
     * @return
     */
    @ApiOperation("更新机构")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("users:office:edit")//权限管理;
    public Result update(@RequestBody Office office) {
        if (Lang.isEmpty(officeService.fetch(office.getId()))) {
            return Result.error(-1,"该机构不存在！");
        }
        if (1 == officeService.updateIgnoreNull(office)) {
            return Result.success();
        }
        return Result.error(-2,"更新失败！");
    }
}
