package cn.people.one.appapi.vo;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by wilson on 2018-10-19.
 */
@Data
public class ArticleMoreVideoVO {
	
	@ApiModelProperty(value = "视频")
    private JSONObject video;
	
	@ApiModelProperty(value = "图片")
    private String image;
}
