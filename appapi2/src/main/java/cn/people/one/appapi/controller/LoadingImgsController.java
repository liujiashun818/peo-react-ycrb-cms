package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.model.LoadingImgs;
import cn.people.one.modules.client.service.ILoadingImgsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开屏图
 * Created by sunday on 2018/10/22.
 */
@Slf4j
@Api(value = "开屏图", tags = {"开屏图相关接口"})
@RestController
@RequestMapping("/api/v2/loadingImgs")
public class LoadingImgsController {

    @Autowired
    private ILoadingImgsService loadingImgsService;

    @ApiOperation("开屏图信息")
    @GetMapping("/getLastLoadingImgs")
    public ResultVO2<LoadingImgs> getLastLoadingImgs() {
        try {
            LoadingImgs loadingImgs = loadingImgsService.getLastLoadingImgs();
            if(Lang.isEmpty(loadingImgs)){
                return ResultVO2.result(CodeConstant.LOADINGIMGS_NOT_EXIST);
            }
            return ResultVO2.success(loadingImgs);
        } catch (Exception e) {
            log.error("获取开屏图信息出错===》/api/v2/loadingImgs/getLastLoadingImgs", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
