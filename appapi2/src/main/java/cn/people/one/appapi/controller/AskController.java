package cn.people.one.appapi.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.people.one.modules.search.model.AskIndexData;
import org.apache.commons.lang.StringUtils;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.people.one.appapi.constant.CodeConstant;
import cn.people.one.appapi.service.AskService;
import cn.people.one.appapi.vo.ResultVO2;
import cn.people.one.appapi.vo.ResultVO3;
import cn.people.one.modules.ask.model.AskDomainResp;
import cn.people.one.modules.ask.model.AskGovernmentType;
import cn.people.one.modules.ask.model.AskQuestionReply;
import cn.people.one.modules.ask.service.IAskDomainService;
import cn.people.one.modules.ask.service.IAskGovernmentService;
import cn.people.one.modules.ask.service.IAskQuestionReplyService;
import cn.people.one.modules.ask.service.IAskTypeService;
import cn.people.one.modules.search.service.IElasticSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(value = "问政接口", tags = {"问政手机端相关接口"})
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/asks")
public class AskController {

    @Autowired
    private IElasticSearchService elasticSearchService;

    @Autowired
    private AskService askService;

    @Autowired
    IAskQuestionReplyService askQuestionReplyService;

    @Autowired
    IAskDomainService askDomainService;

    @Autowired
    IAskTypeService askTypeService;

    @Autowired
    IAskGovernmentService AskGovernmentService;

