/**   
 * @Title: AskDomainVO.java 
 * @Package cn.people.one.modules.ask.model.front 
 * @Description: TODO(用一句话描述该文件做什么) 
 * @author Administrator
 * @date 2019年2月19日 下午6:53:17 
 * @version V1.0   
 */
package cn.people.one.modules.ask.model.front;


import lombok.Data;

import java.util.List;

@Data
public class AskGovernmentVO
{
    private String name;
    private int parentID;
    private String fid; //如果是地方政府，则表示地方政府id，与地方部提供的地方政府fid相同
    private String fup; //地方政府父ID
    private boolean isLocalGov;
    private String path; // 路径
    private int level; // 节点所在的层级数
    private String strId;//TableId_Id
    private int id;
    private byte sort;
    private boolean hasChild;
    private String firstChar;
    private int[] childIds;
    private int questionNum;
    private int replyNum;
    private String areaCode;
    private List<AskGovernmentVO> child;//

}
