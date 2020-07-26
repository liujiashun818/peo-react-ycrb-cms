package cn.people.one.modules.sys.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.sys.model.Log;
import cn.people.one.modules.sys.model.front.LogVO;
import org.nutz.dao.QueryResult;

/**
* 日志Service
*/
public interface ILogService {

	QueryResultVO<Log> page(LogVO logVO);
	void insert(Log log);
}