package cn.people.one.modules.sys.model;

import cn.people.one.modules.base.entity.TreeEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutz.dao.entity.annotation.*;

@Table("sys_menu")
@TableIndexes({@Index(name = "INDEX_SYS_MENU_PREM", fields = {"permission"}, unique = true)})
@Data
@NoArgsConstructor
public class Menu extends TreeEntity<Menu> {

    @Column
    @ColDefine(width = 100)
    @Comment("编码")
    private String code;

    @Column
    @ColDefine(width = 200)
    @Comment("资源路径")
    private String href;

    @Column
    @ColDefine(width = 50)
    @Comment("目标,例子：mainFrame、_blank、_self、_parent、_top")
    private String target;

    @Column
    @ColDefine(width = 100)
    @Comment("图标")
    private String icon;

    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("资源类型，[menu|button]")
    private String resourceType;

    @Column
    @ColDefine(width = 200)
    @Comment("权限标示,menu例子：role:*，button例子：role:create,role:update,role:delete,role:view")
    private String permission;

    @Column(hump = true)
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示")
    private boolean isShow;

    public Menu(Long id){super(id);}

}
