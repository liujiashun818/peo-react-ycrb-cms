package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
 * 文章元数据
 */
@Table("cms_article_meta")
@TableIndexes({@Index(name = "INDEX_CMS_ARTICLE_META_ARTICLE_ID", fields = {"articleId"}, unique = false)})
@Data
public class ArticleMeta extends BaseEntity {

	public ArticleMeta(){
		this.setDelFlag(BaseEntity.STATUS_ONLINE);
	}

	@Column(hump=true)
	private Long articleId;

	@One(field="articleId")
	private Article article;

	@Column(hump=true)
	@ColDefine(width = 200)
	@Comment("字段编号")
	private String fieldCode;

	@Column(hump=true)
	@ColDefine(width = 500)
	@Comment("字段值")
	private String fieldValue;

	public static String FIELD_CODE = "fieldCode";

}
