package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.client.model.front.ClientPushVO;
import cn.people.one.modules.client.service.IClientPushService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sunday on 2018/10/31.
 */
@Slf4j
@Api(value = "推送相关接口", tags = {"推送相关接口"})
@RestController
@RequestMapping("/api/v2/push")
public class PushController {

    @Autowired
    private IClientPushService clientPushService;

    @ApiOperation("AIUI推送搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "time", value = "时间", paramType = "query"),
            @ApiImplicitParam(name = "keyWord", value = "关键字", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageToken", value = "请求第一页时不传此参数, 再次请求时根据上次请求的page部分传递相应数值", paramType = "query")
    })
    @GetMapping("/searchPushInfo")
    public ResultVO2<QueryResult> searchPushInfo(@RequestParam(value = "time",required = false) String time,
                                   @RequestParam(value = "keyWord",required = false) String keyWord,
                                  @RequestParam(value = "size", required = false, defaultValue = "20") Integer size,
                                  @RequestParam(value = "pageToken", required = false, defaultValue = "1") String pageToken) {
        //参数处理
        /**
         * 参数处理====start
         */
        if (size == null) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        Integer page;
        try {
            page = Integer.parseInt(pageToken);
        } catch (Exception e) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }

        if("all".equalsIgnoreCase(time)){
            time = null;
        }

        /**
         * 参数处理====end
         */
        try {
            ClientPushVO clientPushVO = new ClientPushVO();
            clientPushVO.setTime(time);
            clientPushVO.setKeyWords(keyWord);
            clientPushVO.setPageNumber(page);
            clientPushVO.setPageSize(size);
            return ResultVO2.success(clientPushService.searchPushInfo(clientPushVO));
        } catch (Exception e) {
            log.error("AIUI搜索推送出错===》searchPushInfo", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
}
