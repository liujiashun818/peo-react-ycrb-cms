package cn.people.one.modules.client.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.model.FloatingImgs;
import cn.people.one.modules.client.model.front.ClientMenuVO;
import cn.people.one.modules.client.model.front.FloatingImgsVO;
import cn.people.one.modules.client.service.IClientMenuService;
import cn.people.one.modules.client.service.IFloatingImgsService;
import cn.people.one.modules.cms.model.Article;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by sunday on 2018/9/25.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class FloatingImgsService extends BaseService<FloatingImgs> implements IFloatingImgsService {

    @Autowired
    private IClientMenuService clientMenuService;

    /**
     * floating图列表页面搜索查询
     *
     * @param pageNumber
     * @param pageSize
     * @param floatingImgsVO
     * @return
     */
    @Override
    public QueryResultVO<FloatingImgs> findSearchPage(Integer pageNumber, Integer pageSize, FloatingImgsVO floatingImgsVO) {
        Criteria cri = Cnd.cri();
        if (null != floatingImgsVO.getName()) {
            cri.where().and("name", "like", "%" + floatingImgsVO.getName() + "%");
        }
        cri.getOrderBy().asc("del_flag").desc("update_at");
        QueryResultVO<FloatingImgs> result = listPage(pageNumber, pageSize, cri);
        List<FloatingImgs> list = (List<FloatingImgs>) result.getList();
        if (!Lang.isEmpty(list)) {
            list.forEach(item -> {
                //普通文章
                if (!Lang.isEmpty(item.getArticleId())) {
                    Article article = dao.fetch(Article.class, item.getArticleId());
                    if (!Lang.isEmpty(article)) {
                        item.setArticleTitle(article.getTitle());
                    }
                }
            });
        }
        return result;
    }

    /**
     * 反转上线状态
     *
     * @param id
     */
    @Override
    @Transactional
    public FloatingImgs changeOnlineStatus(Long id) {
        FloatingImgs floatingImgs = dao.fetch(tClass, id);
        if (floatingImgs.getDelFlag().equals(FloatingImgs.STATUS_OFFLINE)) {
            floatingImgs.setDelFlag(Article.STATUS_ONLINE);
            //只能有一个浮标图处于上线状态
            List<FloatingImgs> list = dao.query(FloatingImgs.class, Cnd.where("del_flag", "=", BaseEntity.STATUS_ONLINE));
            if (!Lang.isEmpty(list) && list.size() > 0) {
                for (FloatingImgs fi : list) {
                    fi.setDelFlag(BaseEntity.STATUS_OFFLINE);
                    updateIgnoreNull(fi);
                }
            }
        } else {
            floatingImgs.setDelFlag(Article.STATUS_OFFLINE);
        }
        updateIgnoreNull(floatingImgs);
        return floatingImgs;
    }

    /**
     * 浮标图信息
     *
     * @return
     */
    @Override
    public FloatingImgs getLastFloatingImgs() {
        Condition articleCondition = Cnd.where(Article.FIELD_STATUS, "=", 0).
                desc("create_at");
        List<FloatingImgs> list = dao.query(FloatingImgs.class, articleCondition);
        if (null == list || list.size() < 1) {
            return null;
        }
        FloatingImgs floatingImgs = list.get(0);
        //APP栏目类型
        if (!Lang.isEmpty(floatingImgs.getCategoryId())) {
            ClientMenu clientMenu = dao.fetch(ClientMenu.class, floatingImgs.getCategoryId());
            if (!Lang.isEmpty(clientMenu)) {
                floatingImgs.setCategoryName(clientMenu.getName());
                floatingImgs.setClientMenuId(clientMenu.getId());
                floatingImgs.setRealCategoryId(String.valueOf(clientMenu.getCategoryId()));
                floatingImgs.setCategoryHref(clientMenu.getRedirectUrl());
            }

            List<ClientMenuVO> clientMenuVOS = clientMenuService.getTree(floatingImgs.getCategoryId());
            if (!Lang.isEmpty(clientMenuVOS) && clientMenuVOS.size() > 0) {
                floatingImgs.setIsHaveCategoryChild(true);
            } else {
                floatingImgs.setIsHaveCategoryChild(false);
            }

        }

        //新闻详情页类型
        if (!Lang.isEmpty(floatingImgs.getArticleId())) {
            Article article = dao.fetch(Article.class, floatingImgs.getArticleId());
            if (article != null) {
                floatingImgs.setArticleId(article.getArticleId());
                floatingImgs.setArticleType(article.getType());
                floatingImgs.setSysCode(article.getSysCode());
            }
        }

        return floatingImgs;
    }
}
