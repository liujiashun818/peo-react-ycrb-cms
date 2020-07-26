package cn.people.one.modules.comment.model;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

/**
* 评论
* @author 周欣
*/
@Table("comment_comments")
@Data
public class Comments extends BaseEntity {

    @Column(hump = true)
    @Comment("主评论ID（当为主评论时为0）")
    @ApiModelProperty(value = "主评论ID（当为主评论时为0）")
    private Long parentId;

    @Column(hump = true)
    @Comment("栏目ID")
    @ApiModelProperty(value = "栏目ID")
    private Long categoryId;

    @Column
    @ColDefine(width = 2000)
    @Comment("评论内容")
    @ApiModelProperty(value = "评论内容")
    private String content;

    @Column
    @ColDefine(width = 255)
    @Comment("评论图片")
    @ApiModelProperty(value = "评论图片")
    private String image;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("评论类型 （文章 直播等）")
    @ApiModelProperty(value = "评论类型 （文章 直播等）")
    private String sysCode;

    @Column(hump = true)
    @Comment("文章（直播）ID")
    @ApiModelProperty(value = "文章（直播）ID")
    private String articleId;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("文章（直播）标题")
    @ApiModelProperty(value = "文章（直播）标题")
    private String title;

    @Column(hump = true)
    @Comment("用户类型 区分嘉宾、网友、主持人")
    @ApiModelProperty(value = "用户类型 区分嘉宾、网友、主持人")
    private String userType;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("OPENID")
    @ApiModelProperty(value = "OPENID")
    private String userOpenId;

    @Column(hump = true)
    @Comment("系统类型")
    @ApiModelProperty(value = "系统类型")
    private Integer userSysType;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("回复用户OPENID")
    @ApiModelProperty(value = "回复用户OPENID")
    private String replyUserOpenId;

    @Column(hump = true)
    @Comment("系统类型")
    @ApiModelProperty(value = "系统类型")
    private Integer replyUserSysType;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("用户名")
    @ApiModelProperty(value = "用户名")
    private String userName;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("用户头像")
    @ApiModelProperty(value = "用户头像")
    private String userIcon;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("用户ip")
    @ApiModelProperty(value = "用户ip")
    private String userIp;

    @Column(hump = true)
    @ColDefine(width = 255)
    @Comment("用户地域")
    @ApiModelProperty(value = "用户地域")
    private String area;

    @Column
    @Comment("点赞 默认0")
    @ApiModelProperty(value = "点赞 默认0")
    private Integer likes;

    @Column
    @Comment("跟帖楼层")
    @ApiModelProperty(value = "跟帖楼层")
    private Integer floor;

    @Column(hump = true)
    @Comment("我的评论是否正常显示 默认 0正常 1删除")
    @ApiModelProperty(value = "我的评论是否正常显示 默认 0正常 1删除")
    private Integer myCommentStatus;

    @Column(hump = true)
    @ColDefine(width = 100)
    @Comment("回复用户名")
    @ApiModelProperty(value = "回复用户名")
    private String replyUserName;

    @Column(hump = true)
    @Comment("回复评论ID")
    @ApiModelProperty(value = "回复评论ID")
    private Long replyCommentId;

    @Column(hump = true)
    @ColDefine(width = 1000)
    @Comment("管理员回复内容")
    @ApiModelProperty(value = "管理员回复内容")
    private String adminReply;

    @ApiModelProperty(value = "客户端显示的时间")
    private Long time;//客户端显示的时间
    
    @ApiModelProperty(value = "回复的谁")
    private String replyTo;//回复的谁
    
    @ApiModelProperty(value = "文章类型")
    private String type;//文章类型
    
    @ApiModelProperty(value = "分享地址")
    private String shareUrl;//分享地址

    @ApiModelProperty(value = "新版客户端显示时间(2018-11-13)")
    private Date date;//新版客户端显示时间(2018-11-13)

    public Integer getLikes() {
        if(this.likes==null){
            return 0;
        }
        return likes;
    }

    public String getUserType() {
        if(StringUtils.isBlank(this.userType)){
            return "net";
        }
        return userType;
    }
}