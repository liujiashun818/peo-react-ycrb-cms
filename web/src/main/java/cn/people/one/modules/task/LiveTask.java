package cn.people.one.modules.task;

import cn.people.one.modules.live.service.ILiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by wilson on 2018-11-22.
 */
@Slf4j
@Component
public class LiveTask {

    @Autowired
    private ILiveRoomService liveRoomService;

    @Scheduled(fixedRate = 60000)
    public void autoOnline() {
        log.info("Start update live Prevue To Liveing...");
        liveRoomService.autoUpdatePrevueToLiveing();
    }

}
