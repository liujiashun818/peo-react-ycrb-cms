package cn.people.one.appapi.vo;

import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class SubjectVO {
	@ApiModelProperty(value = "id")
    private Long id;
	@ApiModelProperty(value = "专题标题")
    private String title;//专题标题
	@ApiModelProperty(value = "显示标题   true:显示 ，false:不显示")
    private Boolean showTitle;//显示标题
	@ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "图片")
    private String image;//图片
    @ApiModelProperty(value = "分享地址")
    private String shareUrl;
    @ApiModelProperty(value = "图标地址")
    private JSONArray bannerUrl;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;
    private List<BlockVO> blocks;
    
	/* 专题新增视频 */
    @ApiModelProperty(value = "专题页样式 1.普通  2.时间轴 ")
   	private Integer pageType;
    @ApiModelProperty(value = "顶部显示  1.图片 2.视频")
   	private Integer showTop;
    private List<ArticleMediaVO> medias;
    
}