    /**
     * 主页列表
     * @param pageSize
     * @param pageNo
     * @return
     */
    @ApiOperation("问政列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageSize", value = "页大小", required =true, defaultValue = "20",paramType = "query"),
            @ApiImplicitParam(name = "pageNo", value = "当前页",required =true,defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "domainId",value = "领域id(支持按领域查询)",paramType = "query"),
            @ApiImplicitParam(name = "typeId",value = "分类id(支持按分类查询)",paramType = "query"),
            @ApiImplicitParam(name = "title",value = "标题(支持问政搜索)",paramType = "query"),
            @ApiImplicitParam(name = "categoryId",value = "咨询栏目id(支持头图)",paramType = "query")
    })
    @GetMapping("/list")
    public ResultVO3<Map> list(@RequestParam(value = "pageSize",required = false) Integer pageSize, @RequestParam("pageNo") Integer pageNo,
                               @RequestParam(value = "domainId",required = false) Integer domainId,@RequestParam(value = "typeId",required = false) Integer typeId,
                               @RequestParam(value = "title",required = false) String title,@RequestParam(value = "categoryId",required = false) Long categoryId) {
        if (pageSize == null || pageSize < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        int page;
        try {
            page = Integer.valueOf(pageNo);
        } catch (Exception e) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        if (page < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        return askService.list(pageSize, page,domainId,typeId,title,categoryId);
    }

    /**
     * 保存提问问题
     *
     * @param askQuestionReply
     * @return
     */
    @ApiOperation("保存提问问题")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "devicetype", value = "设备类型 1：ios 2：android 3：wp", required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "governmentId", value = "被问机构ID", required = true,dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "title", value = "留言标题", required = true,paramType = "query"),
            @ApiImplicitParam(name = "questionContent", value = "留言正文", required = true,paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, paramType = "query"),
            @ApiImplicitParam(name = "domainId", value = "领域ID", required = true, dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "typeId", value = "分类ID(对应Ask_Type表中的id)", required = true, dataType = "int",paramType = "query"),
            @ApiImplicitParam(name = "attachment", value = "附件", required = false,paramType = "query"),
            @ApiImplicitParam(name = "userPhone", value = "留言时输入的联系电话", required = false,paramType = "query"),
            @ApiImplicitParam(name = "realUserName", value = "用户留言时候的真实姓名", required = false,paramType = "query"),
            @ApiImplicitParam(name = "userName", value = "用户昵称", required = false,paramType = "query"),
            @ApiImplicitParam(name = "isUnknownUser", value = "是否匿名 (1 匿名 0 非匿名)", required = false,paramType = "query"),
            @ApiImplicitParam(name = "publishStatus", value = "发布状态", required = false,paramType = "query",dataType = "int"),
            @ApiImplicitParam(name = "isChecked", value = "是否推送：0未推送、1已推送，默认为0", required = false,paramType = "query",dataType = "boolean"),
            @ApiImplicitParam(name = "userPhoto", value = "用户头像", required = false,paramType = "query"),
            @ApiImplicitParam(name = "pushid", value = "推送通知ID", required = false,paramType = "query"),
    })
    @PostMapping(value = "/saveQuestion")
    public ResultVO2<Map> saveQuestion(@RequestBody AskQuestionReply askQuestionReply) {
        try {
            log.info("askQuestionReply========:" + askQuestionReply);
            askQuestionReplyService.setValue(askQuestionReply);
            AskQuestionReply result = (AskQuestionReply) askQuestionReplyService.save(askQuestionReply);
            if (null != result) {
                Map map = new HashMap();
                map.put("question_code",result.getReturnCode());
                return ResultVO2.success(map);
            } else {
                log.error("保存提问问题出错====》/api/askAndReplay/saveQuestion");
                return ResultVO2.result(CodeConstant.ERROR);
            }
        } catch (Exception e) {
            log.error("保存提问问题出错====》/api/askAndReplay/saveQuestion", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

    /**
     * 问政详情
     * @param askId
     * @return
     */
    @ApiOperation("问政详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "askId", value = "askId", required = true, paramType = "path")
    })
    @GetMapping("/detail/{askId}")
    public ResultVO2<AskQuestionReply> get(@PathVariable("askId") Long askId) {
        return askService.detail(askId);
    }

    /**
     * 问政详情 按标题查询，只返回第一条
     * @param title
     * @return
     */
    @ApiOperation("问政详情 按标题查询，只返回第一条")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", required = true, paramType = "query")
    })
    @GetMapping("/detailByTitle")
    public ResultVO2<AskQuestionReply> detailByTitle(@RequestParam String title) {
        return askService.detail(title);
    }

    /**
     *
     * @param askId
     * @return
     */
    @ApiOperation("问政点赞")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "askId",value = "askId",required = true,paramType = "path")
    })
    @GetMapping("/support/{askId}")
    public  ResultVO2<String>  support(@PathVariable("askId") Long askId){
        return askService.support(askId);
    }

    /**
     * 根据区域选择联动
     *
     * @param gid
     * @return
     */
    @ApiOperation("根据区域选择联动")
    @ApiImplicitParams({@ApiImplicitParam(name = "gid", value = "省级地区id(空或0获取全部省级地区)", required = true)})
    @PostMapping(value = "/getLinkage")
    public ResultVO2<List<AskGovernmentType>> getLinkage(@RequestParam Long gid) {
        try {
            log.info("gid========:" + gid);
            AskGovernmentService.getAllArea(gid);
            return ResultVO2.success(AskGovernmentService.getAllArea(gid));
        } catch (Exception e) {
            log.error("根据区域选择联动出错====》/api/askAndReplay/getLinkage", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }

    /**
     * 提交问题前选择领域
     *
     * @return
     */
    @ApiOperation("提交问题前选择领域")
    @GetMapping(value = "/selectArea")
    public ResultVO2<Map> selectArea() {
        try {
            List<AskDomainResp> askDomain = askDomainService.getAllDomainsIdAndName();
            log.info("askDomain========:" + askDomain);
            List<AskDomainResp> askType = askTypeService.getAllDomainsIdAndName();
            log.info("askType========:" + askType);
            //组装返回报文
            Map data = new HashMap();
            data.put("domain", askDomain);
            data.put("type", askType);
            Map result = new HashMap();
            result.put("data", data);
            return ResultVO2.success(result);
        } catch (Exception e) {
            log.error("提交问题前选择领域====》/api/askAndReplay/selectArea", e);
            return ResultVO2.result(CodeConstant.ERROR);
        }
    }
    
    @ApiOperation("获取我的提问列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user_id", value = "user_id", required = true, paramType = "path"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", defaultValue = "20", paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "页数", defaultValue = "1",paramType = "query")
    })
    @GetMapping("/{user_id}/myAskList")
    public ResultVO3<List<Map>> myAskList(@PathVariable("user_id") String user_id,
                              @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
                              @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber){
        if (StringUtils.isBlank(user_id)) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        if (pageSize == null || pageSize < 1) {
            return ResultVO3.result(CodeConstant.PARAM_ERROR);
        }
        Map result = askQuestionReplyService.myAskList(user_id,pageSize,pageNumber);
        //return ResultVO2.success(result.get("data"), null, Long.valueOf(result.get("recordCount")+""), pageSize, pageNumber);
        return ResultVO3.success((List<Map>)result.get("data"), Long.valueOf(result.get("recordCount")+""), pageSize, pageNumber);
    }


    @ApiOperation("问政标题输入联想，关键字建议3字以上")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyWord", value = "关键字", paramType = "query")
    })
    @GetMapping("/suggest")
    public ResultVO3<List<String>> suggest(@RequestParam(value = "keyWord",required = false) String keyWord) {
        //参数处理
        if(Lang.isEmpty(keyWord)){
            keyWord = "";
        }

        if("".equalsIgnoreCase(keyWord)){
            return ResultVO3.error("关键字不能为空");
        }

        try {
            return ResultVO3.success(elasticSearchService.askSuggest(keyWord));
        } catch (Exception e) {
            log.error("问政标题输入联想出错===》searchArticle", e);
            return ResultVO3.result(CodeConstant.ERROR);
        }
    }


    @ApiOperation("判断是否重复提问")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "问题", paramType = "query")
    })
    @GetMapping("/isRepeatAsk")
    public ResultVO2<AskQuestionReply> isRepeatAsk(@RequestParam String title){
        try {
            AskQuestionReply askQuestionReply=askQuestionReplyService.isRepeatAsk(title);
            return  ResultVO2.success(askQuestionReply);
        } catch (Exception e) {
            log.error("判断是否重复提问出错====/api/asks/isRepeatAsk",e);
            return ResultVO2.error("判断重复失败");
        }

    }

    @ApiOperation("问政搜索")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "title", value = "标题", paramType = "query"),
            @ApiImplicitParam(name = "pageNumber", value = "当前页",defaultValue = "1",paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示条数",defaultValue = "5",paramType = "query"),
    })
    @GetMapping("/askSearch")
    public ResultVO2<List<AskQuestionReply>> askSearch(@RequestParam(required = false) String title,
                                                   @RequestParam(required = false,defaultValue = "1") Integer pageNumber,
                                                   @RequestParam(required = false,defaultValue = "5") Integer pageSize ){
        try {
            List<AskQuestionReply> askQuestionReplyList=askService.askSearch(title,pageNumber,pageSize);
            return ResultVO2.success(askQuestionReplyList);
        } catch (Exception e) {
            log.error("问政搜索出错",e);
            return ResultVO2.error("问政搜索失败");
        }
    }
}
