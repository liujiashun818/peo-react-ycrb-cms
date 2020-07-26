package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.message.SendMessage;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Subject;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.SubjectArticleVO;
import cn.people.one.modules.cms.model.front.SubjectVO;
import cn.people.one.modules.cms.service.ISubjectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by lml on 17-3-17.
 */
@Api(description = "专题管理(cms模块)")
@RestController
@RequestMapping("/api/cms/subject")
@Slf4j
public class SubjectController {

    @Autowired
    private ISubjectService subjectService;

    /**
     * 保存区块
     */
    @ApiOperation("保存区块")
    @RequestMapping(method = RequestMethod.POST)
    public Result<Subject> save(@RequestBody Subject subject) {
        if(subject.getParentId()==null || subject.getParentId()==0){
            return Result.error("专题编号有误");
        }
        Subject _subject = subjectService.insert(subject);
        if (Lang.isEmpty(_subject.getId())) {
            return Result.error("存储区块失败");
        }
        SendMessage.sendSubject(_subject.getParentId());
        return Result.success(_subject);
    }

    @ApiOperation("根据ID获取区块")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/block/{id}", method = RequestMethod.GET)
    public Result<Subject> block(@PathVariable Long id) {
        Subject subject = subjectService.fetch(id);
        if (Lang.isEmpty(subject)) {
            return Result.error("获取区块为空");
        }
        return Result.success(subject);
    }

    @ApiOperation("更新区块")
    @RequestMapping(method = RequestMethod.PATCH)
    public Result<Subject> update(@RequestBody Subject subject) {
        if (Lang.isEmpty(subjectService.updateIgnoreNull(subject))) {
            return Result.error("更新区块失败");
        }
        SendMessage.sendSubject(subject.getParentId());
        return Result.success(subject);
    }

    @ApiOperation("获取区块详情")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result get(@PathVariable(value = "id") Long articleId) {
        SubjectVO subject = subjectService.get(articleId);
        if (Lang.isEmpty(subject)) {
            return Result.error("获取专题为空");
        }
        return Result.success(subject);
    }

    @ApiOperation("保存文章")
    @RequestMapping(value = "/articles", method = RequestMethod.POST)
    public Result saveArticles(@RequestBody SubjectArticleVO subjectArticleVO) {
        try {
            if (subjectArticleVO.getCategoryId() == null) {
                return Result.error("区块编号不能为空");
            }
            if (subjectService.fetch(subjectArticleVO.getCategoryId()).getParentId() == null) {
                return Result.error("只支持在区块下创建文章");
            }
            if (subjectService.saveArticlesToSubject(subjectArticleVO.getList(), subjectArticleVO.getCategoryId())) {
                SendMessage.sendSubject(subjectService.fetch(subjectArticleVO.getCategoryId()).getParentId());
                return Result.success();
            } else {
                return Result.error("存储失败");
            }
        } catch (Exception e) {
            log.error("批量保存引用文章出错===/api/cms/subject/articles",e);
            return Result.error("保存引用文章失败");
        }
    }

    /**
     * 保存专题
     */
    @ApiOperation("保存专题")
    @RequestMapping(value = "/category/subject", method = RequestMethod.POST)
    public Result saveSubjectToCategory(@RequestBody SubjectVO subjectVO) {
        //TODO:model    头图
        SubjectVO _subjectVo = subjectService.saveSubjectToCategory(subjectVO);
        if (!Lang.isEmpty(_subjectVo.getArticle().getId())) {
            SendMessage.sendArticle(_subjectVo.getArticle().getId());
            return Result.success(_subjectVo);
        } else {
            return Result.error("存储失败");
        }
    }

    @ApiOperation("更新栏目专题")
    @RequestMapping(value = "/category/subject", method = RequestMethod.PATCH)
    public Result<SubjectVO> updateSubjectInCategory(@RequestBody SubjectVO subjectVO) {
        if (!Lang.isEmpty(subjectService.updateSubjectInCategory(subjectVO))) {
            SendMessage.sendArticle(subjectVO.getArticle().getId());
            return Result.success(subjectVO);
        } else {
            return Result.error("更新栏目专题失败");
        }
    }

    @ApiOperation("获取区块")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "articleId", value = "ID", required = true, paramType = "query"),
        @ApiImplicitParam(name = "isReference", value = "是否引用", required = false, paramType = "query"),
    })
    @RequestMapping(value = "/blocks", method = RequestMethod.GET)
    public Result<List> findBlocks(@RequestParam Long articleId, @RequestParam(required = false) Boolean isReference) {
        Long realId = articleId;
        if(isReference){
            SubjectVO subjectVO = subjectService.get(articleId);
            if(subjectVO==null){
                return Result.error("获取专题为空");
            }else {
                realId = subjectVO.getId();
            }
        }
        List result = subjectService.findBlocks(realId);
        return Result.success(result);
    }
    @ApiOperation("获取区块下文章列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public Result<QueryResultVO<Article>> findSubjectArticles(@RequestParam Integer pageNumber,
                                                              @RequestParam Integer pageSize, ArticleVO articleVO) {
        if (articleVO.getCategoryId() == null) {
            return Result.error("区块编号有误");
        }
        return Result.success(subjectService.findArticlesInSubject(pageNumber, pageSize, articleVO));
    }

    @ApiOperation("删除区块")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "区块ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/block/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable Long id) {
        if (subjectService.vDelete(id) > 0) {
            SendMessage.sendSubject(subjectService.fetch(id).getParentId());
            return Result.success();
        } else {
            return Result.error("删除区块失败");
        }
    }

    @ApiOperation("删除专题")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "专题ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delSubject(@PathVariable Long id) {
        if (subjectService.delSubject(id) > 0) {
            SendMessage.sendArticle(id);
            return Result.success();
        } else {
            return Result.error("删除专题失败");
        }
    }
}
