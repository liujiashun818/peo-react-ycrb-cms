package cn.people.one.modules.search.model;

import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.cms.model.Article;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.String;

/**
 * Created by lml on 2017/1/17.
 */
@Data
@NoArgsConstructor
@Slf4j
@Document(indexName = "#{askSuggetIndexName}", type = "#{askSuggetTypeName}", replicas = 0)
public class AskSuggestData {

    @Id(auto = false)
    private Long id;
    private String title; // 标题
    private Long date;//客户端的发布日期

    public AskSuggestData(AskQuestionReply ask) {
        this.id =ask.getId();
        this.title=ask.getTitle();
        this.date = ask.getCreateAt();
    }
}
