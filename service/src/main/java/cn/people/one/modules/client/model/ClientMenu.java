package cn.people.one.modules.client.model;

import cn.people.one.modules.base.entity.TreeEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.entity.annotation.*;

/**
 * User: 张新征
 * Date: 2017/2/14 15:51
 * Description:
 */
@Data
@Table("client_menu")
@NoArgsConstructor
@Slf4j
public class ClientMenu extends TreeEntity<ClientMenu>{
    @Column
    @Default("NORMAL")
    @ColDefine(width = 20)
    @Comment("展示位置（默认NORMAL、首次置顶ONE_TOP、总是置顶ALWAYS_TOP、固定置顶FIX）")
    @ApiModelProperty(value = "展示位置")
    private String position;

    @Column(hump = true)
    @Comment("栏目id")
    @ApiModelProperty(value = "栏目id")
    private Long categoryId;

    @Column
    @Comment("菜单名称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(value = "菜单名称")
    private String name;

    @Column(hump = true)
    @Comment("菜单简称")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(value = "栏目id")
    private String simpleName;

    @Column
    @Comment("菜单别名")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(value = "菜单别名")
    private String slug;

    @Column(hump = true)
    @Comment("链接类型")
    @ColDefine(type = ColType.VARCHAR, width = 100)
    @ApiModelProperty(value = "链接类型")
    private String linkType;

    @Column(hump = true)
    @Comment("菜单图标 高亮")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(value = "菜单图标 高亮")
    private String onclickIcon;

    @Column
    @Comment("菜单图标 灰")
    @ColDefine(type = ColType.VARCHAR, width = 50)
    @ApiModelProperty(value = "菜单图标 灰")
    private String icon;

    @Column
    @Comment("菜单链接")
    @ColDefine(type = ColType.VARCHAR, width = 1000)
    @ApiModelProperty(value = "菜单链接")
    private String links;

    @Column(hump = true)
    @Comment("系统类型")
    @ColDefine(type = ColType.VARCHAR)
    @ApiModelProperty(value = "系统类型")
    private String systemType;//系统类型

    @Column
    @Comment("备注")
    @ColDefine(type = ColType.VARCHAR)
    @ApiModelProperty(value = "备注")
    private String remark;//备注

    @Column(hump = true)
    @Comment("展示类型")
    @ColDefine(type = ColType.VARCHAR)
    @ApiModelProperty(value = "展示类型")
    private String viewType;//展示类型

    @Column(hump = true)
    @Comment("是否设置跳转")
    @ColDefine(type = ColType.VARCHAR)
    @ApiModelProperty(value = "是否设置跳转")
    private String isRedirect;//是否设置跳转

    @Column(hump = true)
    @Comment("跳转URL")
    @ColDefine(type = ColType.VARCHAR)
    @ApiModelProperty(value = "跳转URL")
    private String redirectUrl;//跳转URL
    
    @Column(hump = true)
    @Comment("菜单标识")
    @ApiModelProperty(value = "菜单标识 1首页  2区县")
    private Integer tabType;//菜单标识
    
    public ClientMenu(Long id){super(id);}

}
