package cn.people.one.modules.client.service;

import cn.people.one.modules.client.model.front.WechatVO;

/**
 * Created by sunday on 2018/10/24.
 */
public interface IWechatService {

    WechatVO getWechatInfo(String url);
}
