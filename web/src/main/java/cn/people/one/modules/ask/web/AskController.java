package cn.people.one.modules.ask.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.model.front.PageAskQuestionQueryVO;
import cn.people.one.modules.ask.service.IAskGovernmentService;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(description = "问管理")
@RestController
@RequestMapping("/api/cms/ask")
@Slf4j
public class AskController {
    @Autowired
    private IAskQuestionReplyService iAskQuestionReplyService;
    @Autowired
    private IAskGovernmentService iAskGovernmentService;
    /**
     * 查询问政列表
     * @param pageAskQuestionQueryVO
     * @return
     */
    @ApiOperation("查询问政列表")
    @RequestMapping(value = "searchAskQuestions", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result<QueryResultVO<AskQuestionReply>> list(@RequestBody PageAskQuestionQueryVO pageAskQuestionQueryVO) {
        QueryResultVO<AskQuestionReply> queryResultVO=iAskQuestionReplyService.searchAskQuestions(pageAskQuestionQueryVO);
        return Result.success(queryResultVO);
    }

    /**
     * 问政详情
     * @param id
     * @return
     */
    @ApiOperation("问政详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "问政ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result<Map<String, Object>> getDetailById(@PathVariable Long id) {
        return Result.success(iAskQuestionReplyService.getDetailById(id));
    }

    /**
     * 推荐到文章
     * @param categoryId
     * @param askIds
     * @return
     */
    @ApiOperation("推荐到文章")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "categoryId", value = "栏目ID", required = true, paramType = "path"),
        @ApiImplicitParam(name = "askIds", value = "问政ID", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/recommendToArticle/{categoryId}", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result recommendToArticle(@PathVariable Long categoryId,@RequestParam String askIds) {
        return Result.success(iAskQuestionReplyService.recommendToArticle(categoryId,askIds));
    }

    /**
     * 获取所有本地机构
     * @return
     */
    @ApiOperation("获取所有本地机构")
    @RequestMapping(value = "/getAllLocalGov", method = RequestMethod.GET)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result getAllLocalGov() {
        return Result.success(iAskGovernmentService.getAllLocalGov());
    }

    /**
     * 上线
     * @param id
     * @return
     */
    @ApiOperation("上线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "问政ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/online/{id}", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result online(@PathVariable Long id) {
        return  Result.success(iAskQuestionReplyService.online(id));
    }

    /**
     * 下线
     * @param id
     * @return
     */
    @ApiOperation("下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "问政ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/downline/{id}", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result downline(@PathVariable Long id) {

        return Result.success(iAskQuestionReplyService.downline(id));
    }

    /**
     * 保存排序
     * @param list
     * @return
     */
    @ApiOperation("保存排序")
    @RequestMapping(value = "/saveSortAsk", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result saveSortAsk(@RequestBody List<AskQuestionReply> list) {
        iAskQuestionReplyService.batchUpdate(list);
        return Result.success("操作成功");
    }

    /**
     * 置为头条
     * @param id
     * @return
     */
    @ApiOperation("置为头条")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "问政ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/setTop/{id}", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result setTop(@PathVariable Long id) {
        return Result.success(iAskQuestionReplyService.setTop(id));
    }

    /**
     * 取消头条
     * @param id
     * @return
     */
    @ApiOperation("取消头条")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "问政ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/cancelTop/{id}", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//权限管理;
    public Result cancelTop(@PathVariable Long id) {
        return Result.success( iAskQuestionReplyService.cancelTop(id));
    }

    /**
     * 保存问政
     * @param askQuestionReply
     * @return
     */
    @ApiOperation("保存问政")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @RequiresPermissions("cms:asks:view")//
    public Result save(@RequestBody AskQuestionReply askQuestionReply) {
        iAskQuestionReplyService.update(askQuestionReply);
        return Result.success();
    }
}
