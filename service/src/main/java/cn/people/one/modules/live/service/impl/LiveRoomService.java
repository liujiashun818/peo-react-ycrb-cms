package cn.people.one.modules.live.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.live.model.LiveRoom;
import cn.people.one.modules.live.model.LiveUser;
import cn.people.one.modules.live.service.ILiveRoomService;
import cn.people.one.modules.live.service.ILiveUserService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 直播间Service
 *
 * @author cheng
 */
@Service
public class LiveRoomService extends BaseService<LiveRoom> implements ILiveRoomService {

    @Autowired
    private BaseDao dao;

    @Autowired
    private IArticleService articleService;

    @Autowired
    private ILiveUserService userService;

    @Override
    public LiveRoom fetch(Long id) {
        LiveRoom room = super.fetch(id);
        if (null == room) {
            return null;
        }
        if (StringUtils.isNotBlank(room.getHostIds())) {
            List<LiveUser> hosts = Lists.newArrayList();
            Arrays.stream(room.getHostIds().split(",")).forEach(hostId -> {
                LiveUser host = userService.fetch(Long.valueOf(hostId));
                if (!Lang.isEmpty(host)) {
                    hosts.add(host);
                }
            });
            room.setHosts(hosts);
        }
        if (StringUtils.isNotBlank(room.getGuestIds())) {
            List<LiveUser> guests = Lists.newArrayList();
            Arrays.stream(room.getGuestIds().split(",")).forEach(guestId -> {
                LiveUser guest = userService.fetch(Long.valueOf(guestId));
                if (!Lang.isEmpty(guest)) {
                    guests.add(guest);
                }
            });
            room.setGuests(guests);
        }
        Condition condition = Cnd.where("article_id", "=", room.getId())
                .and("sys_code", "=", SysCodeType.LIVE.value())
                .and("type", "=", ArticleType.LIVE.value());
        List<Article> articleList = dao.query(Article.class, condition);
        if (!Lang.isEmpty(articleList)) {
            Article article = articleList.get(0);
            room.setViewType(article.getViewType());
            room.setCategoryId(article.getCategoryId());
            room.setBlock(article.getBlock());
            room.setImageUrl(article.getImageUrl());
            room.setPublishDate(article.getPublishDate());
            room.setDelFlag(article.getDelFlag());
        }
        return room;
    }

    @Override
    @Transactional
    public int incrementHits(Long liveId) {
        LiveRoom room = super.fetch(liveId);
        if (null == room) {
            return 0;
        }

        Condition condition = Cnd.where("article_id", "=", liveId)
                .and("sys_code", "=", SysCodeType.LIVE.value())
                .and("type", "=", ArticleType.LIVE.value())
                .and("is_reference", "=", 0);
        List<Article> articleList = dao.query(Article.class, condition);
        if (articleList != null && articleList.size() > 0) {
            Article live = articleList.get(0);
            if ((new Integer(0)).equals(room.getDelFlag()) && "2".equals(room.getStatus())) {
                int hits = live.getHits() == null ? 1 : live.getHits() + 1;
                live.setHits(hits);
                dao.updateIgnoreNull(live);
                return hits;
            } else {
                return live.getHits() == null ? 0 : live.getHits();
            }
        }

        return 0;
    }

    @Override
    @Transactional
    public Long insertLiveRoom(LiveRoom room) {
        dao.insert(room);
        Article article = new Article();
        article.init();
        article.setSysCode(SysCodeType.LIVE.value());
        article.setArticleId(room.getId());
        article.setType(ArticleType.LIVE.value());
        article.setViewType(room.getViewType());
        article.setListTitle(room.getTitle());
        article.setCategoryId(room.getCategoryId());
        article.setTitle(room.getTitle());
        article.setBlock(room.getBlock());
        article.setDelFlag(room.getDelFlag());
        article.setTags(room.getTag());
        article.setImageUrl(room.getImageUrl());
        article.setPublishDate(room.getPublishDate());
        articleService.insert(article);
        return article.getId();
    }

    @Override
    @Transactional
    public Long updateLiveRoom(LiveRoom room) {
        if (null == room.getId() || room.getId().equals(0)) {
            return 0L;
        }
        dao.update(room);
        Condition condition = Cnd.where("article_id", "=", room.getId())
                .and("sys_code", "=", SysCodeType.LIVE.value())
                .and("type", "=", ArticleType.LIVE.value());
        List<Article> articleList = dao.query(Article.class, condition);
        if (!Lang.isEmpty(articleList)) {
            Article article = articleList.get(0);
            article.setViewType(room.getViewType());
            article.setListTitle(room.getTitle());
            article.setCategoryId(room.getCategoryId());
            article.setTitle(room.getTitle());
            article.setBlock(room.getBlock());
            article.setDelFlag(room.getDelFlag());
            article.setTags(room.getTag());
            article.setImageUrl(room.getImageUrl());
            article.setPublishDate(room.getPublishDate());
            articleService.update(article);
            return article.getId();
        }
        return 0L;
    }

    @Override
    public int vDelete(Long id) {
        int i = super.vDelete(id);
        Condition condition = Cnd.where("article_id", "=", id)
                .and("sys_code", "=", SysCodeType.LIVE.value())
                .and("type", "=", ArticleType.LIVE.value());
        List<Article> articleList = dao.query(Article.class, condition);
        if (!Lang.isEmpty(articleList)) {
            Article article = articleList.get(0);
            i = articleService.vDelete(article.getId());
        }
        return i;
    }

    @Override
    public LiveRoom fetchByApp(Long id) {
        Condition condition = Cnd.where("del_flag", "=", 0)
                .and("sys_code", "=", "live")
                .and("article_id", "=", id);
//                .and("is_reference", "=", 0);
        List<Article> articles = dao.query(Article.class, condition);
        if (null == articles || articles.size() == 0) {
            return null;
        }
        LiveRoom room = fetch(id);
        if (null == room || !room.getDelFlag().equals(0)) {
            return null;
        }
        room.setCategoryId(articles.get(0).getCategoryId());
        room.setSyscode(articles.get(0).getSysCode());
        room.setImageUrl(articles.get(0).getImageUrl());
        List<LiveUser> guests = Lists.newArrayList();
        if (StringUtils.isNotBlank(room.getGuestIds())) {
            Arrays.stream(room.getGuestIds().split(",")).forEach(g -> {
                LiveUser guest = userService.fetch(Long.valueOf(g));
                guests.add(guest);
            });
        }
        room.setGuests(guests);
        return room;
    }

    @Override
    @Transactional
    public void autoUpdatePrevueToLiveing() {
        Sql sql = Sqls.create("             " +
                "UPDATE                     " +
                "   live_room               " +
                "SET                        " +
                "   status=2                " +
                "WHERE                      " +
                "   status=1                " +
                "   and live_time <= now()  " +
                "   and del_flag=0          ");
        dao.execute(sql);
    }

}