package cn.people.one.modules.ask.service.impl;


import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.api.Result;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.ask.service.*;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.ask.model.*;
import cn.people.one.modules.ask.model.front.AskQuestionReplyDetailVO;
import cn.people.one.modules.ask.model.front.PageAskQuestionQueryVO;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ICategoryService;
import cn.people.one.modules.search.service.IElasticSearchService;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.Sqls;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.dao.sql.OrderBy;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.cri.SqlExpressionGroup;
import org.nutz.lang.Lang;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
@Slf4j
@EnableAsync
public class AskQuestionReplyService extends BaseService<AskQuestionReply> implements IAskQuestionReplyService
{
    @Autowired
    BaseDao dao;

    @Autowired
    IAskGovernmentService askGovernmentService;

    @Autowired
    IAskDomainService domainService;

    @Autowired
    IAskTypeService typeService;

    @Autowired
    IArticleService articleService;

    @Autowired
    ICategoryService categoryService;

    @Autowired
    IAskQuestionPushService iAskQuestionPushService;

    @Autowired
    IElasticSearchService elasticSearchService;

    Map<String, Object> cache=new ConcurrentHashMap<>();

    private static final long GOVID=426;

    @Override
    public QueryResultVO<AskQuestionReply> searchAskQuestions(PageAskQuestionQueryVO req)
    {
        QueryResultVO<AskQuestionReply> result=null;
        try
        {
            log.info("searchAskQuestions.req=" + JSON.toJSONString(req));
            if (req.getCurrent() == null)
            {
                req.setCurrent(1);
            }
            if (req.getPageSize() == null)
            {
                req.setCurrent(20);
            }

            if (req.getGovId() != null)
            {
                AskGovernment askGov;
                if(req.getGovId()==0){
                    askGov=askGovernmentService.getGovernmentByFup(req.getGovId());
                }else{
                    askGov=askGovernmentService.getGovernmentById(req.getGovId());
                }

                if (askGov.isLocalGov() && askGov.getHasChild())
                {
                    List<Long> childsId = askGovernmentService.traverseChild(askGov.getFid());
                    req.setGovernmentIds(childsId);
                }
                else
                {
                    List<Long> para = new ArrayList<>();
                    para.add(req.getGovId());
                    req.setGovernmentIds(para);
                }
            }

            Sql sql = preparedQueryParam(req);

            result = listPage(req.getCurrent(), req.getPageSize(), sql);
            for (Object o : result.getList())
            {
                AskQuestionReply r = (AskQuestionReply)o;
                r.setArticleId(r.getId());
                r.setAskDomainName(domainService.getNameByid(r.getDomainId()));
                r.setAskTypeName(typeService.getNameByid(r.getTypeId()));
                r.setAskGovernmentName(askGovernmentService.getNameById(r.getGovernmentId()));
            }
//            log.info("sql=" + criteria.toSql(sql.getEntity()));
        }
        catch (Exception e)
        {
           log.error(e.getMessage(),e);
        }
        return result;
        
    }
    @Override
    public QueryResult searchAskQuestionsCom(PageAskQuestionQueryVO req)
    {
        QueryResult result=new QueryResult();
        try
        {
            log.info("searchAskQuestions.req=" + JSON.toJSONString(req));

            if (req.getGovId() != null)
            {
                AskGovernment askGov;
                if(req.getGovId()==0){
                    askGov=askGovernmentService.getGovernmentByFup(req.getGovId());
                }else{
                    askGov=askGovernmentService.getGovernmentById(req.getGovId());
                }

                if (askGov.isLocalGov() && askGov.getHasChild())
                {
                    List<Long> childsId = askGovernmentService.traverseChild(askGov.getFid());
                    req.setGovernmentIds(childsId);
                }
                else
                {
                    List<Long> para = new ArrayList<>();
                    para.add(req.getGovId());
                    req.setGovernmentIds(para);
                }
            }

            Sql sql = preparedQueryParam(req);

            result = listPage(req.getCurrent(), req.getPageSize(), sql);
            for (Object o : result.getList())
            {
                AskQuestionReply r = (AskQuestionReply)o;
                r.setArticleId(r.getId());
                r.setAskDomainName(domainService.getNameByid(r.getDomainId()));
                r.setAskTypeName(typeService.getNameByid(r.getTypeId()));
                r.setAskGovernmentName(askGovernmentService.getNameById(r.getGovernmentId()));
            }
//            log.info("sql=" + criteria.toSql(sql.getEntity()));
        }
        catch (Exception e)
        {
            log.error(e.getMessage(),e);
        }
        return result;

    }
    /** 
    * @Title: preparedQueryParam 
    * @author Administrator
    * @date 2019年2月21日 上午10:47:46 
    * @Description: TODO(这里用一句话描述这个方法的作用) 
    * @param @param req
    * @param @return  参数说明 
    * @return Sql    返回类型 
    * @throws 
    */
    private Sql preparedQueryParam(PageAskQuestionQueryVO req)
    {
        Sql sql = Sqls.create("SELECT id,id as articleId,tid,title,domain_id,type_id,government_id,"
                              + "user_name,real_user_name,status,question_time,Order_ID,"
                              + "Publish_Status,is_headline,question_content FROM ask_question_reply  $condition");

        Criteria criteria = Cnd.NEW().getCri();
        SqlExpressionGroup where = criteria.where();

        if (CollectionUtils.isNotEmpty(req.getGovernmentIds()))
        {
            List<String> govIds = new ArrayList<>();
            for (Long id : req.getGovernmentIds())
            {
                govIds.add(id + "");
            }
            where.andIn("government_id", govIds.toArray(new String[0]));
        }

        if (req.getIds() != null && req.getIds().length > 0)
        {
            where.andIn("id", req.getIds());
        }
        if (StringUtils.isNotEmpty(req.getStartTime()))
        {
            where.and("question_time", ">=", req.getStartTime());
        }
        if (StringUtils.isNotEmpty(req.getEndTime()))
        {
            where.and("question_time", "<=", req.getEndTime());
        }
        if (req.getDomainId() != null)
        {
            where.andEquals("domain_id", req.getDomainId());
        }
        if (req.getTypeId() != null)
        {
            where.andEquals("type_id", req.getTypeId());
        }
        if (StringUtils.isNotEmpty(req.getStatus()))
        {
            where.andEquals("status", req.getStatus());
        }
        if (StringUtils.isNotEmpty(req.getSource()))
        {
            where.andEquals("source", req.getSource());
        }
        if (StringUtils.isNotEmpty(req.getTitle()))
        {
            where.and("title", "like", "%" + req.getTitle() + "%");
        }
        if (StringUtils.isNotEmpty(req.getQuestionContent()))
        {
            where.and("question_content", "like", "%" + req.getQuestionContent() + "%");
        }
        if (StringUtils.isNotEmpty(req.getOrganization()))
        {
            where.and("organization", "like", "%" + req.getOrganization() + "%");
        }
        if (StringUtils.isNotEmpty(req.getReplyContent()))
        {
            where.and("reply_content", "like", "%" + req.getReplyContent() + "%");
        }
        OrderBy orderBy = criteria.getOrderBy();
        orderBy.desc("is_headline").desc("order_ID").desc("question_time");
        sql.setCondition(criteria);
        
        log.info("sql=" + criteria.toSql(sql.getEntity()));
        return sql;
    }

