package cn.people.one.appapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.people.one.appapi.service.MenuService;
import cn.people.one.appapi.vo.MenuVO;
import cn.people.one.appapi.vo.ResultVO4;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Created by wilson on 2018-10-09.
 */
@Api(value = "菜单API", tags = {"菜单相关接口"})
@RestController
@RequestMapping("/api/v2/menus")
public class MenuController {

    @Autowired
    @Qualifier("menuServiceV2")
    private MenuService menuService;

    @ApiOperation("获取菜单列表")
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 50100, message = "服务器未配置菜单")
    })
    @GetMapping
    public ResultVO4<MenuVO> list(@RequestParam Integer tabType) {
        return menuService.list(tabType);
    }

    @ApiOperation("获取子菜单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "menu id", required = true, paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "成功"),
            @ApiResponse(code = 40100, message = "ID错误，未找到对应的菜单")
    })
    @GetMapping("/{id}")
    public ResultVO4<MenuVO> get(@PathVariable("id") Long id) {
        return menuService.get(id);
    }

}
