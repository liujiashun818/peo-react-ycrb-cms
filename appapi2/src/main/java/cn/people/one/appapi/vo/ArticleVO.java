package cn.people.one.appapi.vo;

import cn.people.one.appapi.provider.ad.model.AdImage;
import cn.people.one.modules.cms.model.Article;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.emory.mathcs.backport.java.util.Arrays;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by wilson on 2018-10-10.
 */
@Data
public class ArticleVO {
	
	@ApiModelProperty(value = "id")
    private Long id;
	
	@ApiModelProperty(value = "\"文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID\"")
    private Long articleId;
	
	@ApiModelProperty(value = "栏目ID")
    private Long categoryId;
	
	@ApiModelProperty(value = "区块 1. 头图 2.列表 3.待选 同一个栏目下可能有不同文章区块，如客户端的头图区和列表区")
    private Integer block;
	
	@ApiModelProperty(value = "标识")
    private Integer delFlag;
	
	@ApiModelProperty(value = "标题")
    private String title;
	
	@ApiModelProperty(value = "列表标题")
    private String listTitle;
	
	@ApiModelProperty(value = "关键字")
    private String keywords;
	
	@ApiModelProperty(value = "文章类型")
    private String type;//文章类型
	
	@ApiModelProperty(value = "是否在专题中")
    private Boolean inSubject;
	
	@ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
    private String sysCode;
	
	@ApiModelProperty(value = "作者")
    private String authors;
	
	@ApiModelProperty(value = "扩展字段名称")
    private String fieldName;//扩展字段名称
	
	@ApiModelProperty(value = "扩展字段值")
    private String fieldValue;//扩展字段值
	
    private Integer pageNumber;
    private Integer pageSize;
    
    @ApiModelProperty(value = "降序检索字段")
    private String desc; //降序检索字段
    
    @ApiModelProperty(value = "升序检索字段")
    private String asc; //升序检索字段
    
    @ApiModelProperty(value = "分享地址")
    private String shareUrl;
    @ApiModelProperty(value = "来源")
    private String source;
    
    @ApiModelProperty(value = "缩略图片")
    private String imageUrl;
    
    @ApiModelProperty(value = "摘要")
    private String description;
    
    @ApiModelProperty(value = "权重 权重越大越靠前")
    private Integer weight;
    
    @ApiModelProperty(value = "点赞数")
    private Integer likes;
    
    @ApiModelProperty(value = "点击数")
    private Integer hits;
    
    @ApiModelProperty(value = "评论数")
    private Integer comments;
    
    @ApiModelProperty(value = "标签")
    private String tags;
    
    @ApiModelProperty(value = "展示类型")
    private String viewType;
    
    @ApiModelProperty(value = "是否裁剪")
    private Boolean isTailor;//是否裁剪
    
    @ApiModelProperty(value = "语音播报开关")
    private Boolean isVoice;//语音播报开关
    
    @ApiModelProperty(value = "")
    private Integer place;

    @ApiModelProperty(value = "链接类型")
    private Integer linkType;
    
    @ApiModelProperty(value = "链接")
    private String link;
    
    @ApiModelProperty(value = "广告图片列表")
    private List<AdImage> adImages;
    
    @ApiModelProperty(value = "肩标题")
    private String introTitle;// 肩标题
    
    @ApiModelProperty(value = "副标题")
    private String subTitle;// 副标题
    private String request_id;// request_id
    
    @ApiModelProperty(value = "起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date beginTime;//查询起始时间

    @ApiModelProperty(value = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date endTime;//查询截止时间

    @ApiModelProperty(value = "有效时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date liveTime;

    @ApiModelProperty(value = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;

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

    @ApiModelProperty(value = "帮的推荐标识")
    private Integer helpFlag;
   	
    private List<String> images;
    private List<String> realImages;
    private List<ArticleMediaVO> medias;
    private List<ArticleMetaVO> metas;
    private List<VoteVO> votes;
    private List<String> liveGuests;
    private String liveStatus;
    private String liveVideo;
    private String liveImage;
    private List<ArticleMoreVideoVO> moreVideos;
    private JSONArray bannerUrl;
    /**
     * 直播类型 1有视频无图 2有图无视频 3无图无视频
     */
    private Integer liveType;
    public ArticleVO() {
        inSubject = false;
    }

    /**
     * 将Article转为ArticleVO
     *
     * @param article
     * @return
     */
    public static ArticleVO transArticle(Article article) {
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article,articleVO,"images","realImages","medias","votes","liveGuests","liveStatus","liveVideo","liveImage","moreVideos","adImages");
        articleVO.setDate(article.getPublishDate());
        articleVO.setImages(article.getImageApp());
        if (StringUtils.isNotBlank(article.getRealImageUrl())) {
            articleVO.setRealImages(Arrays.asList(StringUtils.split(article.getRealImageUrl(), ",")));
        }
        //TODO 扩充字段待添加
        return articleVO;
    }
}
