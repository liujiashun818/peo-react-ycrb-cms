package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.base.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by lml on 2017/1/10.
 */
@Data
public class ArticleDetailVO extends BaseEntity implements Serializable {
	
	@ApiModelProperty(value = "查询起始时间")
    private Long categoryId;  //分类编号
	
	@ApiModelProperty(value = "查询起始时间")
    private String authors;	//作者
	
	@ApiModelProperty(value = "查询起始时间")
    private String content; //内容
	
	@ApiModelProperty(value = "查询起始时间")
    private String copyfrom;	//来源
	
	@ApiModelProperty(value = "查询起始时间")
    private String description; //摘要
	
	@ApiModelProperty(value = "查询起始时间")
    private Long id;	//文章编号
	
	@ApiModelProperty(value = "查询起始时间")
    private String image;	//图片
	
	@ApiModelProperty(value = "查询起始时间")
    private String keywords; //关键字
	
	@ApiModelProperty(value = "查询起始时间")
    private String link;	//文章链接
	
	@ApiModelProperty(value = "查询起始时间")
    private Date publishDate; //发布时间
	
	@ApiModelProperty(value = "查询起始时间")
    private String title; //标题
	
	@ApiModelProperty(value = "查询起始时间")
    private String relation;
	
	@ApiModelProperty(value = "给前端返回相关链接id,title组成的map")
    private List<Map<String,Object>> relationMap;//给前端返回相关链接id,title组成的map
	
	@ApiModelProperty(value = "媒体类型")
    private String mediaType;//媒体类型
	
	@ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
    private String sysCode;
	
	@ApiModelProperty(value = "文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID")
    private Long articleId;
	
	@ApiModelProperty(value = "分享地址")
    private String shareUrl;
	
	@ApiModelProperty(value = "文章类型 普通新闻，图片，音频，视频，快讯，直播，专题")
    private String type;
}
