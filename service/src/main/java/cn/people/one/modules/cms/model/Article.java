package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.IdWorkerEntity;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.file.FileFormateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 文章
 */
@ApiModel
@Table("cms_article")
@Data
public class Article extends IdWorkerEntity {

	private static final long serialVersionUID = -6653259771281332770L;
	@One(field = "id")
	public ArticleData articleData;
	
	@ApiModelProperty(value = "系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
	@Column(hump = true)
	@ColDefine(width = 20, notNull = true)
	@Comment("系统编码 引用文章可能来自其他系统模块，系统编码用于区分原文所在系统模块")
	private String sysCode;
	
	@ApiModelProperty(value = "区块 1. 头图 2.列表 3.待选 同一个栏目下可能有不同文章区块，如客户端的头图区和列表区")
	@Column
	@Comment("区块 1. 头图 2.列表 3.待选 同一个栏目下可能有不同文章区块，如客户端的头图区和列表区")
	private Integer block;
	
	@ApiModelProperty(value = "文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID")
	@Column(hump = true)
	@Comment("文章ID 单独定义文章ID，而不是使用主键ID，可以兼顾实体文章与引用文章,与主键ID相同的时候是实体文章，与主键文章不同的时候是引用文章的ID")
	private Long articleId;
	
	@ApiModelProperty(value = "文章类型 普通新闻，图片，音频，视频，快讯，直播，专题，公益")
	@Column
	@ColDefine(width = 20, notNull = true)
	@Comment("文章类型 普通新闻，图片，音频，视频，快讯，直播，专题，公益")
	private String type;
	
	@ApiModelProperty(value = "是否引用")
	@Column(hump = true)
	@ColDefine(width = 200)
	@Comment("是否引用")
	private Boolean isReference;
	
	@ApiModelProperty(value = "展示类型")
	@Column(hump = true)
	@ColDefine(width = 20, notNull = true)
	@Comment("展示类型")
	private String viewType;
	
	@ApiModelProperty(value = "栏目ID")
	@Column(hump = true)
	@ColDefine(notNull = true)
	@Comment("栏目ID")
	private Long categoryId;
	
	@ApiModelProperty(value = "标题")
	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("标题")
	private String title;
	
	@ApiModelProperty(value = "肩标题")
	@Column(hump = true)
	@ColDefine(width = 500, notNull = true)
	@Comment("肩标题")
	private String introTitle;// 肩标题
	
	@ApiModelProperty(value = "副标题")
	@Column(hump = true)
	@ColDefine(width = 500, notNull = true)
	@Comment("副标题")
	private String subTitle;// 副标题
	
	@ApiModelProperty(value = "列表标题")
	@Column(hump = true)
	@ColDefine(type = ColType.TEXT)
	@Comment("列表标题")
	private String listTitle;
	
	@ApiModelProperty(value = "外部链接")
	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("外部链接")
	private String link;
	
	@ApiModelProperty(value = "来源")
	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("来源")
	private String source;
	
	@ApiModelProperty(value = "作者")
	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("作者")
	private String authors;
	
	@ApiModelProperty(value = "缩略图片")
	@Column
	@ColDefine(width = 500)
	@Comment("缩略图片")
	private String imageUrl;
	
	@ApiModelProperty(value = "原始图片")
	@Column
	@ColDefine(width = 500)
	@Comment("原始图片")
	private String realImageUrl;
	
	@ApiModelProperty(value = "摘要")
	@Column
	@ColDefine(width = 1000)
	@Comment("摘要")
	private String description;
	
	@ApiModelProperty(value = "权重 权重越大越靠前")
	@Column
	@Comment("权重 权重越大越靠前")
	private Integer weight;
	
	@ApiModelProperty(value = "原权重")
	@Column(hump = true)
	@Comment("原权重")
	private Integer originWeight;
	
	@ApiModelProperty(value = "权重过期时间")
	@Column(hump = true)
	@ColDefine(type = ColType.DATETIME)
	@Comment("权重过期时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date weightDate;
	
	@ApiModelProperty(value = "点赞数")
	@Column
	@Default("0")
	@Comment("点赞数")
	private Integer likes;
	
	@ApiModelProperty(value = "点击数")
	@Column
	@Default("0")
	@Comment("点击数")
	private Integer hits;
	
	@ApiModelProperty(value = "评论数")
	@Column
	@Default("0")
	@Comment("评论数")
	private Integer comments;
	
	@ApiModelProperty(value = "关键字")
	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("关键字")
	private String keywords;
	
	@ApiModelProperty(value = "标签")
	@Column
	@ColDefine(width = 200)
	@Comment("标签")
	private String tags;
	
	@ApiModelProperty(value = "发布时间")
	@Column(hump = true)
	@ColDefine(type = ColType.DATETIME)
	@Comment("发布时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date publishDate;
	
	@ApiModelProperty(value = "")
	@Many(field = "articleId")
	private List<ArticleMeta> metas;

	@ApiModelProperty(value = "调查")
    @Many(field = "articleId")
    @Comment("调查")
    private List<Vote> votes;

	@ApiModelProperty(value = "是否在专题中")
	@Column(hump = true)
	@Comment("是否在专题中")
	private Boolean inSubject;//栏目中存储的类型，文章or专题

	@ApiModelProperty(value = "是否裁剪")
	@Column(hump = true)
	@Comment("是否裁剪")
	private Boolean isTailor;//是否裁剪

	@ApiModelProperty(value = "语音播报开关")
	@Column(hump = true)
	@Comment("语音播报开关")
	private Boolean isVoice;//语音播报开关

	@ApiModelProperty(value = "多视频字段")
	@Column(hump = true)
	@ColDefine(customType = "LONGTEXT", type = ColType.TEXT)
	@Comment("多视频字段")
	private String moreVideos;

	@ApiModelProperty(value = "是否展示列表标题")
	@Column(hump = true)
	@Comment("是否展示列表标题")
	private Boolean isShowListTitle;//是否展示列表标题

	@ApiModelProperty(value = "删除人")
	@Column(hump = true)
	@Comment("删除人id")
    private Long DeleteBy;
	
	@ApiModelProperty(value = "删除人")
	@Column(hump = true)
	@Comment("删除人")
    private String DeletOr;
	
	@ApiModelProperty(value = "删除时间")
	@Column(hump = true)
	@ColDefine(type = ColType.DATETIME)
	@Comment("删除时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date deleteDate;
	
	/**
	 * 同步到入住系统的栏目ID
	 */
	@Column(hump = true)
	@Comment("入住系统的栏目ID")
	private Integer syncCategoryId;
	
	@ApiModelProperty(value = "客户端的发布日期")
	private Long date;//客户端的发布日期
	
	@ApiModelProperty(value = "客户端的图片")
	private List<String> imageApp;//客户端的图片
	
	@ApiModelProperty(value = "媒体信息表id")
	private List<Long> mediaIds;//媒体信息表id
	
	@ApiModelProperty(value = "音频地址")
	private String audioUrl;//音频地址
	
	@ApiModelProperty(value = "音频封面")
	private String audioCover;//音频封面
	
	@ApiModelProperty(value = "视频地址")
	private String videoUrl;//视频地址
	
	@ApiModelProperty(value = "视频封面")
	private String videoCover;//视频封面
	
	@ApiModelProperty(value = "客户端列表接口音频视频")
	private List<MediaResourceVO> medias;//客户端列表接口音频视频
	
	@ApiModelProperty(value = "客户端详情接口音频视频")
	private List<ArticleMediaVO> detailMedias;//客户端详情接口音频视频
	
	@ApiModelProperty(value = "音视频时长（秒）")
	private String mediaTime;//音视频时长（秒）
	
	@ApiModelProperty(value = "")
	private String categoryName;
	
	@ApiModelProperty(value = "图集类型图片张数")
	private Integer imageNum;//图集类型图片张数
	
	@ApiModelProperty(value = "发稿人")
	private String contributor;//发稿人
	
	@ApiModelProperty(value = "字段组定义信息")
	private List<FieldGroup> fieldGroups;//字段组定义信息

    //全媒体新加字段
	
	@ApiModelProperty(value = "全媒体采编中稿件原始id")
    @Column
    private Long ori_docid;//全媒体采编中稿件原始id
	
	@ApiModelProperty(value = "全媒体采编中稿件库原始id")
    @Column
    private Integer ori_doclibid;//全媒体采编中稿件库原始id
	
	@ApiModelProperty(value = "原稿标志项，1是原稿，0是非原稿,默认1")
    @Column
    private Integer doc_original_flag;//原稿标志项，1是原稿，0是非原稿,默认1
	
	@ApiModelProperty(value = "全媒体采编中稿件id")
    @Column
    private Long sys_documentid;//全媒体采编中稿件id
//	private Integer sys_doclibid;//全媒体采编中稿件库id

	@ApiModelProperty(value = "作者")
    @Column
	@ColDefine(type = ColType.TEXT)
    private String doc_author;//作者
	
	@ApiModelProperty(value = "稿件编辑")
    @Column
    private String doc_editor;//稿件编辑

	@ApiModelProperty(value = "固定位置")
    @Column(hump = true)
    @Comment("固定位置")
    private Integer position;//2018-09-28 新增字段

	/**
	 * 中央厨房trs文章ID
	 */
	
	@ApiModelProperty(value = "中央厨房trs文章ID")
	@Column(hump = true)
	@Comment("中央厨房trs文章ID")
	private String trsArticleId;
	/**
	 * 中央厨房trs文章稿件库
	 */
	
	@ApiModelProperty(value = "中央厨房trs文章稿件库")
	@Column(hump = true)
	@Comment("中央厨房trs文章稿件库")
	private String doclib;
	/**
	 * 中央厨房trs文章投稿者
	 */
	
	@ApiModelProperty(value = "中央厨房trs文章投稿者")
	@Column(hump = true)
	@Comment("中央厨房trs文章投稿者")
	private String trsCreator;
	/**
	 * 凡闻文章ID
	 */
	
	@ApiModelProperty(value = "凡闻文章ID")
	@Column(hump = true)
	@Comment("凡闻文章ID")
	private String fanewsArticleId;

	
	@ApiModelProperty(value = "推荐标识")
	@Column(hump = true)
	@Comment("推荐标识")
	private Integer recomdFlag;

	@Column(hump = true)
	@Comment("老数据封面图")
	private String image;

	@ApiModelProperty(value = "是否显示头图")
	@Column(hump = true)
	@Comment("是否显示头图")
	private Boolean isShowTopImg;//是否显示头图
	
	@ApiModelProperty(value = "帮的状态 1募集中  2募集结束  3捐助反馈")
	@Column(hump = true)
	@Comment("帮的状态 1募集中  2募集结束  3捐助反馈")
	private Integer helpStatus;
	
	@ApiModelProperty(value = "帮的开始时间")
	@Column(hump = true)
	@Comment("帮的开始时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startDate;
	
	@ApiModelProperty(value = "帮的结束时间")
	@Column(hump = true)
	@Comment("帮的结束时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endDate;
	
	@ApiModelProperty(value = "帮的声明")
	@Column(hump = true)
	@Comment("帮的声明")
	private String helpState;
	
	@ApiModelProperty(value = "分享地址")
	private String shareUrl;

	@ApiModelProperty(value = "定时发布时间")
	@Column(hump = true)
	@Comment("定时发布时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date fixedPublishDate;

	@ApiModelProperty(value = "相关阅读ID")
	private List<Long> relationIds;

	@ApiModelProperty(value = "相关阅读文章列表")
	private List<Article> relationArticles;

	@ApiModelProperty(value = "帮的推荐标识")
	private Integer helpFlag;

	@ApiModelProperty(value = "24小时预览地址")
	private String previewUrl;
    @Override
    public void init() {
    	super.init();
        if (sysCode == null) {
            sysCode = SysCodeType.ARTICLE.value();
        }
        if (getDelFlag() == null) {
            setDelFlag(Article.STATUS_AUDIT);
        }
        if (weight == null) {
            weight = 0;
        }
		if (originWeight == null) {
			originWeight = 0;
		}
        if (hits == null) {
            hits = 0;
        }
        if (likes == null) {
            likes = 0;
        }
        if (comments == null) {
            comments = 0;
        }
        if (inSubject == null) {
            inSubject = false;
        }
        if (isReference == null) {
            isReference = false;
        }
        if(isTailor==null){
        	isTailor =true;
		}
    }

	/**
	 * 对缩略图图片地址做格式化处理
	 * @return
	 */
	public String getImageUrl() {
    	if(StringUtils.isNotBlank(this.imageUrl)){
			return FileFormateUtil.formatFilesToStr(this.imageUrl);
		}
		return imageUrl;
	}

	/**
	 * 对剪裁图片做处理
	 * @return
	 */
	public String getRealImageUrl() {
		if(StringUtils.isNotBlank(this.realImageUrl)){
			return FileFormateUtil.formatFilesToStr(this.realImageUrl);
		}
		return realImageUrl;
	}

	/**
	 * 处理文章类型(兼容老数据)
	 * @return
	 */
	public String getType() {
		if("img".equals(this.type)){
			return ArticleType.IMAGE.value();
		}
		return type;
	}

	public String getTitle() {
		if(StringUtils.isNotBlank(this.title)){
			return StringEscapeUtils.unescapeHtml(this.title);
		}
		return title;
	}

	public String getIntroTitle() {
		if(StringUtils.isNotBlank(this.introTitle)){
			return StringEscapeUtils.unescapeHtml(this.introTitle);
		}
		return introTitle;
	}

	public String getSubTitle() {
		if(StringUtils.isNotBlank(this.subTitle)){
			return StringEscapeUtils.unescapeHtml(this.subTitle);
		}
		return subTitle;
	}

	public String getListTitle() {
		if(StringUtils.isNotBlank(this.listTitle)){
			return StringEscapeUtils.unescapeHtml(this.listTitle);
		}
		return listTitle;
	}

	/**
	 * 文章中的常量字符
	 */
	public static class Constant {
		public static final String IN_SUBJECT = "in_subject";
		public static final String ID = "id";
		public static final String TITLE = "title";
		public static final String PUBLISH_DATE = "publish_date";
		public static final String DELETE_DATE = "delete_date";
		public static final String WEIGHT = "weight";
		public static final String BLOCK = "block";
		public static final String CATEGORY_ID = "category_id";
		public static final String TYPE = "type";
		public static final String AUTHOR = "authors";
		public static final String IS_REFERENCE = "is_reference";
		public static final String HITS = "hits";
		public static final String COMMENTS = "comments";
		public static final String MATAS = "metas";
		public static final String SYSCODE = "sys_code";
		public static final String POSITION="position";
		public static final Integer ARTICLE_HEAD_DEFAULT_MAX = 4;
		public static final String ARTICLE_ID="article_id";
		public static final String PARENT_ID="parent_id";
		public static final String TAGS = "tags";
		public static final String TRS_ARTICLE_ID="trs_article_id";
		public static final String CREATE_AT="create_at";
		public static final String ARTICLE_TYPE_ASK = "ask";// 问 类型
		public static final String ARTICLE_TYPE_ASK_TOPIC = "asktopic";// 问话题
		//凡闻id
		public static final String FANEWSARTICLEID="fanews_article_id";

		public static final String FIXED_PUBLISH_DATE="fixed_publish_date";
	}

	/**
	 * trs
	 */
	public static final String BUILD_ARTICLE_STATUS = "buildArticle";//取稿
	public static final String SIGN_ARTICLE_STATUS = "signArticle";//签发
	public static final String ADOPT_ARTICLE_STATUS = "adoptArticle";//采用
	public static final String CANCEL_ARTICLE_STATUS = "cancelArticle";//撤稿


	/**
	 * 新闻类型
	 */
	public static final String ARTICLE_TYPE_NORMAL = "normal";// 普通新闻
	public static final String ARTICLE_TYPE_IMG = "img";// 图片新闻
	public static final String ARTICLE_TYPE_AUDIO = "audio";// 音频新闻
	public static final String ARTICLE_TYPE_VIDEO = "video";// 视频新闻
	public static final String ARTICLE_TYPE_PAPER = "paper";// 报纸
	public static final String ARTICLE_TYPE_GOV = "government";// 政务
	public static final String ARTICLE_TYPE_ACT = "activity";// 活动
	public static final String ARTICLE_TYPE_SUBJECT = "topic";// 普通专题
	public static final String ARTICLE_TYPE_ALBUM = "album";// 音频专题
	public static final String ARTICLE_TYPE_HELP = "help";// 公益新闻
	public static final String ARTICLE_TYPE_INTERVIEW = "interview";// 访谈
	public static final String ARTICLE_TYPE_LINK = "link";// 链接
	public static final String ARTICLE_TYPE_COLLECTION = "collection";// 征集类型
	public static final String ARTICLE_TYPE_VRVIDEO = "vrvideo";// VR视频
	public static final String ARTICLE_TYPE_ASK = "ask";// 问 类型
	public static final String ARTICLE_TYPE_ASK_TOPIC = "asktopic";// 问话题
	/*public static void main(String[] args) {
		MongoClient mongoClient = new MongoClient("10.10.59.101",27017);
		MongoDatabase database = mongoClient.getDatabase("test");
		// 进入某个文档集
		MongoIterable collection = database.listCollectionNames();
		MongoCursor cursor = collection.iterator();
		while (cursor.hasNext()){
			System.out.println(cursor.next());
		}
	}*/

	/**
	 * 政务的图片新闻类型
	 */
	public static final String PUBLICIMAGE="img";
}
