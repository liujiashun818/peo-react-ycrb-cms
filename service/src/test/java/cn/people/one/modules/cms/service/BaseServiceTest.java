package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhangxinzheng on 2017/1/17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class BaseServiceTest {

    @Autowired
    private BaseService<Article> baseService;

    @Test
    public void save() throws Exception {
        Article article = new Article();
        article.setTitle("test title");
        article.setType("quote");
        article.setViewType("viewType");
        article.setCategoryId(1L);
        article.setListTitle("listTitle");
        article.setSysCode("sysCode");
        ArticleData articleData = new ArticleData();
        articleData.setContent("test content");
        article.setArticleData(articleData);
        article.setCategoryId(1L);
        article.setArticleId(1L);
        baseService.save(article);
    }

    @Test
    public void count() throws Exception {
        assert(baseService.count(Sqls.create("select * from cms_article")) >= 0);
    }


    @Test
    public void delete() throws Exception {
        assert(baseService.delete(1L) == 1);
    }

    @Test
    public void getMaxId() throws Exception {
        assert(baseService.getMaxId() >= 0);
    }

    @Test
    public void vDelete() throws Exception {
        baseService.vDelete(1L);
    }

    @Test
    public void query() throws Exception {
        Cnd cnd = Cnd.NEW();
        assert(baseService.query("title", cnd.where("id",">=",1)).size() > 0);
    }


    @Test
    public void count1() throws Exception {
        Cnd cnd = Cnd.NEW();
        assert(baseService.count("cms_article", cnd.where("id", "<", 0)) == 0);
    }

    @Test
    public void list() throws Exception {
       assert(baseService.list(Sqls.create("select * from cms_article")).size() >= 0);
    }


    @Test
    public void findByIds() throws Exception {
        assert(baseService.findByIds("1,2,3").size() >= 0);
    }

}