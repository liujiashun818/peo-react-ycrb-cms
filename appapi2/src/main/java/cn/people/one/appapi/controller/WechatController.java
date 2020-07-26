package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.model.front.WechatVO;
import cn.people.one.modules.client.service.IWechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sunday on 2018/10/24.
 */
@Slf4j
@Api(value = "微信二次分享时获取签名认证接口", tags = {"微信二次分享时获取签名认证接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v2/wechat")
public class WechatController {

    @Autowired
    private IWechatService wechatService;

    @ApiOperation("微信二次分享时获取签名认证接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "url", value = "url", required = true, paramType = "query")
    })
    @GetMapping("/getWechatInfo")
    public ResultVO2<WechatVO> getWechatInfo(@RequestParam("url") String url) {
        try {
            WechatVO wechatVO = wechatService.getWechatInfo(url);
            if (Lang.isEmpty(wechatVO)) {
                return ResultVO2.result(CodeConstant.EMPTY_RESULT);
            }

            return ResultVO2.success(wechatVO);
        } catch (Exception e) {
            log.error("获取微信二次分享时获取签名认证接口信息出错===》/api/v2/wechat/getWechatInfo", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

}
