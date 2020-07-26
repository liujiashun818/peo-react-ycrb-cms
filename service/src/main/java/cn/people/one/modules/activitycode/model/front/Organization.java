package cn.people.one.modules.activitycode.model.front;



/**
 * 机构
 * Created by zhouxin on 2017/2/7.
 */
public class Organization  {

//    @Comment("项目编号")
    private String appId;


public String getAppId() {
		return appId;
	}


	public void setAppId(String appId) {
		this.appId = appId;
	}


	public String getOrgId() {
		return orgId;
	}


	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getOrgShort() {
		return orgShort;
	}


	public void setOrgShort(String orgShort) {
		this.orgShort = orgShort;
	}


	public String getOrgName() {
		return orgName;
	}


	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}


	//    @Comment("机构ID")
    private String orgId;


//    @Comment("机构简称")
    private String orgShort;


//    @Comment("机构名称")
    private String orgName;

}