package cn.people.one.modules.cms.web;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.base.entity.BaseModel;
import cn.people.one.modules.cms.message.SendMessage;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.MetaParamVO;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ICategoryService;
import cn.people.one.modules.user.utils.UserUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 文章管理
 * Created by lml on 2016/12/10.
 */
@Api(description = "文章管理(cms模块)")
@RestController
@RequestMapping("/api/cms/article")
@Slf4j
public class ArticleController {

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 查询完整列表
     */
    @ApiOperation("查询完整列表")
    @RequestMapping(value = "searchPage", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<Article>> list(ArticleVO article) {
        try {
            QueryResultVO<Article> queryResultVO= articleService.findSearchPage(article);
            return Result.success(queryResultVO);
        } catch (Exception e) {
            log.error("查询文章列表出错===/searchPage",e);
            return Result.error("查询失败");
        }
    }

    @ApiOperation("查询列表")
    @RequestMapping(value = "referPage", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<Article>> referPage(ArticleVO article) {
        return Result.success(articleService.findReferPage(article));
    }


    /**
     * 文章详情（带扩展字段）
     *
     * @param id
     * @return
     */
    @ApiOperation("文章详情（带扩展字段）")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<Article> view(@PathVariable Long id) {
        if (id == null) {
            return Result.error("传入参数异常");
        }
        Article article = articleService.getArticleDetails(id);
        if (null == article) {
            return Result.error("获得的文章为空");
        }
        return Result.success(article);
    }

    /**
     * 上下线
     */
    @ApiOperation("上下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/onOff/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result OnOff(@PathVariable Long id) {
        articleService.changeOnlineStatus(id);
        SendMessage.sendArticle(id);
        return Result.success();
    }

    /**
     * 保存文章
     */
    @ApiOperation("保存文章")
    @RequestMapping(method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result save(@RequestBody Article article) {
        //for founder api
        try {
            article.setDoc_editor(UserUtils.getUser().getName());
            article.setDoc_author(article.getAuthors());
            if (Lang.isEmpty(articleService.save(article))) {
                return Result.error("存储失败");
            }
        } catch (Exception e) {
            log.error("存储文章出错===/api/cms/article",e);
            return Result.error("存储文章失败");
        }
        SendMessage.sendArticle(article.getId());
        return Result.success();
    }

    /**
     * 引用批量保存文章
     */
    @ApiOperation("引用批量保存文章")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "categoryId", value = "栏目ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "block", value = "区块ID", required = true, paramType = "query")
    })
    @RequestMapping(value = "/batchSave", method = RequestMethod.POST)
//    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result batchSave(@RequestBody List<Article> list, @RequestParam Long categoryId, @RequestParam Integer block) {
        if (categoryService.fetch(categoryId) == null) {
            return Result.error("编号无效");
        }
        articleService.batchInsert(list, categoryId, block);
        List<Long> ids = list.stream().map(BaseModel::getId).collect(Collectors.toList());
        ids.forEach(id -> {
            SendMessage.sendArticle(id);
        });
        return Result.success();
    }

    /**
     * 部分修改文章
     */
    @ApiOperation("部分修改文章")
    @RequestMapping(method = RequestMethod.PATCH)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result update(@RequestBody Article article) {
        Article origin = articleService.fetch(article.getId());//修改前的文章
        if (Lang.isEmpty(articleService.save(article))) {
            return Result.error("存储失败");
        }
        if (!origin.getCategoryId().equals(article.getCategoryId())) {//栏目id变了
            SendMessage.sendArticleList(origin.getCategoryId());
        }
        SendMessage.sendArticle(article.getId());
        return Result.success();
    }

    /**
     * 删除文章
     */
    @ApiOperation("删除文章")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result delete(@PathVariable Long id) {
        if (articleService.del(id).getDelFlag().equals(Article.STATUS_DELETE)) {
            SendMessage.sendArticle(id);
            return Result.success();
        }
        return Result.error("删除失败");
    }

