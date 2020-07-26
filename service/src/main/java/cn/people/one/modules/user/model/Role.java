package cn.people.one.modules.user.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.sys.model.Menu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.List;

@ApiModel
@Table("sys_role")
@Data
public class Role extends BaseEntity {
	
	@ApiModelProperty(value = "角色名称")
    @Column
    @ColDefine(width = 100)
    @Comment("角色名称")
    @Name
    private String name;

	@ApiModelProperty(value = "数据范围")
    @Column(hump = true)
    @ColDefine(type = ColType.INT)
    @Comment("数据范围")
    private Integer dataScope;

	@ApiModelProperty(value = "机构ID")
    @Column(hump = true)
    @ColDefine(type = ColType.INT)
    @Comment("机构ID")
    private Long officeId;

	@ApiModelProperty(value = "备注")
    @Column
    @ColDefine(width = 100)
    @Comment("备注")
    private String remark;

	@ApiModelProperty(value = "机构名称")
    @Comment("机构名称")
    private String officeName;

	@ApiModelProperty(value = "")
    private List<Long> menuIds;

	@ApiModelProperty(value = "")
    @ManyMany(from = "roleid", relation = "sys_role_office", target = Office.class, to = "officeid")
    private List<Office> offices;

	@ApiModelProperty(value = "")
    @ManyMany(from = "roleid", relation = "sys_role_menu", target = Menu.class, to = "menuid")
    private List<Menu> menus;

	@ApiModelProperty(value = "")
    @ManyMany(from = "roleid", relation = "sys_user_role", target = User.class, to = "userid")
    private List<User> users;

    public static final Integer DATA_SCOPE_OWNER = 4;

    public static final String OFFICES = "offices";
    public static final String MENUS = "menus";
    public static final String USERS = "users";
}
