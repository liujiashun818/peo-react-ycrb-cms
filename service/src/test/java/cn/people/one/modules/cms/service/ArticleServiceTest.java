package cn.people.one.modules.cms.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nutz.json.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.mockdata.JMockDataManager;
import com.sohu.idcenter.IdWorker;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleData;
import cn.people.one.modules.cms.model.ArticleMeta;
import cn.people.one.modules.cms.model.Vote;
import cn.people.one.modules.cms.model.VoteOption;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.model.type.SysCodeType;
import cn.people.one.modules.cms.vo.ArticleTestVO;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lml on 2016/12/26.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Slf4j()
public class ArticleServiceTest {
    @Autowired
    IArticleService articleService;

    @Autowired
    private BaseDao dao;

    private ArticleTestVO mockData;

    private static IdWorker idWorker = new IdWorker();

    @Before
    public void initMockConfig(){
        JMockDataManager.getInstance().config("mock.properties");
    }


    @Test
    public void getDetailTest(){
        Article article = articleService.getArticleDetails(1L);
        log.info("getDetailTest ",article);
    }

    @Test
    public void deleteTest(){
        articleService.vDelete(1L);
    }

    @Test
    public void insertTest(){
        Article article = new Article();
        mockData = JMockData.mock(ArticleTestVO.class);
        article.setTitle(mockData.getTitle());
        article.setAuthors(mockData.getAuthors());
        article.setDescription(mockData.getDescription());
        article.setType("quote");
        article.setViewType("viewType");
        article.setListTitle("listTitle");
        article.setSysCode("sysCode");
        article.setArticleId(1L);
        ArticleData articleData = new ArticleData();
        articleData.setContent(Json.toJson(mockData));
        article.setArticleData(articleData);
        article.setCategoryId(1L);
        articleService.save(article);
    }

