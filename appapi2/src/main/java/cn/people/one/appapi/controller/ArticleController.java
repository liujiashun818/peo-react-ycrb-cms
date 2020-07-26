package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.provider.ad.model.AdVo;
import cn.people.one.appapi.service.ArticleService;
import cn.people.one.appapi.vo.*;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.service.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by wilson on 2018-10-10.
 */
@Slf4j
@Api(value = "新闻API", tags = {"新闻相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/articles")
public class ArticleController {

    @Autowired
    @Qualifier("articleServiceV2")
    private ArticleService articleService;

    @Autowired
    private IArticleService iArticleService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @ApiOperation("获取指定分类下的新闻列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "categoryId", value = "categoryId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "headPageSize", value = "头部列表页大小", required =false, defaultValue = "6",paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/{categoryId}")
	public ResultVO3<ArticleListVO> list(@PathVariable("categoryId") Long categoryId,
                         @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                         @RequestParam(value = "headPageSize", required = false, defaultValue = "6") Integer headPageSize,
                         @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        return articleService.articleList(categoryId, size, pageToken,headPageSize);
    }

    @ApiOperation("新闻详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "articleId", required = true, paramType = "path")
    })
    @GetMapping("/detail/{articleId}")
	 public ResultVO3<ArticleDetailVO> get(@PathVariable("articleId") Long articleId,
                                           HttpServletRequest request) {

        String platform = request.getParameter("platform"); // app平台 empty时（h5） 统计访问量
        ResultVO3<ArticleDetailVO> rtn = articleService.detail(articleId,null);
        if(rtn.getCode() == 0) {
            // 获取成功
            ArticleDetailVO vo = rtn.getItem();
            if(StringUtils.isEmpty(platform) || "link".equals(vo.getType()) || "image".equals(vo.getType())) {
                // h5请求 或者 图集、外链等新闻直接统计
                // 更新访问量
                Article article = iArticleService.fetch(articleId);
                if(article!=null) {
                    article.setHits(article.getHits()==null?1:(article.getHits()+1));
                    iArticleService.updateIgnoreNull(article);
                }
            }
        }
        return rtn;
    }

    @ApiOperation("点赞OR取消点赞")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章ID", required = true),
            @ApiImplicitParam(name = "likeOrNot", value = "点赞OR取消点赞(1:点赞，-1：取消点赞)", required = true)
    })
    @PostMapping("/likeArticleOrNot")
    public ResultVO2 likeArticleOrNot(@RequestParam Long articleId,
                                     @RequestParam Integer likeOrNot) {
        try {
            if (likeOrNot != 1 && likeOrNot != -1) {
                return ResultVO2.result(CodeConstant.PARAM_ERROR);
            }

            this.articleService.likeArticleOrNot(articleId, likeOrNot);
            return ResultVO2.result(CodeConstant.SUCCESS);
        } catch (Exception e) {
            log.error("点赞OR取消点赞接口出错====》likeArticleOrNot", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

    /**
     * 更新帮的爱心数
     * @param articleId
     * @param likeOrNot
     * @return
     */
    @ApiOperation("更新帮的爱心数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "likeOrNot", value = "点赞OR取消点赞(1:点赞，-1：取消点赞)", required = true, paramType = "path")
    })
    @GetMapping("/saveHelpLikes/{articleId}/{likeOrNot}")
    public ResultVO2 saveHelpLikes(@PathVariable Long articleId,
                                      @PathVariable Integer likeOrNot) {
        try {
            if (likeOrNot != 1 && likeOrNot != -1) {
                return ResultVO2.result(CodeConstant.PARAM_ERROR);
            }

            this.articleService.likeArticleOrNot(articleId, likeOrNot);
            return ResultVO2.result(CodeConstant.SUCCESS);
        } catch (Exception e) {
            log.error("更新帮的爱心数====》saveHelpLikes", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
    @ApiOperation("根据关键字搜索文章")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWord", value = "关键字", paramType = "query"),
            @ApiImplicitParam(name = "time", value = "时间", paramType = "query"),
            @ApiImplicitParam(name = "category", value = "栏目名称", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/searchArticle")
    public ResultVO3<List<ArticleVO>> searchArticle(@RequestParam(value = "keyWord",required = false) String keyWord,
                                  @RequestParam(value = "time",required = false) String time,
                                  @RequestParam(value = "category",required = false) String category,
                                  @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                  @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        //参数处理
        /**
         * 参数处理====start
         */
        if (size == null) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        Integer page;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }

        if(Lang.isEmpty(keyWord)){
            keyWord = "";
        }

        if("all".equalsIgnoreCase(time)){
            time = null;
        }

        /**
         * 参数处理====end
         */
        try {
            return articleService.searchArticle(keyWord, time, category, page, size);
        } catch (Exception e) {
            log.error("根据关键字搜索文章出错===》searchArticle", e);
            return ResultVO3.result(CodeConstant.ERROR);
        }
    }


    /**
     * 广告统计
     * @param adVo
     * @return
     */
    @ApiOperation("广告统计")
    @PostMapping("/advStatus")
    public Result advStatus(@RequestBody AdVo adVo){
        try {
            return articleService.advStatus(adVo);
        } catch (Exception e) {
            log.error("广告统计异常");
            return Result.error("广告统计异常");
        }
    }

    @ApiOperation("新闻详情预览")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "articleId", required = true, paramType = "path")
    })
    @GetMapping("/detailPreview/{articleId}")
    public ResultVO3<ArticleDetailVO> detailPreview(@PathVariable("articleId") String articleId) {
        Long id;
        try {
            try {
                id=Long.valueOf(redisTemplate.opsForValue().get(articleId)+"");
            } catch (NumberFormatException e) {
                return ResultVO3.error("地址已过期不能访问");
            }
            ResultVO3<ArticleDetailVO> rtn = articleService.detail(id,Article.STATUS_AUDIT);
            return rtn;
        } catch (Exception e) {
            log.error("查询文章详情出错",e);
            return ResultVO3.error("查询文章详情出错");
        }
    }

    /**
     * 我的爱心
     * @param size
     * @param pageToken
     * @param userId
     * @return
     */
    @ApiOperation("我的爱心")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "size", value = "页大小", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "页码", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "userId", required = true,paramType = "query")
    })
    @GetMapping("/myHelpList")
    public ResultVO3<ArticleListVO> myHelpList(@RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                               @RequestParam(value = "pageToken", required = false, defaultValue = "1") Integer pageToken,
                                               @RequestParam(value = "userId") String userId) {
        if (size == null) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        if (pageToken == null) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        if (pageToken < 1) {
            pageToken=1;
        }
        try {
            return articleService.myHelpList(userId,size,pageToken);
        } catch (Exception e) {
            log.error("获取我的爱心列表失败",e);
            return ResultVO3.error("获取我的爱心列表失败");
        }
    }
}
