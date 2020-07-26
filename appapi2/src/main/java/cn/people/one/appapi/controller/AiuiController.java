package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.model.front.AiuiVO;
import cn.people.one.modules.client.service.IAiuiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sunday on 2018/10/29.
 */
@Slf4j
@Api(value = "AIUI智能语音集成接口", tags = {"AIUI智能语音集成接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/aiui")
public class AiuiController {

    @Autowired
    private IAiuiService aiuiService;

    @Autowired
    private CacheRepository cacheRepository;

    @ApiOperation("智能语音通用提示语接口")
    @GetMapping("/suggestions")
    public ResultVO2<AiuiVO> getCueWords() {
        try {
            String key = CacheKeyUtils.getAiuiSuggestionsKey();
            Cache<AiuiVO> cache = cacheRepository.getObject(key, AiuiVO.class);
            if (cache.getStatus() == CacheStatus.NOT_CACHING) {
                AiuiVO aiuiVO = aiuiService.getCueWords();
                cacheRepository.cache(key, aiuiVO, CacheConstant.Time.ONE_DAY);
                cache.setObject(aiuiVO);
            }
            return ResultVO2.success(cache.getObject());
        } catch (Exception e) {
            log.error("获取智能语音通用提示语接口信息出错===》/api/v2/aiui/suggestions", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
