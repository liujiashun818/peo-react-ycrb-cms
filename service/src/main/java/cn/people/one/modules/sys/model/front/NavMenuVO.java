package cn.people.one.modules.sys.model.front;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Cheng on 2017/2/21.
 */
@Data
@ApiModel
public class NavMenuVO implements Serializable {
    @ApiModelProperty(value = "菜单ID ")
    private Long id;
    @ApiModelProperty(value = "菜单key值")
    private String key;//
    @ApiModelProperty(value = "菜单编码 ")
    private String code;///key
    @ApiModelProperty(value = "菜单名称")
    private String name;
    @ApiModelProperty(value = "菜单图标 ")
    private String icon;
    @ApiModelProperty(value = "菜单类型 ")
    private String type;
    @ApiModelProperty(value = "菜单地址 ")
    private String href;
    @ApiModelProperty(value = "菜单排序")
    private String sort;
    @ApiModelProperty(value = "是否显示 ")
    private boolean isShow;
    @ApiModelProperty(value = "上级菜单ID ")
    private Integer parentId;// 父级编号
    @ApiModelProperty(value = "权限标识 ")
    private String permission;
    @ApiModelProperty(value = "状态标记(0：正常；3：删除；) ")
    private Integer delFlag;
    @ApiModelProperty(value = "子菜单列表 ")
    private List<NavMenuVO> child;//子菜单
}
