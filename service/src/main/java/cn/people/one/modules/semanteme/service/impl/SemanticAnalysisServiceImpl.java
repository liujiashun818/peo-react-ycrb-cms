package cn.people.one.modules.semanteme.service.impl;

import java.util.HashMap;
import java.util.Map;

import cn.people.one.core.util.http.HttpUtils;
import org.springframework.stereotype.Service;


import cn.people.one.modules.semanteme.model.SemantemeRequest;
import cn.people.one.modules.semanteme.model.SemantemeResult;
import cn.people.one.modules.semanteme.service.ISemanticAnalysisService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SemanticAnalysisServiceImpl implements ISemanticAnalysisService{
    public static String semanticAnalysisServiceAddr = "http://10.100.10.124:8080/PeopleWebClient/index/getAllContent.do";
	private static final String KEYWORD_ID = "keywords";//keywords 服务id
	private static final String SUMMARY_ID = "summary";//summary 服务id
	public SemantemeResult getKeywords(SemantemeRequest req) {
		SemantemeResult result = new SemantemeResult();
		Map<String,String> params = new HashMap<>();
		params.put("id", KEYWORD_ID);
		params.put("content", req.getContent());
		params.put("ifLabel", req.getIfLabel());
		params.put("nMaxKeyLimit", req.getnMaxKeyLimit());
		String resultStr = null;
		boolean isError = false;
        try {
			 resultStr = HttpUtils.doPost(semanticAnalysisServiceAddr, params, "UTF-8", 30);
			 if(resultStr.startsWith("\"") && resultStr.length() > 2){
				 resultStr = resultStr.substring(1,resultStr.length()-1);
			 }
        } catch (Exception e) {
			isError = true;
			log.error(e.getMessage(),e);
		}
		if(isError){
			result.setRes("1");
			result.setMsg("调用getKeywords接口出错!");
		}
		log.info(resultStr);
		result.setResultContent(resultStr);
        return result;
	}

	public SemantemeResult getSummary(SemantemeRequest req) {
		SemantemeResult result = new SemantemeResult();
		Map params = new HashMap();
		params.put("id", SUMMARY_ID);
		params.put("content", req.getContent());
		params.put("ifLabel", req.getIfLabel());
		params.put("nMaxKeyLimit", req.getnMaxKeyLimit());
		String resultStr = null;
		boolean isError = false;
        try {
			 resultStr = HttpUtils.doPost(semanticAnalysisServiceAddr, params, "UTF-8", 30);
			 if(resultStr.startsWith("[") && resultStr.length() > 4){
					resultStr = resultStr.substring(2);
					resultStr = resultStr.substring(0,resultStr.length() -2);
			}
		} catch (Exception e) {
			isError = true;
			log.error(e.getMessage(),e);
		}
		if(isError){
			result.setRes("1");
			result.setMsg("调用getSummary接口出错!");
		}
		log.info(resultStr);
		result.setResultContent(resultStr);
		return result;
	}
}
