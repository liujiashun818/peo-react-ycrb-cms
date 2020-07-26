package cn.people.one.modules.task;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.time.DateHelper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.ArticleTaskRecord;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.IArticleTaskRecordService;
import cn.people.one.modules.live.service.ILiveRoomService;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 文章定时发布定时器
 */
@Slf4j
@Component
public class FixedPublishTask {
    @Autowired
    private BaseDao dao;

    @Scheduled(fixedRate = 300000)
    public void autoPublish() {
        List<ArticleTaskRecord> list=dao.query(ArticleTaskRecord.class, Cnd.where("del_flag","=",0));
        if(list!=null && list.size()>0){
            for(int i=0;i<list.size();i++){
                //固定时间处理成时间戳
                String fixedPublishStr=DateHelper.formatDate(list.get(i).getFixedPublishDate(),DateHelper.DEFAULT_TIME_FORMAT);
                long fixedPublishStamp=DateHelper.getLongByString(fixedPublishStr,DateHelper.DEFAULT_TIME_FORMAT);
                Date date=new Date();
                //系统时间处理成时间戳
                String nowStr=DateHelper.formatDate(date,DateHelper.DEFAULT_TIME_FORMAT);
                long nowStamp=DateHelper.getLongByString(nowStr,DateHelper.DEFAULT_TIME_FORMAT);

                if(fixedPublishStamp<=nowStamp){
                    long id=list.get(i).getId();
                    Article article=dao.fetch(Article.class,id);
                    //到时间把文章设置为上线状态，把发布时间设置为之前设置的定时发布时间，并把定时时间值为空
                    if(article!=null){
                        log.info("articleId："+id+" Start publish artcile...");
                        article.setDelFlag(Article.STATUS_ONLINE);
                        article.setPublishDate(list.get(i).getFixedPublishDate());
                        article.setFixedPublishDate(null);
                        dao.update(article);
                    }
                    //同时把cms_article_task_record的del_flag设置为3，即逻辑删除
                    ArticleTaskRecord articleTaskRecord=dao.fetch(ArticleTaskRecord.class,id);
                    articleTaskRecord.setDelFlag(ArticleTaskRecord.STATUS_DELETE);
                    dao.update(articleTaskRecord);
                }
            }

        }
    }

}
