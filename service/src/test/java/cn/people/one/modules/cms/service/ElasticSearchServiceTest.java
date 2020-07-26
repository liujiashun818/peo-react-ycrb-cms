package cn.people.one.modules.cms.service;

import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.search.service.impl.ElasticSearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2017/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ElasticSearchServiceTest {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @Test
    public void saveTest(){
        Article article = new Article();
        article.setTitle("opopoip");
        article.setId(1111L);
       // elasticSearchService.saveHandle(article);
    }
}
