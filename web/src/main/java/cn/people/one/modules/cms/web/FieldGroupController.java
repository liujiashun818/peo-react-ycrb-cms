package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.FieldGroup;
import cn.people.one.modules.cms.service.IFieldGroupService;
import cn.people.one.modules.cms.service.IFieldService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字段组Controller
 *
 * @author lml
 */
@Api(description = "字段组管理(cms模块)")
@RestController
@RequestMapping("/api/cms/field/group/")
@Slf4j
public class FieldGroupController {

    @Autowired
    private IFieldGroupService fieldGroupService;

    @Autowired
    private IFieldService fieldService;

    @RequestMapping(method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(fieldGroupService.listPage(pageNumber, pageSize));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result view(@PathVariable Long id) {
        FieldGroup fieldGroup = fieldGroupService.fetch(id);
        return Result.success(fieldGroup);
    }

    @RequestMapping(method = RequestMethod.POST)
    //@RequiresPermissions("sys:field:edit")//权限管理;
    public Result save(@RequestBody FieldGroup fieldGroup) {
        fieldGroupService.save(fieldGroup);
        return Result.success(fieldGroup);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
   // @RequiresPermissions("sys:field:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        return Result.success(fieldGroupService.delete(id));
    }

    @RequestMapping(value = "/batchDelete",method = RequestMethod.POST)
    public Result batchDelete(@RequestBody List<String> ids) {
        ids.forEach(id->fieldGroupService.delete(Long.valueOf(id)));
        return Result.success();
    }

    @RequestMapping(value = "/getListByGroupName", method = RequestMethod.GET)
    //@RequiresPermissions("sys:field:view")//权限管理;
    public Result getListByName(@RequestParam String name) {
        return Result.success(fieldGroupService.getListByName(name));
    }

    /**
     * 部分更新
     *
     * @param fieldGroup
     * @return
     */
    /*@RequestMapping(method = RequestMethod.PATCH)
    //@RequiresPermissions("sys:field:edit")//权限管理;
    public Result update(@RequestBody FieldGroup fieldGroup) {
        fieldGroupService.updateIgnoreNull(fieldGroup);
        return Result.success("success");
    }*/

}
