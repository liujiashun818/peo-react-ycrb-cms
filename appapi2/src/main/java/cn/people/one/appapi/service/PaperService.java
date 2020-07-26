package cn.people.one.appapi.service;

import cn.people.one.appapi.vo.newspaper.*;
import cn.people.one.core.util.time.DateHelper;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YX
 * @date 2018/10/19
 * @comment
 */
@Slf4j
@Service
public class PaperService {

    @Value("${theone.httpUrl.newspager.list}")
    private String newspaperListUrl;

    @Value("${theone.httpUrl.newspager.detail}")
    private String newspaperDetailUrl;

    @Value("${theone.httpUrl.newspager.date}")
    private String newspaperDateUrl;

    @Value("${theone.httpUrl.img}")
    private String newsPaperImgUrl;

    @Value("${http.vshare}")
    private String vshareUrl;

    @Value("${theone.project.code}")
    private String code;

    /**
     * 坐标默认常量
     */
    private static final int DEFAULT_COORD_WIDTH = 387 ;
    private static final int DEFAULT_COORD_HEIGHT_RB = 466;
    private static final int DEFAULT_COORD_HEIGHT_WB = 566;
    private static final int DEFAULT_COORD_PID_ONE_RB = 88;
    private static final int DEFAULT_COORD_PID_ONE_WB = 104;
    private static final int DEFAULT_COORD_PID_TWO = 102;
    /**
     * 项目编码
     */
    private static final String PAPER_CODE = "paper";
    /**
     * 默认频道id
     */
    private static final String DEFAULT_CATEGORYID = "8";
    /**
     * 单行/两行显示
     */
    private static final Integer ROW_NUM_ONE = 1;
    private static final Integer ROW_NUM_TWO = 2;
    /***
     * 标志常量
     */
    private static final String ITEM = "Item";
    private static final String PAGE = "Page";
    /**
     * 分享地址前缀常量
     */
    private static final String SHARE_URL_CONSTANT = "detail/paper/";
    /**
     * 手机系统
     */
    private static final String MOBILE_PHONE_SYSTEM_IOS = "ios";
    private static final String MOBILE_PHONE_SYSTEM_ANDROID = "android";

    private final static Pattern IMG_PATTERN = Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?\\s.*?>", Pattern.CASE_INSENSITIVE);

    /**
     * 根据日期和坐标范围获取报纸列表信息
     *
     * @param date
     * @param areaSize
     * @param pjCode
     * @return
     */
    public List<PaperPage> getListByDate(String date, Float[] areaSize, String pjCode) {
        //1、获取报纸系统传输的报纸数据
        NewsInfoList newsInfoList = null;
        //TODO 如果缓存存在，先取缓存
        //2、缓存为空的话调用报纸接口获取信息
        newsInfoList = getListFromPaper(date,pjCode);
        //3、转换信息返回给客户端
        if (newsInfoList == null) {
            return null;
        }
        //初始化最后封装的paperPageList
        List<PaperPage> paperPageList = new ArrayList<>();
        //创建报纸元素list对应key为pageNum+"Item"的map
        Map<String, List<PaperItem>> paperItemMap = new HashMap<>();
        //创建报纸版面信息PaperPage对应key为pageNum+"Page"
        Map<String, PaperPage> paperPageMap = new HashMap<>();
        List<NewsInfo> newsInfos = newsInfoList.getNewsInfoList();
        for (int i = 0; i < newsInfos.size(); i++) {
            NewsInfo newsInfo = newsInfoList.getNewsInfoList().get(i);
            newsInfo.setRowNum(ROW_NUM_ONE);
            //组装paperPage
            PaperPage paperPage = new PaperPage();
            paperPage.setPageName(newsInfo.getPageName());
            paperPage.setPageNum(newsInfo.getPageNum());
            paperPage.setPagePic(newsPaperImgUrl + newsInfo.getPagePic());
            if (StringUtils.isNotBlank(newsInfo.getDocTime())) {
                paperPage.setPeriodNum(DateHelper.getFormatByLong(DateHelper.DEFAULT_DATE_FORMAT, Long.parseLong(newsInfo.getDocTime())));
            }
            //组装paperItem基本属性
            PaperItem paperItem = transToPaperItem(newsInfo, areaSize, pjCode);
            //处理媒体资源
            handleMedia(paperItem, newsInfo);
            //处理rowNum
            handleRowNum(paperItem, newsInfos, i);
            String itemKey = newsInfo.getPageNum() + ITEM;
            String pageKey = newsInfo.getPageNum() + PAGE;
            if (paperItemMap.containsKey(itemKey)) {
                paperItemMap.get(itemKey).add(paperItem);
            } else {
                List<PaperItem> paperItemList = new ArrayList<>();
                paperItemList.add(paperItem);
                paperItemMap.put(itemKey, paperItemList);
            }
            if (!paperPageMap.containsKey(pageKey)) {
                paperPageMap.put(pageKey, paperPage);
            }
        }
        //组装paperPageList
        assemblePaperPageList(paperItemMap, paperPageMap, paperPageList);
        Collections.sort(paperPageList, new Comparator<PaperPage>() {
            @Override
            public int compare(PaperPage o1, PaperPage o2) {
                return Integer.valueOf(o1.getPageNum())-Integer.valueOf(o2.getPageNum());
            }
        });
        return paperPageList;
    }

