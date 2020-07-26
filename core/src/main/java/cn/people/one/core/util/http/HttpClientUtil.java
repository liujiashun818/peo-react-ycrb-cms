package cn.people.one.core.util.http;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class HttpClientUtil {
  private static final Log LOG = LogFactory.getLog(HttpClientUtil.class);
  static CloseableHttpClient httpCilent = null;

  static {
    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
    cm.setMaxTotal(200);
    cm.setDefaultMaxPerRoute(20);
    httpCilent = HttpClients.custom().setConnectionManager(cm).build();

  }
  public static String get(String url) {
      LOG.info("http.get.url="+url);
      HttpGet get = new HttpGet(url);
      try {
        CloseableHttpResponse response = httpCilent.execute(get);
        if (response != null) {
          String result = EntityUtils.toString(response.getEntity());
          try {
            if (response != null) {
              response.close();
            }
          } catch (Exception e) {
          }
          return result;
        }

      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      return null;
    }
  
  public static String post(String url, String content) {
      LOG.info("http.post.url="+url);
      HttpPost post = new HttpPost(url);
      try {
          if(StringUtils.isNotEmpty(content)) {
              post.setEntity(new StringEntity(content));
          }
       
        CloseableHttpResponse response = httpCilent.execute(post);
        if (response != null) {
          String result = EntityUtils.toString(response.getEntity());
          try {
            if (response != null) {
              response.close();
            }
          } catch (Exception e) {
          }
          return result;
        }

      } catch (Exception e) {
        LOG.error(e.getMessage(), e);
      }
      return null;
    }
}