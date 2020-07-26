package cn.people.one.modules.cms.message;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.service.IArticleDataService;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ISubjectService;
import cn.people.one.modules.live.model.LiveTalk;
import cn.people.one.modules.search.service.IElasticSearchService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * User: 张新征
 * Date: 2017/4/12 13:01
 * Description:
 */
@Component
@Slf4j
public class SendMessage {
	private static JsonMapper jsonMapper = JsonMapper.defaultMapper();
	private static IArticleService articleService;
	private static ISubjectService subjectService;
	private static IElasticSearchService elasticSearchService;
	private static IArticleDataService articleDataService;


	@Autowired
	public void setElasticSearchService(IElasticSearchService elasticSearchService){
		SendMessage.elasticSearchService = elasticSearchService;
	}

	@Autowired
	public void setArticleService(IArticleService articleService) {
		SendMessage.articleService = articleService;
	}

	@Autowired
	public void setArticleDataService(IArticleDataService articleDataService) {
		SendMessage.articleDataService = articleDataService;
	}

	@Autowired
	public void setSubjectService(ISubjectService subjectService) {
		SendMessage.subjectService = subjectService;
	}

	public static void sendArticle(Long id){
		Article article = articleService.fetch(id);

		if(null != article && null != article.getCategoryId()){
			article.setArticleData(articleDataService.fetch(id));
			elasticSearchService.saveArticle(article);
		}
	}

	public static void sendArticleList(Long categoryId){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("article_list",thriftUrl +"/cms/article/lists?TArticleRequest_request={\"categoryId\":"+categoryId+",\"block\":2,\"pageSize\":200}");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("文章列表消息通知完成:" + map);
//		map.clear();
//		map.put("article_head",thriftUrl +"/cms/article/lists?TArticleRequest_request={\"categoryId\":"+categoryId+",\"block\":1,\"pageSize\":4}");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("文章头图消息通知完成" + map);
	}

	public static void sendArticleDetail(Long id){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("article_detail",thriftUrl +"/cms/article/detail?articleId="+id);
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("文章详情消息通知完成:"+map);
	}

	public static void sendSubject(Long id){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("subject", thriftUrl + "/cms/subject/subject?TSubjectRequest_request={\"id\":"+id+",\"pageSize\":200}");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("专题详情消息通知完成"+map);
	}

	public static void sendLiveRoom(Long id){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("live_room", thriftUrl +"/cms/live/room?TLiveRequest_request={\"id\":"+id+"}");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("直播间详情消息通知完成"+map);
	}

	public static void sendLiveTalk(Long id){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("live_talk", thriftUrl + "/cms/live/talk?TLiveRequest_request={\"id\":"+id+",\"time\"::time}");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("直播间对话消息通知完成"+map);
	}

	public static void sendTalkDown(LiveTalk talk){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("talk_down", talk.getRoomId());
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("对话下线消息通知完成"+map);
	}

	public static void sendLoadingImgs(){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("loadingImgs", thriftUrl +"/cms/loadingImgs/getLastLoadingImgs");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
//		log.info("Loading图消息通知完成"+map);
	}

	public static void sendMenu(){
//		Map<String,Object> map = Maps.newHashMap();
//		map.put("menu", thriftUrl +"/app/menu/lists");
//		rabbitTemplate.convertAndSend(jsonMapper.toJson(map));
	}
}
