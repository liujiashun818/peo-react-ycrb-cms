package cn.people.one.modules.cms.service.impl;

import cn.people.one.core.aop.annotation.TimeMonitor;
import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.time.ClockUtil;
import cn.people.one.core.util.time.DateUtil;
import cn.people.one.modules.activitycode.utils.RestTemplateUtil;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.client.model.ClientPush;
import cn.people.one.modules.client.service.IClientPushService;
import cn.people.one.modules.cms.model.*;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.front.MediaResourceVO;
import cn.people.one.modules.cms.model.front.SxrbAppcmsCategoryMenuVO;
import cn.people.one.modules.cms.model.gov.Article2PubliccmsVO;
import cn.people.one.modules.cms.model.gov.ArticlePubiccmsVO;
import cn.people.one.modules.cms.model.gov.MediaForSxrbCms;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.CategoryModelEnum;
import cn.people.one.modules.cms.service.*;
import cn.people.one.modules.file.model.MediaInfo;
import cn.people.one.modules.file.service.IMediaInfoService;
import cn.people.one.modules.user.model.User;
import cn.people.one.modules.user.service.IUserService;
import cn.people.one.modules.user.utils.UserUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.dao.util.cri.Static;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 定时发布
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@EnableAsync
public class ArticleTaskRecordService extends BaseService<ArticleTaskRecord> implements IArticleTaskRecordService {

}
