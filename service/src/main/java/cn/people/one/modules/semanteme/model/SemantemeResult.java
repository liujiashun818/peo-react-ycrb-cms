package cn.people.one.modules.semanteme.model;

import java.io.Serializable;

public class SemantemeResult implements Serializable{

	private static final long serialVersionUID = 8590661017087196083L;
	
	private String res = "0";//返回0成功 其他失败
	private String msg = "";//错误信息
	private String resultContent ="";//返回结果
	public String getRes() {
		return res;
	}
	public void setRes(String res) {
		this.res = res;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResultContent() {
		return resultContent;
	}
	public void setResultContent(String resultContent) {
		this.resultContent = resultContent;
	}
	
	public String toString() {
		return "SemantemeResult [res=" + res + ", msg=" + msg
				+ ", resultContent=" + resultContent + "]";
	}
	
}
