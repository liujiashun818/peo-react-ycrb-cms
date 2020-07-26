package cn.people.one.modules.client.service;

import cn.people.one.modules.client.constant.EClientMenuPosition;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.model.front.ClientMenuVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by cheng on 2017/3/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j
public class ClientMenuServiceTest {
	@Autowired
	private IClientMenuService clientMenuService;

	@Test
	public void save(){
		//2
		ClientMenu recommend = new ClientMenu();
		recommend.setParentId(1L);
		recommend.setPosition(EClientMenuPosition.FIX.value());
		recommend.setName("推荐栏目");
		recommend.setSimpleName("推荐栏目");
		recommend.setSlug("recommend");
		clientMenuService.save(recommend);

		//3
		ClientMenu news = new ClientMenu();
		news.setParentId(2L);
		news.setCategoryId(3L);
		news.setLinks("{\"head\":\"thrift://10.100.10.86:8180/cms/article/lists?TArticleRequest.categoryId=3&TArticleRequest.block=1&TArticleRequest.pageSize=4\",\"list\":\"thrift://10.100.10.86:8180/cms/article/lists?TArticleRequest.categoryId=3&TArticleRequest.block=2&TArticleRequest.pageSize=200\"}");
		news.setPosition(EClientMenuPosition.FIX.value());
		news.setName("热点");
		news.setSimpleName("热点");
		news.setSlug("news");
		clientMenuService.save(news);

		//4
		ClientMenu comment = new ClientMenu();
		comment.setParentId(2L);
		comment.setPosition(EClientMenuPosition.ALWAYS_TOP.value());
		comment.setName("时评");
		comment.setSimpleName("时评");
		comment.setSlug("comment");
		clientMenuService.save(comment);

		//5
		ClientMenu clazz = new ClientMenu();
		clazz.setParentId(1L);
		clazz.setName("分类");
		clazz.setSimpleName("分类");
		clazz.setSlug("class");
		clientMenuService.save(clazz);

		//6
		ClientMenu society = new ClientMenu();
		society.setParentId(5L);
		society.setName("社会");
		society.setSimpleName("社会");
		society.setSlug("class");
		clientMenuService.save(society);

		//7
		ClientMenu finance = new ClientMenu();
		finance.setParentId(5L);
		finance.setPosition(EClientMenuPosition.ONE_TOP.value());
		finance.setName("财经");
		finance.setSimpleName("财经");
		finance.setSlug("finance");
		clientMenuService.save(finance);

		//8
		ClientMenu education = new ClientMenu();
		education.setParentId(5L);
		education.setName("教育");
		education.setSimpleName("教育");
		education.setSlug("education");
		clientMenuService.save(education);

		//9
		ClientMenu local = new ClientMenu();
		local.setParentId(1L);
		local.setName("本地");
		local.setSimpleName("本地");
		local.setSlug("local");
		clientMenuService.save(local);

		//10
		ClientMenu beijing = new ClientMenu();
		beijing.setParentId(8L);
		beijing.setName("北京");
		beijing.setSimpleName("京");
		beijing.setSlug("beijing");
		clientMenuService.save(beijing);

		//11
		ClientMenu shanghai = new ClientMenu();
		shanghai.setParentId(8L);
		shanghai.setName("上海");
		shanghai.setSimpleName("沪");
		shanghai.setSlug("shanghai");
		clientMenuService.save(shanghai);
	}

	/**
	 * 测试菜单树
	 * @throws Exception
	 */
	@Test
	public void getMenuTree() throws Exception {
		List<ClientMenuVO> list = clientMenuService.getTree(1L);
		log.info("getMenuTree ",list.size());
	}

}