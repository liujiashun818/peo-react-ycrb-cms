package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.CategoryMeta;

/**
 * Created by lml on 2017/5/3.
 */
public interface ICategoryMetaService extends IBaseService<CategoryMeta> {
    void deleteMetas(String slug);
}
