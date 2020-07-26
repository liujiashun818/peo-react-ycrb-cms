package cn.people.one.modules.ask.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.model.front.AskQuestionReplyVO;
import cn.people.one.modules.ask.model.front.PageAskQuestionQueryVO;
import cn.people.one.modules.cms.model.Article;
import org.nutz.dao.QueryResult;

import java.util.List;
import java.util.Map;

public interface IAskQuestionReplyService extends IBaseService<AskQuestionReply>{
    /**
     * @Description: 根据页面参数，查询问政信息
     * @return 问政信息查询结果列表，分页等信息对象QueryResult
     */
    QueryResultVO<AskQuestionReply> searchAskQuestions(PageAskQuestionQueryVO query);

    QueryResult searchAskQuestionsCom(PageAskQuestionQueryVO query);
    /**
     * @Description: 根据问题id查询问题详情
     * @return map，key有3个，domainList问题领域列表，typeList问题类型列表，detail问题详情
     */
    Map<String, Object> getDetailById(Long id);
    /**
     * @Description: 推荐到文章
     * @param categoryId 栏目id
     * @param  askIds 问政id列表,逗号分隔多个id
     * @return 成功失败标志
     */
    boolean recommendToArticle(Long categoryId,String askIds);
    List<AskQuestionReply> getHeadResult();

    List<AskQuestionReply> getNormalResult(Integer pageSize, Integer pageNo);

    QueryResult getResult(Integer pageSize,Integer pageNo);

    void updateSupport(Long askId,Integer newZan);

    Result online(Long id);

    Result downline(Long id);

    void batchUpdate(List<AskQuestionReply> list);

    Result setTop(Long id);

    Result cancelTop(Long id);

    Map myAskList(String user_id, Integer pageSize, Integer pageNumber);

    AskQuestionReply setValue(AskQuestionReply askQuestionReply);

    void update(AskQuestionReply askQuestionReply);

    /*
    查询全部分页查找
     */
    @Deprecated
    List<AskQuestionReply> getAll(int page, int size);

    /**
     * 用留言标题 查找问政数据id,只查第一条
     * @param title
     * @return
     */
    AskQuestionReply getFirstRdForCheck(String title);

    /**
     * 判断是否重复提问
     * @param title
     * @return
     */
    AskQuestionReply isRepeatAsk(String title);

    /**
     * 用留言标题 查询一定条数的数据
     * @param title
     * @param pageSize
     * @return
     */
    QueryResult queryForCheck(String title,int pageSize);
}