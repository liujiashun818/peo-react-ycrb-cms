package cn.people.one.modules.cms.model.gov;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author YX
 * @date 2019-03-15
 * @comment
 */
@Data
public class ArticlePubiccmsVO {
    /**
     * 文章id
     */
    public Long id;
    public Long category;
    public String orgname;
    public String orgid;
    public String orglogo;
    public String orglikes;
    public String orgurl;
    public String title;
    public String cover;
    public String summary;
    public String tag;
    public String content;
    /**
     *  作者
     */
    private String authors;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "GMT+8")
    public Date publishDate;

    /**
     * 备注
     */
    private String remarks;
    /**
     * 创建者
     */
    private Long createBy;
    /**
     * 创建日期
     */
    private Date createDate;
    /**
     * 更新者
     */
    private Long updateBy;
    /**
     * 更新日期
     */
    private Date updateDate;
    /**
     * 删除标记（0：上线；1：下线；2：审核；3：删除）
     */
    private String delFlag;

    /**
     * 栏目id
     */
    private Long categoryId;
    /**
     * 列表标题
     */
    private String shortTitle;

    /**
     * 描述、摘要
     */
    private String description;
    /**
     * 文章图片(缩略图)
     */
    private String image;

    /**
     * 肩标题
     */
    private String introTitle;

    /**
     *  副标题
     */
    private String subTitle;

    /**
     *  关键字
     */
    private String keywords;

    /**
     *  来源
     */
    private String copyfrom;
    /**
     *  标签（独家……）
     */
//    private String tags;

    /**
     *  类型（news：新闻;imgs：多图;subject：专题;live:直播）
     */
    private String type;
    /**
     *   外部链接
     */
    private String link;

    /**
     *  文件路径，用于冗余视频或者音频类型的媒体地址，引用的时候也一样要复制过来，方便列表显示
     */
    private String fileUrl;
    /**
     *  播放时长，用于冗余视频或者音频类型的时长，引用的时候也一样要复制过来，方便列表显示
     */
    private String times;

    /**
     *  附件（多图、音频、视频）
     */
    private String files;
    /**
     *  附件描述（多图、音频、视频）
     */
    private String filesDesc;
    /**
     *  推荐位
     */
    private Integer posid;

    /**
     *  所属专栏
     */
    private String columnType;

    /**
     *  展示类型
     */
    private String viewType;
    /**
     *  通栏展示
     */
    private Boolean bannerView;

    /**
     * 新闻小图标
     */
    private String icon;
    /**
     *  点击数
     */
    private Integer hits;
    /**
     *   评论数
     */
    private Integer comments;
    /**
     *  虚拟评论数
     */
    private Integer vcomments;
    /**
     *  是否允许评论
     */
    private String allowComment;

    /**
     *  点赞数
     */
    private Integer likes;
    /**
     *  虚拟点赞数
     */
    private Integer vlikes;

    /**
     *  推送状态
     */
    private String pushStatus;

    /**
     *  分享地址
     */
    private String shareUrl;
    /**
     *  分享Logo
     */
    private String shareLogo;

    /**
     *  权重，越大越靠前
     */
    private Integer weight;
    /**
     *  权重期限，超过期限，将weight设置为0
     */
    private Date weightDate;

    /**
     *  源文id
     */
    private Long sourceId;

    /**
     *  推荐到政务大厅
     */
    private Boolean hallStatus;
    /**
     *  推荐
     */
    private String recommend;

    /**
     *  显示样式(政务机构用) normal:左图右文，banner:通栏，tree:三图
     */
    private String displayType;

    /**
     * 介绍
     */
    private String introduction;

    /**
     * 媒体文件
     */
    private List<MediaForSxrbCms> medias;
}
