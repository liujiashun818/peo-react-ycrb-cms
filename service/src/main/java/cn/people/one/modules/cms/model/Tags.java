package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * Created by sunday on 2018/10/16.
 */
@Table("cms_tags")
@Data
public class Tags extends BaseEntity {

	@ApiModelProperty(value = "标签名称")
    @Column
    @ColDefine(width = 50,type = ColType.VARCHAR, notNull = true)
    @Comment("标签名称")
    private String name;

	@ApiModelProperty(value = "备注")
    @Column
    @ColDefine(width = 500,type = ColType.VARCHAR)
    @Comment("备注")
    private String description;//备注

	@ApiModelProperty(value = "文章使用标签个数")
    @Column
    @ColDefine(type = ColType.INT)
    @Comment("统计文章数(冗余字段)")
    private Integer articleCount;//文章使用标签个数

}