    /**
     * AIUI获取报纸文章了列表信息接口
     *
     * @param date
     * @param areaSize
     * @param pjCode
     * @return
     */
    public List<PaperPage> getAiuiListInfo(String date, Float[] areaSize, String pjCode) {
        //1、获取报纸系统传输的报纸数据
        NewsInfoList newsInfoList = null;
        //TODO 如果缓存存在，先取缓存
        //2、缓存为空的话调用报纸接口获取信息
        newsInfoList = getListFromPaper(date,pjCode);
        //3、转换信息返回给客户端
        if (newsInfoList == null) {
            return null;
        }
        //初始化最后封装的paperPageList
        List<PaperPage> paperPageList = new ArrayList<>();
        //创建报纸元素list对应key为pageNum+"Item"的map
        Map<String, List<PaperItem>> paperItemMap = new HashMap<>();
        //创建报纸版面信息PaperPage对应key为pageNum+"Page"
        Map<String, PaperPage> paperPageMap = new HashMap<>();
        List<NewsInfo> newsInfos = newsInfoList.getNewsInfoList();
        for (int i = 0; i < newsInfos.size(); i++) {
            NewsInfo newsInfo = newsInfoList.getNewsInfoList().get(i);
            if (cn.people.one.core.util.text.StringUtils.isBlank(newsInfo.getTitle())) {
                //AIUI只能语音识别过滤文章标题为空的报纸
                continue;
            }
            newsInfo.setRowNum(ROW_NUM_ONE);
            //组装paperPage
            PaperPage paperPage = new PaperPage();
            paperPage.setPageName(newsInfo.getPageName());
            paperPage.setPageNum(newsInfo.getPageNum());
            paperPage.setPagePic(newsPaperImgUrl + newsInfo.getPagePic());
            if (StringUtils.isNotBlank(newsInfo.getDocTime())) {
                paperPage.setPeriodNum(DateHelper.getFormatByLong("yyyy-MM-dd", Long.parseLong(newsInfo.getDocTime())));
            }
            //组装paperItem基本属性
            PaperItem paperItem = transToPaperItem(newsInfo, areaSize, pjCode);
            //处理媒体资源
            handleMedia(paperItem, newsInfo);
            //处理rowNum
            handleRowNum(paperItem, newsInfos, i);
            String itemKey = newsInfo.getPageNum() + ITEM;
            String pageKey = newsInfo.getPageNum() + PAGE;
            if (paperItemMap.containsKey(itemKey)) {
                paperItemMap.get(itemKey).add(paperItem);
            } else {
                List<PaperItem> paperItemList = new ArrayList<>();
                paperItemList.add(paperItem);
                paperItemMap.put(itemKey, paperItemList);
            }
            if (!paperPageMap.containsKey(pageKey)) {
                paperPageMap.put(pageKey, paperPage);
            }
        }
        //组装paperPageList
        assemblePaperPageList(paperItemMap, paperPageMap, paperPageList);
        Collections.sort(paperPageList, new Comparator<PaperPage>() {
            @Override
            public int compare(PaperPage o1, PaperPage o2) {
                return Integer.valueOf(o1.getPageNum())-Integer.valueOf(o2.getPageNum());
            }
        });
        return paperPageList;
    }

