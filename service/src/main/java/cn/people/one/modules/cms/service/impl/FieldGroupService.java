package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Field;
import cn.people.one.modules.cms.model.FieldGroup;
import cn.people.one.modules.cms.service.IArticleMetaService;
import cn.people.one.modules.cms.service.ICategoryMetaService;
import cn.people.one.modules.cms.service.IFieldGroupService;
import cn.people.one.modules.cms.service.IFieldService;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 字段组Service
 *
 * @author lml
 */
@Service
@Transactional(readOnly = true)
public class FieldGroupService extends BaseService<FieldGroup> implements IFieldGroupService {

    @Autowired
    private IFieldService fieldService;
    @Autowired
    private IArticleMetaService articleMetaService;
    @Autowired
    private ICategoryMetaService categoryMetaService;

    @Autowired
    private BaseDao dao;

    @Transactional
    private void deleteFieldsRelation(FieldGroup fieldGroup) {
        List slugList= new ArrayList();
        fieldGroup.getFields().forEach(field -> {
            slugList.add(field.getSlug());
        });
        List<Field> fields = fieldService.findFieldsByGroupId(fieldGroup.getId());
        if (!Lang.isEmpty(fields)) {
            fields.forEach(field -> {
                field.setGroupId(null);
                fieldService.save(field);
                //如果新保存的组中包含了已有的字段slug则不清除，否则清除meta表中的扩展字段信息
                if(!slugList.contains(field.getSlug())){
                    articleMetaService.deleteMetas(field.getSlug());
                    categoryMetaService.deleteMetas(field.getSlug());
                }
            });
        }
    }

    private void saveRelation(Boolean isInsert, FieldGroup fieldGroup, String fieldName) {
        //先清除关联字段再更新
        if (!isInsert) {
            dao.clearLinks(dao.fetchLinks(dao.fetch(FieldGroup.class,fieldGroup.getId()), fieldName), fieldName);
        }
        dao.insertRelation(fieldGroup, fieldName);
    }

    @Override
    @Transactional
    public int delete(Long id) {
        List<Field> list = fieldService.findFieldsByGroupId(id);
        if (!Lang.isEmpty(list)) {
            list.stream().forEach(field -> {
                //扩展字段清理
                articleMetaService.deleteMetas(field.getSlug());
                categoryMetaService.deleteMetas(field.getSlug());
                //字段不做删除处理，清空对应的组id
                field.setGroupId(null);
                fieldService.save(field);
            });
        }
        //清除关联关系
        FieldGroup fieldGroup = dao.fetch(FieldGroup.class,id);
        dao.clearLinks(dao.clearLinks(fieldGroup, FieldGroup.CATEGORIES),FieldGroup.CATEGORY_MODELS);
        return vDelete(id);
    }

    @Override
    @Transactional
    public Object save(FieldGroup fieldGroup) {
        fieldGroup.setDelFlag(FieldGroup.STATUS_ONLINE);
        if (fieldGroup.getId() != null) {
            //清除原有关联字段
            deleteFieldsRelation(fieldGroup);
            //更新组关联栏目本身
            saveRelation(false, fieldGroup, FieldGroup.CATEGORIES);
            //更新组关联栏目模型
            saveRelation(false, fieldGroup, FieldGroup.CATEGORY_MODELS);
            super.save(fieldGroup);
            //组本身
        } else {
            super.save(fieldGroup);
            saveRelation(true, fieldGroup, FieldGroup.CATEGORIES);
            saveRelation(true, fieldGroup, FieldGroup.CATEGORY_MODELS);
        }
        fieldService.addFields(fieldGroup.getId(), fieldGroup.getFields());
        return fieldGroup;
    }

    /**
     * 获取字段组详情(字段信息 关联栏目 关联栏目模型)
     * @param id
     * @return
     */
    @Override
    public FieldGroup fetch(Long id) {
        //查找关联的栏目模型 栏目本身 
        FieldGroup fieldGroup = dao.fetch(FieldGroup.class, id);
        List<Field> fields = fieldService.findFieldsByGroupId(id);
        if (fieldGroup != null) {
            fieldGroup.setFields(fields);
        }
        fieldGroup = dao.fetchLinks(dao.fetchLinks(fieldGroup,FieldGroup.CATEGORIES),FieldGroup.CATEGORY_MODELS);
        return fieldGroup;
    }

    public List<FieldGroup> setInfo(List<FieldGroup> groups) {
        if (!Lang.isEmpty(groups)) {
                groups.forEach(fieldGroup -> fieldGroup.setFields(fieldService.findFieldsByGroupId(fieldGroup.getId()))
            );
        }
        return groups;
    }

    @Override
    public List<FieldGroup> getListByName(String name) {
        List<FieldGroup> groups = dao.query(tClass, getDelFlag(null).and("name", "like", "%" + name + "%"));
        groups.stream().forEach(fieldGroup -> {
            List<Field> list = fieldService.findFieldsByGroupId(fieldGroup.getId());
            fieldGroup.setFields(list);
        });
        return groups;
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer
            pageSize) {
        QueryResult result = super.listPage(pageNo, pageSize, getDelFlag(null));
        return result;
    }
}