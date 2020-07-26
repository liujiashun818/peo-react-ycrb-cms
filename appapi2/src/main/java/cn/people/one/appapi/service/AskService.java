package cn.people.one.appapi.service;

import cn.people.one.appapi.cache.CacheRepository;
import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.vo.ArticleVO;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.ResultVO3;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.ask.model.AskDomain;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.model.AskType;
import cn.people.one.modules.ask.model.front.AskQuestionReplyDetailAppVO;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import cn.people.one.modules.search.model.AskIndexData;
import cn.people.one.modules.search.service.IElasticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("askService")
public class AskService{
    @Autowired
    private IAskQuestionReplyService iAskQuestionReplyService;

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private ArticleService articleService;

    @Autowired
    BaseDao dao;
    @Autowired
    private IElasticSearchService elasticSearchService;
    @Value("${http.vshare}")
    private String vshareUrl;
    private static final String SHARE_URL_CONSTANT = "detail/";

    public ResultVO3<Map> list(Integer pageSize, Integer pageNo,Integer domainId,Integer typeId,String title,Long categoryId ) {
        //头部列表》查询资讯文章
        Map<String,Object> map = new HashMap<>();
        if(pageNo==1 && categoryId!=null){
            List<ArticleVO> headResult=articleService.getHeadArticleList(categoryId,8,pageNo);
            map.put("head",headResult);
        }
        //正常列表
        Cnd cnd=Cnd.where("status",">",0).and("status","<",4)
                .and("publish_status","=",1).and("is_headline","<>",1);
        if(domainId!=null){
            cnd.and("domain_id","=",domainId);
        }
        if(typeId!=null){
            cnd.and("type_id","=",typeId);
        }
        if(StringUtils.isNotBlank(title)){
            cnd.and("title","like","%" + title + "%");
        }
        cnd.desc("order_id").desc("question_time");
        QueryResult queryResult=iAskQuestionReplyService.listPage(pageNo,pageSize,cnd);
        List<AskQuestionReply> list=queryResult.getList(AskQuestionReply.class);
        List<AskQuestionReplyDetailAppVO> listVO = getAskQuesttionReplyList(list);
        map.put("list",listVO);
        return ResultVO3.success(map, queryResult.getPager().getRecordCount(), pageSize, pageNo);
    }

    private List<AskQuestionReplyDetailAppVO> getAskQuesttionReplyList(List<AskQuestionReply> result) {
        List<AskQuestionReplyDetailAppVO> list = new ArrayList<>();

        if(result !=null && result.size()>0){
            for (AskQuestionReply askQuestionReply : result){
                //TODO
                AskDomain askDomain=dao.fetch(AskDomain.class,askQuestionReply.getDomainId());
                AskType askType=dao.fetch(AskType.class,askQuestionReply.getTypeId());
                AskQuestionReplyDetailAppVO askQuestionReplyDetailAppVO=new AskQuestionReplyDetailAppVO();
                askQuestionReplyDetailAppVO.setQuestionId(askQuestionReply.getId());
                askQuestionReplyDetailAppVO.setTitle(StringEscapeUtils.unescapeHtml(askQuestionReply.getTitle()));
                askQuestionReplyDetailAppVO.setStatus(askQuestionReply.getStatus());
                askQuestionReplyDetailAppVO.setRealUserName(askQuestionReply.getOrganization());
                askQuestionReplyDetailAppVO.setHeadImage(askQuestionReply.getHeadImage());
                askQuestionReplyDetailAppVO.setFollowNum(askQuestionReply.getFollowNum());
                askQuestionReplyDetailAppVO.setLikeNum(askQuestionReply.getZanNum());
                askQuestionReplyDetailAppVO.setViewType("ask");
                askQuestionReplyDetailAppVO.setNewsLink("1|"+askQuestionReply.getId());
                askQuestionReplyDetailAppVO.setPrefixId("_ask_10000_"+askQuestionReply.getId());
                askQuestionReplyDetailAppVO.setCommentsNum(askQuestionReply.getFalsecommentsNum());
                askQuestionReplyDetailAppVO.setSysCode("ask");
                askQuestionReplyDetailAppVO.setFieldAndType(askDomain.getName()+"|"+askType.getName());
                list.add(askQuestionReplyDetailAppVO);
            }
        }
        return list;
    }
    public ResultVO2<AskQuestionReply> detail(Long askId) {
        if (askId == null || askId < 1) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        AskQuestionReply askQuestionReply = iAskQuestionReplyService.fetch(askId);
        if (askQuestionReply != null) {
            AskDomain askDomain=dao.fetch(AskDomain.class,askQuestionReply.getDomainId());
            AskType askType=dao.fetch(AskType.class,askQuestionReply.getTypeId());
            askQuestionReply.setSysCode("ask");
            askQuestionReply.setCommentsNum(askQuestionReply.getFalsecommentsNum());
            askQuestionReply.setShareUrl(vshareUrl+SHARE_URL_CONSTANT+"question/"+askId);
            askQuestionReply.setFieldAndType(askDomain.getName()+"|"+askType.getName());
            return ResultVO2.success(askQuestionReply);
        } else {
            return ResultVO2.result(CodeConstant.ASK_NOT_EXIST);
        }
    }

    /**
     * 通过标题获取详情
     * @param title
     * @return
     */
    public ResultVO2<AskQuestionReply> detail(String title) {
        AskQuestionReply askQuestionReply= iAskQuestionReplyService.getFirstRdForCheck(title);
        if (askQuestionReply != null) {
            askQuestionReply.setSysCode("ask");
            askQuestionReply.setCommentsNum(askQuestionReply.getFalsecommentsNum());
            askQuestionReply.setShareUrl(vshareUrl+SHARE_URL_CONSTANT+"question/"+askQuestionReply.getId());
            return ResultVO2.success(askQuestionReply);
        } else {
            return ResultVO2.result(CodeConstant.ASK_NOT_EXIST);
        }
    }

    public ResultVO2<String> support(Long askId){
        if (askId == null || askId < 1) {
            return ResultVO2.result(CodeConstant.PARAM_ERROR);
        }
        AskQuestionReply  askQuestionReply = iAskQuestionReplyService.fetch(askId);
        if(askQuestionReply != null){
            int newZan;
            if(askQuestionReply.getZanNum()!=null){
                newZan=askQuestionReply.getZanNum()+1;
            }else{
                newZan=1;
            }
            iAskQuestionReplyService.updateSupport(askId,newZan);
            return ResultVO2.success("SUCCESS");
        }else{
            return ResultVO2.result(CodeConstant.ASK_NOT_EXIST);
        }
    }

    /**
     * 问政搜索接口
     * @param title
     * @return
     */
    public List<AskQuestionReply> askSearch(String title, Integer pageNumber, Integer pageSize) {
        List<AskIndexData> askIndexDataList=elasticSearchService.askSearch(title,pageNumber,pageSize);
        List<AskQuestionReply> askQuestionReplyList=new ArrayList<>();
        if(askIndexDataList!=null && askIndexDataList.size()>0){
            for (AskIndexData askIndexData : askIndexDataList) {
                AskQuestionReply askQuestionReply=new AskQuestionReply();
                BeanUtils.copyProperties(askIndexData,askQuestionReply);
                askQuestionReplyList.add(askQuestionReply);
            }
        }
        return askQuestionReplyList;
    }
}
