package cn.people.one.modules.cms.model.gov;


import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.type.ArticleType;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 同步文章到入住系统(同原appcms中ArticleVO)
 */
@Data
public class Article2PubliccmsVO implements Serializable {

	/**
	 *
	 */
    private static final long serialVersionUID = 1L;
	private String category_id; // string 栏目id
	private String id; // 编号
	private String title; // 标题
	private String shortTitle;// 短标题
	private String introTitle;// 肩标题
	private String subTitle;// 副标题
	private String type; // 类型（news：新闻;imgs：多图;subject：专题;live:直播）
	private String description;// 描述、摘要
	private String content; // 内容
	private String authors;// 作者
	private String copyfrom;// 来源
	private String createDate;// 创建日期
	private String introduction;// 导读
	private String referenceId;// 引用的文章ID（引用类文章）
	private String referenceType;// 引用系统类型
	private String helpStartDate;// 帮的开始时间
	private String helpEndDate;// 帮的结束时间
	private String helpStatus;// 帮的状态
	private String helpState;
	private Integer likes;// 点赞数
	private String image; // 文章图片(缩略图)
	private String viewType; // 展示类型
	private String syncCategoryId; //同步到入住系统的栏目ID
	private String syncArticleId; //同步的文章ID
	private String delFlag;
	private String keywords;//关键字
	private Integer weight;//权重
	/**
	 * 外链
	 */
	private String link;
	/**
	 * 媒体文件
	 */
	private List<MediaForSxrbCms> medias;

	/**
	 * 展示类型
	 */
	private String displayType;

	public Article2PubliccmsVO(Article a) {
		this.referenceType = Constant.REFERENCE_TYPE_CMS;
		this.referenceId = a.getArticleId()+"";
		this.id = a.getId() + "_" + referenceType + "_" + referenceId;
		this.authors=a.getAuthors();
		this.category_id = a.getCategoryId()+"";
		//添加articleData判空
		if(a.articleData!=null && StringUtils.isNotBlank(a.articleData.getContent())){
			this.content = a.getArticleData().getContent();
		}
		this.copyfrom = a.getSource();
		this.createDate = DateHelper.getFormatByLong(DateHelper.DEFAULT_TIME_FORMAT,a.getCreateAt());
		this.description = a.getDescription();
//		this.helpStartDate= null==a.getStartDate()?"":a.getStartDate().getTime()+"";
//		this.helpEndDate= null==a.getEndDate()?"":a.getEndDate().getTime()+"";
//		this.helpState = a.getHelpState();
//		this.helpStatus=a.getHelpStatus();
//		this.introduction= a.getIntroduction();
		this.introTitle=a.getIntroTitle();
		this.title=a.getTitle();
		this.likes= a.getLikes();
		this.shortTitle=a.getListTitle();
		this.setSubTitle(a.getSubTitle());
		if(ArticleType.IMAGE.value().equals(a.getType())){
		    this.type=Article.PUBLICIMAGE;
        }else{
            this.setType(a.getType());
        }
		this.image= a.getImageUrl().replace(",","|");
		this.viewType= a.getViewType();
		this.syncCategoryId = a.getSyncCategoryId()+"";
		this.syncArticleId = String.valueOf(a.getId());
		this.delFlag = a.getDelFlag()+"";
		this.keywords = a.getKeywords();
		this.weight = a.getWeight();
		this.displayType="normal";
		this.link=a.getLink();
	}

	public static class Constant{
		static final String REFERENCE_TYPE_CMS="cms";
	}
}
