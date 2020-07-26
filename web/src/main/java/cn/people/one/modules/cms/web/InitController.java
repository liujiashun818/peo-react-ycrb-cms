package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.app.service.impl.InitData;
import cn.people.one.modules.search.service.IElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@EnableConfigurationProperties(Front.class)
public class InitController {

    @Autowired
    private Front front;

    @Autowired
    private InitData initData;

    @Autowired
    private IElasticSearchService elasticSearchService;

    @RequestMapping(value = "/init/{categoryId}", method = RequestMethod.GET)
    public Result init(@PathVariable Long categoryId) {
        initData.initData(categoryId);
        return Result.success();
    }

    @RequestMapping(value = "/clear/{categoryId}", method = RequestMethod.GET)
    public Result clear(@PathVariable Long categoryId) {
        initData.clearData(categoryId);
        return Result.success();
    }

    @RequestMapping(value = "/front", method = RequestMethod.GET)
    public Result projectCode() {
        return Result.success(front);
    }

    @RequestMapping(value = "/index/rebuild", method = RequestMethod.GET)
    public Result rebuildIndex(@RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage) {
        log.info("Start rebuild index...");
        elasticSearchService.saveAll(startPage);
        return Result.success("success");
    }

    @RequestMapping(value = "/index_ask/rebuild", method = RequestMethod.GET)
    public Result rebuildAskIndex(@RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage) {
        log.info("Start rebuild ask index...");
        elasticSearchService.saveAllAsk(startPage);
        return Result.success("success");
    }
}