    @Override
    public Map<String, Object> getDetailById(Long id)
    {
        Map<String, Object> map = new HashMap<>();
        List<AskType> typeList = typeService.getAllTypes();
        List<AskDomain> domainList = domainService.getAllDomains();
        AskQuestionReply detail = dao.fetch(tClass, id);
        if(detail.getFalsecommentsNum()==null || detail.getFalsecommentsNum()==0){
            detail.setFalsecommentsNum(detail.getCommentsNum());
        }
        AskQuestionReplyDetailVO vo = new AskQuestionReplyDetailVO();
        BeanUtils.copyProperties(detail, vo);

        map.put("domainList", domainList);
        map.put("typeList", typeList);
        map.put("detail", vo);
        return map;
    }

    @Override
    @Transactional
    public boolean recommendToArticle(Long categoryId, String askIds)
    {
        try
        {
            Category category = categoryService.fetch(categoryId);
            PageAskQuestionQueryVO req = new PageAskQuestionQueryVO();
            req.setIds(askIds.split(","));

            QueryResult result = searchAskQuestionsCom(req);
            List<AskQuestionReply> list = (List<AskQuestionReply>)result.getList();
            if (null != category && CollectionUtils.isNotEmpty(list))
            {
                for (AskQuestionReply ask : list)
                {
                    Article article = new Article();
                    article.setCategoryId(categoryId);
                    article.setTitle(ask.getTitle());
                    article.setSubTitle(ask.getTitle());
//                    article.setShareUrl("/askDetail.html?id="+ask.getId());//TODO 生成shareURL
                    article.setType("ask");
                    article.setSysCode("ask");
                    article.setViewType("normal");
                    article.setPublishDate(ask.getQuestionTime());
                    System.out.println(ask.getQuestionContent());
                    article.setDescription(ask.getQuestionContent());//TODO:内容怎么对应
                    if (ask.getHeadImage() != null && !"".equals(ask.getHeadImage()))
                    {
                        article.setImageUrl(ask.getHeadImage());// TODO:图片对应哪个
                    }

                    article.setIsReference(false);
                    ArticleData articleData=new ArticleData();
                    articleData.setId(article.getId());
                    articleData.setContent(ask.getQuestionContent());
                    article.setArticleData(articleData);
                    article.setDelFlag(BaseEntity.STATUS_AUDIT);
                    article.setDescription(ask.getTitle());
                    article.setBlock(2);
                    article.setArticleId(ask.getId());
                    articleService.save(article);
                    Article article1=dao.fetch(Article.class,article.getId());
                    article1.setArticleId(ask.getId());
                    dao.update(article1);
                }
            }
            return true;
        }
        catch (Exception e)
        {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<AskQuestionReply> getHeadResult() {
        Sql sql = Sqls.queryRecord("SELECT *  FROM  ask_question_reply  WHERE  " +
                "status>0 and status<4 and publish_status=1 and is_headline=1 $condition");
        sql.setCondition(Cnd.orderBy().desc("order_id").desc("question_time"));
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(AskQuestionReply.class));
        dao.execute(sql);
        return sql.getList(AskQuestionReply.class);
    }

    @Override
    public List<AskQuestionReply> getNormalResult(Integer pageSize,Integer pageNo) {
        Sql sql = Sqls.queryRecord("SELECT *  FROM  ask_question_reply  WHERE  " +
                "status>0 and status<4 and publish_status=1 and is_headline<>1 $condition");
        Pager pager = new Pager();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        sql.setPager(pager);
        sql.setCondition(Cnd.orderBy().desc("order_id").desc("question_time"));
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(AskQuestionReply.class));
        dao.execute(sql);
        return sql.getList(AskQuestionReply.class);
    }

