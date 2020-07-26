package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.ArticleService;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.SubjectVO;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wilson on 2018-10-10.
 */
@Api(value = "专题API", tags = {"专题相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/subjects")
public class SubjectController {

    @Autowired
    @Qualifier("articleServiceV2")
    private ArticleService articleService;

    @Autowired
    private CacheRepository cacheRepository;
    @Autowired
    private IArticleService iArticleService;

	@ApiOperation("获取专题下的区块列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "articleId", required = true, paramType = "path"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/{articleId}")
    public ResultVO2<SubjectVO> subject(@PathVariable("articleId") Long articleId,
                            @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                            @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        if (articleId == null || articleId < 1) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        if (size == null || size < 1) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        Integer pageNumber;
        try {
            pageNumber = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }

        String key = CacheKeyUtils.getSubjectListKey(articleId, pageNumber, size);
        Cache<SubjectVO> cache = cacheRepository.getObject(key, SubjectVO.class);
        if (cache.getStatus() == CacheStatus.NOT_CACHING) {
            SubjectVO subjectVO = articleService.getArticleBlockBySubject(articleId, pageNumber, size);
            if(!Lang.isEmpty(subjectVO)){
                cacheRepository.cache(key, subjectVO, CacheConstant.Time.TEN_SECOND);
                cache.setObject(subjectVO);
            }else{
                return ResultVO2.result(CodeConstant.SUBJECT_NOT_EXIST);
            }
        }

        // 第一页时计数
        if(pageNumber == 1) {
            // 获取成功
            Article article = iArticleService.findArticleByArticleId(articleId, SysCodeType.SUBJECT.value(),
                    ArticleType.SUBJECT.value());
            if(article!=null) {
                // 更新访问量
                article.setHits(article.getHits()==null?1:(article.getHits()+1));
                iArticleService.updateIgnoreNull(article);
            }
        }

        return ResultVO2.success(cache.getObject());
    }
    
    
}
