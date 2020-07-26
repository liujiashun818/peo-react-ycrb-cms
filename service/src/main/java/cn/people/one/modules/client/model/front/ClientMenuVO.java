package cn.people.one.modules.client.model.front;

import lombok.Data;

import java.util.List;

/**
 * Created by lml on 17-2-15.
 */
@Data
public class ClientMenuVO{

    private Long id;
    private boolean type;
    private Long categoryId;
    private String name;
    private String simpleName;
    private String slug;
    private String linkType;
    private String onclickIcon;//菜单图标 高亮
    private String icon;//菜单图标 灰
    private String links;
    private String systemType;//系统类型
    private String remark;//备注
    private String sort;
    private String viewType;//展示类型
    private String position;//位置
    private Integer delFlag;
    private Integer parentId;// 父级编号
    private String categoryName;
    private String isRedirect;//是否设置跳转
    private String redirectUrl;//跳转URL
    private Integer tabType;//菜单标识

    private List<ClientMenuVO> child;//子菜单

}
