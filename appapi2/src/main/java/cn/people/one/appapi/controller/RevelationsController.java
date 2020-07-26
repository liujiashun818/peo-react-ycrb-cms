package cn.people.one.appapi.controller;

import javax.servlet.http.HttpServletRequest;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.modules.cms.service.IRevelationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cn.people.one.appapi.util.IpUtils;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.cms.model.Revelations;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Slf4j
@Api(value = "爆料API", tags = {"爆料相关接口"})
@RestController
@RequestMapping("/api/revelation")
public class RevelationsController {

    @Autowired
    private IRevelationsService revelationService;


	@SuppressWarnings("rawtypes")
	@ApiOperation("新增爆料信息")
    @PostMapping("/add")
    public ResultVO2 add(@RequestBody Revelations revelations,
                        HttpServletRequest request) {
        try {
            revelations.setIp(IpUtils.getIp(request));
            revelationService.saveRevelations(revelations);
            return ResultVO2.result(CodeConstant.SUCCESS);
        } catch (Exception e) {
            log.error("新增爆料信息出错====》/api/revelation/add",e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

}
