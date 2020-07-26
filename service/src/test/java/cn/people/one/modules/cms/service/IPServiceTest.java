/**
 *   
 * 
 * @Title: RevelationServiceTest.java 
 * @Package cn.people.one.modules.cms.service 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Administrator
 * @date 2019年2月11日 下午2:45:48 
 * @version V1.0   
 */
package cn.people.one.modules.cms.service;


import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.http.IpAreaInfo;
import cn.people.one.modules.sys.service.IPService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;


/**
 *  
 * 
 * @ClassName: BaseAreaServiceTest 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2019年2月18日 下午2:45:48    
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class IPServiceTest
{
    @Autowired
    private BaseDao dao;

    @Autowired
    IPService service;

    @Test
    public void test()
    {
        
        IpAreaInfo ipinfo= service.findIpArea("124.127.104.130");
        log.info(JSON.toJSONString(ipinfo));
        ipinfo= service.findIpArea("202.118.30.75");
        log.info(JSON.toJSONString(ipinfo));

    }
}
