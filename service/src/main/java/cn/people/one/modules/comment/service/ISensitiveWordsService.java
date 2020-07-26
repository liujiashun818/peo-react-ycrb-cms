package cn.people.one.modules.comment.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.comment.model.SensitiveWords;

import java.util.Set;

/**
* 敏感词Service
* @author 周欣
*/
public interface ISensitiveWordsService extends IBaseService<SensitiveWords>{

    /**
     * 判断内容中是否包含敏感词
     * @param content
     * @return
     */
    boolean isIncludeSensitiveWord(String content);

    /**
     * 获取内容中的敏感词
     * @param content
     * @return
     */
    Set<String> getSensitiveWord(String content);

    /**
     * 文章内容处理 如果内容中含有敏感词则加红处理 如果没有敏感词则不处理
     * @param content
     * @return
     */
    String checkSensitiveWordContent(String content);

    SensitiveWords replaceSensitiveWordContent(String content);


}