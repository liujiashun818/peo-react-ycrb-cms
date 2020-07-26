package cn.people.one.modules.live.service;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.live.model.LiveRoom;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by zhangxinzheng on 2017/3/20.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class LiveRoomServiceTest {
    private static JsonMapper jsonMapper = JsonMapper.nonNullMapper();

    @Autowired
    private ILiveRoomService roomService;

    @Test
    public void fetch() throws Exception {
        log.info(jsonMapper.toJson(roomService.fetch(4L)));
    }

    @Test
    public void insertLiveRoom() throws Exception {
        for (int i = 0; i < 10; i++) {
            LiveRoom room = new LiveRoom();
            room.setCategoryId(1L);
            room.setHostIds("1");
//            room.setHost("主持人1");
            room.setGuestIds("3");
//            room.setGuest("嘉宾1");
            room.setTitle("直播间" + i);
            room.setViewType("banner");
            room.setShowTitle(true);
            room.setStatus("1");
            room.setImage("http://png11");
            room.setVideo("http://video111");
            room.setLiveTime(new Date());
            room.setComments(101 + 10*i);
            room.setHits(120 + 10*i);
            room.setLikes(133 + 10*i);
            room.setTag("直播标签" + i);
            room.setDescription("描述" + i);
            roomService.insertLiveRoom(room);
            log.info(jsonMapper.toJson(roomService.fetch(room.getId())));
        }
    }

    @Test
    public void updateLiveRoom() throws Exception {
        LiveRoom room = new LiveRoom();
        room.setId(5L);
        room.setTag("标签修改");
        roomService.updateLiveRoom(room);
    }

    @Test
    public void vDelete() throws Exception {

    }

    @Test
    public void fetchByApp() throws Exception {
        log.info(jsonMapper.toJson(roomService.fetchByApp(5L)));
    }

}