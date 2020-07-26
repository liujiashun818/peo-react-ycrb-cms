package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.model.FloatingImgs;
import cn.people.one.modules.client.service.IFloatingImgsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 浮标图
 * Created by sunday on 2018/10/22.
 */
@Slf4j
@Api(value = "浮标图", tags = {"浮标图相关接口"})
@RestController
@RequestMapping("/api/v2/floatingImgs")
public class FloatingImgsController {

    @Autowired
    private IFloatingImgsService floatingImgsService;

    @ApiOperation("浮标图信息")
    @GetMapping("/getLastFloatingImgs")
    public ResultVO2<FloatingImgs> getLastFloatingImgs() {
        try {
            FloatingImgs floatingImgs = floatingImgsService.getLastFloatingImgs();
            if(Lang.isEmpty(floatingImgs)){
                return ResultVO2.result(CodeConstant.FLOATINGIMGS_NOT_EXIST);
            }
            return ResultVO2.success(floatingImgs);
        } catch (Exception e) {
            log.error("获取浮标图信息出错===》/api/v2/floatingImgs/getLastFloatingImgs", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
