package cn.people.one.modules.client.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.file.FileFormateUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * Created by sunday on 2018/9/25.
 */
@Table("mobile_floating_imgs")
@Data
public class FloatingImgs extends BaseEntity {

    @Column
    @ColDefine(width = 255)
    @Comment("名称")
    @ApiModelProperty(value = "名称")
    private String name;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("图片地址")
    @ApiModelProperty(value = "图片地址")
    private String imgUrl;

    @Column
    @ColDefine(width = 20)
    @Comment("首页是否显示")
    @ApiModelProperty(value = "首页是否显示")
    private String isShow;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("跳转类型")
    @ApiModelProperty(value = "跳转类型")
    private String type;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("状态")
    @ApiModelProperty(value = "状态")
    private String status;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("跳转地址")
    @ApiModelProperty(value = "跳转地址")
    private String redirectUrl;

    @Column(hump = true)
    @Comment("栏目ID")
    @ApiModelProperty(value = "栏目ID")
    private Long categoryId;

    @Column(hump = true)
    @Comment("文章ID")
    @ApiModelProperty(value = "文章ID")
    private Long articleId;

    @ApiModelProperty(value = "栏目名称")
    private String categoryName;//栏目名称
    
    @ApiModelProperty(value = "真实菜单栏目ID")
    private String realCategoryId;//真实菜单栏目ID
    
    @ApiModelProperty(value = "当为APP栏目类型时是否含有子栏目")
    private Boolean isHaveCategoryChild;//当为APP栏目类型时是否含有子栏目
    
    @ApiModelProperty(value = "栏目链接")
    private String categoryHref;//栏目链接
    
    @ApiModelProperty(value = "栏目所对应的客户端菜单ID")
    private Long clientMenuId;//栏目所对应的客户端菜单ID
    
    @ApiModelProperty(value = "文章类型")
    private String articleType;//文章类型
    
    @ApiModelProperty(value = "文章标题")
    private String articleTitle;//文章标题
    
    @ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
    private String sysCode;
    
    @ApiModelProperty(value = "创建时间")
    private Long createAt;//
    
    @ApiModelProperty(value = "创建人")
    private Long createBy;//文章标题
    
    @ApiModelProperty(value = "修改时间")
    private Long updateAt;//
    
    @ApiModelProperty(value = "修改人")
    private Long updateBy;//文章标题
    
  /**  
   
    
    
    **/
    public String getImgUrl() {
        return FileFormateUtil.formatFileSrcToWeb(this.imgUrl);
    }
}
