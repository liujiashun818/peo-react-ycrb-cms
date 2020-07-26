package cn.people.one.modules.activitycode.utils;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Arrays;

/**
 * Created by sunday on 2017/4/13.
 */
public class RestTemplateUtil {

    private static final String rmrb_user = "rmrb";
    private static final String rmrb_password = "admin123456";

    public static HttpHeaders getHeaders(){
        String plainCredentials= rmrb_user+":"+rmrb_password;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        return headers;
    }
}
