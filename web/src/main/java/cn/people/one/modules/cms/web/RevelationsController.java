package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.cms.model.Revelations;
import cn.people.one.modules.cms.service.IRevelationsService;
import cn.people.one.modules.common.CodeConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 新闻爆料Controller xyl 2019-02-18
 * modify by zhouc 2019-02-26
 */
@Api(description = "新闻爆料")
@RestController
@Slf4j
@RequestMapping("/api/cms/revelations")
public class RevelationsController {
    @Autowired
    private IRevelationsService revelationsService;

    /**
     * 根据id删除爆料
     *
     * @param id
     * @return
     */
    @ApiOperation("删除新闻")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "新闻ID", required = true, paramType = "path"),
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result delete(@PathVariable Long id) {
        try {
            log.info("【 delete 】" + id.toString());
            return Result.success(revelationsService.delete(id));
        } catch (Exception e) {
            log.error("根据id删除新闻爆料出错===》 /api/cms/revelations/delete", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 新增爆料
     *
     * @param revelations
     * @return
     */
    @ApiOperation("新增爆料")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result save(@RequestBody Revelations revelations) {
        try {
            log.info("【 save 】" + revelations.toString());
            return Result.success(revelationsService.insert(revelations));
        } catch (Exception e) {
            log.error("新增爆料出错===》 /api/cms/revelations/save", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

    /**
     * 根据id查询新闻爆料
     *
     * @param id
     * @return
     */
    @ApiOperation("根据id查询新闻爆料")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "id", value = "新闻ID", required = true, paramType = "query"),
    })
    @RequestMapping(value = "/form", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<Revelations> form(@RequestParam Long id) {
        try {
            log.info("【 form 】" + id.toString());
            Revelations revelations=revelationsService.getRevelationsById(id);
            return Result.success(revelations);
        } catch (Exception e) {
            log.error("根据id查询新闻爆料出错===》 /api/cms/revelations/form", e);
            return Result.error(CodeConstant.ERROR);
        }

    }

    /**
     * 获取新闻爆料列表
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @ApiOperation("获取新闻爆料列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query")
    })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<Revelations>> list(@RequestParam Integer pageNumber, @RequestParam Integer pageSize) {
        try {
            log.info("【 list 】" + pageNumber.toString() + "; " + pageSize.toString());
            return Result.success(revelationsService.listPage(pageNumber, pageSize));
        } catch (Exception e) {
            log.error("获取新闻爆料列表出错===》 /api/cms/revelations/list", e);
            return Result.error(CodeConstant.ERROR);
        }
    }


    /**
     * 根据名称name获取新闻爆料列表
     *
     * @param pageNumber
     * @param pageSize
     * @param
     * @return
     */
    @ApiOperation("根据名称name获取新闻爆料列表")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "pageNumber", value = "页码", required = true, paramType = "query"),
        @ApiImplicitParam(name = "pageSize", value = "页大小", required = true, paramType = "query"),
        @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query")
    })
    @RequestMapping(value = "/listByName", method = RequestMethod.GET)
    @RequiresPermissions("cms:articles:view")//权限管理;
    public Result<QueryResultVO<Revelations>> listByName(@RequestParam Integer pageNumber, @RequestParam Integer pageSize, @RequestParam String name) {
        try {
            log.info("【 listByName 】" + pageNumber.toString() + "; " + pageSize.toString() + "; " + name);
            return Result.success(revelationsService.findListByName(pageNumber, pageSize, name));
        } catch (Exception e) {
            log.error("获取新闻爆料列表出错===》 /api/cms/revelations/listByName", e);
            return Result.error(CodeConstant.ERROR);
        }
    }

}
