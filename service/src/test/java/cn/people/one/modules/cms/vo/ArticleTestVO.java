package cn.people.one.modules.cms.vo;

import com.github.jsonzou.jmockdata.mockdata.JmockDataWrapper;
import lombok.Data;

/**
 * Created by Cheng on 2017/2/11.
 */
@Data
public class ArticleTestVO extends JmockDataWrapper {
    private String title;
    private String authors;
    private String description;
    private String content;
}