    /**
     * 批量切换上下线
     */
    @ApiOperation("批量切换上下线")
    @RequestMapping(value = "/batchOnOff", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result batchOnOff(@RequestParam String articleIds) {
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                articleService.changeOnlineStatus(Long.valueOf(id));
                SendMessage.sendArticle(Long.valueOf(id));
            });
        }
        return Result.success();
    }

    /**
     * 批量删除
     * @param articleIds
     * @return
     */
    @ApiOperation("批量删除")
    @RequestMapping(value = "batchDel",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")
    public Result batchDel(@RequestParam String articleIds){
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                articleService.del(Long.valueOf(id));
                SendMessage.sendArticle(Long.valueOf(id));
            });
        }
        return Result.success();
    }

    /**
     * 加入扩展字段
     */
    @ApiOperation("加入扩展字段")
    @RequestMapping(value = "/addMetaData", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    @ResponseBody
    public void addMetaData(@RequestBody MetaParamVO metaDataVO) {
        articleService.addMetaData(metaDataVO.getArticleId(), metaDataVO.getFieldCode(), metaDataVO.getFieldValue());
    }

    @ApiOperation("批量更新")
    @RequestMapping(value = "/batchUpdate", method = RequestMethod.POST)
//    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result batchUpdate(@RequestBody List<Article> list) {
        articleService.batchUpdate(list);
        List<Long> ids = list.stream().map(article -> article.getId()).collect(Collectors.toList());
        ids.forEach(id -> SendMessage.sendArticle(id));
        return Result.success();
    }

    /**
     * 给文章设置固定位置
     *
     * @param id
     * @param position
     * @return
     */
    @ApiOperation("给文章设置固定位置")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "position", value = "位置号", required = true, paramType = "query"),
    })
    @RequestMapping(value = "setPosition", method = RequestMethod.GET)
    public Result setPosition(@RequestParam Long id, @RequestParam Integer position) {
        try {
            //判断文章所属栏目下文章数===》判断文章可以设置的位置
            int effectRow=this.articleService.setPosition(id, position);
            if(effectRow==0){
                return Result.error("栏目下没有足够文章，不能设置此位置");
            }
            if(effectRow==1){
                return Result.success();
            }
        } catch (Exception e) {
            log.error("给文章设置固定位置出错----------》setPosition", e);
            return Result.error("设置失败");
        }
        return Result.success();
    }

    /**
     * 获取银川日报入住系统的栏目列表
     * @return
     */
    @ApiOperation("获取银川日报入住系统的栏目列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "articleId", value = "文章ID", required = true, paramType = "query")
    })
    @RequestMapping(value = "/getPublicCmsCategoryList", method = RequestMethod.GET)
    public Result<Map<String,Object>> getPublicCmsCategoryList(String articleId){
        try {
            List<Category> categoryList = articleService.getPublicCmsCategoryList();
            Map<String,Object> resultMap=new HashMap<>();
            resultMap.put("categoryList",categoryList);
            resultMap.put("articleId",articleId);
            return Result.success(resultMap);
        } catch (Exception e) {
            log.error("获取山西日报入住系统栏目树出错====/api/cms/article/getPublicCmsCategoryList",e);
            return Result.error("获取山西日报入住系统栏目树失败");
        }
    }
    /**
     * 同步文章到山西入住系统
     * @param articleId
     * @param syncCategoryId
     */
    @ApiOperation("同步文章到山西入住系统")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "articleId", value = "文章ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "syncCategoryId", value = "同步栏目ID", required = true, paramType = "query")
    })
    @RequiresPermissions("cms:articles:edit")
    @RequestMapping(value = "syncArticle",method = RequestMethod.POST)
    public Result syncArticle(Long articleId,Integer syncCategoryId){
        try {
            String status=articleService.syncArticle2PublicCms(articleId,syncCategoryId);
            if("1".equals(status)){
                return Result.success();
            }
            return Result.error("同步文章到山西入住系统失败");
        } catch (Exception e) {
            log.error("同步文章到山西入住系统出错====/api/cms/article/syncArticle",e);
            return Result.error("同步文章到山西入住系统失败");
        }
    }

    /**
     * 获取入住系统文章分页列表
     * @param title  文章标题
     * @param pageNumber 当前页码
     * @param pageSize 每页显示条数
     * @return
     */
    @ApiOperation("获取入住系统文章分页列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "title", value = "标题", required = false, paramType = "query"),
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = "/getPubliccmsArticle",method = RequestMethod.GET)
    public Result<QueryResult> getPubliccmsArticle(@RequestParam(required = false) String title, @RequestParam Integer pageNumber, @RequestParam Integer pageSize){
        try {
            Map paramMap=new HashMap();
            paramMap.put("title",title);
            paramMap.put("pageNumber",pageNumber);
            paramMap.put("pageSize",pageSize);
            QueryResult queryResult=this.articleService.getPubliccmsArticle(paramMap);
            return Result.success(queryResult);
        } catch (Exception e) {
            log.error("获取入住系统文章列表出错====/api/cms/article/getPubliccmsArticle",e);
            return Result.error("获取入住系统文章列表失败！");
        }
    }

    /**
     * 获取政务文章保存到cms
     * @return
     */
    @ApiOperation("获取政务文章保存到cms")
    @RequestMapping(value = "/savePubliccmsArticle",method = RequestMethod.POST)
    public Result savePubliccmsArticle(@RequestBody Map paramMap){

        try {
            Long categoryId= Long.valueOf(paramMap.get("categoryId")+"");
            String articleIds= (String) paramMap.get("articleIds");
            String block=paramMap.get("block")+"";
            articleService.savePubliccmsArticle(categoryId,articleIds,block);
            return Result.success();
        } catch (Exception e) {
            log.error("保存出错",e);
            return Result.error("保存失败");
        }
    }
    /**
     * 文章预览分享地址
     * @return
     */
    @ApiOperation("文章预览分享地址")
    @RequestMapping(value = "getArticlePreviewUrl",method = RequestMethod.POST)
    public Result getArticlePreviewUrl(@RequestParam Long id) {
        return Result.success(articleService.getArticlePreviewUrl(id));
    }


    /**
     * 回收站还原至下线
     */
    @ApiOperation("回收站还原至下线")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/OffLine/{id}", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result OffLine(@PathVariable Long id) {
        articleService.changeOffLineStatus(id);
        SendMessage.sendArticle(id);
        return Result.success();
    }

    /**
     * 批量还原至下线
     */
    @ApiOperation("回收站批量还原至下线")
    @RequestMapping(value = "/batchOffLine", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result batchOffLine(@RequestParam String articleIds) {
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                articleService.changeOffLineStatus(Long.valueOf(id));
                SendMessage.sendArticle(Long.valueOf(id));
            });
        }
        return Result.success();
    }

    /**
     * 提前发布接口
     * @param id
     * @return
     */
    @ApiOperation("提前发布接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/advancePublish/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result advancePublish(@PathVariable Long id) {
        articleService.advancePublish(id);
        return Result.success();
    }

    /**
     * 取消发布
     * @param id
     * @return
     */
    @ApiOperation("取消发布")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "文章ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/cancelPublish/{id}", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:edit")//权限管理;
    public Result cancelPublish(@PathVariable Long id) {
        articleService.cancelPublish(id);
        return Result.success();
    }


    /**
     * 批量提前发布
     * @param articleIds
     * @return
     */
    @ApiOperation("批量提前发布")
    @RequestMapping(value = "batchAdvancePublish",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")
    public Result batchAdvancePublish(@RequestParam String articleIds){
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                articleService.advancePublish(Long.valueOf(id));
            });
        }
        return Result.success();
    }

    /**
     * 批量取消发布
     * @param articleIds
     * @return
     */
    @ApiOperation("批量取消发布")
    @RequestMapping(value = "batchCancelPublish",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")
    public Result batchCancelPublish(@RequestParam String articleIds){
        List<String> ids = null;
        if (StringUtils.isNotEmpty(articleIds)) {
            ids = Arrays.asList(articleIds.split(","));
        }
        if (null != ids && ids.size() > 0) {
            ids.forEach(id -> {
                articleService.cancelPublish(Long.valueOf(id));
            });
        }
        return Result.success();
    }

    /**
     * 设置定时发布
     * @param articleVO
     * @return
     */
    @ApiOperation("设置定时发布")
    @RequestMapping(value = "fixedPublish",method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:edit")
    public Result fixedPublish(@RequestBody ArticleVO articleVO){
        try{
            articleService.fixedPublish(articleVO);
            return Result.success();
        }catch (Exception e){
            log.error("设置定时发布时间失败",e);
            return Result.error("设置定时发布时间失败");
        }
    }

    /**
     * 相关推荐文章添加列表
     * @param article
     * @return
     */
    @ApiOperation("相关推荐文章添加列表")
    @RequestMapping(value = "recommondPage", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<Article>> recommondPage(ArticleVO article) {
        try{
            return Result.success(articleService.recommondPage(article));
        }catch (Exception e){
            log.info("获取相关推荐文章添加列表异常",e);
            return Result.error("获取相关推荐文章添加列表异常");
        }
    }
}
