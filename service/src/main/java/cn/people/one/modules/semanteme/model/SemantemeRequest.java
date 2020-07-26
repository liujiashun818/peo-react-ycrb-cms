package cn.people.one.modules.semanteme.model;

import java.io.Serializable;

public class SemantemeRequest implements Serializable{

	private static final long serialVersionUID = -5354159951232494323L;
	private String nMaxKeyLimit;//提取长度
	private String ifLabel = "0";
	private String content;//正文
	public String getnMaxKeyLimit() {
		return nMaxKeyLimit;
	}
	public void setnMaxKeyLimit(String nMaxKeyLimit) {
		this.nMaxKeyLimit = nMaxKeyLimit;
	}
	public String getIfLabel() {
		return ifLabel;
	}
	public void setIfLabel(String ifLabel) {
		this.ifLabel = ifLabel;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public String toString() {
		return "SemantemeRequest [nMaxKeyLimit=" + nMaxKeyLimit + ", ifLabel="
				+ ifLabel + ", content=" + content + "]";
	}
	
}