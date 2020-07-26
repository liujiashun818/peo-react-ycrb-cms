package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.base.entity.TreeEntity;
import cn.people.one.modules.client.model.ClientMenu;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CategoryVO extends TreeEntity<CategoryVO> implements Serializable{

	@ApiModelProperty(value = "子菜单")
    private List<CategoryVO> child;//子菜单
	
	@ApiModelProperty(value = "栏目模型")
    private String model;//栏目模型
	
	@ApiModelProperty(value = "机构编号")
    private Long officeId;//机构编号
	
	@ApiModelProperty(value = "组织机构名称")
    private String officeName;//组织机构名称
	
	@ApiModelProperty(value = "排序")
    private Integer sort;
	
	@ApiModelProperty(value = "父级编号")
    private Long parentId;// 父级编号
	
	@ApiModelProperty(value = "状态码")
    private Integer delFlag;
	
	@ApiModelProperty(value = "")
    private Integer modelId;
	
	@ApiModelProperty(value = "评论自动上线 1允许")
    private Boolean isAutoOnline;
	
	@ApiModelProperty(value = "关联的菜单")
    private List<ClientMenu> menuList;//关联的菜单
}
