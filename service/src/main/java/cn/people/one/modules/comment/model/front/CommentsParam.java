package cn.people.one.modules.comment.model.front;

import lombok.Data;
import org.omg.CORBA.PRIVATE_MEMBER;

/**
 * @author YX
 * @date 2019-03-11
 * @comment
 */
@Data
public class CommentsParam {

    /**
     * 回复评论id
     */
    private Long parentId;
    /**
     * 评论内容
     */
    private String content;
    /**
     * 评论人姓名
     */
    private String userName;
    /**
     * 是否管理员回复
     */
    private Boolean isAdmin;
    /**
     * IP
     */
    private String ip;

    /**
     * 文章id
     */
    private String articleId;

    /**
     * 系统编码
     */
    private String sysCode;
}
