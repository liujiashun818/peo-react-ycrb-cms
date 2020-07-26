package cn.people.one.modules.comment.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.text.KeywordFilter;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.comment.model.SensitiveWords;
import cn.people.one.modules.comment.service.ISensitiveWordsService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* 敏感词Service
* @author 周欣
*/
@Service
@Transactional(readOnly = true)
@Slf4j
public class SensitiveWordsService extends BaseService<SensitiveWords> implements ISensitiveWordsService {

    @Autowired
    private BaseDao dao;

    private KeywordFilter keywordFilter;

    /**
     * 正则匹配HTML尖括号以外的元素
     */
    private final static Pattern HTML_PATTERN=Pattern.compile(">(.*?)<",Pattern.CASE_INSENSITIVE);

    /**
     * 判断内容是否包含敏感词
     * @param content
     * @return
     */
    public boolean isIncludeSensitiveWord(String content){
        //if (null == keywordFilter) {
            initKeywordFilter();
        //}
        return keywordFilter.isContentKeyWords(content);
    }

    /**
     * 获取内容里的敏感词
     *
     * @param content
     * @return
     */
    public Set<String> getSensitiveWord(String content) {
        if (null == keywordFilter) {
            initKeywordFilter();
        }
        return keywordFilter.getTxtKeyWords(content);
    }

    public void initKeywordFilter() {
        keywordFilter = new KeywordFilter();
        Cnd cnd = Cnd.NEW();
        SensitiveWords sensitiveWords = dao.fetch(SensitiveWords.class,cnd.where("level", "=", 1));
        if(null == sensitiveWords){
            log.warn("系统没有配置敏感词，请注意！");
            return;
        }
        if (!StringUtils.isEmpty(sensitiveWords.getSensitiveWord())) {
            String[] words = sensitiveWords.getSensitiveWord().split(",");
            List<String> keywords = Arrays.asList(words);
            keywordFilter.addKeywords(keywords);
        } else {
            log.warn("系统没有配置敏感词，请注意！");
        }
    }

    public SensitiveWords replaceSensitiveWordContent(String content){
        SensitiveWords sensitiveWords = new SensitiveWords();
        sensitiveWords.setIsHaveSensitiveWords(false);
        sensitiveWords.setSensitiveWordsContent(content);
        if(Lang.isEmpty(content)){
            return sensitiveWords;
        }
        //实现敏感词管理 敏感词匹配可以采用DFA算法实现
        boolean flag = isIncludeSensitiveWord(content);
        if (flag) {
            //正则匹配过滤掉无关HTML元素
            Matcher matcher = HTML_PATTERN.matcher(content);
            while(matcher.find()) {
                String match = matcher.group(1);
                String matchNew;
                for (String str : getSensitiveWord(match)) {
                    matchNew = match.replaceAll(str, "<b><font color=\"#FF0000\">" + str + "</font></b>");
                    if(StringUtils.isNotBlank(matchNew)){
                        content=content.replace(match,matchNew);
                    }
                }
            }
            /*for (String str : getSensitiveWord(content)) {
                content = content.replaceAll(str, "<b><font color=\"#FF0000\">" + str + "</font></b>");
            }*/
            sensitiveWords.setIsHaveSensitiveWords(true);
            sensitiveWords.setSensitiveWordsContent(content);
        }
        return sensitiveWords;
    }

    public String checkSensitiveWordContent(String content){

        if(Lang.isEmpty(content)){
            return content;
        }
        //实现敏感词管理 敏感词匹配可以采用DFA算法实现
        boolean flag = isIncludeSensitiveWord(content);
        if (flag) {
            for (String str : getSensitiveWord(content)) {
                content = content.replaceAll(str, "<b><font color=\"#FF0000\">" + str + "</font></b>");
            }
        }
        return content;
    }

}