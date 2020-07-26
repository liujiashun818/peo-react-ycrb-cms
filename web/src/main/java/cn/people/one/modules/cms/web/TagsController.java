package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Tags;
import cn.people.one.modules.cms.service.ITagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import cn.people.one.core.base.api.Result;

import java.util.List;

/**
 * Created by sunday on 2018/10/16.
 */
@Api(description = "标签管理(cms模块)")
@RestController
@RequestMapping("/api/cms/tags")
@Slf4j
public class TagsController {

    @Autowired
    private ITagsService tagsService;

    @ApiOperation("标签列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("cms:tags:view")//权限管理;
    public Result<QueryResultVO<Tags>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(tagsService.findSearchPage(pageNumber, pageSize));
    }

    @ApiOperation("查询总数")
    @RequestMapping(value = "queryCount",method = RequestMethod.GET)
    @RequiresPermissions("cms:tags:view")//权限管理;
    public Result queryCount() {
        return Result.success(tagsService.queryCount());
    }

    @ApiOperation("查询所有标签")
    @RequestMapping(value = "queryAll",method = RequestMethod.GET)
    public Result<List<Tags>> queryAll() {
        return Result.success(tagsService.queryAll());
    }

    @ApiOperation("标签详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "标签ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:tags:view")//权限管理;
    public Result<Tags> view(@PathVariable Long id) {
        return Result.success(tagsService.fetch(id));
    }

    @ApiOperation("保存标签")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:tags:edit")//权限管理;
    public Result<Tags> save(@RequestBody Tags tags) {
        if(isExistTagsName(tags)){
            return Result.error("标签名不能重复，请仔细检查是否已经存在该标签名称！");
        }
        tagsService.insert(tags);
        return Result.success(tags);
    }

    @ApiOperation("修改标签")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:tags:edit")//权限管理;
    public Result<Tags> updateIgnoreNull(@RequestBody Tags tags) {
        if(isExistTagsName(tags)){
            return Result.error("标签名不能重复，请仔细检查是否已经存在该标签名称！");
        }
        tagsService.updateIgnoreNull(tags);
        return Result.success(tags);
    }

    @ApiOperation("删除标签")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "标签ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:tags:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        Tags tags = tagsService.fetch(id);
        List<Article> articleList = tagsService.getArticlesByTags(tags);
        if(!Lang.isEmpty(articleList) && articleList.size() > 0){
            return Result.error("被文章使用的标签不能删除!");
        }
        int flag = tagsService.delete(id);
        return Result.success(flag);
    }

    /**
     * 判断是否已经存在该标签
     * @param tags
     * @return
     */
    private boolean isExistTagsName(Tags tags){
        boolean isExist = false;
        List<Tags> tagsList = tagsService.queryAll();
        for(Tags exist: tagsList){
            if(exist.getName().equalsIgnoreCase(tags.getName())){
                if(tags.getId()!=null && !tags.getId().equals(exist.getId())){
                    isExist = true;//标签名已经存在
                    break;
                }else if(tags.getId()==null){
                    isExist = true;//标签名已经存在
                    break;
                }
            }
        }
        return isExist;
    }
}
