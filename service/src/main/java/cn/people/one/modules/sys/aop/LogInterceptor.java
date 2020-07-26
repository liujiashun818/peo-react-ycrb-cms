package cn.people.one.modules.sys.aop;

import cn.people.one.core.util.mapper.JsonMapper;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.sys.model.Log;
import cn.people.one.modules.sys.service.ILogService;
import cn.people.one.modules.user.utils.UserUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Map;

/**
 * User: 张新征
 * Date: 2017/4/11 10:28
 * Description:
 */
@Aspect
@Component
public class LogInterceptor {
	@Autowired
	private ILogService logService;
	JsonMapper jsonMapper = JsonMapper.defaultMapper();

	@Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public void webLog() {
	}

	@Around("webLog()")
	public Object logInterceptor(ProceedingJoinPoint point) throws Throwable{
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		Log log = new Log();
		log.setCreateDate(new Date());
		if(null != UserUtils.getUser()){
			log.setCreateBy(UserUtils.getUser().getId());
		}
		log.setMethod(request.getMethod());
		if("GET".equalsIgnoreCase(request.getMethod())){
			log.setParams(setParams(request.getParameterMap()));
		}else {
			Object[] args = point.getArgs();
			if(null != args && args.length > 0){
			    String param="";
				if(args[0] instanceof MultipartFile){
					param="MultipartFile";
				}else if(args[0].getClass().isArray() && args[0] instanceof MultipartFile[]){
					param="MultipartFile medias";
				}else{
						//判断如果不是文件请求，Ueditor的文件请求会引起序列化出错
						if(null != args[0] && !StandardMultipartHttpServletRequest.class.isInstance(args[0])){
							try {
								param=jsonMapper.toJson(args[0]);
							} catch (Exception e) {
								param="参数错误";
							}
						}
					}
					log.setParams(param);
				}
		}
		log.setRemoteAddr(getIp(request));
		log.setRequestUri(request.getRequestURI());
		log.setType("1");
		log.setUserAgent(request.getHeader("user-agent"));
		Object result = null;
		try {
			result = point.proceed();
		} catch (Throwable t) {
			log.setType("2");
			StringWriter stringWriter = new StringWriter();
			t.printStackTrace(new PrintWriter(stringWriter));
			log.setException(stringWriter.toString());
			throw t;
		}finally {
			logService.insert(log);
		}
		return result;
	}

	/**
	 * 设置请求参数
	 */
	private String setParams(Map paramMap){
		if (paramMap == null){
			return "";
		}
		StringBuilder params = new StringBuilder();
		for (Map.Entry<String, String[]> param : ((Map<String, String[]>)paramMap).entrySet()){
			params.append(("".equals(params.toString()) ? "" : "&") + param.getKey() + "=");
			String paramValue = (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "");
			params.append(StringUtils.abbr(StringUtils.endsWithIgnoreCase(param.getKey(), "password") ? "" : paramValue, 100));
		}
		return params.toString();
	}

	private static String getIp(HttpServletRequest request) {
		if (request == null) {
			return "unknown";
		}
		String ip = request.getHeader("x-forwarded-for");
		if (org.apache.commons.lang3.StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Forwarded-For");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
		}
		if (org.apache.commons.lang3.StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)){
			//多次反向代理后会有多个ip值，第一个ip才是真实ip
			int index = ip.indexOf(",");
			if (index != -1) {
				return ip.substring(0, index);
			} else {
				return ip;
			}
		}
		return ip;
	}
}
