package cn.people.one.modules.search.model;

import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.String;

/**
 * Created by lml on 2017/1/17.
 */
@Data
@NoArgsConstructor
@Slf4j
@Document(indexName = "#{elasticsearchIndexName}", type = "#{elasticsearchIndexType}", replicas = 0)
public class ArticleIndexData {

    @Id(auto = false)
    private Long id;

    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title; // 标题
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String introTitle; // 肩标题
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String subTitle; // 副标题
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String listTitle; // 列表标题
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String authors;// 作者
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String description; // 摘要
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String keywords; // 关键词
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word", store = true)
    private String tags; // 标签

//    private String content;
//    private String contentText;
    private Date publishDate;//发布时间
    private String sysCode;
    private Long articleId;
    private Long categoryId;
    private String link;
    private Long date;//客户端的发布日期
    private String imageUrl;
    private String type;
    private String viewType;

    public ArticleIndexData(Article article) {
        try {
            BeanUtils.copyProperties(this, article);
//            ArticleData data = article.getArticleData();

//            if (data != null && StringUtils.isNotBlank(data.getContent())) {
//                this.setContent(data.getContent());
//                this.setContentText(Jsoup.parse(data.getContent()).text());
//            }
        } catch (Exception ex) {
            log.error("ArticleIndexData初始化失败", ex.getStackTrace());
        }
    }

    public static final String KEYWORDS = "keywords";
    public static final String TITLE = "title";
    public static final String LIST_TITLE = "listTitle";
//    public static final String CONTENT = "content";
//    public static final String CONTENT_TEXT = "contentText";
    public static final String DESCRIPTION = "description";

}
