package cn.people.one.appapi.vo;

import cn.people.one.appapi.provider.ad.model.WebView;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;

import java.util.Date;
import java.util.List;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class ArticleDetailVO {
	
	@ApiModelProperty(value = "主键id")
    private Long id;
	
	@ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
    private String sysCode;
	
	@ApiModelProperty(value = "文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID")
    private Long articleId;
	
	@ApiModelProperty(value = "文章类型 普通新闻，图片，音频，视频，快讯，直播，专题")
    private String type;
	
	@ApiModelProperty(value = "栏目ID")
    private Long categoryId;
	
	@ApiModelProperty(value = "标题")
    private String title;
	
	@ApiModelProperty(value = "列表标题")
    private String listTitle;
	
	@ApiModelProperty(value = "外部链接")
    private String link;
	
	@ApiModelProperty(value = "源文")
    private String source;
	
	@ApiModelProperty(value = "作者")
    private String authors;
	
	@ApiModelProperty(value = "简介")
    private String description;
	
	@ApiModelProperty(value = "点赞数")
    private Integer likes;
	
	@ApiModelProperty(value = "点击数")
    private Integer hits;
	
	@ApiModelProperty(value = "评论数")
    private Integer comments;
	
	@ApiModelProperty(value = "关键字")
    private String keywords;
	
	@ApiModelProperty(value = "标签")
    private String tags;
	
	@ApiModelProperty(value = "内容")
    private String content;
	
	@ApiModelProperty(value = "展示类型")
    private String viewType;
	
	@ApiModelProperty(value = "分享地址")
    private String shareUrl;
	
	@ApiModelProperty(value = "是否裁剪")
    private Boolean isTailor;//是否裁剪
	
	@ApiModelProperty(value = "语音播报开关")
    private Boolean isVoice;//语音播报开关

	@ApiModelProperty(value = "肩标题")
    private String introTitle;// 肩标题
	
	@ApiModelProperty(value = "副标题")
    private String subTitle;// 副标题

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;

    @ApiModelProperty(value = "是否显示头图")
    private Boolean isShowTopImg;//是否显示头图
    
    @ApiModelProperty(value = "帮的状态 1募集中  2募集结束  3捐助反馈")
	private Integer helpStatus;
	
	@ApiModelProperty(value = "帮的开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startDate;
	
	@ApiModelProperty(value = "帮的结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "帮的声明")
	private String helpState;

    private List<ArticleMediaVO> medias;
    private List<ArticleMetaVO> metas;
    private List<VoteVO> votes;
    private List<String> images;
    private List<ArticleMoreVideoVO> moreVideos;
    private JSONArray bannerUrl;
    private List<WebView> ads;
    private List<String> realImages;

    @ApiModelProperty(value = "相关推荐列表")
    private List<ArticleVO> recommondArticle;

    public String getDescription() {
        if(StringUtils.isNotBlank(description)){
            return StringEscapeUtils.unescapeHtml4(description);
        }
        return description;
    }
}
