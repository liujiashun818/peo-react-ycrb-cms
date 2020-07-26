package cn.people.one.modules.user;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.live.model.LiveTalk;
import cn.people.one.modules.live.service.ILiveTalkService;
import cn.people.one.modules.user.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhangxinzheng on 2017/3/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class UserServiceTest {
    private static JsonMapper jsonMapper = JsonMapper.nonNullMapper();

    @Autowired
    private IUserService userService;

    @Test
    public void user() throws Exception {
        log.info(jsonMapper.toJson(userService.fetch("15600880056")));
    }

}