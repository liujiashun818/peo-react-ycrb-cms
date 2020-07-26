package cn.people.one.modules.cms.service;

import cn.people.one.modules.cms.model.front.ArticleStatusVO;
import cn.people.one.modules.cms.model.front.StatsVO;

import java.util.List;
import java.util.Map;

/**
 * Created by maliwei.tall on 2017/4/11.
 */
public interface IStatsService {

        List<StatsVO> queryStats(Map<String, String> paramMap);
        List<StatsVO> queryCount(Map<String, String> paramMap);
//        Map queryStats(Integer pageNo, Integer pageSize,Map<String, String> paramMap);
        List<ArticleStatusVO> queryArticleStats(Map<String, String> paramMap);
}
