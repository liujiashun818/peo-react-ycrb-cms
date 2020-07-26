package cn.people.one.modules.live.model;

import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.file.FileFormateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 直播间
 *
 * @author cheng
 */
@Table("live_room")
@Data
public class LiveRoom extends BaseEntity {

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("主持人id 逗号分隔")
    private String hostIds;

    @Column
    @ColDefine(width = 500)
    @Comment("主持人")
    private String host;

    @Column(hump = true)
    @ColDefine(width = 200)
    @Comment("嘉宾id 逗号分隔")
    private String guestIds;

    @Column
    @ColDefine(width = 500)
    @Comment("嘉宾 逗号分隔")
    private String guest;

    @Column
    @ColDefine(width = 500)
    @Comment("标题")
    private String title;

    @Column(hump = true)
    @ColDefine(type = ColType.BOOLEAN)
    @Comment("是否显示标题 1是，0否")
    private Boolean showTitle;

    @Column
    @ColDefine(width = 1000)
    @Comment("摘要")
    private String description;

    @Column
    @ColDefine(width = 1)
    @Comment("直播状态 1预告 2直播中 3已结束")
    private String status;

    @Column
    @ColDefine(width = 1000)
    @Comment("视频地址")
    private String video;

    @Column
    @ColDefine(width = 1000)
    @Comment("视频回放地址")
    private String playback;

    @Column(hump = true)
    @ColDefine(width = 1000)
    @Comment("图片地址")
    private String image;

    @Column(hump = true)
    @ColDefine(type = ColType.TIMESTAMP)
    @Comment("直播开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date liveTime;

    @Column
    @Comment("评论数")
    private Integer comments;

    @Column
    @Comment("点赞数")
    private Integer likes;

    @Column
    @Comment("点击量")
    private Integer hits;

    @Column
    @Comment("标签 直播或访谈")
    private String tag;

    @Column(hump = true)
    @ColDefine(width = 1)
    @Comment("直播类型 1有视频无图 2有图无视频 3无图无视频")
    private Integer liveType;

    private List<LiveUser> hosts;
    private String viewType;//展示类型
    private Long categoryId;//栏目id
    private Integer block;//1头图 2列表
    private String imageUrl;//缩略图
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishDate;//发布时间
    private String syscode;

    private List<LiveUser> guests;//嘉宾多个客户端显示

    //直播状态 1预告 2直播中 3已结束
    public static final Integer LIVE_STATUS_PREVUE = 1;
    public static final Integer LIVE_STATUS_LIVEING = 2;
    public static final Integer LIVE_STATUS_FINISHED = 3;

    /**
     * 处理原appcms图片地址
     * @return
     */
    public String getImage() {
        return FileFormateUtil.formatFileSrcToWeb(this.image);
    }
}