package cn.people.one.setup;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.core.util.time.DateFormatUtil;
import cn.people.one.core.util.time.DateUtil;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.CategoryModel;
import cn.people.one.modules.sys.model.Dict;
import cn.people.one.modules.sys.model.Log;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.user.model.Office;
import cn.people.one.modules.user.model.Role;
import cn.people.one.modules.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.nutz.dao.Sqls;
import org.nutz.dao.TableName;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.reflections.Reflections;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

/**
 * 建表
 */
@Component
@Slf4j
public class MainSetup implements InitializingBean {

	@Autowired
	@Qualifier("baseDao")
	private BaseDao dao;

	@Autowired
	private ResourceLoader resourceLoader;

	@Override
	public void afterPropertiesSet() throws Exception {
		Reflections reflections = new Reflections("cn.people.one.modules");
		Set<Class<?>> singletons = reflections.getTypesAnnotatedWith(Table.class);
		for(Class<?> clazz : singletons){
			if(null == clazz){
				continue;
			}
			if(clazz.equals(Log.class)){
				Date secondYear = DateUtil.nextYear(new Date());
				Date thirdYear = DateUtil.nextYear(secondYear);
				TableName.run(DateFormatUtil.formatDate("yyyy", new Date()), () ->dao.create(clazz, false));
				TableName.run(DateFormatUtil.formatDate("yyyy", secondYear), () ->dao.create(clazz, false));
				TableName.run(DateFormatUtil.formatDate("yyyy", thirdYear), () ->dao.create(clazz, false));
			}else {
				dao.create(clazz, false);
			}
			Daos.migration(dao,clazz,true,false,false);
		}
		if(dao.count(ClientMenu.class) == 0){
//			execute("classpath:db/client_menu.sql");
		}
		if(dao.count(Category.class) == 0){
//			execute("classpath:db/cms_category.sql");
		}
		if(dao.count(Dict.class) == 0){
//			execute("classpath:db/sys_dict.sql");
		}
		if(dao.count(Menu.class) == 0){
//			execute("classpath:db/sys_menu.sql");
		}
		if(dao.count(User.class) == 0 && dao.count(Role.class) == 0 && dao.count(Office.class) == 0) {
//			execute("classpath:db/user_role_office.sql");
		}
		if(dao.count(CategoryModel.class) == 0){
//			execute("classpath:db/cms_category_model.sql");
		}
	}

	private void execute(String path){
		InputStream in = null;
		try {
			in = resourceLoader.getResource(path).getInputStream();
			StringWriter writer = new StringWriter();
			IOUtils.copy(in, writer, "UTF-8");
			String sqlStr = writer.toString();
			Arrays.stream(sqlStr.split(";")).filter(s -> StringUtils.isNotBlank(s)).forEach(s -> {
				Sql sql = Sqls.create(s);
				dao.execute(sql);
			});
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}finally {
			try {
				if(null != in){
					in.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}
}