    @Override
    public QueryResult getResult(Integer page, Integer size) {
        //TODO
        Condition condition = Cnd.where("status", ">", 0)
                .and("status","<",4)
                .and("is_headline","<>",1)
                .desc("id")
                .desc("question_time");
        return this.listPage(AskQuestionReply.class, page, size, condition);
    }

    @Override
    public AskQuestionReply getFirstRdForCheck(String title) {
        QueryResult r = queryForCheck(title,1);
        if(r ==null || r.getList().isEmpty()) {
            return null;
        }
        AskQuestionReply rtn = (AskQuestionReply)r.getList().get(0);
        return rtn;
    }

    /**
     * 判断提问是否重复
     * @param title
     * @return
     */
    @Override
    public AskQuestionReply isRepeatAsk(String title) {
        AskQuestionReply askQuestionReply= getFirstRdForCheck(title);
        return askQuestionReply;
    }

    @Override
    public QueryResult queryForCheck(String title, int pageSize) {
        Condition condition = Cnd.where("status", ">", 0)
                .and("status","<",4)
                .and("publish_Status","=",1)
                .and("title","=",title).orderBy("question_time","desc");
        return this.listPage(AskQuestionReply.class, 1, pageSize, condition);
    }

    @Deprecated
    @Override
    public List<AskQuestionReply> getAll(int pageNo, int pageSize) {
        Sql sql = Sqls.queryRecord("SELECT *  FROM  ask_question_reply $condition");
        Pager pager = new Pager();
        pager.setPageNumber(pageNo);
        pager.setPageSize(pageSize);
        sql.setPager(pager);
        sql.setCondition(Cnd.where("status", ">", 0)
                .and("status","<",4)
                .and("publish_Status","=",1));
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(dao.getEntity(AskQuestionReply.class));
        dao.execute(sql);
        return sql.getList(AskQuestionReply.class);
    }
    @Override
    @Transactional
    public void updateSupport(Long askId,Integer newZan) {
        Sql sql=Sqls.create("update ask_question_reply set zan_num="+ newZan+" where id="+askId);
        dao.execute(sql);
    }

