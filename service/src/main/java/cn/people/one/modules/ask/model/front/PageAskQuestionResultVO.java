package cn.people.one.modules.ask.model.front;

import java.io.Serializable;

import lombok.Data;

@Data
public class PageAskQuestionResultVO  implements Serializable{
	private int govId;
	private String title;
	private String askDomainName;
	private String askTypeName;
	private String askGovernmentName;
	private String userName;
	private int status;
	private String questionTime;
    private Integer orderID;
    private Integer publishStatus;
    private String isHeadline;
}