    /**
     * id自增测试
     */
    @Test
    public void idWorkerTest(){
        final long idepo = System.currentTimeMillis() - 3600 * 1000L;
        IdWorker iw = new IdWorker(1, 1, 0, idepo);
        IdWorker iw2 = new IdWorker(idepo);
        for (int i = 0; i < 10; i++) {
            System.out.println(iw.getId() + " -> " + iw2.getId());
        }
        System.out.println(iw);
        System.out.println(iw2);
        long nextId = iw.getId();
        System.out.println(nextId);
        long time = iw.getIdTimestamp(nextId);
        System.out.println(time+" -> "+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(time)));
    }

    @Test
    public void idGeneratorTest(){
        for (int i = 0; i < 10; i++) {
            System.out.println(idWorker.getId());
        }
    }

    /**
     * 调查插入
     */
    @Test
    public void voteInsertTest(){
        Article article = new Article();
        List votes = new ArrayList<Vote>();
        //模拟第一个调查
        Vote vote1 = new Vote();
        vote1.setDate(new Date());
        vote1.setTitle("test1");
        vote1.setType(0);
        List options = new ArrayList<VoteOption>();
        VoteOption option1 = new VoteOption();
        option1.setTitle("option1");
        option1.setHits(0);
        VoteOption option2 = new VoteOption();
        option2.setTitle("option2");
        option2.setHits(0);
        options.add(option1);
        options.add(option2);
        vote1.setOptions(options);
        votes.add(vote1);

        //模拟第二个调查
        Vote vote2 = new Vote();
        vote2.setDate(new Date());
        vote2.setTitle("test2");
        vote2.setType(0);
        List options2 = new ArrayList<VoteOption>();
        VoteOption option21 = new VoteOption();
        option21.setTitle("option21");
        option21.setHits(0);
        VoteOption option22 = new VoteOption();
        option22.setTitle("option22");
        option22.setHits(0);
        options2.add(option21);
        options2.add(option22);
        vote2.setOptions(options2);
        votes.add(vote2);

        article.setVotes(votes);
        article.setCategoryId(1L);
        article.setTitle("ceshi");
        article.setSysCode(SysCodeType.ARTICLE.value());
        article.setType(ArticleType.NORMAL.value());
        article.setViewType("normal");
        articleService.save(article);
    }

    /**
     * 更新的情况分三种，分别测试
     */
    @Test
    public void voteUpdateTest(){
        Article article = articleService.getArticleDetails(1L);
        //给调查1增加选项
        Vote vote1 = article.getVotes().get(0);
        VoteOption voteOption14 = new VoteOption();
        voteOption14.setTitle("option14");
        voteOption14.setHits(0);
        vote1.getOptions().add(voteOption14);

        //添加调查
        Vote vote3 = new Vote();
        vote3.setDate(new Date());
        vote3.setTitle("test3");
        vote3.setType(0);
        List options = new ArrayList<VoteOption>();
        VoteOption option1 = new VoteOption();
        option1.setTitle("option31");
        option1.setHits(0);
        VoteOption option2 = new VoteOption();
        option2.setTitle("option32");
        option2.setHits(0);
        options.add(option1);
        options.add(option2);
        vote3.setOptions(options);
        article.getVotes().add(vote3);
        articleService.save(article);
    }

    //调查删除更新
    @Test
    public void voteDeleteUpdateTest(){
        Article article = articleService.getArticleDetails(389L);
        Vote vote = new Vote();
        vote.setOptions(new ArrayList<>());
        VoteOption voteOption = new VoteOption();
        voteOption.setTitle("option");
        voteOption.setHits(0);
        vote.getOptions().add(voteOption);
        List votes = new ArrayList();
        votes.add(vote);
        article.setVotes(votes) ;
        articleService.save(article);
    }

    @Test
    public void getVotesDetailTest(){
        Article article = articleService.getArticleDetails(389L);
        System.out.println(article);
    }

    /**
     * 加入测试数据
     * @throws SQLException
     */
    @Test
    public void batchInsertTest() throws SQLException {
        long categoryId = 54;

        //0.焦点图
        for (int i = 0;i < 4; i++){
            Article article = new Article();
            article.setCategoryId(categoryId);
            article.setSysCode("normal");
            article.setTitle("焦点图新闻");
            article.setListTitle(article.getTitle());
            article.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
            article.setType(ArticleType.NORMAL.value());
            article.setViewType("banner");
            article.setBlock(1);
            article.setComments(12);
            article.setTags("独家");
            //ArticleData articleData = new ArticleData();
            //articleData.setImages("");
            //dao.insertWith(article,"fields|articleData");
            article.init();
            dao.insert(article);
        }

        //1.无图
        Article article1 = new Article();
        article1.setCategoryId(categoryId);
        article1.setSysCode("normal");
        article1.setTitle("无图新闻");
        article1.setListTitle(article1.getTitle());
        article1.setType(ArticleType.NORMAL.value());
        article1.setViewType("normal");
        article1.setBlock(2);
        article1.setComments(13);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article1);

        //2.图文+推广标签
        Article article2 = new Article();
        article2.setCategoryId(categoryId);
        article2.setSysCode("normal");
        article2.setTitle("图文+推广标签");
        article2.setListTitle(article2.getTitle());
        article2.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article2.setType(ArticleType.NORMAL.value());
        article2.setViewType("normal");
        article2.setBlock(2);
        article2.setComments(12);
        article2.setTags("推广");
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article2);

        //3.图文音频
        Article article3 = new Article();
        article3.setCategoryId(categoryId);
        article3.setSysCode("normal");
        article3.setTitle("图文音频");
        article3.setListTitle(article3.getTitle());
        article3.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article3.setType("audio");
        article3.setViewType("normal");
        article3.setBlock(2);
        article3.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article3);

        //4.图文视频
        Article article4 = new Article();
        article4.setCategoryId(categoryId);
        article4.setSysCode("normal");
        article4.setTitle("图文视频");
        article4.setListTitle(article4.getTitle());
        article4.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article4.setType("audio");
        article4.setViewType("normal");
        article4.setBlock(2);
        article4.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article4);

        //5.图文通栏+独家标签
        Article article5 = new Article();
        article5.setCategoryId(categoryId);
        article5.setSysCode("normal");
        article5.setTitle("焦点图新闻");
        article5.setListTitle(article5.getTitle());
        article5.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article5.setType(ArticleType.NORMAL.value());
        article5.setViewType("banner");
        article5.setBlock(2);
        article5.setComments(12);
        article5.setTags("独家");
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article5);

        //6.通栏音频
        Article article6 = new Article();
        article6.setCategoryId(categoryId);
        article6.setSysCode("normal");
        article6.setTitle("通栏音频");
        article6.setListTitle(article6.getTitle());
        article6.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article6.setType("audio");
        article6.setViewType("banner");
        article6.setBlock(2);
        article6.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article6);

        //7.通栏视频
        Article article7 = new Article();
        article7.setCategoryId(categoryId);
        article7.setSysCode("normal");
        article7.setTitle("通栏视频");
        article7.setListTitle(article7.getTitle());
        article7.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article7.setType("audio");
        article7.setViewType("banner");
        article7.setBlock(2);
        article7.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article7);


        //8.通栏幻灯
        Article article8 = new Article();
        article8.setCategoryId(categoryId);
        article8.setSysCode("normal");
        article8.setTitle("通栏幻灯");
        article8.setListTitle(article8.getTitle());
        article8.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4,http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article8.setType("normal");
        article8.setViewType("banner");
        article8.setBlock(2);
        article8.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article8);

        //9.专题
        Article article9 = new Article();
        article9.setCategoryId(categoryId);
        article9.setSysCode("subject");
        article9.setTitle("通栏专题");
        article9.setListTitle(article9.getTitle());
        article9.setImageUrl("http://online-cms-peopleapp-com.cdn.bcebos.com/upload/image/201703/201703080943421052.png@!w4");
        article9.setType("normal");
        article9.setViewType("banner");
        article9.setBlock(2);
        article9.setComments(12);
        //ArticleData articleData = new ArticleData();
        //articleData.setImages("");
        //dao.insertWith(article,"fields|articleData");
        dao.insert(article9);
        //10.直播
    }

    /**
     * 测试文章管理页面的搜索
     */
    @Test
    public void getArticlesTest(){
        ArticleVO articleVO = new ArticleVO();
        articleVO.setCategoryId(0L);
        articleVO.setBlock(1);
        articleVO.setTitle("焦点");
        articleVO.setDelFlag(Article.STATUS_ONLINE);
        articleVO.setEndTime(new Date());
        //QueryResult queryResult =  articleService.findSearchPage(1,1000,articleVO);
        //System.out.println(queryResult.getList());
    }

    @Test
    public void search(){
        JsonMapper jsonMapper = new JsonMapper();
        ArticleVO articleVO = new ArticleVO();
        articleVO.setDesc("hits");
        System.out.println(jsonMapper.toJson(articleVO));
       // QueryResult queryResult = articleService.findSearchPage(1,10,articleVO);
       // System.out.println(queryResult);
    }

    @Test
    public void fetch(){
        Article article = articleService.getArticleDetails(1L);
        log.info(article.toString());
    }

    @Test
    public void saveMeta(){
        Article article = articleService.fetch(2L);
        List<ArticleMeta> metas = new ArrayList<>();
        ArticleMeta articleMeta = new ArticleMeta();
        articleMeta.setFieldCode("code1");
        articleMeta.setFieldValue("value1");
        articleMeta.setDelFlag(3);
        metas.add(articleMeta);
        article.setMetas(metas);
        articleService.save(article);
    }
}
