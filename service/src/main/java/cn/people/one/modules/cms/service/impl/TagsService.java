package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.core.util.time.DateFormatUtil;
import cn.people.one.modules.client.model.FloatingImgs;
import cn.people.one.modules.client.model.front.FloatingImgsVO;
import cn.people.one.modules.client.service.IFloatingImgsService;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Tags;
import cn.people.one.modules.cms.model.front.TagsVO;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ITagsService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by sunday on 2018/9/25.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class TagsService extends BaseService<Tags> implements ITagsService {

    /**
     * 标签列表页面搜索查询
     *
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public QueryResultVO<Tags> findSearchPage(Integer pageNumber, Integer pageSize) {
        //重写查询方法
        Sql sql=Sqls.create("SELECT ct.id,ct.`name`,(SELECT if(COUNT(ca.id) is null,0,COUNT(ca.id)) " +
                "FROM cms_article AS ca WHERE ca.tags=ct.`name` AND ca.del_flag=@del_flag) AS articleCount from cms_tags ct\n" +
                 "WHERE ct.del_flag=0 \n"+
                "ORDER BY ct.create_at desc");
        sql.setParam(Tags.FIELD_STATUS,Tags.STATUS_ONLINE);
        QueryResultVO<Tags> queryResult=listPage(pageNumber,pageSize,sql);
        return queryResult;
    }

    /**
     * 查询所有标签
     * @return
     */
    @Override
    public List<Tags> queryAll(){
        List<Tags> tagsList =
                dao.query(Tags.class, Cnd.where("del_flag", "=", BaseEntity.STATUS_ONLINE));
        return tagsList;
    }

    public List<Article> getArticlesByTags(Tags tags) {
        Condition cnd = getDelFlag(Article.STATUS_ONLINE).and(Article.Constant.TAGS, "=", tags.getName());
        List<Article> articles = dao.query(Article.class,cnd);
        return articles;
    }

    /**
     * 查询每个标签下文章总数
     * @return
     */
    public List<TagsVO> queryCount() {
        StringBuilder ql = new StringBuilder();
        ql.append("SELECT tags AS Tags, count(*) AS Count FROM cms_article WHERE tags is not null GROUP BY tags");

        Sql sql = Sqls.create(ql.toString());
        sql.setCallback(new SqlCallback() {
            public List<TagsVO> invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {

                List<TagsVO> list = new ArrayList<>();
                while (rs.next()) {
                    TagsVO vo = new TagsVO();
                    vo.setTags(rs.getString("Tags"));
                    vo.setCount(rs.getInt("Count"));
                    list.add(vo);
                }
                return list;
            }
        });
        dao.execute(sql);
        List<TagsVO> list = sql.getList(TagsVO.class);
        return list;
    }

    @Override
    @Transactional
    public int updateIgnoreNull(Tags tags) {
        Tags origin = fetch(tags.getId());//修改之前的标签
        int flag = super.updateIgnoreNull(tags);//更新标签
        //同步更新文章列表中使用原标签的文章
        List<Article> articleList = getArticlesByTags(origin);
        if(!Lang.isEmpty(articleList) && articleList.size() > 0){
            for(Article article:articleList){
                article.setId(article.getId());
                article.setTags(tags.getName());
                dao.updateIgnoreNull(article);
            }
        }
        return flag;
    }
}
