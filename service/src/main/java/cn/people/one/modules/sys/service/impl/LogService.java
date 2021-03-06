package cn.people.one.modules.sys.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.core.util.time.DateFormatUtil;
import cn.people.one.modules.sys.model.Log;
import cn.people.one.modules.sys.model.front.LogVO;
import cn.people.one.modules.sys.service.ILogService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.TableName;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
* 日志Service
*/
@Service
public class LogService implements ILogService {

    @Autowired
    private BaseDao dao;
    @Autowired
    private IUserService userService;

    @Override
    public QueryResultVO<Log> page(LogVO logVO) {
        QueryResultVO<Log> queryResultVO=null;
        Criteria cri = Cnd.cri();
        if (StringUtils.isNotBlank(logVO.getURI())) {
            cri.where().andLike("request_uri", "%" + logVO.getURI().trim() + "%");
        }
        if (null != logVO.getUserId()) {
            cri.where().andEquals("create_by", logVO.getUserId());
        }
        if (null != logVO.getBeginTime()) {
            cri.where().and("create_date", ">", logVO.getBeginTime());
        }
        if (null != logVO.getEndTime()) {
            cri.where().and("create_date", "<", logVO.getEndTime());
        }
        if (null != logVO.getIsException()) {
            if (logVO.getIsException().equals(2)) {
                cri.where().andEquals("type", 2);
            } else if (logVO.getIsException().equals(1)) {
                cri.where().andEquals("type", 1);
            }
        }
        if (null == logVO.getPageNumber()) {
            logVO.setPageNumber(1);
        }
        if (null == logVO.getPageSize()) {
            logVO.setPageSize(20);
        }
        try {
            TableName.set(DateFormatUtil.formatDate("yyyy", new Date()));
            Pager pager = dao.createPager(logVO.getPageNumber(), logVO.getPageSize());
            List<Log> logs = dao.query(Log.class, cri, pager);
            logs.stream().forEach(log -> {
                if (log != null && log.getCreateBy() != null) {
                    User user = userService.fetch(log.getCreateBy());
                    if (user != null) {
                        log.setUserName(user.getName());
                    }
                }
            });
            pager.setRecordCount(dao.count(Log.class, cri));
            return new QueryResultVO<>(logs, pager);
        }finally {
            TableName.clear();
        }
    }
    @Override
    public void insert(Log log) {
        TableName.run(DateFormatUtil.formatDate("yyyy", new Date()), () -> dao.insert(log));
    }
}