package cn.people.one.modules.guestbook.model;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

/**
 * 反馈
 * Created by sunday on 2017/4/11.
 */
@Table("cms_guestbook")
@Data
public class Guestbook extends BaseEntity {

    @Column
    @ColDefine(width = 255)
    @Comment("留言分类（咨询、建议、投诉、其它)")
    @ApiModelProperty(value = "留言分类（咨询、建议、投诉、其它")
    private String type;

    @Column
    @ColDefine(width = 2000)
    @Comment("留言内容")
    @ApiModelProperty(value = "留言内容")
    private String content;

    @Column
    @ColDefine(width = 500)
    @Comment("图片地址")
    @ApiModelProperty(value = "图片地址")
    private String images;

    @Column(hump = true)
    @ColDefine(width = 2000)
    @Comment("联系方式")
    @ApiModelProperty(value = "联系方式")
    private String contactInfo;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("手机型号")
    @ApiModelProperty(value = "手机型号")
    public String deviceModel;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("分辨率")
    @ApiModelProperty(value = "分辨率")
    public String deviceSize;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("系统版本")
    @ApiModelProperty(value = "系统版本")
    public String deviceType;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("网络环境")
    @ApiModelProperty(value = "网络环境")
    public String netType;

    @Column
    @ColDefine(width = 255)
    @Comment("操作系统")
    @ApiModelProperty(value = "操作系统")
    public String osversion;

    @Column
    @ColDefine(width = 255)
    @Comment("版本")
    @ApiModelProperty(value = "版本")
    public String version;

    @Column
    @ColDefine(width = 100)
    @Comment("留言IP")
    @ApiModelProperty(value = "留言IP")
    private String ip;

    @Column
    @ColDefine(width = 255)
    @Comment("标题")
    @ApiModelProperty(value = "标题")
    private String title;
    
}
