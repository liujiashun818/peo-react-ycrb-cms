package cn.people.one.appapi.converter;

import cn.people.one.appapi.vo.CommentVO;
import cn.people.one.modules.comment.model.Comments;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Date;

/**
 * Created by wilson on 2018-10-16.
 */
public class CommentConverter {

    public static CommentVO toVO(Comments po) {
        if (po == null) {
            return null;
        }

        CommentVO vo = new CommentVO();
        vo.setId(po.getId());
        vo.setLikes(po.getLikes() == null ? 0 : po.getLikes());
        vo.setContent(StringEscapeUtils.unescapeHtml(po.getContent()));
        vo.setDate(new Date(po.getCreateAt()));
        vo.setArticleId(po.getArticleId());
        vo.setCategoryId(po.getCategoryId());
        vo.setUsername(po.getUserName());
        vo.setUserOpenId(po.getUserOpenId());
        vo.setSysCode(po.getSysCode());
        vo.setTitle(StringEscapeUtils.unescapeHtml(po.getTitle()));
        vo.setUserIcon(po.getUserIcon());
        vo.setImage(po.getImage());
        return vo;
    }

}
