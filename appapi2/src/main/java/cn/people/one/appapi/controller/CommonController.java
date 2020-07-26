package cn.people.one.appapi.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wilson on 2018-10-10.
 */
@Api(value = "公共API", tags = {"公共接口"})
@RestController
public class CommonController {

    /**
     * 用于监听集群节点健康状态
     */
    @GetMapping("/check")
    @ApiOperation("用于监听集群节点健康状态")
    public Object check() {
        return "ok";
    }

}
