package cn.people.one.appapi.vo;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.cms.model.front.LiveTalkMediaVO;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import com.google.gson.Gson;
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
public class LiveTalkVO {
    private Long id;//ID
    @ApiModelProperty(value = "内容")
    private String content;//内容
    @ApiModelProperty(value = "时间")
    private Long time;//时间
    @ApiModelProperty(value = "内容图片")
    private String image;//内容图片
    @ApiModelProperty(value = "是否置顶")
    private Boolean top = false;//是否置顶
    @ApiModelProperty(value = "用户的ID")
    private String userOpenId;//用户的ID
    @ApiModelProperty(value = "用户的地域")
    private String area;//用户的地域
    @ApiModelProperty(value = "用户的昵称")
    private String userName;//用户的昵称
    @ApiModelProperty(value = "用户角色，主持人，嘉宾，网友")
    private String userType;//用户角色，主持人，嘉宾，网友
    @ApiModelProperty(value = "用户头像")
    private String userIcon;//用户头像
    @ApiModelProperty(value = "删除标示（0：上线；其他下线）")
    private String delFlag;//删除标示（0：上线；其他下线）
    @ApiModelProperty(value = "回复")
    private List<LiveTalkReplyVO> replies;//回复
    @ApiModelProperty(value = "时间 pattern = yyyy-MM-dd'T'HH:mm:ssZ, timezone = Asia/Shanghai")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;//时间
    @ApiModelProperty(value = "客户端详情接口音频视频")
	private LiveTalkMediaVO medias;//客户端详情接口音频视频
}
