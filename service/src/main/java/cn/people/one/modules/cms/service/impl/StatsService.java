package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.core.util.time.DateFormatUtil;
import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.cms.model.front.ArticleStatusVO;
import cn.people.one.modules.cms.model.front.StatsVO;
import cn.people.one.modules.cms.service.IStatsService;
import org.apache.http.client.utils.DateUtils;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by maliwei.tall on 2017/4/11.
 */
@Service
@Transactional(readOnly = true)
public class StatsService implements IStatsService {

	@Autowired
	private BaseDao dao;

	public List<StatsVO> queryStats(Map<String, String> paramMap) {
		StringBuilder ql = new StringBuilder();

		ql.append("	SELECT max(c.id) AS categoryId, max(c. NAME) AS categoryName, max(category3_.id) AS parentId, max(category3_. NAME) AS parentName, 	");
		ql.append("	count(*) AS articleCount, sum(a.hits) AS hitsCount, sum(a.comments) AS commentsCount, max(a.update_at) AS updateTime FROM cms_article a 	");
		ql.append("	INNER JOIN cms_category c ON a.category_id = c.id ");
		ql.append("	INNER JOIN cms_category category3_ ON c.parent_id = category3_.id 	");
		ql.append("	INNER JOIN sys_office o ON c.office_id = o.id 	");

		ql.append("	WHERE 1=1");
		try {
			Long categoryId = paramMap.get("categoryId") == null ? 0L : Long.parseLong(paramMap.get("categoryId"));
			if (categoryId > 0) {
				ql.append(" and (c.id = " + categoryId + " or c.parent_ids like '%," + categoryId + ",%')");
			}

			Long officeId = paramMap.get("officeId") == null ? 0L : Long.parseLong(paramMap.get("officeId"));
			if (officeId > 0) {
				ql.append(" and (o.id = " + officeId + " or o.parent_ids like '%," + officeId + ",%')");
			}
			if (StringUtils.isNotBlank(paramMap.get("beginTime")) && StringUtils.isNotBlank(paramMap.get("endTime"))) {
				Long beginTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("beginTime")).getTime();
//	if (beginTime == null){
//		beginTime = DateUtils.setDays(new Date(), 1);
//		paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
//	}
				Long endTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("endTime")).getTime();
//	if (endTime == null){
//		endTime = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
//		paramMap.put("endDate", DateUtils.formatDate(endTime, "yyyy-MM-dd"));
//	}
				ql.append("	AND (a.create_at BETWEEN " + beginTime + " AND " + endTime + " ) ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ql.append("	AND c.del_flag = 0 GROUP BY category3_.sort, category3_.id, c.sort, c.id ORDER BY category3_.sort, category3_.id, c.sort, c.id	");

		Sql sql = Sqls.create(ql.toString());
//        sql.params().set("delFlag", 0);
		sql.setCallback(new SqlCallback() {
			public List<StatsVO> invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {

				List<StatsVO> list = new ArrayList<StatsVO>();
				while (rs.next()) {
					StatsVO vo = new StatsVO();
					vo.setCategoryId(rs.getString("categoryId"));
					vo.setCategoryName(rs.getString("categoryName"));
					vo.setParentId(rs.getString("parentId"));
					vo.setParentName(rs.getString("parentName"));
					vo.setArticleCount(rs.getInt("articleCount"));
					vo.setHitsCount(rs.getInt("hitsCount"));
					vo.setCommentsCount(rs.getInt("commentsCount"));
//					vo.setUpdateTime(DateUtils.formatDate(new Date(rs.getString("updateTime")), "yyyy-MM-dd hh:mm:ss"));
					vo.setUpdateTime(DateFormatUtil.formatDate("yyyy-MM-dd HH:mm:ss", Long.parseLong(rs.getString("updateTime"))));
					list.add(vo);
				}
				return list;
			}
		});
		dao.execute(sql);
		List<StatsVO> list = sql.getList(StatsVO.class);
		return list;
	}

	public List<StatsVO> queryCount(Map<String, String> paramMap) {
		StringBuilder ql = new StringBuilder();

		ql.append("	SELECT max(a.authors) AS authors,count(a.id) AS articleCount, sum(a.hits) AS hitsCount, sum(a.comments) AS commentsCount, max(a.update_at) AS updateTime FROM cms_article a ");
		ql.append("	WHERE 1=1");
		try {
			if (StringUtils.isNotBlank(paramMap.get("authors"))) {
				ql.append(" and a.authors like '%" + paramMap.get("authors") + "%'");
			}

			if (StringUtils.isNotBlank(paramMap.get("beginTime")) && StringUtils.isNotBlank(paramMap.get("endTime"))) {
				Long beginTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("beginTime")).getTime();
//	if (beginTime == null){
//		beginTime = DateUtils.setDays(new Date(), 1);
//		paramMap.put("beginDate", DateUtils.formatDate(beginDate, "yyyy-MM-dd"));
//	}
				Long endTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("endTime")).getTime();
//	if (endTime == null){
//		endTime = DateUtils.addDays(DateUtils.addMonths(beginDate, 1), -1);
//		paramMap.put("endDate", DateUtils.formatDate(endTime, "yyyy-MM-dd"));
//	}
				ql.append("	AND (a.create_at BETWEEN " + beginTime + " AND " + endTime + " ) ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		ql.append("	AND a.del_flag = 0 GROUP BY a.authors ORDER BY articleCount desc,commentsCount desc,hitsCount desc");

		Sql sql = Sqls.create(ql.toString());
		sql.setCallback(new SqlCallback() {
			public List<StatsVO> invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {

				List<StatsVO> list = new ArrayList<StatsVO>();
				while (rs.next()) {
					StatsVO vo = new StatsVO();
					vo.setAuthors(rs.getString("authors"));
					vo.setArticleCount(rs.getInt("articleCount"));
					vo.setHitsCount(rs.getInt("hitsCount"));
					vo.setCommentsCount(rs.getInt("commentsCount"));
//					vo.setUpdateTime(DateUtils.formatDate(new Date(rs.getString("updateTime")), "yyyy-MM-dd hh:mm:ss"));
					if (StringUtils.isNotBlank(rs.getString("updateTime")))
						vo.setUpdateTime(DateFormatUtil.formatDate("yyyy-MM-dd hh:mm:ss", Long.parseLong(rs.getString("updateTime"))));

					list.add(vo);
				}
				return list;
			}
		});
		dao.execute(sql);
		List<StatsVO> list = sql.getList(StatsVO.class);
		return list;
	}
	public List<ArticleStatusVO> queryArticleStats(Map<String, String> paramMap) {
		StringBuilder ql = new StringBuilder();
		ql.append(" SELECT u. NAME AS userName,o. NAME AS officeName,count(*) AS articleCount,");
		ql.append(" sum(a.hits) AS hitsCount,sum(a.comments) AS commentsCount FROM cms_article a");
		ql.append(" INNER JOIN sys_user u ON u.id=a.create_by");
		ql.append(" INNER JOIN sys_office o ON u.office_id = o.id where 1=1");
		try{
			if (StringUtils.isNotBlank(paramMap.get("userName"))) {
				ql.append(" and u.name like '%" + paramMap.get("userName") + "%'");
			}
			if (StringUtils.isNotBlank(paramMap.get("beginTime")) && StringUtils.isNotBlank(paramMap.get("endTime"))) {
				Long beginTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("beginTime")).getTime();
				Long endTime = DateFormatUtil.pareDate(DateFormatUtil.PATTERN_ISO_ON_DATE, paramMap.get("endTime")).getTime();
				ql.append("	AND (a.create_at BETWEEN " + beginTime + " AND " + endTime + " ) ");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		ql.append(" and a.del_flag=0 and o.del_flag=0 GROUP BY u.id ORDER BY articleCount DESC ");
		Sql sql = Sqls.create(ql.toString());
		sql.setCallback(new SqlCallback() {
			public List<ArticleStatusVO> invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
				List<ArticleStatusVO> list = new ArrayList();
				while (rs.next()) {
					ArticleStatusVO vo = new ArticleStatusVO();
					vo.setUserName(rs.getString("userName"));
					vo.setOfficeName(rs.getString("officeName"));
					vo.setArticleCount(rs.getInt("articleCount"));
					vo.setHitsCount(rs.getInt("hitsCount"));
					vo.setCommentsCount(rs.getInt("commentsCount"));
					list.add(vo);
				}
				return list;
			}
		});
		dao.execute(sql);
		List<ArticleStatusVO> list = sql.getList(ArticleStatusVO.class);
		return list;
	}
}
