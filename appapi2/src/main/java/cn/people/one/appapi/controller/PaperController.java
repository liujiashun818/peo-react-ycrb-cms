package cn.people.one.appapi.controller;

import cn.people.one.appapi.cache.Cache;
import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.cache.CacheStatus;
import cn.people.one.appapi.constant.CacheConstant;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.PaperService;
import cn.people.one.appapi.util.CacheKeyUtils;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.newspaper.PaperDetail;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author YX
 * @date 2018/10/19
 * @comment
 */
@Slf4j
@Api(value = "报纸API", tags = {"报纸相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/paper")
public class PaperController {

    @Autowired
    private PaperService paperService;

    @Autowired
    private CacheRepository cacheRepository;

    @ApiOperation("获取指定日期的报纸列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "日期(格式yyyy-MM-dd)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pagesSize", value = "报纸版面坐标范围", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pjCode", value = "项目编码(日报：ycrb,晚报：ycwb)", required = true, paramType = "query")
    })
    @GetMapping("/list")
    public ResultVO list(@RequestParam String date, @RequestParam String pagesSize,
                         @RequestParam String pjCode) {
        Float[] areaSize = null;
        /**
         * 处理热区参数
         */
        try {
            String[] areaSizeStr = pagesSize.split("x");
            areaSize = new Float[areaSizeStr.length];
            for (int i = 0; i < areaSizeStr.length; i++) {
                Float size = Float.parseFloat(areaSizeStr[i]);
                areaSize[i] = size;
            }
        } catch (NumberFormatException e) {
            return new ResultVO(CodeConstant.PARAM_ERROR);
        }
        try {
            return new ResultVO(paperService.getListByDate(date, areaSize, pjCode));
        } catch (Exception e) {
            log.error("获取指定日期的报纸列表信息出错===》/api/v2/paper/list", e);
            return new ResultVO(CodeConstant.ERROR);
        }
    }

    @ApiOperation("AIUI获取指定日期的报纸列表信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "日期", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pagesSize", value = "报纸版面坐标范围", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pjCode", value = "项目编码(日报：ycrb,晚报：ycwb)", required = true, paramType = "query")
    })
    @GetMapping("/getAiuiPaperInfo")
    public ResultVO getAiuiPaperInfo(@RequestParam String date, @RequestParam String pagesSize,
                                     @RequestParam String pjCode) {
        Float[] areaSize = null;
        /**
         * 处理热区参数
         */
        try {
            String[] areaSizeStr = pagesSize.split("x");
            areaSize = new Float[areaSizeStr.length];
            for (int i = 0; i < areaSizeStr.length; i++) {
                Float size = Float.parseFloat(areaSizeStr[i]);
                areaSize[i] = size;
            }
        } catch (NumberFormatException e) {
            return new ResultVO(CodeConstant.PARAM_ERROR);
        }
        try {
            return new ResultVO(paperService.getAiuiListInfo(date, areaSize, pjCode));
        } catch (Exception e) {
            log.error("AIUI获取指定日期的报纸列表信息出错===》/api/v2/paper/getAiuiPaperInfo", e);
            return new ResultVO(CodeConstant.ERROR);
        }
    }


    @ApiOperation("根据文章id获取详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "articleId", value = "文章id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "paperName", value = "系统编码(日报：ycrb,晚报：ycwb)", paramType = "query"),
            @ApiImplicitParam(name = "platform", value = "手机系统", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pjCode", value = "项目编码", paramType = "query")
    })
    @GetMapping("/detail")
    public ResultVO detail(@RequestParam String articleId,
                           @RequestParam(required = false) String paperName,
                           @RequestParam String platform,
                           @RequestParam(required = false) String pjCode) {
        try {
            String key = CacheKeyUtils.getPaperDetailKey(articleId, paperName, platform, pjCode);
            Cache<PaperDetail> cache = cacheRepository.getObject(key, PaperDetail.class);
            if (cache.getStatus() == CacheStatus.NOT_CACHING) {
                PaperDetail detail = paperService.getDetailByArticleId(articleId, paperName, platform, pjCode);
                // 特殊文字处理（Safari不兼容问题）
                if(detail != null ) {
                    detail.setTitle(StringEscapeUtils.unescapeHtml(detail.getTitle()));
                    detail.setSubTitle(StringEscapeUtils.unescapeHtml(detail.getSubTitle()));
                    detail.setContent(StringEscapeUtils.unescapeHtml(detail.getContent()));
                }
                cacheRepository.cache(key, detail, CacheConstant.Time.ONE_DAY);
                cache.setObject(detail);
            }
            return new ResultVO(cache.getObject());
        } catch (Exception e) {
            log.error("根据文章id获取详细信息出错===》/api/v2/paper/detail", e);
            return new ResultVO(CodeConstant.ERROR);
        }
    }

    @ApiOperation("获取历史新闻报纸信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "paperName", value = "系统编码(日报：ycrb,晚报：ycwb)", required = true, paramType = "query"),
            @ApiImplicitParam(name = "pjCode", value = "项目编码(日报：ycrb,晚报：ycwb)", required = true, paramType = "query")
    })
    @GetMapping("/prepaper")
    public ResultVO prepaper(@RequestParam String paperName,
                             @RequestParam String pjCode) {
        try {
            return new ResultVO(paperService.getPrepaperInfo(paperName, pjCode));
        } catch (Exception e) {
            log.info("获取历史新闻报纸信息出错===》/api/v2/paper/prepaper", e);
            return new ResultVO(CodeConstant.ERROR);
        }
    }
}
