package cn.people.one.appapi.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author YX
 * @date 2018/10/25
 * @comment
 */
@Data
public class LiveCommentVO {
    private Long id;//ID
    private String content;//内容
    private String image;//内容图片
    private Long time;//时间
    private String area;//地区
    private Integer likes;//点赞数
    private String userOpenId;//用户OpenId
    private String userSysType;//用户系统类型
    private String userName;//用户昵称
    private String userIcon;//用户头像
    private String userType;//用户类型（主持人、嘉宾、网友）
    private List<LiveCommentReplyVO> replies;//回复
    private String shareUrl;//分享链接
    private String adminReply;//管理员回复
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Asia/Shanghai")
    private Date date;//创建时间
}
