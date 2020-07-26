package cn.people.one.modules.activitycode.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.activitycode.model.front.ActivityVO;
import cn.people.one.modules.activitycode.model.front.CodeVo;
import cn.people.one.modules.activitycode.model.front.StatisticsVO;
import cn.people.one.modules.activitycode.service.IActivityCodeService;
import cn.people.one.modules.activitycode.utils.RestTemplateUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import cn.people.one.modules.activitycode.model.front.Organization;
import org.nutz.dao.QueryResult;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sunday on 2017/4/13.
 */
@Service
@Slf4j
public class ActivityCodeService implements IActivityCodeService {

	@Value("${activitycode.domain}")
	private String domain;

	@Value("${activitycode.appId}")
	private String appId;

	@Autowired
	private BaseDao dao;

	/**
	 * 保存邀请码活动
	 *
	 * @param activity
	 */
	public Object activityAdd(ActivityVO activity) {
		String result = "SUCCESS";
		try {
			Organization ass = new Organization();
			ass.setAppId(appId);
//		ass.setOrgId("123");
//		ass.setOrgName("");
			ass.setOrgShort(activity.getPrefix());
			log.info("##### "+ass.getOrgShort());
			RestTemplate restTemplate = new RestTemplate();
			HttpEntity<Object> requesta = new HttpEntity<Object>(ass, RestTemplateUtil.getHeaders());
			log.info("##### organazation/update:" +restTemplate.postForEntity(domain + "organazation/update", requesta, String.class, ass).toString());;

			activity.setAppId(appId);
			activity.setType(1);// 机构类型活动设置为1
			RestTemplate restTemplate2 = new RestTemplate();
			HttpEntity<Object> request = new HttpEntity<Object>(activity, RestTemplateUtil.getHeaders());
//            restTemplate.postForLocation(domain+"activity/save",request,activity);

			if (!restTemplate2.postForEntity(domain + "activity/save", request, String.class, activity).toString().contains("200 OK"))
				result = null;

		} catch (Exception e) {
			result = null;
			log.error("activityAdd method error!!!", e);
		}
		return result;
	}

	/**
	 * 邀请码展示
	 *
	 * @param type 1机构 2用户
	 * @return
	 */
	public QueryResult showActivityCode(Integer pageNumber, Integer pageSize, String type) {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("type", type);
		paramMap.put("appId", appId);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<>(RestTemplateUtil.getHeaders());
		ResponseEntity<String> entity = restTemplate.exchange(domain + "code/getCodeList/{type}/{appId}", HttpMethod.GET, request, String.class, paramMap);
		List<CodeVo> codeVoList = new Gson().fromJson(entity.getBody(), new TypeToken<List<CodeVo>>() {
		}.getType());
		Pager pager = dao.createPager(setPageNumber(pageNumber), setPageSize(pageSize));
		QueryResult result = new QueryResult(codeVoList, pager);
		return result;
	}

	/**
	 * 邀请码统计
	 *
	 * @param
	 * @return
	 */
	public QueryResult statActivityCode(Integer pageNumber, Integer pageSize) {
		Map<String, String> paramMap = new HashMap<>();
		paramMap.put("appId", appId);
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> request = new HttpEntity<>(RestTemplateUtil.getHeaders());
		ResponseEntity<String> entity = restTemplate.exchange(domain + "statistics/getStatisticsList/{appId}", HttpMethod.GET, request, String.class, paramMap);
		List<StatisticsVO> statisticsVOList = new Gson().fromJson(entity.getBody(), new TypeToken<List<StatisticsVO>>() {
		}.getType());
		Pager pager = dao.createPager(setPageNumber(pageNumber), setPageSize(pageSize));
		QueryResult result = new QueryResult(statisticsVOList, pager);
		return result;
	}

	private Integer setPageSize(Integer pageSize) {
		if (null == pageSize || pageSize == 0) {
			return 30;
		}
		return pageSize;
	}

	private Integer setPageNumber(Integer pageNumber) {
		if (null == pageNumber || pageNumber == 0) {
			return 1;
		}
		return pageNumber;
	}


}
