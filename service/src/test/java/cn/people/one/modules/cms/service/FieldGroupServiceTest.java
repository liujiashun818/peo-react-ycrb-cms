package cn.people.one.modules.cms.service;

import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.CategoryModel;
import cn.people.one.modules.cms.model.Field;
import cn.people.one.modules.cms.model.FieldGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lml on 2017/5/2.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class FieldGroupServiceTest {

    @Autowired
    private IFieldGroupService fieldGroupService;

    @Test
    public void saveTest(){
        FieldGroup fieldGroup = new FieldGroup();

        List<Category>categories = new ArrayList<>();
        Category category = new Category();
        category.setId(1L);
        categories.add(category);
        Category category1 = new Category();
        category1.setId(2L);
        categories.add(category1);
        fieldGroup.setCategories(categories);

        List<CategoryModel>categoryModels = new ArrayList<>();
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.setId(1L);
        categoryModels.add(categoryModel);
        CategoryModel categoryModel1 = new CategoryModel();
        categoryModel1.setId(2L);
        categoryModels.add(categoryModel1);
        fieldGroup.setCategoryModels(categoryModels);

        fieldGroup.setName("字段组2");
        List<Field> fields = new ArrayList<>();
        Field field1 = new Field();
        field1.setName("字段21");
        field1.setSlug("field21");
        fields.add(field1);

        Field field2 = new Field();
        field2.setName("字段22");
        field2.setSlug("field22");
        fields.add(field2);
        fieldGroup.setFields(fields);
        fieldGroupService.save(fieldGroup);
    }

    @Test
    public void updateTest(){
        FieldGroup fieldGroup = fieldGroupService.fetch(7L);
        if(fieldGroup!=null){
            fieldGroup.setName("字段组2-更新");
            List<Category>categories = fieldGroup.getCategories();
            if(!Lang.isEmpty(categories)){
                categories.remove(0);
                Category category = new Category();
                category.setId(1L);
                categories.add(category);
            }
            List<CategoryModel>categoryModels = fieldGroup.getCategoryModels();
            if(!Lang.isEmpty(categoryModels)) {
                categoryModels.remove(0);
                CategoryModel categoryModel = new CategoryModel();
                categoryModel.setId(1L);
                categoryModels.add(categoryModel);
            }
            fieldGroup.setName("字段组2");
            List<Field> fields = fieldGroup.getFields();
            if(!Lang.isEmpty(fields)) {
                fields.remove(0);
                Field field1 = new Field();
                field1.setName("字段23");
                field1.setSlug("field23");
                fields.add(field1);
            }
            fieldGroupService.save(fieldGroup);
        }
    }

    @Test
    public void deleteTest(){
        fieldGroupService.delete(1L);
    }

    @Test
    public void fetch(){
        FieldGroup fieldGroup = fieldGroupService.fetch(7L);
        log.info(fieldGroup.toString());
    }

}
