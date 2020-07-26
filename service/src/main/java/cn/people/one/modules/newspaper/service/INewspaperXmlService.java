package cn.people.one.modules.newspaper.service;

import cn.people.one.modules.base.service.IBaseService;
import cn.people.one.modules.newspaper.model.Attachment;
import cn.people.one.modules.newspaper.model.NewspaperXml;
import cn.people.one.modules.newspaper.model.NewspaperXmlVO;
import cn.people.one.modules.sys.model.Dict;
import io.swagger.models.auth.In;
import org.nutz.dao.QueryResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TreeMap;

/**
* 直播嘉宾/主持人Service
* @author cheng
*/
public interface INewspaperXmlService extends IBaseService<NewspaperXml>{

     // 字典表type
     static final String DIC_TYPE_NEWSPAPER_CODE = "newspaper_code";

     static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
    /**
     * 获取指定日期的最新文件夹path
     * @param dir 各报纸存放地址
     * @param paperCode 报纸编码
     * @param nsdate 指定日期
     * @return
     */
    String getDatePath(String dir,String paperCode,String nsdate);

    /**
     * 更新当天报纸，读取xml，删除就数据并重新插入
     * @param paperCode 报纸编码
     * @param paperName 报纸名称
     * @param nsdate 指定日期 yyyyMMdd
     * @param filePath 报纸最新文件地址（路径）
     * @return
     */
    TreeMap<String,List<NewspaperXml>> renewPaper(String paperCode, String paperName, String nsdate, String filePath)  throws ParseException;

    /**
     * 格式化pageNum便于排序
     * @param pageNum
     * @return
     */
    String formatPageNum(String pageNum);

    /**
     * 获取当天报纸
     * @param paperCode 报纸编码
     * @param nsdate 指定日期 yyyyMMdd
     * @return
     */
    List<NewspaperXml> getPapers(String paperCode, String nsdate);

    /**
     * 获取报纸中的一篇文章
     * @param paperCode  报纸编码
     * @param docCode    文章编码 201905060001
     * @return
     */
    NewspaperXml getPaperDetail(String paperCode, String docCode);

    /**
     * 向CMS 同步数据
     * @param dict      系统配置
     * @param nsdate    日期 yyyyMMdd
     * @param cmsUrl    url
     * @param treeMap   稿件数据
     */
    void doSend2Cms(Dict dict, String nsdate, String cmsUrl, TreeMap<String,List<NewspaperXml>> treeMap);


    /**
     * 准备jspn参数
     * @param picurl    图片名 分号分隔 正文用
     * @param pictext   图片说明 分号分隔 正文用
     * @param imgUrl    图片path
     * @return
     */
    List<Attachment> formatAttachment(String picurl, String pictext, String imgUrl);

    /**
     * 报纸列表
     * @param newspaperXmlVO
     * @return
     */
    QueryResult list(NewspaperXmlVO newspaperXmlVO);

    /**
     * 删除报纸
     * @param id
     * @return
     */
    NewspaperXml del(Long id);

    /**
     * 报纸详情
     * @param id
     * @return
     */
    NewspaperXml getPaperById(Long id);

    /**
     * 更新报纸
     * @param newspaperXml
     * @return
     */
    NewspaperXml updateNew(NewspaperXml newspaperXml);

    /**
     * 报纸上线
     * @param id
     * @return
     */
    NewspaperXml onLine(Long id);

    /**
     * 报纸下线
     * @param id
     * @return
     */
    NewspaperXml downLine(Long id);

    NewspaperXml getPaperDetailWeb(String paperCode, String docCode);

    List<String> getDateList(String paperName, String pjCode);
}