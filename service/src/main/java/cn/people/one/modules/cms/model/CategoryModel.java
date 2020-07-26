package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;

import java.util.List;

/**
 * Created by lml on 2017/4/28.
 */
@NoArgsConstructor
@Data
@Table("cms_category_model")
public class CategoryModel extends BaseEntity {
    @Comment("模型名称")
    @Column
    private String name;//模型名称

    @ManyMany(relation = "t_category_model_field_group", from = "category_model_id", to = "field_group_id")
    List<FieldGroup> fieldGroups;//字段组 FIELDS

    public static final String FIELD_GROUPS = "fieldGroups";
}