    @Override
    @Transactional
    public Result online(Long id) {
        AskQuestionReply askQuestionReply=dao.fetch(AskQuestionReply.class,Cnd.where("id","=",id));
        if(askQuestionReply !=null){
            Sql sql=Sqls.create("update ask_question_reply set publish_status=1 where id="+id);
            dao.execute(sql);
            // 保存到ES索引
            askQuestionReply.setPublishStatus(1);
            elasticSearchService.saveAsk(askQuestionReply);
            elasticSearchService.saveSuggestAsk(askQuestionReply);
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @Override
    @Transactional
    public Result downline(Long id) {
        AskQuestionReply askQuestionReply=dao.fetch(AskQuestionReply.class,Cnd.where("id","=",id));
        if(askQuestionReply !=null){
            Sql sql=Sqls.create("update ask_question_reply set publish_status=0 where id="+id);
            dao.execute(sql);
            // 保存到ES索引
            askQuestionReply.setPublishStatus(0);
            elasticSearchService.saveAsk(askQuestionReply);
            elasticSearchService.saveSuggestAsk(askQuestionReply);
            return Result.success("操作成功");
        }
        return Result.error("操作失败");
    }

    @Override
    @Transactional
    public void  batchUpdate(List<AskQuestionReply> list) {
        if (!Lang.isEmpty(list)) {
            list.stream().forEach((t) -> {
                updateIgnoreNull(t);
            });
        }
    }

    @Override
    @Transactional
    public Result setTop(Long id) {
        AskQuestionReply askQuestionReply=dao.fetch(AskQuestionReply.class,Cnd.where("is_headline","=","1"));
        if(askQuestionReply != null){
            Sql sql=Sqls.create("update ask_question_reply set is_headline=0 where id="+askQuestionReply.getId());
            dao.execute(sql);
        }
        AskQuestionReply askQuestionReplyResult=dao.fetch(AskQuestionReply.class,Cnd.where("id","=",id));
        if(askQuestionReplyResult == null){
            return Result.error("操作失败");
        }
        askQuestionReplyResult.setIsHeadline(1);
        dao.update(askQuestionReplyResult);
        return Result.success("操作成功");
    }

    @Override
    @Transactional
    public Result cancelTop(Long id) {
        AskQuestionReply askQuestionReply=dao.fetch(AskQuestionReply.class,Cnd.where("id","=",id));
        if(askQuestionReply == null){
            return Result.error("操作失败");
        }
        askQuestionReply.setIsHeadline(0);
        dao.update(askQuestionReply);
        // 保存到ES索引
        elasticSearchService.saveAsk(askQuestionReply);
        elasticSearchService.saveSuggestAsk(askQuestionReply);
        return Result.success("操作成功");
    }

    /**
     * 对留言状态进行转换
     * @param status
     * @return
     */
    private String convert(Integer status){
        if(0 == status){
            return "未审核";
        }
        if(1 == status){
            return "已审核";
        }
        if(2 == status){
            return "办理中";
        }
        if(3 == status){
            return "已回复";
        }
        if(4 == status){
            return "审核未通过";
        }else{
            return  null;
        }
    }

    /**
     * 保存提问问题，设置默认参数
     * @param askQuestionReply
     * @return
     */
    @Override
    public AskQuestionReply setValue(AskQuestionReply askQuestionReply){

        askQuestionReply.setOriginalTitle(askQuestionReply.getTitle());
        askQuestionReply.setSysCode("ask");
        askQuestionReply.setIsHeadline(0);
        askQuestionReply.setStatus(0);//留言状态：0未审核、1已审核、2办理中、3已回复,4审核未通过，默认为0
        askQuestionReply.setGovernmentId(GOVID);
        askQuestionReply.setQuestionTime(new Date());
        //tid如果是对地方政府的提问，则表示提问id，与地方部提供的数据的tid相同；否则为空
        AskGovernment askGovernment = askGovernmentService.fetch(askQuestionReply.getGovernmentId());
        if(askGovernment != null &&  "1".equals(askGovernment.getHasChild())){
            askQuestionReply.setTid(askQuestionReply.getGovernmentId());
        }

        if(askQuestionReply.getPublishStatus() == null){
            askQuestionReply.setPublishStatus(1);//发布状态
        }
        askQuestionReply.setOrderID(0);//顺序
        if(askQuestionReply.getIsChecked() == null){
            askQuestionReply.setIsChecked(false);
        }

        //设置给前端的返回码
        Date date = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMdd");
        String questionTime=dateFormat.format(date);
        askQuestionReply.setReturnCode("WZ"+questionTime+((int)(Math.random()*(9999-1000+1))+1000));

        // 保存到ES索引
        elasticSearchService.saveAsk(askQuestionReply);
        elasticSearchService.saveSuggestAsk(askQuestionReply);
        return  askQuestionReply;
    }

    @Override
    @Transactional
    public void update(AskQuestionReply askQuestionReply) {
        AskQuestionReply askQuestionReply1=dao.fetch(AskQuestionReply.class,askQuestionReply.getId());
        askQuestionReply.setTid(askQuestionReply1.getTid());
        askQuestionReply.setQuestionTime(askQuestionReply1.getQuestionTime());
        dao.updateIgnoreNull(askQuestionReply);
        //只有已回复并且回复内容有修改的时候才进行推送
        if(askQuestionReply.getStatus()==3 && !StringUtils.equals(askQuestionReply1.getReplyContent(),askQuestionReply.getReplyContent())){
            iAskQuestionPushService.savePushContent(askQuestionReply);
        }

        // 保存到ES索引
        elasticSearchService.saveAsk(askQuestionReply);
        elasticSearchService.saveSuggestAsk(askQuestionReply);
    }

    /**
     * 获取我的提问列表
     * 分页条件查询
     * @param user_id
     * @param pageSize
     * @param pageNumber
     * @return
     */
    @Override
    public Map  myAskList(String user_id, Integer pageSize, Integer pageNumber){

        List data = new ArrayList<>();
        Condition condition = Cnd.where("user_id","=",user_id).desc("question_time");
        QueryResult resultQuery = listPage(pageNumber, pageSize , condition, "id");

        Pager pager = dao.createPager(pageNumber, pageSize);
        List<AskQuestionReply> askQuestionReplyList = dao.query(AskQuestionReply.class, condition, pager);
        pager.setRecordCount(dao.count(AskQuestionReply.class));

        for(AskQuestionReply askQuestionReply : askQuestionReplyList){
            Map map = new HashMap();
            map.put("title", askQuestionReply.getTitle());//留言标题
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            map.put("questionTime", formatter.format(askQuestionReply.getQuestionTime()));//留言时间
            map.put("status", askQuestionReply.getStatus());//留言状态：0未审核、1已审核、2办理中、3已回复,4审核未通过，默认为0',
            if(askQuestionReply.getSysCode() == null){
                map.put("sysCode", "ask");
            }else {
                map.put("sysCode", askQuestionReply.getSysCode());
            }
            map.put("questionId", askQuestionReply.getId());
            //获取该问题的提问机构
            AskGovernment askGovernment = askGovernmentService.fetch(askQuestionReply.getGovernmentId());
            String peopleName = askGovernment.getName();
            Long fup = Long.valueOf(askGovernment.getFup());
            Condition con = Cnd.where("fid","=",fup);
            List<AskGovernment> askGovernment1 = dao.query(AskGovernment.class, con, null);
            String govenmentName = askGovernment1.get(0).getName();
            String name = govenmentName+","+peopleName;
            map.put("name", name);
            data.add(map);
        }
        Map temp = new HashMap();
        temp.put("pageCount", resultQuery.getPager().getPageCount());
        temp.put("recordCount", resultQuery.getPager().getRecordCount());
        temp.put("data", data);
        return temp;
    }
}