package cn.people.one.modules.client.model.front;

import lombok.Data;

/**
 * Created by sunday on 2018/9/25.
 */
@Data
public class FloatingImgsVO {

    private String name;
    private String imgUrl;
    private String isShow;
    private String type;
    private String status;
    private String redirectUrl;
    private Long categoryId;
    private Long articleId;
    private String articleTitle;
}
