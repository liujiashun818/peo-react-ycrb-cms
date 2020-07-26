package cn.people.one.modules.live.model;

import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.cms.model.front.LiveTalkMediaVO;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
* 直播间的对话
* @author cheng
*/
@Table("live_talk")
@Data
public class LiveTalk extends BaseEntity {

    @Column(hump = true)
    private Long roomId;

    @ApiModelProperty(value = "0表示为主持人或嘉宾发言 大于0表示引用网友的评论回复")
    @Column(hump = true)
    @Comment("0表示为主持人或嘉宾发言 大于0表示引用网友的评论回复")
    private Long parentId;

    @ApiModelProperty(value = "是否置顶")
    @Column
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否置顶")
    private Boolean top;

    @ApiModelProperty(value = "内容")
    @Column
    @ColDefine(width = 1000)
    @Comment("内容")
    private String content;

    @ApiModelProperty(value = "图片")
    @Column
    @ColDefine(width = 500)
    @Comment("图片")
    private String image;

    @ApiModelProperty(value = "用户id")
    @Column(hump = true)
    @Comment("用户id")
    private String openId;

    @ApiModelProperty(value = "用户的地域")
    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("用户的地域")
    private String area;

    @ApiModelProperty(value = "用户头像")
    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("用户头像")
    private String userIcon;

    @ApiModelProperty(value = "用户昵称")
    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("用户昵称")
    private String userName;

    @ApiModelProperty(value = "用户角色")
    @Column(hump = true)
    @ColDefine(width = 50)
    @Comment("用户角色")
    private String userType;

    @ApiModelProperty(value = "媒体资源数据json")
    @Column(hump = true)
    @ColDefine(width = 500)
    @Comment("媒体资源数据")
    private String mediaJson;

	@ApiModelProperty(value = "客户端详情接口音频视频")
	private LiveTalkMediaVO media;//客户端详情接口音频视频
	
    private Long commentId;//评论id
    private LiveTalk liveReply;//回复
    private Long time;
    private String method;//webSocket通信调用的方法名称
    private String type;

    /**
     * 处理返回media
     * @return
     */
    public LiveTalkMediaVO getMedia() {
        if(StringUtils.isNotBlank(this.mediaJson)){
            return new Gson().fromJson(this.mediaJson, LiveTalkMediaVO.class);
        }
        return media;
    }
}