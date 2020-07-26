package cn.people.one.modules.search.model;

import cn.people.one.modules.ask.model.AskQuestionReply;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.entity.annotation.Id;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldIndex;

import java.util.Date;

import static org.springframework.data.elasticsearch.annotations.FieldType.String;

/**
 * @author YX
 * @date 2019-06-17
 * @comment
 */
@Data
@NoArgsConstructor
@Slf4j
@Document(indexName = "#{elasticsearchAskIndexName}", type = "#{elasticsearchAskIndexType}", replicas = 0)
public class AskIndexData {
    @Id(auto = false)
    private Long id;
    /**
     * 标题
     */
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String title;
    /**
     * 提问内容
     *//*
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String questionContent;
    *//**
     * 回复内容
     *//*
    @Field(type = String, index = FieldIndex.analyzed, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String replyContent;*/
    /**
     * 提问时间
     */
    private Date questionTime;
    /**
     * 回复时间
     */
    private Date replyTime;
    /**
     * 发布时间
     */
    private Date publishTime;
    /**
     * 排序号
     */
    private Integer orderID;

    /**
     * 模块编码
     */
    private String sysCode;

    /**
     * 状态
     */
    private Integer status;

    public AskIndexData(AskQuestionReply askQuestionReply) {
        BeanUtils.copyProperties(askQuestionReply,this);
    }

    public static class Constant{
        public static final String TITLE="title";
        public static final String STATUS="status";
    }
}
