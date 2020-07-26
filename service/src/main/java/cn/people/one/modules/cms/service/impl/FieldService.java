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

import java.util.List;

/**
 * 字段描述Service
 *
 * @author lml
 */
@Service
@Transactional(readOnly = true)
public class FieldService extends BaseService<Field> implements IFieldService {

    @Autowired
    private BaseDao dao;
    @Autowired
    private IArticleMetaService articleMetaService;
    @Autowired
    private ICategoryMetaService categoryMetaService;
    @Autowired
    private IFieldGroupService fieldGroupService;

    @Override
    public List<Field> findFieldsByGroupId(Long groupId) {
        return query(null, getDelFlag(null).and("group_id", "=", groupId));
    }

    @Transactional
    @Override
    public void addFields(Long groupId, List<Field> fields) {
        if (!Lang.isEmpty(fields)) {
            fields.stream().forEach(field -> {
                field.setDelFlag(Field.STATUS_ONLINE);
                field.setGroupId(groupId);
                save(field);
            });
        }
    }

    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        QueryResult result = listPage(pageNo, pageSize, getDelFlag(null));
        List<Field>fields = (List<Field>)result.getList();
        if(result!=null && !Lang.isEmpty(fields)){
            fields.forEach(field->{
                if(field.getGroupId()!=null){
                    FieldGroup group = dao.fetch(FieldGroup.class,field.getGroupId());
                    if(group!=null){
                        field.setFieldGroupName(group.getName());
                    }
                }
            });
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(Long id) {
        Field field = fetch(id);
        if(field==null){
            return 0;
        }
        articleMetaService.deleteMetas(field.getSlug());
        categoryMetaService.deleteMetas(field.getSlug());
        return vDelete(id);
    }

    public Boolean slugExist(String slug){
        List<Field> list = dao.query(Field.class, getDelFlag(null).and("slug","=",slug));
        if(!Lang.isEmpty(list)){
            return true;
        }
        return false;
    }

}