package cn.people.one.modules.newspaper.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * 数字报XML文件根节点
 * 层级关系如下：
 * <TRS>根节点
 *     <REC>文章(多个)
 *         <NNIData>
 *             <Media>媒体
 *                 <Video></Video>视频
 *             </Media>
 *         </NNIData>
 *     </REC>
 * </TRS>
 */
@XmlRootElement(name = "TRS")
public class Trs extends ArrayList<NewspaperXml>{
    @XmlElement(name = "Rec")
    public List<NewspaperXml> getRecs() {
        return this;
    }
}