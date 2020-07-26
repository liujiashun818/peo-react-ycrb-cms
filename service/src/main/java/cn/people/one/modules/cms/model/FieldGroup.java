package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * 字段组
 *
 * @author lml
 */
@Table("cms_field_group")
@Data
public class FieldGroup extends BaseEntity {

    @Column
    @ColDefine(width = 200)
    @Comment("字段组名称")
    private String name;

    @Column
    @ColDefine(width = 100)
    @Comment("字段组描述")
    private String description;

    private List<Field> fields;

    @ManyMany(relation = "t_category_field_group", from = "field_group_id", to = "category_id")
    List<Category> categories;//所属栏目组

    @ManyMany(relation = "t_category_model_field_group", from = "field_group_id", to = "category_model_id")
    List<CategoryModel> categoryModels;//字段组 FIELDS

    public static final String CATEGORIES = "categories";
    public static final String CATEGORY_MODELS = "categoryModels";

}