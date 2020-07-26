package cn.people.one.modules.client.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.client.model.front.ClientPushVO;
import org.nutz.dao.QueryResult;

/**
 * Created by lml on 17-2-27.
 */
public interface IClientPushService extends IBaseService<ClientPush> {

    /**
     * AIUI推送搜索接口
     * @param clientPushVO
     * @return
     */
    QueryResult searchPushInfo(ClientPushVO clientPushVO);
}
