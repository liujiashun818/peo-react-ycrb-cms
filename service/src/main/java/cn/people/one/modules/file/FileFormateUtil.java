package cn.people.one.modules.file;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author YX
 * @date 2019-03-29
 * @comment
 */
@Component
public class FileFormateUtil {

    @Value("${upload.domain}")
    private String uploadDomain;

    public static  String UPLOADDOMAIN;
    private static final String HTTP_PREFIX="http://";
    /**
     * 文本编辑器图片地址前缀(政务)
     */
    private static final String CONTENT_SRC_PUBLICCMS="/publiccms/";
    /**
     * 文本编辑器图片地址前缀(原appcms)
     */
    private static final String CONTENT_SRC_APPCMS="/appcms/";
    /**
     * 文本编辑器图片地址新前缀
     */
    private static final String CONTENT_NEW_SRC="data/ycrb/";

    /**
     * 匹配所有正常的图片地址，截取src中引号内内容
     */
    private final static Pattern PATTERT_SRC=Pattern.compile("<img[^<>]*?\\ssrc=['\"]?(.*?)['\"]?(?:\"|\').*?>",Pattern.CASE_INSENSITIVE);
    @PostConstruct
    public void getApiToken() {
        UPLOADDOMAIN = uploadDomain;
    }

    /**
     * 格式化图片地址
     * @param src
     * @return
     */
    public static String formatFileSrcToWeb(String src) {
        if (StringUtils.isBlank(src)){
            return src;
        }
        if (src.startsWith(HTTP_PREFIX)) {
            return src;
        }
        return UPLOADDOMAIN+src;
    }

    /**
     * 格式化图片数组 返回数组
     * @param srcs
     * 多张图片
     * @return
     */
    public static String[] formatFilesToArry(String srcs) {
        if (StringUtils.isBlank(srcs)){
            return null;
        }
        String[] urls=null;
        if (srcs.contains("|")) {
            urls = srcs.split("\\|");
        }else if(srcs.contains(",")){
            urls = srcs.split(",");
        } else {
            urls=new String[1];
            urls[0]=formatFileSrcToWeb(srcs);
            return urls;
        }
        for (int i = 0; i < urls.length; i++) {
            urls[i] = formatFileSrcToWeb(urls[i]);
        }
        return urls;
    }

    /**
     * 格式化图片数组 返回字符串
     * @param srcs
     * 多张图片
     * @return
     */
    public static String formatFilesToStr(String srcs) {
        if (StringUtils.isBlank(srcs)){
            return null;
        }
        String[] urls=null;
        if (srcs.contains("|")) {
            urls = srcs.split("\\|");
        } else if(srcs.contains(",")){
            urls=srcs.split(",");
        }else{
            return formatFileSrcToWeb(srcs);
        }
        for (int i = 0; i < urls.length; i++) {
            urls[i] = formatFileSrcToWeb(urls[i]);
        }
        return StringUtils.join(urls, ",");
    }

    /**
     * 格式化content
     * @param content
     * @return
     */
    public static String formatContent(String content){
        if(StringUtils.isBlank(content)){
            return content;
        }
        Matcher matcher = PATTERT_SRC.matcher(content);
        while(matcher.find()) {
            String src = matcher.group(1);
            String srcNew;
            if(src.contains("\"/>")){
                src=src.substring(0,src.indexOf("\"/>"));
            }
            if(!(src.startsWith("http://")||src.startsWith("https://"))){
                if(src.contains(CONTENT_SRC_PUBLICCMS)){
                    srcNew=src.replace(CONTENT_SRC_PUBLICCMS,UPLOADDOMAIN+CONTENT_NEW_SRC);
                    content=content.replace(src,srcNew);
                }else if(src.contains(CONTENT_SRC_APPCMS)){
                    srcNew=src.replaceAll(CONTENT_SRC_APPCMS,UPLOADDOMAIN+CONTENT_NEW_SRC);
                    content=content.replace(src,srcNew);
                }
            }
        }
        return content;
    }
}
