package cn.people.one.modules.cms.service;

import cn.people.one.modules.cms.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by lml on 2016/12/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
//@Transactional
@Slf4j
public class CategoryServiceTest {

    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IFieldGroupService fieldGroupService;
    @Autowired
    private IFieldService fieldService;

    @Test
    public void save(){
        //2
        Category recommend = new Category();
        recommend.setParentId(1L);
        recommend.setName("推荐栏目");
        recommend.setSlug("recommend");
        recommend.setModel("normal");
        categoryService.save(recommend);

        //3
        Category news = new Category();
        news.setParentId(2L);
        news.setName("热点");
        news.setSlug("news");
        recommend.setModel("normal");
        categoryService.save(news);
    }

    @Test
      public void fetch(){
            Category category = categoryService.fetch(1L);
            log.info(category.toString());
      }

}
