package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleMeta;
import cn.people.one.modules.cms.model.Field;
import cn.people.one.modules.cms.service.IArticleMetaService;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.IFieldService;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lml on 2016/12/23.
 */
@Service
@Transactional(readOnly = true)
public class ArticleMetaService extends BaseService<ArticleMeta> implements IArticleMetaService {
    @Autowired
    private BaseDao dao;
    @Autowired
    private IFieldService fieldService;
    @Autowired
    private IArticleService articleService;

    @Override
    @Transactional
    public void deleteMetas(String slug){
        if(StringUtils.isNotBlank(slug)){
            Cnd cnd = Cnd.where(ArticleMeta.FIELD_CODE,"=",slug);
            List<ArticleMeta>list = dao.query(ArticleMeta.class,cnd);
            //删除meta表中内容而不是伪删除,否则伪删除的内容在关联查询的时候也能查询到
            list.forEach(articleMeta -> {
                Article article = articleService.fetch(articleMeta.getArticleId());
                delete(articleMeta.getId());
            });
        }
    }

    @Override
    public List<String> getFieldsCode() {
        List<String>fieldNames = new ArrayList<>();
        Sql sql = Sqls.create("select distinct field_code from cms_article_meta where del_flag = @delFlag");
        sql.params().set(BaseEntity.FIELD_STATUS, ArticleMeta.STATUS_ONLINE);
        List list = list(sql);
        if(!Lang.isEmpty(list)){
            list.forEach(fieldCode ->{
                Field field = fieldService.fetch(fieldCode.toString());
                if(field!=null && field.getIsAllowSearch() && StringUtils.isNotBlank(field.getName())){
                    fieldNames.add(field.getName());
                }
            });
        }
        return fieldNames;
    }
}
