package cn.people.one.modules.client.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.LoadingImgs;
import cn.people.one.modules.client.model.front.LoadingImgsVO;
import cn.people.one.modules.client.service.ILoadingImgsService;
import cn.people.one.modules.cms.message.SendMessage;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.file.model.MediaInfo;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.acl.LastOwnerException;
import java.util.List;

/**
 * loading图service
 * Created by sunday on 2017/4/12.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class LoadingImgsService extends BaseService<LoadingImgs> implements ILoadingImgsService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IArticleService articleService;

    /**
     * loading图列表页面搜索查询
     *
     * @param pageNumber
     * @param pageSize
     * @param loadingImgsVO
     * @return
     */
    @Override
    public QueryResultVO<LoadingImgs> findSearchPage(Integer pageNumber, Integer pageSize, LoadingImgsVO loadingImgsVO) {
        Criteria cri = Cnd.cri();
        if (null != loadingImgsVO.getName()) {
            cri.where().and("name", "like", "%" + loadingImgsVO.getName() + "%");
        }
        cri.getOrderBy().asc("del_flag").desc("update_at");
        QueryResultVO<LoadingImgs> result = listPage(pageNumber, pageSize, cri);
        return result;
    }

    /**
     * 反转上线状态
     *
     * @param id
     */
    @Override
    @Transactional
    public LoadingImgs changeOnlineStatus(Long id) {
        LoadingImgs loadingImgs = dao.fetch(tClass, id);
        if (loadingImgs.getDelFlag().equals(LoadingImgs.STATUS_OFFLINE)) {
            loadingImgs.setDelFlag(Article.STATUS_ONLINE);
            //只能有一个开屏图处于上线状态
            List<LoadingImgs> list = dao.query(LoadingImgs.class, Cnd.where("del_flag", "=", BaseEntity.STATUS_ONLINE));
            if (!Lang.isEmpty(list) && list.size() > 0) {
                for (LoadingImgs li : list) {
                    li.setDelFlag(BaseEntity.STATUS_OFFLINE);
                    updateIgnoreNull(li);
                }
            }
        } else {
            loadingImgs.setDelFlag(Article.STATUS_OFFLINE);
        }
        updateIgnoreNull(loadingImgs);
        return loadingImgs;
    }

    @Override
    public void sendLoadingImgs() {
        SendMessage.sendLoadingImgs();
    }

    /**
     * 开屏图信息
     *
     * @return
     */
    @Override
    public LoadingImgs getLastLoadingImgs() {
        Condition articleCondition = Cnd.where(Article.FIELD_STATUS, "=", 0).
                desc("create_at");
        List<LoadingImgs> list = dao.query(LoadingImgs.class, articleCondition);
        if (null == list || list.size() < 1) {
            return null;
        }

        LoadingImgs loadingImgs = list.get(0);
        if (!Lang.isEmpty(loadingImgs.getArticleId())) {
            Article article = dao.fetch(Article.class, Long.valueOf(loadingImgs.getArticleId()));
            if (article != null) {
                loadingImgs.setArticleId(article.getArticleId());
                loadingImgs.setArticleType(article.getType());
                loadingImgs.setSysCode(article.getSysCode());
            }
        }

        if (!Lang.isEmpty(loadingImgs.getVideoUrl())) {
            List<MediaInfo> mediaInfoList = dao.query(MediaInfo.class, Cnd.where("fileUrl", "=", loadingImgs.getVideoUrl()));
            if (!Lang.isEmpty(mediaInfoList) && mediaInfoList.size() > 0) {
                MediaInfo mediaInfo = mediaInfoList.get(0);
                if (!Lang.isEmpty(mediaInfo.getHdUrl())) {
                    loadingImgs.setVideoUrl(mediaInfo.getHdUrl());
                }
            }
        }

        return loadingImgs;
    }
}
