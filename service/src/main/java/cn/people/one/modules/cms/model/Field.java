package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

/**
 * 字段描述
 * @author lml
 */
@Table("cms_field")
@Data
@NoArgsConstructor
public class Field extends BaseEntity {

    @Column
    @ColDefine(width = 200)
    @Comment("字段名称")
    private String name;

    @Column
    @ColDefine(width = 200)
    @Comment("字段别名")
    private String slug;

    @Column
    @ColDefine(width = 20)
    @Comment("字段类型")
    private String type;

    @Column
    @ColDefine(width = 2000)
    @Comment("字段选项信息")
    private String options;

    @Column(hump = true)
    @Comment("字段组编号")
    private Long groupId;

    @Column
    @ColDefine(width = 200)
    @Comment("占位符")
    private String placeholder;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("默认值")
    private String defaultValue;

    @Column
    @ColDefine(width = 1000)
    @Comment("描述字段")
    private String description;

    @Column
    @Comment("效验信息")
    @ColDefine(width =1000)
    private String validate;

    @Column
    @ColDefine(width = 200)
    @Comment("展示逻辑")
    private String logic;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("单一或者多个实例")
    private String simpleOrMore;

    @Column(hump = true)
    @Comment("是否允许搜索")
    private Boolean isAllowSearch;

    private String fieldGroupName;
}