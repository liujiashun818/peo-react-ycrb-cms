package cn.people.one.modules.live.service;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.live.model.LiveTalk;
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
public class LiveTalkServiceTest {
    private static JsonMapper jsonMapper = JsonMapper.nonNullMapper();

    @Autowired
    private ILiveTalkService talkService;

    @Test
    public void findTalksByRoomId() throws Exception {
        log.info(jsonMapper.toJson(talkService.findTalksByRoomId(1L,0l)));
    }

    @Test
    public void speak() throws Exception{
        for (int i=0; i<10; i++){
            LiveTalk talk = new LiveTalk();
            talk.setRoomId(1L);
            talk.setContent("主持人发言" + i);
            talk.setTop(false);
            talk.setOpenId(1 +"");
            talk.setUserName("主持人1");
            talk.setUserType("host");
            talkService.speak(talk,"");
        }
    }

    @Test
    public void reply() throws Exception {
        for (int i=0; i<10; i++) {
            LiveTalk talk = new LiveTalk();
            talk.setParentId(1L + i);
            talk.setRoomId(1L);
            talk.setContent("嘉宾1回复主持人1" + i);
            talk.setTop(false);
            talk.setOpenId(3 + "");
            talk.setUserName("嘉宾1");
            talk.setUserType("guest");
            talkService.reply(talk);
        }
    }

    @Test
    public void top() throws Exception {

    }

    @Test
    public void on() throws Exception {

    }

    @Test
    public void off() throws Exception {

    }

}