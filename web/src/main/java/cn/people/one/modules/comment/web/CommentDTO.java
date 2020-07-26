package cn.people.one.modules.comment.web;

import lombok.Data;

/**
 * User: 张新征
 * Date: 2017/4/14 10:47
 * Description:
 */
@Data
public class CommentDTO {
    private String articleId;
    private String sysCode;
    private String content;
    private String userIp;
    private String parentId;
    private String categoryId;
    private String userName;
    private String userIcon;
    private String userSysType;
    private String replyUserOpenId;
    private String replyUserSysType;
    private String replyUserName;
    private String replyCommentId;
    private String image;
    private String title;
    private String userType;
    private String userOpenId;
    private String area;
    private String likes;
    private String floor;
    private String myCommentStatus;
    private String time;//客户端显示的时间
    private String replyTo;//回复的谁
}
