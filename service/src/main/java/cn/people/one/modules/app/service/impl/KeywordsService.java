package cn.people.one.modules.app.service.impl;

import cn.people.one.modules.app.model.Keywords;
import cn.people.one.modules.app.service.IKeywordsService;
import cn.people.one.modules.base.service.impl.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* 搜索关键词Service
* @author Cheng
*/
@Service
@Transactional(readOnly = true)
@Slf4j
public class KeywordsService extends BaseService<Keywords> implements IKeywordsService {

    //项目启动时会监听队列中的消息并消费，此处只用来测试
    //@RabbitListener(queues = {"one-article-api-queue"})//可以监听多个队列
    public void processMessage(String message) throws Exception{//参数message是rabbitmq队列中的消息体
        log.info(message);
    }

    @Override
    public List<Keywords> appKeywordsList() {
        Sql sql = Sqls.create("SELECT id,title FROM app_keywords order by update_at desc");
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(Keywords.class));
        dao.execute(sql);
        List<Keywords> list = sql.getList(Keywords.class);
        return list;
    }
}