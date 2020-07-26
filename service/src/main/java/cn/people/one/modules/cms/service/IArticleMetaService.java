package cn.people.one.modules.cms.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.cms.model.ArticleMeta;

import java.util.List;

/**
 * Created by lml on 2016/12/23.
 */
public interface IArticleMetaService extends IBaseService<ArticleMeta> {

    void deleteMetas(String slug);

    List<String> getFieldsCode();
}
