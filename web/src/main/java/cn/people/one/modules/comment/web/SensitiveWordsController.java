package cn.people.one.modules.comment.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.comment.model.SensitiveWords;
import cn.people.one.modules.comment.service.impl.SensitiveWordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
* 敏感词Controller
* @author 周欣
*/
@Api(description = "敏感词管理（comment模块）")
@RestController
@RequestMapping("/api/comment/sensitiveWords/")
@Slf4j
public class SensitiveWordsController {

    @Autowired
    private SensitiveWordsService sensitiveWordsService;

    @ApiOperation("敏感词详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "敏感词ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:comment:view")//权限管理;
    public Result<SensitiveWords> view(@PathVariable Long id) {
        return Result.success(sensitiveWordsService.fetch(id));
    }

    @ApiOperation("保存敏感词")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result<SensitiveWords> save(@RequestBody SensitiveWords sensitiveWords) {
        sensitiveWordsService.save(sensitiveWords);
        return Result.success(sensitiveWords);
    }

    @ApiOperation("更新敏感词")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result<SensitiveWords> updateIgnoreNull(@RequestBody SensitiveWords sensitiveWords) {
        sensitiveWordsService.updateIgnoreNull(sensitiveWords);
        return Result.success(sensitiveWords);
    }

    @ApiOperation("删除敏感词")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "敏感词ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:comment:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(sensitiveWordsService.delete(id));
    }

    @ApiOperation("替换内容里面的敏感词")
    @RequestMapping(value = "replaceSensitiveWordContent", method = RequestMethod.POST)
    public Result<SensitiveWords> replaceSensitiveWordContent(@RequestBody SensitiveWords sensitiveWords) {
        SensitiveWords result = sensitiveWordsService.replaceSensitiveWordContent(sensitiveWords.getSensitiveWordsContent());
        return Result.success(result);
    }

}
