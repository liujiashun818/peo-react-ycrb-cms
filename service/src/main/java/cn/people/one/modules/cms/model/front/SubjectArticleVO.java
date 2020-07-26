package cn.people.one.modules.cms.model.front;

import cn.people.one.modules.cms.model.Article;
import lombok.Data;

import java.util.List;

/**
 * Created by lml on 2017/4/5.
 */
@Data
public class SubjectArticleVO {
    List<Article> list;
    Long categoryId;
}