    /**
     * 根据itemMap和pageMap 组装最后结果paperPageList
     *
     * @param paperItemMap
     * @param paperPageMap
     * @param paperPageList
     */
    private void assemblePaperPageList(Map<String, List<PaperItem>> paperItemMap, Map<String, PaperPage> paperPageMap, List<PaperPage> paperPageList) {
        Set<Map.Entry<String, List<PaperItem>>> paperItemSet = paperItemMap.entrySet();
        Set<Map.Entry<String, PaperPage>> paperPageSet = paperPageMap.entrySet();
        for (Map.Entry<String, PaperPage> paperPageEntry : paperPageSet) {
            String pageNum = paperPageEntry.getKey().replace(PAGE, "");
            for (Map.Entry<String, List<PaperItem>> paperItemListEntry : paperItemSet) {
                String itemNum = paperItemListEntry.getKey().replace(ITEM, "");
                if (pageNum.equals(itemNum)) {
                    paperPageEntry.getValue().setPaperItemList(paperItemListEntry.getValue());
                    paperPageList.add(paperPageEntry.getValue());
                    break;
                }
            }
        }
        //处理viewType
        for (int i = 0; i < paperPageList.size(); i++) {
            List<PaperItem> paperItemList = paperPageList.get(i).getPaperItemList();
            for (int j = 0; j < paperItemList.size(); j++) {
                if (j == 1) {
                    if (paperItemList.get(j).getRowNum().equals(paperItemList.get(j - 1).getRowNum())) {
                        paperItemList.get(j).setViewType(PaperItem.Constant.view_type4);
                        paperItemList.get(j - 1).setViewType(PaperItem.Constant.view_type4);
                    } else {
                        if (StringUtils.isNotBlank(paperItemList.get(j).getVideoLink1())) {
                            paperItemList.get(j).setViewType(PaperItem.Constant.view_type5);
                        } else if (paperItemList.get(j).getImage() != null && paperItemList.get(j).getImage().size() > 0) {
                            paperItemList.get(j).setViewType(PaperItem.Constant.view_type3);
                        } else {
                            paperItemList.get(j).setViewType(PaperItem.Constant.view_type2);
                        }
                    }
                }
            }
        }
    }

    /**
     * 将从报纸系统获得的newsInfo转换成newsItem
     *
     * @param newsInfo
     */
    private PaperItem transToPaperItem(NewsInfo newsInfo, Float[] areaSize, String pjCode) {
        PaperItem paperItem = new PaperItem();
        paperItem.setArticleId(newsInfo.getId()+"_"+pjCode);
        //TODO  报纸的默认栏目
        paperItem.setCategoryId(DEFAULT_CATEGORYID);
        paperItem.setId(newsInfo.getId()+"_"+pjCode);
        paperItem.setNewsLink("1|" + newsInfo.getId());
        paperItem.setNewsTimestamp(newsInfo.getDocTime());
        paperItem.setPaperName(pjCode);
        //格式化文档时间
        if (StringUtils.isNotBlank(newsInfo.getDocTime())) {
            paperItem.setNewsDatetime(DateHelper.getFormatByLong("yyyy-MM-dd", Long.parseLong(newsInfo.getDocTime())));
        }
        //获得热区坐标
        if (areaSize != null) {
            String points = getHotspotArea(newsInfo.getCoords(), areaSize[0], areaSize[1],pjCode);
            if (StringUtils.isNotBlank(points)) {
                paperItem.setPoints(points);
            }
        }
        paperItem.setSysCode(PAPER_CODE);
        paperItem.setTitle(newsInfo.getTitle());
        paperItem.setPjCode(pjCode);//TODO
        paperItem.setRowNum(ROW_NUM_ONE);
        paperItem.setViewType(PaperItem.Constant.view_type2);
        return paperItem;
    }

