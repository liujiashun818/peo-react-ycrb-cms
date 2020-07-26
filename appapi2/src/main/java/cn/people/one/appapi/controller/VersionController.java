package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.service.IVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sunday on 2018/10/23.
 */
@Slf4j
@Api(value = "获取安卓版本接口", tags = {"获取安卓版本接口"})
@RestController
@RequestMapping("/api/v2/version")
public class VersionController {

    @Autowired
    private IVersionService versionService;

    @ApiOperation("获取安卓版本接口 item:{version:版本,type:类型,description:描述,down_url:下载地址}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appversion", value = "客户端版本号", required = true, paramType = "query")
    })
    @GetMapping("/getVersion")
    public ResultVO2<Object> getVersion(@RequestParam("appversion") int appversion) {
        try {
            return ResultVO2.success(versionService.getVersion(appversion));
        } catch (Exception e) {
            log.error("获取安卓版本接口信息出错===》/api/v2/version/getVersion", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
