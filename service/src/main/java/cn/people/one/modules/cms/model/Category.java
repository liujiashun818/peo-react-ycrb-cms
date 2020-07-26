package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.TreeEntity;
import cn.people.one.modules.file.FileFormateUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

/**
 * Created by lml on 2016/12/22.
 */
@Table("cms_category")
@Data
public class Category extends TreeEntity<Category> {

    @Column
    @Comment("栏目图片")
    private String image;    // 栏目图片
    @Column
    @Comment("链接")
    private String href;    // 链接
    @Column
    @Comment("描述")
    private String description; // 描述，填写有助于搜索引擎优化

    @Column
    @Comment("别名")
    private String slug;//别名

    @Column(hump = true)
    @Comment("栏目模型编号")
    private Long modelId;//栏目模型编号

    /**
     * 文章在列表上的展示类型
     */
    @Column(hump = true)
    @Comment("展示类型")
    private String viewType;

    @Column(hump = true)
    @Comment("机构编号")
    private Long officeId;//机构编号

    @Column(hump = true)
    @Comment("是否允许评论显示 1允许")
    private boolean allowViewComment;//是否允许评论显示

    @Column(hump = true)
    @Comment("评论自动上线 1允许")
    private Boolean isAutoOnline;//评论自动上线

    @ManyMany(relation = "t_category_field_group", from = "category_id", to = "field_group_id")
    List<FieldGroup> fieldGroups;//本身的扩展字段组 FIELDS

    @Many(field = "categoryId")
    private List<CategoryMeta> metas;

    @Override
    public void init() {
        if (isAutoOnline == null) {
            isAutoOnline = false;
        }
        super.init();
    }

    @Override
    public boolean equals(Object obj) {
        boolean flag = obj instanceof Category;
        if (flag) {
            Category category = (Category) obj;
            return category.getId() == this.getId();
        } else {
            return false;
        }
    }

    /**
     * 栏目图片处理
     * @return
     */
    public String getImage() {
        if(StringUtils.isNotBlank(this.image)){
            return FileFormateUtil.formatFileSrcToWeb(this.image);
        }
        return image;
    }

    /**
     * 冗余字段
     */
    private String model;//栏目模型

    /**
     * 常量
     */
    public static final String FIELD_GROUPS = "fieldGroups";
    public static final String MATAS = "metas";
    public static final Long DEFAULT_HELP_CATEGORY_MODELID=8L;

    /**
     * 静态常量
     */
    public static class Constant{
        public static final String SLUG="slug";//别名
        public static final String SLUG_VALUE="lifeService";//别名值
        public static final String DEL_FLAG="del_flag";//状态码
        public static final String NAME="name";//名称
    }
}
