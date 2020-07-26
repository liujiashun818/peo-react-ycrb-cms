package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.CategoryModel;
import cn.people.one.modules.cms.service.ICategoryModelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lml on 2017/5/3.
 */
@Api(description = "栏目模型管理(cms模块)")
@RestController
@RequestMapping("/api/cms/category/model")
public class CategoryModelController {

    @Autowired
    private ICategoryModelService categoryModelService;

    @ApiOperation("栏目模型列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    public Result<QueryResultVO<CategoryModel>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(categoryModelService.listPage(pageNumber, pageSize));
    }

    @ApiOperation("栏目模型详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "栏目模型ID", required = true, paramType = "path"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<CategoryModel> view(@PathVariable Long id) {
        return Result.success(categoryModelService.fetch(id));
    }

    @ApiOperation("保存栏目模型")
    @RequestMapping(method = RequestMethod.POST)
    public Result<CategoryModel> save(@RequestBody CategoryModel categoryModel) {
        if (!Lang.isEmpty(categoryModelService.save(categoryModel))) {
            return Result.success(categoryModel);
        }
        return Result.error("保存失败");
    }

    @ApiOperation("删除栏目模型")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "栏目模型ID", required = true, paramType = "path"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable Long id) {
        if (categoryModelService.vDelete(id) > 0) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @ApiOperation("更新栏目模型")
    @RequestMapping(method = RequestMethod.PATCH)
    public Result update(@RequestBody CategoryModel categoryModel) {
        if (categoryModelService.updateIgnoreNull(categoryModel)>0) {
            return Result.success();
        }
        return Result.error("更新失败");
    }

    @ApiOperation("获取所有栏目模型")
    @RequestMapping(value = "/all",method = RequestMethod.GET)
    public Result<List<CategoryModel>> getAll(){
        return Result.success(categoryModelService.getAll());
    }

}
