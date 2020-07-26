package cn.people.one.modules.comment.service;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.comment.model.Comments;
import cn.people.one.modules.comment.model.front.CommentsParam;
import cn.people.one.modules.comment.model.front.CommentsVO;
import io.swagger.models.auth.In;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 评论Service
 *
 * @author 周欣
 */
public interface ICommentsService extends IBaseService<Comments> {
    /**
     * 切换上下线状态
     *
     * @param id
     */
    void changeOnlineStatus(Long id);

    Comments query(Condition cnd);

    List<Comments> comments(String sysCode, Long articleId);

    QueryResult comments(String sysCode, Long articleId, Integer page, Integer size);

    QueryResult myComments(String openId, Integer page, Integer size);

    QueryResultVO<Comments> findSearchPage(Integer pageNumber, Integer pageSize, CommentsVO commentsVO);

    List<Comments> replyComments(String openId, Integer page, Integer size);

    long countReplyComments(String openId);

    List<Comments> queryByCnd(Cnd condition);

    Comments saveReplay(CommentsParam requestComment, Comments originalComment);

    Comments addComment(CommentsParam commentsParam);

    QueryResultVO<Comments> listOfArticle(Map<String,String> params);

    void changeOnlineOffStatus(Long id, boolean on);

    QueryResultVO<Comments> findSearchPageForPublic(Integer pageNumber, Integer pageSize,
                                                    CommentsVO commentsVO);

    Comments saveArticleComment(Comments comments);

    /**
     * 审核评论
     * @param commentId
     * @param actionStatus
     */
    void auditComment(Long commentId, Integer actionStatus);

    void pushComment(Comments comment) throws IOException;
}