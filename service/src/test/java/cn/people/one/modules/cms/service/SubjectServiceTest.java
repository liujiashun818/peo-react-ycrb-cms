package cn.people.one.modules.cms.service;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.cms.model.Subject;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

/**
 * Created by lml on 2017/3/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class SubjectServiceTest {

    @Autowired
    private ISubjectService subjectService;

    @Autowired
    private BaseDao baseDao;

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 保存专题&区块
     */
    @Test
    public void saveSubjectTest() {
        for (int i = 0; i < 10; i++) {
            Subject subject = new Subject();
            subject.setParentId(0L);
            subject.setType(ArticleType.SUBJECT.value());
            subject.setTitle("专题测试" + i);
            subject.setShowTitle(true);
            subject.setWeight(30);
            //subject.setParentId(0);
            subject.setViewType("normal");
            subjectService.save(subject);
            for (int j = 0; j < 2; j++) {
                Subject block = new Subject();
                block.setParentId(subject.getId());
                block.setTitle("区块测试" + i + j);
                block.setViewType("banner");
                subjectService.save(block);
            }

        }
    }

    /**
     * 保存专题到栏目
     */
  /*  @Test
    public void saveSubjectToCategoryTest() {
        List<Subject> result = baseDao.query(Subject.class, Cnd.NEW().where("id", "<", "80").and("parent_id","=","0"));
        subjectService.saveSubjectToCategory(result.get(0), 1);
    }*/

    /**
     * 保存文章到专题
     */
    /*@Test
    public void saveArticleToSubjectTest() {
        List<Article> list = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            Article article = new Article();
            article.setTitle("专题文章保存"+i);
            article.setViewType("normal");
            list.add(article);
        }
        subjectService.saveArticleToSubject(list,61);
    }*/

    /**
     * 获取专题（区块）下面的文章
     */
    @Test
    public void findArticlesInSubjectTest() throws IOException {
        ArticleVO articleVO = new ArticleVO();
        articleVO.setCategoryId(1L);
        QueryResult result = subjectService.findArticlesInSubject(1,10,articleVO);
        objectMapper.writeValueAsString(result);
    }

}
