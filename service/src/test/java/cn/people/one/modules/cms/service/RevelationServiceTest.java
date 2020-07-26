/**   
* @Title: RevelationServiceTest.java 
* @Package cn.people.one.modules.cms.service 
* @Description: TODO(用一句话描述该文件做什么) 
* @author Administrator
* @date 2019年2月11日 下午2:45:48 
* @version V1.0   
*/
package cn.people.one.modules.cms.service;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.cms.model.Revelations;
import cn.people.one.modules.cms.model.front.RevelationsVO;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.dao.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

/** 
* @ClassName: RevelationServiceTest 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author Administrator
* @date 2019年2月17日 下午2:45:48 
*  
*/
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class RevelationServiceTest
{
    @Autowired
    private BaseDao dao;
    @Autowired
    IRevelationsService service;
    
    @Test
    public void test() {
        //Daos.createTablesInPackage(dao, "cn.people.one.modules.cms.model", false);
        Revelations revelation=new Revelations();
        revelation.setUserId("123");
        
        revelation.setTitle("新闻标题");
        revelation.setContent("新闻内容");
        revelation.setIp("223.202.116.5");
        revelation.setContactInfo("13800001111");
        revelation.setRemarks("223.202.116.5(IpInfo [country=中国, province=北京, city=北京, county=])");
        revelation.setMedias("http://img.sxrbw.com/data/appuploadfile/1467357091filename.jpg,http://img.sxrbw.com/data/appuploadfile/1467357095filename.jpg");
        revelation.setName("张三");
       Object o= service.save(revelation);
       log.info(JSON.toJSONString(o));
        RevelationsVO v=new RevelationsVO();
        v.setTitle("新闻标题");
        v.setPageNumber(1);
        v.setPageSize(30);
        QueryResult result = service.findListByName(v.getPageNumber(),v.getPageSize(),revelation.toString());
        log.info(JSON.toJSONString(result));
        Assert.assertEquals(result.getList().size()>0, true);
        log.info(JSON.toJSONString(result.getList().get(0)));
        revelation= (Revelations)result.getList().get(0);
        Assert.assertEquals(revelation.getTitle(),"新闻标题");
        service.delete(revelation.getId());
//        result = service.find(v);
        log.info(JSON.toJSONString(result));
        Assert.assertEquals(result.getList().size(), 0);
        
    }
}
