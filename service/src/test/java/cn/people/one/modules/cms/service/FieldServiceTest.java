package cn.people.one.modules.cms.service;

import cn.people.one.modules.cms.model.Field;
import cn.people.one.modules.cms.model.FieldGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lml on 2017/4/26.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class FieldServiceTest {

    @Autowired
    private IFieldGroupService fieldGroupService;
    @Autowired
    private IFieldService fieldService;

    @Test
    public void save(){
        Field field = new Field();
        field.setName("字段组6-字段1");
        field.setGroupId(6L);
        field.setSlug("fielld61");
        fieldService.save(field);
    }

    @Test
    public void update(){
        FieldGroup fieldGroup = fieldGroupService.fetch(1L);
        if(fieldGroup!=null){
            for(int i=0;i<fieldGroup.getFields().size();i++){
                fieldGroup.getFields().get(i).setName("更新-字段-"+i);
            }
            Field field = new Field();
            field.setName("字段添加");
            fieldGroup.getFields().add(field);
            fieldGroupService.save(fieldGroup);
        }

    }

    @Test
    public void del(){
        fieldService.delete(36L);
    }

}
