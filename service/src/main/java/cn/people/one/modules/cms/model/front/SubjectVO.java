package cn.people.one.modules.cms.model.front;


import java.util.List;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.Article;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * Created by lml on 17-3-3.
 */
@Data
public class SubjectVO extends BaseEntity{

    private Article article;
    private Boolean showTitle;//显示标题
    private String image;//图片
    private String title;//专题标题
    private Long id;
    private String bannerUrl;//专题通栏类型多缩略图和描述字段
    @ApiModelProperty(value = "顶部显示  1.图片 2.视频 ")
   	private Integer showTop;
    @ApiModelProperty("专题页样式类型 1.普通  2.时间轴 ")
	private Integer pageType;
    @ApiModelProperty(value = "媒体信息表id")
	private List<Long> mediaIds;//媒体信息表id
}
