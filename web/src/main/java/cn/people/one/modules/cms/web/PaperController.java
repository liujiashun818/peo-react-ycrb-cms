package cn.people.one.modules.cms.web;

import cn.people.one.core.base.api.Result;
import cn.people.one.modules.newspaper.model.NewspaperXml;
import cn.people.one.modules.newspaper.model.NewspaperXmlVO;
import cn.people.one.modules.newspaper.service.INewspaperXmlService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.QueryResult;
import org.nutz.lang.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 报纸接口
 * xyl
 */
@Api(description = "报纸接口")
@RestController
@RequestMapping("/api/cms/paper")
@Slf4j
public class PaperController {
    @Autowired
    private INewspaperXmlService iNewspaperXmlService;

    /**
     * 报纸列表
     * @param NewspaperXmlVO
     * @return
     */
    @RequestMapping(value = "searchPage", method = RequestMethod.POST)
    public Result list(@RequestBody NewspaperXmlVO NewspaperXmlVO) {
        try {
            QueryResult queryResult=iNewspaperXmlService.list(NewspaperXmlVO);
            return Result.success(queryResult);
        } catch (Exception e) {
            return Result.error("查询失败");
        }
    }

    /**
     * 报纸详情
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.GET)
    public Result get(@PathVariable Long id){
        if(id==null){
            return Result.error("参数异常");
        }
        NewspaperXml newspaperXml=iNewspaperXmlService.getPaperById(id);
        if(newspaperXml==null){
            return Result.error("获取的报纸为空");
        }
        return Result.success(newspaperXml);
    }

    /**
     * 更新报纸
     * @param newspaperXml
     * @return
     */
    @RequestMapping(value = "/update",method = RequestMethod.POST)
    public Result update(@RequestBody NewspaperXml newspaperXml){
        try {
            if (Lang.isEmpty(iNewspaperXmlService.updateNew(newspaperXml))) {
                return Result.error("更新失败");
            }
        }catch (Exception e){
            return Result.error("更新报纸失败");
        }
        return Result.success();
    }

    /**
     * 删除报纸
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}",method = RequestMethod.DELETE)
    public Result delete(@PathVariable Long id){
        if(iNewspaperXmlService.del(id).getDelFlag().equals(NewspaperXml.STATUS_DELETE)){
            return Result.success();
        }
        return Result.error("删除失败");
    }

    /**
     * 报纸上线
     * @param id
     * @return
     */
    @RequestMapping(value = "/online/{id}",method = RequestMethod.GET)
    public Result onLine(@PathVariable Long id){
        iNewspaperXmlService.onLine(id);
        return Result.success();
    }

    /**
     * 报纸下线
     * @param id
     * @return
     */
    @RequestMapping(value = "/downline/{id}",method =RequestMethod.GET)
    public Result downLine(@PathVariable Long id){
        iNewspaperXmlService.downLine(id);
        return  Result.success();
    }
}
