package cn.people.one.modules.search;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.search.service.IElasticSearchService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by lml on 2017/1/17.
 */
@Api(description = "搜索模块")
@RestController
@RequestMapping(value = "/api/search/article")
public class ElasticSearchController {

    @Autowired
    private IElasticSearchService elasticSearchService;

    @RequestMapping(value = "/articles", method = RequestMethod.GET)
    public Result articleSearch(@RequestParam(defaultValue = "", required = false) String keywords,
                                @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
                                @RequestParam(required = false, defaultValue = "200") Integer pageSize) throws UnsupportedEncodingException {

        List list = elasticSearchService.articleSearch(keywords, pageNumber, pageSize);
        return Result.success(list);
    }

    @RequestMapping(value = "/saveAll", method = RequestMethod.GET)
    public Result saveAll(@RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage) {
        elasticSearchService.saveAll(startPage);
        return Result.success();
    }

    @RequestMapping(value = "/deleteAll", method = RequestMethod.DELETE)
    public Result deleteAll() {
        elasticSearchService.deleteAll();
        return Result.success();
    }

    /**
     * 清除所有问政索引
     * @return
     */
    @RequestMapping(value = "/deleteAllAsk", method = RequestMethod.DELETE)
    public Result deleteAllAsk() {
        elasticSearchService.deleteAllAsk();
        return Result.success();
    }

    @RequestMapping(value = "/removeIndex", method = RequestMethod.GET)
    public Result removeIndex() {
        elasticSearchService.removeIndex();
        return Result.success();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findOne(@PathVariable Long id) {
        return Result.success(elasticSearchService.findOne(id));
    }

}
