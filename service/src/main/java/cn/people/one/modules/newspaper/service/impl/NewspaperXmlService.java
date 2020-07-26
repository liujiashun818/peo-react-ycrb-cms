package cn.people.one.modules.newspaper.service.impl;

import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.core.util.http.HttpUtils;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.base.service.impl.BaseService;
import cn.people.one.modules.newspaper.model.Attachment;
import cn.people.one.modules.newspaper.model.NewspaperXml;
import cn.people.one.modules.newspaper.model.NewspaperXmlVO;
import cn.people.one.modules.newspaper.model.Trs;
import cn.people.one.modules.newspaper.service.INewspaperXmlService;
import cn.people.one.modules.newspaper.util.FileUtils;
import cn.people.one.modules.newspaper.util.JaxbMapper;
import cn.people.one.modules.sys.model.Dict;
import cn.people.one.modules.sys.service.IDictService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Criteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
* 直播间的对话Service
* @author cheng
*/
@Transactional(readOnly = true)
@Service
@Slf4j
public class NewspaperXmlService extends BaseService<NewspaperXml> implements INewspaperXmlService {

    @Value("${upload.domain}")
    private String uploadDomain;

    private static final String IMG_SRC="http://epaper.sxrb.com/";

    @Autowired
    private BaseDao dao;

    @Autowired
    public TaskExecutor taskExecutor;

    @Autowired
    private IDictService dictService;

    @Autowired
    private INewspaperXmlService newspaperXmlService;

