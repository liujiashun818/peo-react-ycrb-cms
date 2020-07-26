package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by lml on 2017/5/3.
 */
@Table("cms_category_meta")
@TableIndexes({@Index(name = "INDEX_CMS_CATEGORY_META_CATEGORY_ID", fields = {"categoryId"}, unique = false)})
@Data
public class CategoryMeta extends BaseEntity {
    public CategoryMeta(){
        this.setDelFlag(BaseEntity.STATUS_ONLINE);
    }

    @Column(hump=true)
    private Long categoryId;

    @One(field="categoryId")
    private Category category;

    @Column(hump=true)
    @ColDefine(width = 200)
    @Comment("字段编号")
    private String fieldCode;

    @Column(hump=true)
    @ColDefine(width = 500)
    @Comment("字段值")
    private String fieldValue;

    public static String FIELD_CODE = "fieldCode";

}
