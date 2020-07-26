package cn.people.one.modules.ask.service;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.cms.model.*;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.vo.ArticleTestVO;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.mockdata.JMockDataManager;
import com.sohu.idcenter.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lml on 2016/12/26.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class askServiceTest {
    @Autowired
    IAskQuestionReplyService askQuestionReplyService;

    @Before
    public void initMockConfig(){
        JMockDataManager.getInstance().config("mock.properties");
    }


    @Test
    public void getDetailTest(){
        AskQuestionReply askQuestionReply = askQuestionReplyService.getFirstRdForCheck("危房改造和精准扶贫");
        log.info("getDetailTest {} ",askQuestionReply.getTitle());
    }
}
