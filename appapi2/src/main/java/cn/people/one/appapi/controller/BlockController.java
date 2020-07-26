package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.ArticleService;
import cn.people.one.appapi.vo.ArticleVO;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.ResultVO4;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wilson on 2018-10-10.
 */
@Slf4j
@Api(value = "专题区块API", tags = {"专题区块相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/blocks")
public class BlockController {

    @Autowired
    @Qualifier("articleServiceV2")
    private ArticleService articleService;

    @ApiOperation("获取区块下文章列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "blockId", value = "blockId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 40100, message = "ID错误，未找到对应的菜单")
    })
    @GetMapping("/{blockId}")
    public ResultVO4<ArticleVO> blocks(@PathVariable("blockId") Long blockId,
                                       @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                       @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        /**
         * 参数处理====start
         */
        if (blockId == null) {
            return ResultVO4.result(CodeConstant.ERROR);
        }
        if (size == null) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }
        Integer page;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO4.result(CodeConstant.PARAM_ERROR);
        }
        /**
         * 参数处理====end
         */
        try {
            return articleService.getArticlePageByBlockId(blockId, size, page);
        } catch (Exception e) {
            log.error("根据区块id获取区块下文章列表出错 -> api/v2/blocks/{blockId}", e);
            return ResultVO4.result(CodeConstant.SYSTEM_ERROR);
        }
    }

}
