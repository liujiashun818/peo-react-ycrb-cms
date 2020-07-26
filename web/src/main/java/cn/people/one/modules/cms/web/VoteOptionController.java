package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.service.impl.VoteOptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by lml on 2017/3/21.
 */
@Api(description = "调查选项管理(cms模块)")
@RestController
@RequestMapping("/api/cms/voteOption")
@Slf4j
public class VoteOptionController {

	@Autowired
	private VoteOptionService voteOptionService;

    @ApiOperation("删除选项")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "选项ID", required = true, paramType = "path")
    })
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public Result delete(@PathVariable Long id) {
		if (voteOptionService.delete(id) > 0) {
			return Result.success();
		} else {
			return Result.error("删除选项错误");
		}
	}
}
