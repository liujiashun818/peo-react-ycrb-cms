package cn.people.one.appapi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author YX
 * @date 2019-03-14
 * @comment
 */
@Data
public class ArticleGovVO {

    /**
     * 主键id
     */
	@ApiModelProperty(value = "主键id")
    private Long id;

    /**
     * 栏目id
     */
	@ApiModelProperty(value = "栏目id")
    private Long categoryId;
    /**
     * 文章articleId
     */
	@ApiModelProperty(value = "文章id")
    private Long articleId;
    /**
     * 状态码
     */
	@ApiModelProperty(value = "状态码")
    private String delFlag;
    /**
     * 标题
     */
	@ApiModelProperty(value = "标题")
    private String title;
    /**
     * 列表标题
     */
	@ApiModelProperty(value = "列表标题")
    private String shortTitle;
    /**
     * 描述
     */
	@ApiModelProperty(value = "描述")
    private String description;
    /**
     * 关键词
     */
	@ApiModelProperty(value = "关键词")
    private String keywords;
    /**
     * 文章类型
     */
	@ApiModelProperty(value = "文章类型")
    private String type;
    /**
     * 作者
     */
	@ApiModelProperty(value = "作者")
    private String authors;
    /**
     * 分享地址
     */
	@ApiModelProperty(value = "分享地址")
    private String shareUrl;
    /**
     * 链接
     */
	@ApiModelProperty(value = "链接")
    private String link;
    /**
     * 来源
     */
	@ApiModelProperty(value = "来源")
    private String copyfrom;
    /**
     * 标签
     */
	@ApiModelProperty(value = "标签")
    private String tags;
    /**
     * 展示类型
     */
	@ApiModelProperty(value = "展示类型")
    private String viewType;
    /**
     * 点击数
     */
	@ApiModelProperty(value = "点击数")
    private Integer hits;
    /**
     * 评论数
     */
	@ApiModelProperty(value = "评论数")
    private Integer comments;
    /**
     * 是否允许评论
     */
	@ApiModelProperty(value = "是否允许评论")
    private String allowComment;
    /**
     * 点赞数
     */
	@ApiModelProperty(value = "点赞数")
    private Integer likes;
    /**
     * 权重
     */
	@ApiModelProperty(value = "权重")
    private Integer weight;
    /**
     * 发布时间
     */
	@ApiModelProperty(value = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanxi")
    private Date publishDate;

    /**
     * 机构名称
     */
	@ApiModelProperty(value = "机构名称")
    private String officeName;

    /**
     * 机构关注数
     */
	@ApiModelProperty(value = "机构关注数")
    private Integer officeLikes;

    /**
     * 文章缩略图
     */
	@ApiModelProperty(value = "文章缩略图")
    private String image;
    private Integer pageNumber;
    private Integer pageSize;
    //降序检索字段
    @ApiModelProperty(value = "降序检索字段")
    private String desc;
    //升序检索字段
    @ApiModelProperty(value = "升序检索字段")
    private String asc;

    /**
     * 栏目先发后审控制
     */
    @ApiModelProperty(value = "栏目先发后审控制")
    private String cateGoryIsAudit;
    public static final String CATEGORYISAUDIT = "1";//1为先发后审,栏目下文章的评论直接上线
}
