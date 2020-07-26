package cn.people.one.modules.ask.model;


import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;


@Table("ask_type")
@Data
public class AskType extends BaseEntity
{

    private static final long serialVersionUID = 8557281370241873660L;

    @Column
    @ColDefine(width = 255)
    @Comment("名称")
    private String name;
    @Column
    @ColDefine(width = 3)
    @Comment("排序")
    private Byte sort;
    
    @Column(hump=true)
    @ColDefine(width = 3)
    @Comment("是否使用：0否、1是，默认为1")
    private Boolean isUse;

}