package cn.people.one.appapi.provider.ad.model;

import com.alibaba.fastjson.annotation.JSONField;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by wilson on 2018-10-29.
 */
@Data
public class WebView {

	@ApiModelProperty(value = "id")
    private Long id;

	@ApiModelProperty(value = "")
    private Long tagid;

	@ApiModelProperty(value = "")
    @JSONField(name = "redirect_type")
    private Integer redirectType;

	@ApiModelProperty(value = "")
    @JSONField(name = "redirect_url")
    private String redirectUrl;

	@ApiModelProperty(value = "类型")
    private Integer type;

	@ApiModelProperty(value = "开始时间")
    @JSONField(name = "start_time", format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date startTime;

	@ApiModelProperty(value = "结束时间")
    @JSONField(name = "end_time", format = "yyyy-MM-dd'T'HH:mm:ssZ")
    private Date endTime;

    private List<AdImage> images;

	@ApiModelProperty(value = "")
    @JSONField(name = "advert_position")
    private Integer advertPosition;

	@ApiModelProperty(value = "标题")
    private String title;

	@ApiModelProperty(value = "链接")
    private String link;

    @JSONField(name = "click_type")
	@ApiModelProperty(value = "链接")
    private Integer clickType;
}
