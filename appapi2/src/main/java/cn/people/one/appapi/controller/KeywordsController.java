package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.newspaper.PaperDetail;
import cn.people.one.modules.app.model.Keywords;
import cn.people.one.modules.app.service.IKeywordsService;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 搜索关键词手机端controller
 * Created by zhouc on 2019/2/26.
 */
@Api(description = "搜索关键词管理")
@RestController
@RequestMapping("/api/keywords")
@Slf4j
public class KeywordsController {

    @Autowired
    IKeywordsService keywordsService;

    @Autowired
    private CacheRepository cacheRepository;

    /**
     * APP关键词列表
     *
     * @return
     */
	@ApiImplicitParam()
    @RequestMapping(value = "/keywordsList", method = RequestMethod.GET)
    public ResultVO2<List<Keywords>> list() {
        try {
            String key = CacheKeyUtils.getKeywordsKey();
            Cache<String> cache = cacheRepository.getObject(key, String.class);
            if (cache.getStatus() == CacheStatus.NOT_CACHING) {
                List<Keywords> keywords = keywordsService.appKeywordsList();
                String str = JSONObject.toJSONString(keywords);
                cacheRepository.cache(key, str, CacheConstant.Time.FIVE_MINUTE);
                cache.setObject(str);
            }
            String json = cache.getObject();
            if(json == null || "null".equals(json) || "".equals(json)) {
                cacheRepository.delete(key);
            }
            log.info("【keywords】===========" + json);
            return ResultVO2.success(JSONObject.parseArray(json,Keywords.class));
        }catch (Exception e){
            log.error("新增爆料信息出错====》/api/keywords/keywordsList",e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
    
}
