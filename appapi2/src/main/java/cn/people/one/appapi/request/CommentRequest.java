package cn.people.one.appapi.request;

import lombok.Data;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

import cn.people.one.modules.cms.model.front.MediaResourceVO;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by wilson on 2018-10-16.
 */
@Data
public class CommentRequest {
	
	@ApiModelProperty(value = "评论文章id")
    private String articleId;
	
	@ApiModelProperty(value = "栏目ID")
    private Long categoryId;
	
	@ApiModelProperty(value = "评论id")
    private Long commentId;
	
	@ApiModelProperty(value = "标题")
    private String title;
	
	@ApiModelProperty(value = "评论内容")
    private String content;
	
	@ApiModelProperty(value = "图片地址")
    private List<String> imageUrls;
	
	@ApiModelProperty(value = "回复评论ID")
    private Integer replyCommentId;
	
	@ApiModelProperty(value = "回复用户openId")
    private String replyUserOpenId;
	
	@ApiModelProperty(value = "系统类型")
    private Integer replyUserSysType;
	
	@ApiModelProperty(value = "评论类型 （文章 直播等)")
    private String sysCode;
	
	@ApiModelProperty(value = "用户ClientId")
    private String userClientId;
	
	@ApiModelProperty(value = "用户OpenId")
    private String userOpenId;
	
	@ApiModelProperty(value = "用户系统类型")
    private Integer userSysType;
	
	@ApiModelProperty(value = "用户令牌")
    private String userToken;
	
	@ApiModelProperty(value = "回复用户名")
    private String replyUserName;
	
	@ApiModelProperty(value = "用户名")
    private String userName;
	
	@ApiModelProperty(value = "用户IP")
    private String userIp;
	
	@ApiModelProperty(value = "用户头像")
    private String userIcon;
	
	@ApiModelProperty(value = "用户类型 区分嘉宾、网友、主持人")
    private String userType;
	
}
