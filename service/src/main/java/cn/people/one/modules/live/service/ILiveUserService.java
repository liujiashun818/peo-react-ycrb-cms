package cn.people.one.modules.live.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.live.model.LiveUser;

import java.util.List;

/**
* 直播嘉宾/主持人Service
* @author cheng
*/
public interface ILiveUserService extends IBaseService<LiveUser>{

    List<LiveUser> list(String role);
}