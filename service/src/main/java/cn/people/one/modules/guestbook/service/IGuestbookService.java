package cn.people.one.modules.guestbook.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.guestbook.model.Guestbook;
import cn.people.one.modules.guestbook.model.front.GuestbookVO;
import org.nutz.dao.QueryResult;

/**
 * Created by sunday on 2017/4/11.
 */
public interface IGuestbookService extends IBaseService<Guestbook> {

    QueryResultVO<Guestbook> findSearchPage(Integer pageNumber, Integer pageSize, GuestbookVO guestbookVO);
}
