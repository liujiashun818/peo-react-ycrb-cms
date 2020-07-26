package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.cms.model.Field;
import cn.people.one.modules.cms.service.impl.FieldService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段描述Controller
 *
 * @author lml
 */
@Api(description = "字段管理(cms模块)")
@RestController
@RequestMapping("/api/cms/field/")
@Slf4j
public class FieldController {

    @Autowired
    private FieldService fieldService;

    @RequestMapping(method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(fieldService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result view(@PathVariable Long id) {
        return Result.success(fieldService.fetch(id));
    }

    @RequestMapping(method = RequestMethod.POST)
  //  @RequiresPermissions("sys:field:edit")//权限管理;
    public Result save(@RequestBody Field field) {
        fieldService.save(field);
        return Result.success(field);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    //@RequiresPermissions("sys:field:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(fieldService.delete(id));
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST)
    public Result batchDelete(@RequestBody List<Long> ids) {
        if(!Lang.isEmpty(ids)){
            ids.forEach(id->fieldService.delete(id));
        }
        return Result.success();
    }

    /**
     * 部分更新
     *
     * @param field
     * @return
     */
    @RequestMapping(method = RequestMethod.PATCH)
    //@RequiresPermissions("sys:field:edit")//权限管理;
    public Result update(@RequestBody Field field) {
        fieldService.updateIgnoreNull(field);
        return Result.success("success");
    }

    @RequestMapping(value = "/exist", method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result slugExist(String slug) {
        if(!StringUtils.isNotBlank(slug)){
            return Result.error(-1,"参数为空");
        }
        return Result.success(fieldService.slugExist(slug));
    }

}
