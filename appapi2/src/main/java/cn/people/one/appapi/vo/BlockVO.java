package cn.people.one.appapi.vo;

import lombok.Data;

import java.util.List;

/**
 * Created by wilson on 2018-10-11.
 */
@Data
public class BlockVO {
    private Long id;
    private String viewType;
    private String title;
    private List<ArticleVO> articles;
    private PageVO page;
}
