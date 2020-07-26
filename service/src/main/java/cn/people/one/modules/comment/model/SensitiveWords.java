package cn.people.one.modules.comment.model;

import cn.people.one.modules.base.entity.BaseEntity;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

/**
* 敏感词
* @author 周欣
*/
@Table("comment_sensitive_words")
@Data
public class SensitiveWords extends BaseEntity {

    @Column(hump = true)
    @ColDefine(customType = "mediumtext", type = ColType.TEXT)
    @Comment("敏感词")
    private String sensitiveWord;

    @Column
    @ColDefine(width = 20)
    @Comment("敏感词级别")
    private String level;

    private Boolean isHaveSensitiveWords;//是否含有敏感词
    private String sensitiveWordsContent;//处理敏感词之后的内容
}