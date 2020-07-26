package cn.people.one.appapi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by wilson on 2018-10-09.
 */
@Data
@ApiModel(value = "Menu", description = "菜单实体对象")
public class MenuVO {

	@ApiModelProperty(value = "id")
    private Long id;
	
	@ApiModelProperty(value = "栏目id")
    private Long categoryId;
	
	@ApiModelProperty(value = "父级编号")
    private Integer parentId;
	
	@ApiModelProperty(value = "展示位置（默认NORMAL、首次置顶ONE_TOP、总是置顶ALWAYS_TOP、固定置顶FIX）")
    private String position;
	
	@ApiModelProperty(value = "菜单名称")
    private String name;
	
	@ApiModelProperty(value = "菜单简称")
    private String simpleName;
	
	@ApiModelProperty(value = "菜单别名")
    private String slug;
	
	@ApiModelProperty(value = "链接类型")
    private String linkType;
	
	@ApiModelProperty(value = "菜单图标 高亮")
    private String onclickIcon;
	
	@ApiModelProperty(value = "菜单图标 灰")
    private String icon;
	
	@ApiModelProperty(value = "菜单链接")
    private Map links;
	
	@ApiModelProperty(value = "系统类型")
    private String systemType;//系统类型
	
	@ApiModelProperty(value = "备注")
    private String remark;//备注
	
	@ApiModelProperty(value = "展示类型")
    private String viewType;//展示类型
	
	@ApiModelProperty(value = "排序")
    private Integer sort;
	
	@ApiModelProperty(value = "分组编码")
    private String groupCode;
	
	@ApiModelProperty(value = "分组名称")
    private String groupName;
	
	@ApiModelProperty(value = "子菜单")
    private List<MenuVO> children;
	
	@ApiModelProperty(value = "是否设置跳转")
    private String isRedirect;//是否设置跳转
	
	@ApiModelProperty(value = "跳转URL")
    private String redirectUrl;//跳转URL

}