    /**
     * 处理paperItem中的rowNum
     *
     * @param paperItem
     * @param newsInfos
     * @param i
     */
    private void handleRowNum(PaperItem paperItem, List<NewsInfo> newsInfos, Integer i) {
        //处理rowNum和viewType
        paperItem.setViewType(PaperItem.Constant.view_type2);
        if (i == 0) {
            paperItem.setRowNum(ROW_NUM_ONE);
        } else if (i == 1) {
            if (StringUtils.isBlank(newsInfos.get(i - 1).getCover())
                    && StringUtils.isBlank(newsInfos.get(i).getCover())) {
                paperItem.setRowNum(newsInfos.get(i - 1).getRowNum());
            } else {
                paperItem.setRowNum(ROW_NUM_ONE.equals(newsInfos.get(i - 1).getRowNum()) ? ROW_NUM_TWO : ROW_NUM_ONE);
            }
        } else {
            if (StringUtils.isBlank(newsInfos.get(i - 1).getCover())
                    && StringUtils.isBlank(newsInfos.get(i).getCover())
                    && newsInfos.get(i - 1).getRowNum() != newsInfos.get(i - 2).getRowNum()) {
                paperItem.setRowNum(newsInfos.get(i - 1).getRowNum());
            } else {
                paperItem.setRowNum(ROW_NUM_ONE.equals(newsInfos.get(i - 1).getRowNum()) ? ROW_NUM_TWO : ROW_NUM_ONE);
            }
        }
        newsInfos.get(i).setRowNum(paperItem.getRowNum());
    }

    /**
     * 处理paperItem里面的媒体资源
     *
     * @param paperItem
     */
    private void handleMedia(PaperItem paperItem, NewsInfo newsInfo) {
        List<String> imageList = new ArrayList<>();
        for (NewsInfoMedias newsInfoMedias : newsInfo.getMedias()) {
            //视频
            if (StringUtils.isNotBlank(newsInfoMedias.getAttach())
                    && newsInfoMedias.getType() == NewsInfoMedias.Constant.TYPE_VIDEO
                    && newsInfoMedias.getEncodeType() == NewsInfoMedias.Constant.ENCODETYPE_VIDEO1) {
                paperItem.setVideoLink1(newsInfoMedias.getAttach());
            }
            if (StringUtils.isNotBlank(newsInfoMedias.getAttach())
                    && newsInfoMedias.getType() == NewsInfoMedias.Constant.TYPE_VIDEO
                    && newsInfoMedias.getEncodeType() == NewsInfoMedias.Constant.ENCODETYPE_VIEDO2) {
                paperItem.setVideoLink2(newsInfoMedias.getAttach());
            }
            //图片
            if (StringUtils.isNotBlank(newsInfoMedias.getAttach())
                    && newsInfoMedias.getType() == NewsInfoMedias.Constant.TYPE_IMAGE
                    && (newsInfoMedias.getEncodeType() == NewsInfoMedias.Constant.ENCODETYPE_IMAGE2 ||
                    newsInfoMedias.getEncodeType() == NewsInfoMedias.Constant.ENCODETYPE_IAMGE1)) {
                imageList.add(newsPaperImgUrl + newsInfoMedias.getAttach());
            }
        }
        if (imageList.size() > 0) {
            paperItem.setImage(Lists.newArrayList(imageList.get(0)));
            newsInfo.setCover(imageList.get(0));
        }
    }

