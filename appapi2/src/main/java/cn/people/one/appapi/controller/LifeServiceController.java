package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.LifeService;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.vo.LifeServiceVO;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO2;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author YX
 * @date 2018/10/17
 * @comment
 */
@Slf4j
@Api(value = "生活服务API", tags = {"生活服务相关接口"})
@RestController
@RequestMapping("/api/v2/lifeService")
public class LifeServiceController {

    @Autowired
    private LifeService lifeService;

    @Autowired
    private CacheRepository cacheRepository;

    @ApiOperation("获取生活服务列表")
    @GetMapping("/list")
    public ResultVO2<Object> list() {
        try {
            String key = CacheKeyUtils.getLiveServiceKey();
            Cache<LifeServiceVO> cache = cacheRepository.getObject(key, LifeServiceVO.class);
            LifeServiceVO lifeServiceVO = cache.getObject();
            if(lifeServiceVO!=null){
                return ResultVO2.success(lifeServiceVO);
            }
            lifeServiceVO=lifeService.getList();
            if (cache.getStatus() == CacheStatus.NOT_CACHING) {
                cacheRepository.cache(key, lifeServiceVO, CacheConstant.Time.ONE_MONTH);
                cache.setObject(lifeServiceVO);
            }
            return ResultVO2.success(lifeServiceVO);

        } catch (Exception e) {
            log.error("获取生活服务出错===》/api/v2/lifeService/list", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
