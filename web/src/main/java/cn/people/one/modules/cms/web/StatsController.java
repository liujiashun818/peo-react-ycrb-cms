package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.front.ArticleStatusVO;
import cn.people.one.modules.cms.model.front.StatsVO;
import cn.people.one.modules.cms.service.impl.StatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by maliwei.tall on 2017/4/12.
 */
@Api(description = "统计")
@RestController
@RequestMapping("/api/cms/stats/")
@Slf4j
public class StatsController {
    @Autowired
    private StatsService statsService;

    @ApiOperation("统计分析列表")
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    @RequiresPermissions("analysis:statistics:view")//权限管理;
    public Result<List<StatsVO>> list(@RequestParam Map<String, String> paramMap) {
        log.info("【 stats 】"+paramMap.toString());
        return Result.success(statsService.queryStats(paramMap));
}
    @ApiOperation("数量")
    @RequestMapping(value = "/count",method = RequestMethod.GET)
    @RequiresPermissions("analysis:statistics:view")//权限管理;
    public Result count(@RequestParam Map<String, String> paramMap) {
        log.info("【 count 】"+paramMap.toString());
        return Result.success(statsService.queryCount(paramMap));
    }

    /**
     * 发稿量统计
     * @param paramMap
     * @return
     */
    @ApiOperation("发稿量统计")
    @RequestMapping(value = "/articleList",method = RequestMethod.GET)
    @RequiresPermissions("analysis:sending:view")//权限管理;
    public Result<List<ArticleStatusVO>> articleList(@RequestParam Map<String, String> paramMap) {
        log.info("【 stats 】"+paramMap.toString());
        return Result.success(statsService.queryArticleStats(paramMap));
    }
}