    /**
     * 从报纸系统获取报纸信息列表
     *
     * @param date (默认格式yyyy-MM-dd)
     * @return
     */
    private NewsInfoList getListFromPaper(String date,String pjCode) {
        log.info("**********************************调用报纸信息列表接口》开始******************************************");
        NewsInfoList newsInfoList = null;
        date = date.replaceAll("-", "");
        String listUrl = newspaperListUrl + "code=" + pjCode + "&date=" + date;
        log.info("访问报纸系统》报纸信息列表接口地址》" + listUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置请求头部：token,编码
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> result = restTemplate.getForEntity(listUrl, String.class);
        log.info("**********************************响应结果状态》" + result.getStatusCodeValue());
        log.info("**********************************调用报纸信息列表接口》结束******************************************");
        if (result.getStatusCodeValue() == 200) {
            String newsInfoListStr = result.getBody();
            newsInfoList = (NewsInfoList) jsonToBean(NewsInfoList.class, newsInfoListStr);
            List<NewsInfo> newsInfoListNew = new ArrayList<>();
            if (newsInfoList != null && newsInfoList.getNewsInfoList() != null) {
                for (int i = 0; i < newsInfoList.getNewsInfoList().size(); i++) {
                    NewsInfo newsInfo = (NewsInfo) jsonToBean(NewsInfo.class, newsInfoList.getNewsInfoList().get(i));
                    List<NewsInfoMedias> newsInfoMediasList = new ArrayList<>();
                    for (int j = 0; j < newsInfo.getMedias().size(); j++) {
                        NewsInfoMedias newsInfoMedias = (NewsInfoMedias) jsonToBean(NewsInfoMedias.class, newsInfo.getMedias().get(j));
                        newsInfoMediasList.add(newsInfoMedias);
                    }
                    newsInfo.setMedias(newsInfoMediasList);
                    newsInfoListNew.add(newsInfo);
                }
                newsInfoList.setNewsInfoList(newsInfoListNew);
            }
        }
        return newsInfoList;
    }


    /**
     * 处理热区坐标
     *
     * @param coords 从报纸拿到的热区坐标数据eg: 52.396514%,1.960784%#99.443234%,1.960784%#99.443234%,17.647059%#52.396514%,17.647059%
     * @param width  手机宽度
     * @param height 手机高度
     * @return
     */
    private String getHotspotArea(String coords, Float width, Float height,String pjCode) {
        //根据code不同，设置宽高参数
        int defaultHeight=DEFAULT_COORD_HEIGHT_WB;
        int defaultWidth=DEFAULT_COORD_WIDTH;
        int defaultCoordPid=DEFAULT_COORD_PID_ONE_WB;
        if(code.equals(pjCode)){
            defaultHeight=DEFAULT_COORD_HEIGHT_RB;
            defaultCoordPid=DEFAULT_COORD_PID_ONE_RB;
        }
        //处理原始坐标数据//52.396514%,1.960784%#99.443234%,1.960784%#99.443234%,17.647059%#52.396514%,17.647059%
        //1、,用#替换；
        coords = (coords + "#").replace(",", "#");
        //2、以%#作为分割符，将坐标串分割成坐标数组
        String[] coordsArry = coords.split("%#");
        StringBuilder points = new StringBuilder("");
        //3、循环算出各个坐标点的xy值
        for (int i = 0; i < coordsArry.length; i++) {
            Double coordinate = Double.parseDouble(coordsArry[i]);
            int coordinateStr = 0;
            if ((width != null && height != null && width != 0 && height != 0)) {
                if (((double) width / height) <= ((double) defaultWidth / defaultHeight)) {
                    if (i % 2 == 0) {
                        coordinateStr = (int) Math.round(coordinate / defaultCoordPid / defaultHeight * height * defaultWidth);
                        points.append(coordinateStr + ",");
                    } else {
                        coordinateStr = (int) Math.round(coordinate / DEFAULT_COORD_PID_TWO * height);
                        points.append(coordinateStr + ";");
                    }
                } else {
                    if (i % 2 == 0) {
                        coordinateStr = (int) Math.round(coordinate / DEFAULT_COORD_PID_TWO * width);
                        points.append(coordinateStr + ",");
                    } else {
                        coordinateStr = (int) Math.round(coordinate / defaultCoordPid / defaultWidth * width * defaultHeight);
                        points.append(coordinateStr + ";");
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(points)) {
            return points.substring(0, points.length() - 1);
        } else {
            return points.toString();
        }
    }

    /**
     * 根据文章id获取文章详情
     *
     * @param articleId
     * @param paperName
     * @param platForm
     * @param pjCode
     * @return
     */
    public PaperDetail getDetailByArticleId(String articleId, String paperName, String platForm, String pjCode) {
        //1、TODO 缓存中获取文章
        //2、如果缓存为空则调用报纸接口获取详细信息
        //处理articleId参数
        String id=articleId.substring(0,articleId.lastIndexOf("_"));
        if(StringUtils.isBlank(pjCode) || StringUtils.isBlank(paperName)){
            pjCode=articleId.substring(articleId.lastIndexOf("_")+1,articleId.length());
            paperName=articleId.substring(articleId.lastIndexOf("_")+1,articleId.length());
        }
        NewsInfo newsInfo = getDetailFromPaper(id, paperName);
        PaperItem paperItem = transToPaperItem(newsInfo, null, pjCode);
        PaperDetail paperDetail = new PaperDetail();
        try {
            BeanUtils.copyProperties(paperItem,paperDetail);
        } catch (Exception e) {
            log.error("copy报纸属性出错===》getDetailByArticleId", e);
            return null;
        }
        //解析报纸详情的其他属性
        List<String> newsClassId = new ArrayList<>();
        paperDetail.setNewsClassId(newsClassId);
        paperDetail.setIntroTitle(newsInfo.getIntroTitle());
        paperDetail.setSubTitle(newsInfo.getSubTitle());
        paperDetail.setCopyfrom(newsInfo.getPubSource());
        paperDetail.setAuthors(newsInfo.getAuthors());
        //新的shareUrl地址拼接不再需要时间，注释掉便于以后再用
//        String dateTime = DateHelper.getFormatByLong("yyyyMMdd", Long.parseLong(newsInfo.getDocTime()));
        String shareUrl = vshareUrl + SHARE_URL_CONSTANT + articleId;// + "/" + dateTime;
        paperDetail.setShareUrl(shareUrl);
        paperDetail.setShortTitle(newsInfo.getMobileTitle().trim());
        paperDetail.setContent(StringEscapeUtils.unescapeHtml(newsInfo.getContent()));
        paperDetail.setIntroduction("");
        paperDetail.setDescription("");
        paperDetail.setShareSlogan("");
        paperDetail.setTags("");
        //媒体资源处理
        paperDetail.setMedias(newsInfo.getMedias());
        List<NewsInfoMedias> imgAll = new ArrayList<>();
        //媒体资源处理
        handleMediaForDetail(paperDetail, imgAll, platForm);
        //组装imgAll
        assembleImgAll(imgAll, newsInfo.getContent());
        paperDetail.setImgall(imgAll);
        if (imgAll != null && imgAll.size() > 0) {
            paperDetail.setCover(imgAll.get(0).getImgUrl());
        }
        return paperDetail;
    }

    /**
     * 媒体资源处理(详情专用)
     *
     * @param paperDetail
     */
    private void handleMediaForDetail(PaperDetail paperDetail, List<NewsInfoMedias> imgAll, String platForm) {
        for (NewsInfoMedias newsInfoMedias : paperDetail.getMedias()) {
            if (newsInfoMedias.getType() == NewsInfoMedias.Constant.TYPE_VIDEO) {
                newsInfoMedias.setTypeStr(NewsInfoMedias.Constant.VIDEO);
                if (MOBILE_PHONE_SYSTEM_IOS.equals(platForm)) {
                    newsInfoMedias.setEncodeTypeStr(NewsInfoMedias.Constant.VEDIO_IOS);
                } else if (MOBILE_PHONE_SYSTEM_ANDROID.equals(platForm)) {
                    newsInfoMedias.setEncodeTypeStr(NewsInfoMedias.Constant.VEDIO_ANDROID);
                }
            } else if (newsInfoMedias.getType() == NewsInfoMedias.Constant.TYPE_IMAGE) {
                newsInfoMedias.setTitle("");
                newsInfoMedias.setImgUrl(newsPaperImgUrl + newsInfoMedias.getAttach());
                newsInfoMedias.setTypeStr(NewsInfoMedias.Constant.IMG);
                newsInfoMedias.setSourceUrl(newsPaperImgUrl + newsInfoMedias.getAttach());
                NewsInfoMedias tempNewsInfoMedias = NewsInfoMedias.init(newsInfoMedias);
                String title = newsInfoMedias.getAttach().substring(newsInfoMedias.getAttach().lastIndexOf("/") + 1, newsInfoMedias.getAttach().length());
                tempNewsInfoMedias.setTitle(title);
                tempNewsInfoMedias.setCanview(NewsInfoMedias.Constant.CANVIEW_YES);
                imgAll.add(tempNewsInfoMedias);
            }
        }
    }

    /**
     * 组装所有图片数组(详情)
     *
     * @param imgAll
     * @param content
     */
    private void assembleImgAll(List<NewsInfoMedias> imgAll, String content) {
        if(StringUtils.isEmpty(content)) {
            return;
        }
        Matcher matcher = IMG_PATTERN.matcher(content);
        while (matcher.find()) {
            NewsInfoMedias newsInfoMedias = new NewsInfoMedias();
            newsInfoMedias.setType(NewsInfoMedias.Constant.TYPE_IMAGE);
            newsInfoMedias.setTypeStr(NewsInfoMedias.Constant.IMG);
            newsInfoMedias.setCanview(NewsInfoMedias.Constant.CANVIEW_YES);
            String src = matcher.group(1);
            if (src.contains("\"/>")) {
                src = src.substring(0, src.indexOf("\"/>"));
            }
            if (src.contains("display=no")) {
                newsInfoMedias.setCanview(NewsInfoMedias.Constant.CANVIEW_NO);
            }
            if (src.startsWith("http://") || src.startsWith("https://")) {
                newsInfoMedias.setSourceUrl(src);
                newsInfoMedias.setImgUrl(src);
            }
            imgAll.add(newsInfoMedias);
        }
    }

    /**
     * 调用报纸系统接口获取文章详情
     *
     * @param articleId
     * @param paperName
     * @return
     */
    private NewsInfo getDetailFromPaper(String articleId, String paperName) {
        log.info("**********************************调用报纸信息详情接口》开始******************************************");
        NewsInfo newsInfo = null;
        String detailUrl = newspaperDetailUrl + "code=" + paperName + "&id=" + articleId;
        log.info("访问报纸系统》报纸信息详情接口地址》" + detailUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        //设置请求头部：token,编码
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> result = restTemplate.getForEntity(detailUrl, String.class);
        log.info("**********************************响应结果状态》" + result.getStatusCodeValue());
        log.info("**********************************调用报纸信息详情接口》结束******************************************");
        if (result.getStatusCodeValue() == 200) {
            String newsInfoJson = result.getBody();
            newsInfo = (NewsInfo) jsonToBean(NewsInfo.class, newsInfoJson);
            List<NewsInfoMedias> newsInfoMediasList = new ArrayList<>();
            if (newsInfo.getMedias() != null && newsInfo.getMedias().size() > 0) {
                for (int i = 0; i < newsInfo.getMedias().size(); i++) {
                    NewsInfoMedias newsInfoMedias = (NewsInfoMedias) jsonToBean(NewsInfoMedias.class, newsInfo.getMedias().get(i));
                    newsInfoMediasList.add(newsInfoMedias);
                }
                newsInfo.setMedias(newsInfoMediasList);
            }
        }
        return newsInfo;
    }

    /**
     * json对应实体
     *
     * @param beanClass
     * @param objOrigin
     * @return
     */
    private Object jsonToBean(Class beanClass, Object objOrigin) {
        JSONObject jsonObject = JSONObject.fromObject(objOrigin);
        return JSONObject.toBean(jsonObject, beanClass);
    }

    /**
     * 获取历史新闻报纸
     *
     * @param paperName
     * @param pjCode
     * @return
     */
    public List<PaperDate> getPrepaperInfo(String paperName, String pjCode) {
        List<String> pubDateList = null;
        List<PaperDate> paperDateList = new ArrayList<>();
        //1、TODO 如果缓存存在先取缓存
        //2、调用报纸接口获取信息
        pubDateList = getPrepaperInfoFromPaper(paperName, pjCode);
        if (pubDateList != null && pubDateList.size() > 0) {
            for (String pubDate : pubDateList) {
                PaperDate paperDate = new PaperDate();
                String dateFormate = DateHelper.secondFormate("yyyy-MM-dd", pubDate, "yyyyMMdd");
                paperDate.setDate(dateFormate);
                paperDate.setTitle(dateFormate);
                paperDateList.add(paperDate);
            }
        }
        return paperDateList;
    }

    /**
     * 调用报纸接口获取历史新闻报纸信息
     *
     * @param paperName
     * @param pjCode
     */
    private List<String> getPrepaperInfoFromPaper(String paperName, String pjCode) {
        log.info("**********************************调用报纸历史报纸信息接口》开始******************************************");
        List<String> pubDateList = new ArrayList<>();
        String dateUrl = newspaperDateUrl + "code=" + paperName;
        log.info("访问报纸系统》历史报纸信息接口地址》" + dateUrl);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        ResponseEntity<String> result = restTemplate.getForEntity(dateUrl, String.class);
        log.info("**********************************响应结果状态》" + result.getStatusCodeValue());
        log.info("**********************************调用报纸信息列表接口》结束******************************************");
        if (result.getStatusCodeValue() == 200) {
            String pubDate = result.getBody();
            JSONArray jsonArray = JSONArray.fromObject(pubDate);
            for (Object o : jsonArray) {
                String pubDateStr = (String) o;
                pubDateList.add(pubDateStr);
            }
        }
        return pubDateList;
    }
}
