package cn.people.one.modules.cms.model;

import cn.people.one.modules.base.entity.BaseEntity;
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
 * 文章定时发布记录表
 */
@ApiModel
@Table("cms_article_task_record")
@Data
public class ArticleTaskRecord extends IdWorkerEntity {


	@Id(auto = false)
	private Long id;
	

	@ApiModelProperty(value = "定时发布时间")
	@Column(hump = true)
	@ColDefine(type = ColType.DATETIME)
	@Comment("定时发布时间")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date fixedPublishDate;
}
