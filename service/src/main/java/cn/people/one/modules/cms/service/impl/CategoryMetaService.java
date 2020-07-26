package cn.people.one.modules.cms.service.impl;

import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.CategoryMeta;
import cn.people.one.modules.cms.service.ICategoryMetaService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by lml on 2017/5/3.
 */
@Service
@Transactional
public class CategoryMetaService extends BaseService<CategoryMeta> implements ICategoryMetaService {
    @Override
    @Transactional
    public void deleteMetas(String slug){
        if(StringUtils.isNotBlank(slug)){
            Cnd cnd = Cnd.where(CategoryMeta.FIELD_CODE,"=",slug);
            List<CategoryMeta> list = dao.query(CategoryMeta.class,cnd);
            list.forEach(categoryMeta -> delete(categoryMeta.getId()));
        }
    }
}
