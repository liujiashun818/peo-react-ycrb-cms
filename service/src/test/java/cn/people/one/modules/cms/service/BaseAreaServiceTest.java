package cn.people.one.modules.cms.service;


import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.sys.model.BaseArea;
import cn.people.one.modules.sys.service.IBaseAreaService;
import lombok.extern.slf4j.Slf4j;


/**
 *  
 * 
 * @ClassName: BaseAreaServiceTest 
 * @Description: TODO(这里用一句话描述这个类的作用) 
 * @author Administrator
 * @date 2019年2月20日 下午2:45:48    
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class BaseAreaServiceTest
{
    @Autowired
    private BaseDao dao;

    @Autowired
    IBaseAreaService service;

    @Test
    public void test()
    {
        
        dao.create(BaseArea.class, false);
        //Daos.createTablesInPackage(dao, "cn.people.one.modules.sys.model", false);
        //Daos.migration(dao, "cn.people.one.modules.sys.model", true, false, false);
        List<BaseArea> list = service.queryAllBaseArea();
        log.info(JSON.toJSONString(list));
        list = service.queryBaseAreaByPid("2");
        log.info(JSON.toJSONString(list));
        list = service.queryBaseAreaByLevel("1");
        log.info(JSON.toJSONString(list));

    }
}
