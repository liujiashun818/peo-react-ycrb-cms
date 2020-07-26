package cn.people.one.appapi.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by sunday on 2018/10/12.
 */
@Data
public class ArticleListVO {

    private List<ArticleVO> head;
    private List<ArticleVO> list;
    private long count;

}
