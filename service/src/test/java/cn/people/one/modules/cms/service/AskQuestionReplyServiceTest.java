package cn.people.one.modules.cms.service;


import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import cn.people.one.modules.ask.model.front.PageAskQuestionQueryVO;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class AskQuestionReplyServiceTest
{

    @Autowired
    IAskQuestionReplyService ask;

    @Test
    public void test()
    {
        PageAskQuestionQueryVO query=new PageAskQuestionQueryVO();
//        query.setGovId(371);
//        QueryResult result=ask.searchAskQuestions(query);
//        log.info(JSON.toJSONString(result.getPager().getPageCount()));
//        log.info(JSON.toJSONString(result.getPager().getRecordCount()));
//        log.info(JSON.toJSONString(result.getPager()));
//        log.info(JSON.toJSONString(result.getList()));
        
        log.info("========================================");
        query.setGovId(11L);//371
        query.setTitle("太原");
        query.setQuestionContent("问题");
        query.setReplyContent("答复");
        query.setStatus("3");
        query.setPageSize(20);
        QueryResult result=ask.searchAskQuestions(query);
        log.info(JSON.toJSONString(result.getList().size()));
        result=ask.searchAskQuestions(query);
        log.info(JSON.toJSONString(result.getList().size()));
        result=ask.searchAskQuestions(query);
        log.info(JSON.toJSONString(result.getList().size()));
        result=ask.searchAskQuestions(query);
        log.info(JSON.toJSONString(result.getList().size()));
        //{"attachment":"attachment","commentsNum":3,"domainId":7,"falseZanNum":5,"falsecommentsNum":2,"headImage":"head","organization":"太原市人民政府办公厅","originalTitle":"关于太原市漪汾苑社区供暖问题","realUserName":"real","replyContent":"热心网友：\r\n    您好！首先非常感谢您对网民留言事业发展的关心。现就您提问题答复如下：\r\n    经了解，漪汾苑社区建于1992至1994年，共建有69栋楼，居住着13000多居民。建成至今已有20多年。目前漪汾苑社区一些楼房确实存在供热管网老化现象，导致楼座地下室管道漏水、住宅楼长期遭水浸泡。\r\n    今年太原市对老旧小区的提升改造方案中未将供暖改造纳入，万柏林区政府要求兴华街办积极与漪汾苑小区物业公司沟通，希望该物业提出解决办法。\r\n    在此，真诚地希望您对我们提出宝贵的意见和建议。\r\n","replyTime":1503024600000,"shareLogo":"share_logo","status":3,"title":"关于太原市漪汾苑社区供暖问题","typeId":6,"userName":"匿名网友","userPhone":"user_phone","zanNum":1}
        Map<String, Object> map=ask.getDetailById(1198028L);
        log.info(JSON.toJSONString(map));
    }
}
