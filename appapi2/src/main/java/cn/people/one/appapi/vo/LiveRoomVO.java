package cn.people.one.appapi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author YX
 * @date 2018/10/15
 * @comment
 */
@Data
public class LiveRoomVO {
	@ApiModelProperty(value = "直播ID")
    private Long id;//直播ID
	
	@ApiModelProperty(value = "标题")
    private String title;//标题
	
	@ApiModelProperty(value = "直播状态 1预告 2直播中 3已结束")
    private Integer status;//直播状态 1预告 2直播中 3已结束
	
	@ApiModelProperty(value = "是否显示标题")
    private String showTitle;//是否显示标题
	
	@ApiModelProperty(value = "描述")
    private String description;//描述
	
	@ApiModelProperty(value = "图片地址（视频封面图）")
    private String image;//图片地址（视频封面图）
	
	@ApiModelProperty(value = "缩略图(视频焦点图)")
    private String imageUrl;//缩略图(视频焦点图)
	
	@ApiModelProperty(value = "视频地址")
    private String video;//视频地址
	
	@ApiModelProperty(value = "视频回放地址")
    private String playback;//视频回放地址
	
	@ApiModelProperty(value = "栏目id")
    private Long categoryId;//栏目id
	
	@ApiModelProperty(value = "分享页url")
    private String shareUrl;//分享页url
	
	@ApiModelProperty(value = "编码")
    private String sysCode;//编码
	
	@ApiModelProperty(value = "点击量")
    private Integer hits;//点击量
	
	@ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;//创建时间
	
	@ApiModelProperty(value = "嘉宾")
    private List<LiveUserVO> guests;//嘉宾

    @ApiModelProperty(value = "标签")
    private String tag;
    /**
     * 直播类型 1有视频无图 2有图无视频 3无图无视频
     */
    private Integer liveType;
    public Integer getHits() {
        if(this.hits==null){
            this.hits=0;
        }
        return hits;
    }
}
