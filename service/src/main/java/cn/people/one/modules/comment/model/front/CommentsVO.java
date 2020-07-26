package cn.people.one.modules.comment.model.front;

import lombok.Data;

/**
 * Created by sunday on 2017/3/29.
 */
@Data
public class CommentsVO {
    private Integer delFlag;
    private String content;
    private String articleId;
    private String categoryIds;
    private String sysCode;
}
