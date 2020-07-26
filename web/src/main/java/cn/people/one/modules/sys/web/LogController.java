package cn.people.one.modules.sys.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.sys.model.Log;
import cn.people.one.modules.sys.model.front.LogVO;
import cn.people.one.modules.sys.service.ILogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日志Controller
 *
 * @author cuiyukun
 */
@Api(description = "日志管理(sys模块)")
@RestController
@RequestMapping("/api/sys/log")
@Slf4j
public class LogController {

	@Autowired
	private ILogService logService;

    @ApiOperation("日志列表")
	@RequestMapping(method = RequestMethod.GET)
    @RequiresPermissions("analysis:log:view")//权限管理;
    public Result<QueryResultVO<Log>> list(LogVO logVO){
	    return Result.success(logService.page(logVO));
    }
}
