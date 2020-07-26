package cn.people.one.appapi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * Created by wilson on 2018-10-16.
 */
@Data
public class CommentVO {
	
	@ApiModelProperty(value = "id")
    private Long id;
	
	@ApiModelProperty(value = "用户名")
    private String username;
	
	@ApiModelProperty(value = "内容")
    private String content;
	
	@ApiModelProperty(value = "用户openId")
    private String userOpenId;
	
	@ApiModelProperty(value = "系统编码")
    private String sysCode;
	
	@ApiModelProperty(value = "新闻类型")
    private String articleType;
	
	@ApiModelProperty(value = "新闻id")
    private String articleId;
	
	@ApiModelProperty(value = "栏目id")
    private Long categoryId;
	
	@ApiModelProperty(value = "标题")
    private String title;
	
	@ApiModelProperty(value = "点赞数")
    private int likes;
	
	@ApiModelProperty(value = "用户头像")
    private String userIcon;
	
	@ApiModelProperty(value = "日期")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;
	
	@ApiModelProperty(value = "回复评论id")
    private CommentVO parent;
	
	@ApiModelProperty(value = "是否删除")
    private boolean isDeleted;

	@ApiModelProperty(value = "评论图片")
	private String image;
}
