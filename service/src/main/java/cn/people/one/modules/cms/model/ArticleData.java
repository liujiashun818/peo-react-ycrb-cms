package cn.people.one.modules.cms.model;

import cn.people.one.core.util.base.annotation.NotNull;
import cn.people.one.modules.base.entity.IdWorkerEntity;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.file.FileFormateUtil;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import com.alibaba.fastjson.JSONArray;

import java.util.Date;
import java.util.List;

/**
 * 文章大字段内容
 */
@Table("cms_article_data")
@Data
public class ArticleData extends IdWorkerEntity {
	@Id(auto = false)
	private Long id;

	@Column
	@ColDefine(customType = "LONGTEXT", type = ColType.TEXT)
	@NotNull
	@Comment("内容")
	private String content;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("图集")
	private String images;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("音频")
	private String audios;

	@Column
	@ColDefine(type = ColType.TEXT)
	@Comment("视频")
	private String videos;

	@Column(hump = true)
	@ColDefine(width = 500)
	@Comment("调查标题")
	private String surveyTitle;

	@Column(hump = true)
	@Comment("是否多选")
	private Boolean isMultipleChoice;

	@Column(hump = true)
	@Comment("是否显示调查结果")
	private Boolean isShowResult;

	@Column(hump = true)
	@Comment("调查截止日期")
	private Date endTime;

	@Column
	@ColDefine(width = 500)
	@Comment("文件")
	private String files;

	@Column(hump=true)
	@ColDefine(width = 500)
	@Comment("相关新闻")
	private String relatedArticles;

	private List<ArticleMediaVO> imageJson;
	private List<ArticleMediaVO> audioJson;
	private List<ArticleMediaVO> videoJson;

	public static final String ID = "ID";
	/**
	 * 处理原appcms文章内容
	 * @return
	 */
	public String getContent() {
		return FileFormateUtil.formatContent(this.content);
	}
}
