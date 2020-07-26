package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

/**
 * 相关文章推荐
 */
@Table("cms_article_relation")
@Data
public class ArticleRelation extends BaseEntity {
    @Column
    @ColDefine(width = 255,type = ColType.VARCHAR)
    @Comment("备注")
    private String remarks;//备注

    @Column(hump = true)
    @ColDefine(width = 255,type = ColType.VARCHAR)
    @Comment("文件路径")
    private String fileUrl;//文件路径

    @Column(hump = true)
    @ColDefine(width = 3000,type = ColType.VARCHAR)
    @Comment("列表封面图片")
    private String image;//列表封面图片

    @Column(hump = true)
    @ColDefine(width = 32,type = ColType.INT)
    @Comment("引用的文章ID")
    private Long referenceId;//引用的文章ID

    @Column(hump = true)
    @ColDefine(width = 255,type = ColType.VARCHAR)
    @Comment("引用系统类型，如cms、paper")
    private String referenceType;//引用系统类型，如cms、paper

    @Column(hump = true)
    @ColDefine(width = 255,type = ColType.VARCHAR)
    private String times;

    @Column(hump = true)
    @ColDefine(type = ColType.TEXT)
    @Comment("标题")
    private String title;//标题

    @Column(hump = true)
    @ColDefine(width = 255,type = ColType.VARCHAR)
    @Comment("类型")
    private String type;//类型

    @Column(hump = true)
    @ColDefine(width = 32,type = ColType.INT)
    @Comment("文章ID")
    private Long articleId;//文章ID

    @Column(hump = true)
    @ColDefine(width = 20,type = ColType.VARCHAR)
    @Comment("展示类型（0 相关新闻展示，1功能区展示）")
    private String viewType;//展示类型（0 相关新闻展示，1功能区展示）

    @Column(hump = true)
    @ColDefine(type = ColType.TEXT)
    @Comment("外部链接")
    private String link;//外部链接

    @Column(hump = true)
    @ColDefine(width = 20,type = ColType.VARCHAR)
    @Comment("所在位置（0 相关新闻  1 相关功能）")
    private String position;//所在位置（0 相关新闻  1 相关功能）

    @Column(hump = true)
    @ColDefine(notNull = true)
    @Comment("栏目ID")
    private Long categoryId;

    @Column(hump = true)
    @ColDefine(type = ColType.TEXT)
    @Comment("标签")
    private String tags;//标签

    @Column(hump = true)
    @ColDefine(type = ColType.DATETIME)
    @Comment("发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishDate;

    @Column(hump = true)
    @ColDefine(type = ColType.TEXT)
    @Comment("分享链接")
    private String shareUrl;//分享链接
}
