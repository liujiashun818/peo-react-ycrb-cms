package cn.people.one.modules.app.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.app.model.Keywords;
import cn.people.one.modules.app.service.impl.KeywordsService;
import cn.people.one.modules.base.entity.BaseModel;
import cn.people.one.modules.common.CodeConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 搜索关键词Controller
 *
 * @author Cheng
 * modify by zhouc 2019-02-26
 */
@Api(description = "搜索关键词管理(app模块)")
@RestController
@RequestMapping("/api/app/keywords")
@Slf4j
public class KeywordsController {

    @Autowired
    private KeywordsService keywordsService;

    /**
     * 关键词列表
     * @param title
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @ApiOperation("关键词列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "title", value = "标题", required = false, paramType = "query"),
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = {"list", ""}, method = RequestMethod.GET)
    public Result<QueryResultVO<Keywords>> list(@RequestParam(value = "title",required = false,defaultValue = "") String title,
                                                @RequestParam(value = "pageNumber",required = true,defaultValue = "1") Integer pageNumber,
                                                @RequestParam(value = "pageSize",required = true,defaultValue = "10") Integer pageSize) {
        Condition cnd = Cnd.NEW();
        if(StringUtils.isNotEmpty(title)){
            cnd = Cnd.where("title", "=", title);
        }
        //添加排序
        ((Cnd) cnd).desc(Keywords.Constant.UPDATE_AT);
        try {
            log.info("title========:" + title.toString()+":pageNumber======"+pageNumber+":pageSize======"+pageSize);
            return Result.success(keywordsService.listPage(pageNumber, pageSize, cnd, "id|title|remarks"));
        }catch (Exception e){
            log.error("查看关键词列表失败===》 /api/app/keywords/list");
            return Result.error(CodeConstant.ERROR);
        }

    }

    /**
     * 根据id查看关键词
     * @param id
     * @return
     */
    @ApiOperation("根据id查看关键词")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "关键词ID", required = true, paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result<Keywords> view(@PathVariable Long id) {
        try {
            log.info("id========:" + id.toString());
            return Result.success(keywordsService.fetch(id));
        }catch (Exception e){
            log.error("根据id查看关键词失败===》 /api/app/keywords/view");
            return Result.error(CodeConstant.ERROR);
        }

    }

    /**
     * 新增关键词
     * @param keywords
     * @return
     */
    @ApiOperation(value = "新增关键词")
    @RequestMapping(method = RequestMethod.POST)
    public Result save(@RequestBody Keywords keywords) {
        try {
            log.info("keywords========:" + keywords.toString());
            keywordsService.save(keywords);
            return Result.success(keywords);
        }catch (Exception e){
            log.error("新增关键词失败===》 /api/app/keywords/save");
            return Result.error(CodeConstant.ERROR);
        }

    }

    /**
     * 根据id删除关键词
     * @param id
     * @return
     */
    @ApiOperation("根据id删除关键词")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id",value = "关键词ID",required = true,paramType = "path")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable Long id) {
        try {
            log.info("【id】========================"+id);
            return Result.success(keywordsService.delete(id));
        }catch (Exception e){
            log.error("删除关键词失败===》 /api/app/keywords/delete");
            return Result.error(CodeConstant.ERROR);
        }
    }
}
