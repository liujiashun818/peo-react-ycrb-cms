package cn.people.one.modules.activitycode.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.activitycode.model.front.ActivityVO;
import cn.people.one.modules.activitycode.service.IActivityCodeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sunday on 2017/4/13.
 */
@Api(description = "邀请码管理")
@RestController
@RequestMapping("/api/activityCode")
@Slf4j
public class ActivityCodeController {

    private static JsonMapper jsonMapper = JsonMapper.defaultMapper();

    @Autowired
    private IActivityCodeService activityCodeService;

    @RequestMapping(method = RequestMethod.POST,value = "/activityAdd")
    @RequiresPermissions("client:activityCode:edit")//权限管理;
    public Result activityAdd(@RequestBody ActivityVO activity) {
        if (Lang.isEmpty(activityCodeService.activityAdd(activity))) {
            return Result.error(-1,"存储失败");
        }
        return Result.success("0","存储成功");
    }

    @RequestMapping(method = RequestMethod.GET,value = "/showActivityCode")
    @RequiresPermissions("client:activityCode:view")//权限管理;
    public Result showActivityCode(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam String type) {
        return Result.success(activityCodeService.showActivityCode(pageNumber,pageSize,type));
    }

    @RequestMapping(method = RequestMethod.GET,value = "/statActivityCode")
    @RequiresPermissions("client:activityCode:view")//权限管理;
    public Result statActivityCode(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        return Result.success(activityCodeService.statActivityCode(pageNumber,pageSize));
    }
}
