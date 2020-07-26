package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.front.CategoryVO;
import cn.people.one.modules.cms.service.ICategoryService;
import cn.people.one.modules.cms.service.IFieldGroupService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by lml on 2016/12/26.
 */
@Api(description = "栏目管理(cms模块)")
@Slf4j
@RestController
@RequestMapping("/api/cms/category")
public class CategoryController {

    @Value("${theone.project.rootId}")
    private Long rootId;

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IFieldGroupService fieldGroupService;

    @ApiOperation("栏目列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("cms:category:view")//权限管理;
    public Result<QueryResultVO<Category>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(categoryService.listPage(pageNumber, pageSize));
    }

    @ApiOperation("栏目详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "栏目ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:category:view")//权限管理;
    public Result<Category> view(@PathVariable Long id) {
        return Result.success(categoryService.fetch(id));
    }

    @ApiOperation("保存栏目")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result<Category> save(@RequestBody Category category) {
        Category category1=categoryService.getCategoryByName(category);
        if(category1!=null){
            return Result.error("栏目名已存在");
        }
        if (!Lang.isEmpty(categoryService.save(category))) {
            return Result.success(category);
        }
        return Result.error("保存失败");
    }

    @ApiOperation("删除栏目")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "栏目ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        if(id == 1){
            return Result.error("顶级菜单不允许删除");
        }
        String isRelation = categoryService.isRelatedToCategory(id);
        if(!"false".equals(isRelation)){
            return Result.error(-1,isRelation);
        }
        if (categoryService.delete(id) > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }


    /**
     * 部分更新
     *
     * @param category
     * @return
     */
    @ApiOperation("部分更新")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result update(@RequestBody Category category) {
        if (Lang.isEmpty(categoryService.fetch(category.getId()))) {
            return Result.error("该栏目不存在");
        }
        if (categoryService.save(category)!=null) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 栏目列表
     *
     * @return
     */
    @ApiOperation("栏目列表")
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    @RequiresPermissions("cms:category:view")//权限管理;
    public Result< List<CategoryVO>> getTree() {
        User user = UserUtils.getUser();
        if(user == null || user.getId()==null){
            return Result.error(Result.NOT_LOGGED_IN_CODE_ERROR,"非登录用户");
        }
        return Result.success(categoryService.getTree(rootId,user.getId()));
    }

    /**
     * 上下线
     *
     * @param id
     * @return
     */
    @ApiOperation("上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "栏目ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/onOff/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result OnOff(@PathVariable Long id) {
        if (Lang.isEmpty(categoryService.fetch(id))) {
            return Result.error("栏目编号有误");
        }
        if (categoryService.updateIgnoreNull(categoryService.changeOnlineStatus(id)) > 0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    /**
     * 根据父栏目ID查询子栏目
     * @param id
     * @return
     */
    @ApiOperation("根据父栏目ID查询子栏目")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "父栏目ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/queryByParentId/{id}", method = RequestMethod.GET)
    public Result<List<Category>> queryByParentId(@PathVariable Long id) {
        if (Lang.isEmpty(categoryService.fetch(id))) {
            return Result.error("栏目编号有误");
        }
        return Result.success(categoryService.queryByParentId(id));
    }

    /**
     * 批量切换上下线
     *
     * @param categoryIds
     * @return
     */
    @ApiOperation("批量切换上下线")
    @RequestMapping(value = "/batchOnOff", method = RequestMethod.POST)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result batchOnOff(@RequestParam String categoryIds) {
        List<String> ids = null;
        if (StringUtils.isNotEmpty(categoryIds)) {
            ids = Arrays.asList(categoryIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                categoryService.updateIgnoreNull(categoryService.changeOnlineStatus(Long.valueOf(id)));
            });
        }
        return Result.success();
    }

    @ApiOperation("批量更新")
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
    @RequiresPermissions("cms:category:edit")//权限管理;
    public Result batchUpdate(@RequestBody List<Category> list) {
        categoryService.batchUpdate(list);
        return Result.success();
    }

    /**
     * 判断是否重名
     * @param name
     * @return
     */
    @ApiOperation("判断是否重名")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "name", value = "栏目名", required = true, paramType = "query")
    })
    @RequestMapping(value = "/isRepeatName",method = RequestMethod.POST)
    public Boolean isRepeatName(@RequestParam String name){
        Boolean isRepeatName=true;
        try {
            isRepeatName=categoryService.isRepeatName(name);
        } catch (Exception e) {
            log.error("判断栏目重名出错===",e);
        }
        return isRepeatName;
    }
}
