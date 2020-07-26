package cn.people.one.modules.app.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.front.ArticleMediaVO;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ICategoryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * User: 张新征
 * Date: 2017/5/2 17:24
 * Description:
 */
@Service
@Slf4j
public class InitData {

	@Autowired
	private BaseDao dao;
	@Autowired
	private IArticleService articleService;

	@Autowired
	private ICategoryService categoryService;

	public void initData(Long categoryId){
		imageBanner(categoryId);
		noPicture(categoryId);
		audio(categoryId);
		imageNormal(categoryId);
		audioBanner(categoryId);
		video(categoryId);
		videoBanner(categoryId);
	}

	public void clearData(Long categoryId){
		Condition condition = Cnd.where("authors", "=", "robot").and("category_id", "=", categoryId);
		List<Article> list = dao.query(Article.class, condition);
		if(null != list && list.size() > 0){
			for (Article article : list){
				if(null != article.getArticleData()){
					dao.delete(article.getArticleData());
				}
			}
		}
		dao.clear(Article.class, condition);
	}

	//图文通栏
	private void imageBanner(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("image");
		article.setViewType("banner");
		article.setTitle("图文通栏");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>图文通栏图文通栏图文通栏图文通栏图文通栏图文通栏图文通栏图文通栏图文通栏</p>");
		List<ArticleMediaVO> imageJson = Lists.newArrayList();
		ArticleMediaVO media0 = new ArticleMediaVO();
		media0.setIndex("0");
		media0.setImage("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		media0.setDescription("图文通栏0");
		imageJson.add(media0);
		ArticleMediaVO media1 = new ArticleMediaVO();
		media1.setIndex("1");
		media1.setImage("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/1657ea2f-408a-4b7a-aa9c-1b8b28f73278.jpg");
		media1.setDescription("图文通栏1");
		imageJson.add(media1);
		articleData.setImageJson(imageJson);
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//无图新闻
	private void noPicture(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("common");
		article.setViewType("normal");
		article.setTitle("无图新闻");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>无图新闻无图新闻无图新闻无图新闻无图新闻无图新闻无图新闻无图新闻</p>");
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//图文
	private void imageNormal(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("image");
		article.setViewType("normal");
		article.setTitle("图文");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>图文图文图文图文图文图文图文图文图文图文</p>");
		List<ArticleMediaVO> imageJson = Lists.newArrayList();
		ArticleMediaVO media0 = new ArticleMediaVO();
		media0.setIndex("0");
		media0.setImage("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		media0.setDescription("图文0");
		imageJson.add(media0);
		ArticleMediaVO media1 = new ArticleMediaVO();
		media1.setIndex("1");
		media1.setImage("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/1657ea2f-408a-4b7a-aa9c-1b8b28f73278.jpg");
		media1.setDescription("图文1");
		imageJson.add(media1);
		articleData.setImageJson(imageJson);
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//图文音频
	private void audio(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("audio");
		article.setViewType("normal");
		article.setTitle("图文音频");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>图文音频图文音频图文音频图文音频图文音频图文音频图文音频</p>");
		articleData.setAudios("[{\"id\":1,\"resources\":[{\"enctype\":\"64\",\"size\":2283376,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/mp3-64/32db4a6eee174fd18c44bdf07b79f5b2/61193e43-a23d-4bce-80ea-11c0df41df52.mp3\"},{\"enctype\":\"128\",\"size\":4566269,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/mp3-128/32db4a6eee174fd18c44bdf07b79f5b2/61193e43-a23d-4bce-80ea-11c0df41df52.mp3\"}],\"times\":285,\"title\":\"五月天 - 星空.mp3\",\"type\":\"audio\"}]");
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//通栏音频
	private void audioBanner(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("audio");
		article.setViewType("banner");
		article.setTitle("通栏音频");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>通栏音频通栏音频通栏音频通栏音频通栏音频通栏音频通栏音频</p>");
		articleData.setAudios("[{\"id\":1,\"resources\":[{\"enctype\":\"64\",\"size\":2283376,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/mp3-64/32db4a6eee174fd18c44bdf07b79f5b2/61193e43-a23d-4bce-80ea-11c0df41df52.mp3\"},{\"enctype\":\"128\",\"size\":4566269,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/mp3-128/32db4a6eee174fd18c44bdf07b79f5b2/61193e43-a23d-4bce-80ea-11c0df41df52.mp3\"}],\"times\":285,\"title\":\"五月天 - 星空.mp3\",\"type\":\"audio\"}]");
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//图文视频
	private void video(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("video");
		article.setViewType("normal");
		article.setTitle("图文视频");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>图文视频图文视频图文视频图文视频图文视频图文视频图文视频图文视频</p>");
		articleData.setVideos("[{\"id\":21,\"image\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-Snapshot/c6f932aa3cb4462c84a649bacb889811/1000.jpg\",\"resources\":[{\"enctype\":\"ld\",\"size\":5600126,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-ld/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"},{\"enctype\":\"sd\",\"size\":10281071,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-sd/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"},{\"enctype\":\"hd\",\"size\":21955051,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-hd/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"}],\"times\":100,\"title\":\"030008010058AD33C122CD38C66A99B01A54B5-563A-EC86-60F9-3B5BFD9C504F.mp4\",\"type\":\"video\"}]");
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

	//通栏视频
	private void videoBanner(Long categoryId){
		Article article = new Article();
		article.setCategoryId(categoryId);
		article.setSysCode("article");
		article.setBlock(2);
		article.setType("video");
		article.setViewType("banner");
		article.setTitle("通栏视频");
		article.setSource("测试数据");
		article.setAuthors("robot");
		article.setTags("独家");
		article.setPublishDate(new Date());
		article.setInSubject(false);
		article.setImageUrl("http://rmrbpre.img-cn-beijing.aliyuncs.com/test/dir2/20170502/974a94e8-32eb-43c1-8f49-fa8991125fd4.jpg");
		ArticleData articleData = new ArticleData();
		articleData.setContent("<p>通栏视频通栏视频通栏视频通栏视频通栏视频通栏视频通栏视频通栏视频</p>");
		articleData.setVideos("[{\"id\":21,\"image\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-Snapshot/c6f932aa3cb4462c84a649bacb889811/1000.jpg\",\"resources\":[{\"enctype\":\"ld\",\"size\":5600126,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-ld/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"},{\"enctype\":\"sd\",\"size\":10281071,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-sd/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"},{\"enctype\":\"hd\",\"size\":21955051,\"url\":\"http://medias-product.oss-cn-beijing.aliyuncs.com/Act-ss-mp4-hd/c6f932aa3cb4462c84a649bacb889811/e441fc7e-c5ed-4c54-8215-0da837cd31ec.mp4\"}],\"times\":100,\"title\":\"030008010058AD33C122CD38C66A99B01A54B5-563A-EC86-60F9-3B5BFD9C504F.mp4\",\"type\":\"video\"}]");
		article.setArticleData(articleData);
		article.setDelFlag(0);
		articleService.save(article);
	}

}
