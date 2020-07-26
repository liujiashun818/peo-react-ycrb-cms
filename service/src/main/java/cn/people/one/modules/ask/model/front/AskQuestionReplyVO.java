package cn.people.one.modules.ask.model.front;

import lombok.Data;

/**
 * zhouc
 */
@Data
public class AskQuestionReplyVO{
    private int devicetype;
    private int governmentId;
    private String title;
    private String questionContent;
    private String userId;
    private int domainId;
    private int typeId;
    private String attachment;
    private String userPhone;
    private String realUserName;
    private String userName;
    private String isUnknownUser;
    private Integer publishStatus;
    private Boolean isChecked;
}
