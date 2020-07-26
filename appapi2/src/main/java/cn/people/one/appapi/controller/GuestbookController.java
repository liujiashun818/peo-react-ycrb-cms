package cn.people.one.appapi.controller;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.modules.guestbook.model.Guestbook;
import cn.people.one.modules.guestbook.service.IGuestbookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author YX
 * @date 2018/10/17
 * @comment
 */
@Slf4j
@Api(value = "意见反馈API", tags = {"意见反馈相关接口"})
@RestController
@RequestMapping("/api/v2/guestbook")
public class GuestbookController {

    @Autowired
    private IGuestbookService guestbookService;

	@ApiOperation("保存意见反馈")
    @PostMapping("/saveGuestBook")
    public ResultVO2<Guestbook> saveGuestBook(@RequestBody Guestbook guestbook) {
        try {
            return ResultVO2.success(guestbookService.insert(guestbook));
        } catch (Exception e) {
            log.error("保存意见反馈出错===》/api/v2/guestbook/saveGuestBook", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

}