    @Bean
    private TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(55);
        executor.setKeepAliveSeconds(3600);
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }

    @Override
    public String getDatePath(String dir, String paperCode, String nsdate) {
        File path = new File(dir+paperCode);
        if(!path.exists()) {
            return null;
        }

        String lastPath = "";
        // 获取子文件夹
        File[] folders = path.listFiles();
        for (File file : folders) {
            if(file.isDirectory()) {
                String filePath = file.getAbsolutePath();
                if(filePath.contains(nsdate)) {
                    // 只处理当天的文件夹
                    if(filePath.compareTo(lastPath)>0) {
                        lastPath=filePath;
                    }
                }
            }
        }

        return lastPath;
    }

    @Override
    public List<NewspaperXml> getPapers(String paperCode, String nsdate) {
        return dao.query(NewspaperXml.class, Cnd.where("paper_code","=",paperCode).and("nsdate","=",nsdate));
        //return dao.fetch("news_paper_xml", Cnd.where("paper_code","=",paperCode).and("nsdate","=",nsdate));
    }

    @Override
    public NewspaperXml getPaperDetail(String paperCode, String docCode) {
        log.info("getPaperDetail(String paperCode:{}, String docCode:{})",paperCode,docCode);
        List<NewspaperXml> ls = dao.query(NewspaperXml.class, Cnd.where("paper_code","=",paperCode).and("doc_code","=",docCode).and("del_flag","=",0));
        if(ls!=null && !ls.isEmpty()) {
            return  ls.get(0);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = false)
    public TreeMap<String,List<NewspaperXml>> renewPaper(String paperCode, String paperName, String nsdate, String filePath)  throws ParseException {

        TreeMap<String,List<NewspaperXml>> treeMap = readXml2List(filePath);

        // 删除旧的
        List<NewspaperXml> olds = getPapers(paperCode,nsdate);
        for (NewspaperXml o:olds) {
            dao.delete(o);
        }

        // 重新插入新的
        int page_index = 0;
        int doc_index = 0;
        Date docT =dateFormat.parse(nsdate);
        String docTime = docT.getTime()+"";
        for (List<NewspaperXml> ls: treeMap.values()) {
            String pagePic = String.format("page_%02d.jpg",++page_index);
            for (NewspaperXml xml:ls) {
                xml.setPaperCode(paperCode);        //报纸编码
                xml.setPaperName(paperName);        //报纸名称
                xml.setNsdate(nsdate);              //日期编码 20190126

                xml.setType("7");                   //媒体类型,  (新闻1；专题2 ；聚合3；音频4；视频5；图片6 ；日报7）")
                xml.setDocTime(docTime);            //报纸日期 1551196800000

                // FIXME 背景图片应该跟版面对应，一般b开头，jpg结尾
                xml.setPagePic(pagePic);  //背景图片
                xml.setDocCode(nsdate+ String.format("%04d",++doc_index)); //文章编码 yyyyMMdd0000 id

                xml.setMobileTitle(xml.getTitle()); //手机标题 FIXME 没传？

                // 坐标重新处理(x1,y1#x2,y2#x3,y3#x4,y4),按百分比计算
                xml.setCoords(formatCoords(xml.getWidth(), xml.getHeight(), xml.getCoords()));
                xml.setDelFlag(0);
                dao.insert(xml);
            }
        }
        return treeMap;
    }

    /**
     * 读取xml 并按照版面排序
     * @param filePath
     * @return
     */
    private TreeMap<String,List<NewspaperXml>> readXml2List(String filePath) {

        TreeMap<String,List<NewspaperXml>> pageList = new TreeMap<>();
        File path = new File(filePath);
        if(path.exists()) {
            File[] forlds = path.listFiles();
            for (File file : forlds) {
                if(file.getName().toLowerCase().endsWith(".xml")) {
                    List<NewspaperXml> ls = readOneXml(file);
                    if(ls != null && !ls.isEmpty()) {
                        String pageNum = formatPageNum(ls.get(0).getPageNum());
                        pageList.put(pageNum,ls);
                    }
                }
            }
        }
        return pageList;
    }

    @Override
    public String formatPageNum(String pageNum) {
        if(pageNum==null) {
            // 空，0
            pageNum="0";
        }
        if(pageNum.length()==1) {
            // 只有一位 ，前补0
            pageNum = "0"+pageNum;
        } else if((pageNum.length()>2)) {
            // 两位以上,不是以零开头  ，前补0
            if(!pageNum.startsWith("0")) {
                pageNum = "0"+pageNum;
            }
        }

        return  pageNum;
    }

    /**
     * 向CMS 同步数据
     * @param dict      系统配置
     * @param nsdate    日期 yyyyMMdd
     * @param treeMap   稿件数据
     */
    @Override
    public void doSend2Cms(Dict dict, String nsdate, String cmsUrl, TreeMap<String,List<NewspaperXml>> treeMap){

        taskExecutor.execute(new Runnable() {
            public void run() {
                // 栏目id;图片外链地址
                String[] ary=dict.getDescription().split(";");

                // 先删除旧数据
                JSONObject jsonObject = new JSONObject();
                Map<String, String> params = new HashMap<>();
                jsonObject.put("time",nsdate);
                jsonObject.put("chnlname",dict.getLabel());
                jsonObject.put("chnlid",ary[0]);
                String del_result = HttpUtils.doPost(cmsUrl + "pager/del", jsonObject.toJSONString() , "UTF-8", 60000);

                if(del_result != null) {
                    // 拼图片前缀
                    String imgUrl = ary[1] + dict.getValue()+"/" + nsdate + "/";
                    for (List<NewspaperXml> ls: treeMap.values()) {
                        for (NewspaperXml xml:ls) {

                            // 已json格式推送到cms
                            String json = xml2JsonStr(ary[0],dict.getLabel(),nsdate,imgUrl,xml);
                            try {
                                HttpUtils.doPost(cmsUrl + "pager/add", json, "UTF-8", 120000);
                            } catch (Exception ex) {
                                log.error("doSend2Cms paperCode:{},nsdate:[],docCode:{},errpr:{}",
                                        dict.getValue(),nsdate,xml.getDocCode(),ex.getMessage());
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 读取一个xml到list
     * @param file
     * @return
     */
    private List<NewspaperXml> readOneXml(File file) {
        if(file==null || !file.exists()) {
            return null;
        }

        Trs trs = convert(file.getAbsolutePath());
        return trs.getRecs();
    }

    /**
     * 将XML转换为Trs对象
     *
     * @param xmlFilePath
     * @return Trs对象
     */
    private Trs convert(String xmlFilePath) {
        String xml = FileUtils.readFile(xmlFilePath);
        return JaxbMapper.fromXml(xml, Trs.class);
    }

    /**
     * 准备jspn参数
     * @param chnlid    栏目id
     * @param chnlname  栏目名称
     * @param nsdate    日期 yyyyMMdd
     * @param imgUrl    图片path
     * @param xml       数据
     * @return
     */
    private String xml2JsonStr(String chnlid, String chnlname, String nsdate, String imgUrl, NewspaperXml xml) {
        JSONObject rtn = new JSONObject();
        //<SOURCE>山西日报</SOURCE>---chnlname
        rtn.put("chnlname",chnlname);
        //栏目id -------chnlid
        rtn.put("chnlid",chnlid);
        //<NSDATE>20190126</NSDATE>---time
        rtn.put("time",nsdate);
        //<INTROTITLE>习近平在中共中央政治局第十二次集体学习时强调</INTROTITLE> ------- pretitle
        rtn.put("pretitle",xml.getIntrotitle());
        //<TITLE>推动媒体融合向纵深发展 巩固全党全国人民共同思想基础</TITLE>----title
        rtn.put("title",xml.getTitle());
        //<SUBTITLE></SUBTITLE>----subtitle
        rtn.put("subtitle",xml.getSubTitle());
        //<AUTHOR>本报编辑</AUTHOR>------creator
        rtn.put("creator",xml.getAuthor());
        // 正文中图片，多个用分号分割
        List<Attachment> atts = formatAttachment(xml.getPicUrl(),xml.getPicText(),imgUrl);
        JSONArray array = new JSONArray();

        //        <PICURL></PICURL>------attachment.attachmenturl
        //        <PICTEXT></PICTEXT>-------attachment.attachmentname
        // <CONTENT>------contentString
        StringBuilder sb = new StringBuilder();
        if(atts!=null && !atts.isEmpty()) {
            for (Attachment att:atts) {
                // 拼接图片
                sb.append(String.format("<div align=\"center\"><img src=\"%s\" title=\"%s\" alt=\"%s\" width=\"550\" oldsrc=\"%s\"></div>",
                        att.getAttachmenturl(),att.getAttachmentname(),att.getAttachmentname(),att.getAttachmentorigin()));

                JSONObject attachment = new JSONObject();
                attachment.put("attachmenturl",att.getAttachmenturl());
                attachment.put("attachmentname",att.getAttachmentname());
                array.add(attachment);
            }
        }
        rtn.put("attachment",array);
        sb.append(HtmlUtils.htmlUnescape(xml.getContent()));
        rtn.put("content",sb.toString());

        return rtn.toJSONString();
    }

    /**
     * 准备jspn参数
     * @param picurl    图片名 分号分隔 正文用
     * @param pictext   图片说明 分号分隔 正文用
     * @param imgUrl    图片path
     * @return
     */
    @Override
    public List<Attachment> formatAttachment(String picurl, String pictext, String imgUrl) {
        List<Attachment> rtn = new ArrayList<>();
        String[] picAry = null;
        if(picurl != null) {
            picurl=picurl.trim();
            if(!"".equals(picurl)) {
                picAry=picurl.split(";");
            }
        }
        if(picAry!=null) {
            String[] picText = (pictext+"").trim().split(";");
            for (int i = 0; i <picAry.length ; i++) {
                // 图片说明文字
                String txt="";
                if(i<picText.length) {
                    txt = picText[i];
                }
                System.out.println(picAry[i]);

                Attachment att = new Attachment();
                if(picAry[i].startsWith(uploadDomain)){
                    att.setAttachmenturl(picAry[i]);
                }else {
                    att.setAttachmenturl(imgUrl+picAry[i]);
                }
                att.setAttachmentorigin(picAry[i]);
                att.setAttachmentname(txt);

                rtn.add(att);
            }
        }

        return rtn;
    }

    /**
     * 报纸列表
     * @param newspaperXmlVO
     * @return
     */
    @Override
    public QueryResult list(NewspaperXmlVO newspaperXmlVO) {
        Criteria criteria=Cnd.cri();
        if(StringUtils.isNotBlank(newspaperXmlVO.getTitle())){
            criteria.where().and("title","like","%"+newspaperXmlVO.getTitle()+"%");
        }
        if(StringUtils.isNotBlank(newspaperXmlVO.getDocCode())){
            criteria.where().and("doc_code","like","%"+newspaperXmlVO.getDocCode()+"%");
        }
        if(StringUtils.isNotBlank(newspaperXmlVO.getContent())){
            criteria.where().and("content","like","%"+newspaperXmlVO.getContent()+"%");
        }
        if(StringUtils.isNotBlank(newspaperXmlVO.getPageNum())){
            criteria.where().and("page_num","like","%"+newspaperXmlVO.getPageNum()+"%");
        }
        if(StringUtils.isNotBlank(newspaperXmlVO.getStartTime())){
            criteria.where().and("nsdate",">=",newspaperXmlVO.getStartTime().substring(0,10).replace("-",""));
        }
        if(StringUtils.isNotBlank(newspaperXmlVO.getEndTime())){
            criteria.where().and("nsdate","<=",newspaperXmlVO.getEndTime().substring(0,10).replace("-",""));
        }
        if(newspaperXmlVO.getDelFlag()==null){
            criteria.where().and("del_flag","<",NewspaperXml.STATUS_DELETE);
        }else {
            criteria.where().and("del_flag","=",newspaperXmlVO.getDelFlag());
        }
        criteria.where().and("paper_code","=","sxrb");
        criteria.getOrderBy().desc("nsdate");
        criteria.getOrderBy().asc("doc_code");
        QueryResult result=listPage(newspaperXmlVO.getPageNumber(),newspaperXmlVO.getPageSize(),criteria);
        return result;
    }


    /**
     * 格式化坐标
     * @param width         原始图片宽度
     * @param height        原始图片高度
     * @param xmlcoords     相当于原始图片坐标 <ZB>30,2065;840,2065;840,2397;30,2397</ZB>
     * @return
     */
    private String formatCoords(Integer width,Integer height,String xmlcoords) {
        if(width==null ||height ==null || xmlcoords==null) {
            // 无效值
            return null;
        }
        if(width<= 0 || height <=0) {
            // 避免除零
            return null;
        }

        String[] ary = xmlcoords.split(";");      // <ZB>30,2065;840,2065;840,2397;30,2397</ZB>
        if(ary.length < 4) {
            // 必须是4个坐标
            return null;
        }

        String coords="";     // 坐标重新处理(x1,y1#x2,y2#x3,y3#x4,y4),按百分比计算
        BigDecimal hundred = BigDecimal.valueOf(100);

        for(int i=0;i<ary.length;i++) {
            String[] xy=ary[i].split(",");
            if(xy.length != 2) {
                // 必须是4个坐标
                return null;
            }

            try {
                BigDecimal bigx = new BigDecimal(xy[0]);
                BigDecimal bigy = new BigDecimal(xy[1]);

                coords += bigx.multiply(hundred).divide(BigDecimal.valueOf(width),6,BigDecimal.ROUND_CEILING) + "%,";
                coords += bigy.multiply(hundred).divide(BigDecimal.valueOf(height),6,BigDecimal.ROUND_CEILING) + "%#";
            } catch (Exception e) {
                log.warn("formatCoords Exception:{}",e.getMessage());
                return null;
            }
        }

        // 去除最后一个#
        if(coords.length()>0) {
            coords=coords.substring(0,coords.length()-1);
        }

        return coords;
    }

    /**
     * 删除报纸
     * @param id
     * @return
     */
    @Override
    @Transactional
    public NewspaperXml del(Long id) {
        NewspaperXml newspaperXml=dao.fetch(NewspaperXml.class,id);
        newspaperXml.setDelFlag(NewspaperXml.STATUS_DELETE);
        dao.updateIgnoreNull(newspaperXml);
        return newspaperXml;
    }

    /**
     * 报纸详情
     * @param id
     * @return
     */
    @Override
    public NewspaperXml getPaperById(Long id) {
        NewspaperXml newspaperXml=dao.fetch(NewspaperXml.class,id);

        Dict dict = dictService.fetchByTypeValue(INewspaperXmlService.DIC_TYPE_NEWSPAPER_CODE,"sxrb");
        if(dict==null) {
            log.info("报纸：{}，没有对接","sxrb");
            return null;
        }
        NewspaperXml xml = newspaperXmlService.getPaperDetailWeb("sxrb",newspaperXml.getDocCode());
        if(xml == null) {
            // 没有数据
            return  null;
        }
        // 栏目id;图片外链地址
        String[] ary=dict.getDescription().split(";");
        String imgUrl = ary[1] + dict.getValue()+"/" + xml.getNsdate() + "/";
        List<Attachment> imgInfoList=new ArrayList<>();
        if(newspaperXml.getPicUrl()!=null && !"".equals(newspaperXml.getPicUrl())){
            String picUrl=newspaperXml.getPicUrl();
            String[] urlArray = picUrl.split(";");
            String picText=newspaperXml.getPicText();
            String[] textArray = picText.split(";");
            int j=0;
            for (int i = 0; i < urlArray.length; i++) {
                Attachment imgInfo=new Attachment();
                if(urlArray[i].startsWith(uploadDomain)){
                    imgInfo.setAttachmenturl(urlArray[i]);
                }else{
                    imgInfo.setAttachmenturl(imgUrl+urlArray[i]);
                }
                if(textArray.length==0){
                    imgInfo.setAttachmentname("");
                }
                if(j<textArray.length){
                    imgInfo.setAttachmentname(textArray[j]);
                    j++;
                }
                imgInfoList.add(imgInfo);
            }
        }
        newspaperXml.setAttachmentList(imgInfoList);
        return newspaperXml;
    }

    /**
     * 更新报纸
     * @param newspaperXml
     * @return
     */
    @Override
    @Transactional
    public NewspaperXml updateNew(NewspaperXml newspaperXml) {
        List<Attachment> attachmentList=newspaperXml.getAttachmentList();
        List<String> urlArray=new ArrayList<>();
        List<String> textArray=new ArrayList<>();
        if(attachmentList!=null && attachmentList.size()>0){
            for (int i=0;i< attachmentList.size();i++){
                urlArray.add(attachmentList.get(i).getAttachmenturl());
                textArray.add(attachmentList.get(i).getAttachmentname());
            }
        }
        if(urlArray!=null && urlArray.size()>0){
            for (int i=0;i<urlArray.size();i++){
                if(urlArray.get(i).trim().startsWith(IMG_SRC)){
                    int index=urlArray.get(i).lastIndexOf("/")+1;
                    String urlSub=urlArray.get(i).substring(index,urlArray.get(i).length());
                    urlArray.set(i,urlSub);
                }
            }
        }
        String newUrl=StringUtils.join(urlArray, ";");
        String newText=StringUtils.join(textArray, ";");

        newspaperXml.setPicUrl(newUrl);
        newspaperXml.setPicText(newText);

        newspaperXmlService.save(newspaperXml);
        return newspaperXml;
    }

    /**
     * 报纸上线
     * @param id
     * @return
     */
    @Override
    @Transactional
    public NewspaperXml onLine(Long id) {
        NewspaperXml newspaperXml=dao.fetch(NewspaperXml.class,id);
        newspaperXml.setDelFlag(NewspaperXml.STATUS_ONLINE);
        dao.updateIgnoreNull(newspaperXml);
        return newspaperXml;
    }

    /**
     * 报纸下线
     * @param id
     * @return
     */
    @Override
    @Transactional
    public NewspaperXml downLine(Long id) {
        NewspaperXml newspaperXml=dao.fetch(NewspaperXml.class,id);
        newspaperXml.setDelFlag(NewspaperXml.STATUS_OFFLINE);
        dao.updateIgnoreNull(newspaperXml);
        return newspaperXml;
    }

    @Override
    public NewspaperXml getPaperDetailWeb(String paperCode, String docCode) {
        log.info("getPaperDetail(String paperCode:{}, String docCode:{})",paperCode,docCode);
        List<NewspaperXml> ls = dao.query(NewspaperXml.class, Cnd.where("paper_code","=",paperCode).and("doc_code","=",docCode));
        if(ls!=null && !ls.isEmpty()) {
            return  ls.get(0);
        }
        return null;
    }

    @Override
    public List<String> getDateList(String paperName, String pjCode) {
        List<NewspaperXml> list=dao.query(NewspaperXml.class,Cnd.where("paper_code","=",paperName).groupBy("nsdate").desc("nsdate"));
        List<String> dateList=new ArrayList<>();
        for (int i=0;i<list.size();i++){
            String nsdate=list.get(i).getNsdate();
            dateList.add(nsdate.substring(0,4)+"-"+nsdate.substring(4,6)+"-"+nsdate.substring(6,8));
        }
        return dateList;
    }
}