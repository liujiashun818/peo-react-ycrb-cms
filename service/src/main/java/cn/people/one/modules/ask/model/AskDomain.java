package cn.people.one.modules.ask.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import lombok.Data;
@Table("ask_domain")
@Data
public class AskDomain extends BaseEntity {
	private static final long serialVersionUID = 6800180001003953472L;
    @Column
    @ColDefine(width = 255)
    @Comment("标题")
    @ApiModelProperty(value = "标题")
    private String name;
    @Column
    @ColDefine(width = 3,type=ColType.INT)
    @Comment("排序")
    @ApiModelProperty(value = "排序")
    private Byte sort;
    @Column
    @ColDefine(width = 1,type=ColType.INT)
    @Comment("是否使用：0否、1是，默认为1")
    @ApiModelProperty(value = "是否使用：0否、1是，默认为1")
    private Boolean isUse;
    
}