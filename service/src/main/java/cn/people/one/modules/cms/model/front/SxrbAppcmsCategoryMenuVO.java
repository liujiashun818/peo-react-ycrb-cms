package cn.people.one.modules.cms.model.front;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sunday on 2017/5/3.
 */
@Data
public class SxrbAppcmsCategoryMenuVO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String parentId;
    private String name;
    private List<SxrbAppcmsCategoryMenuVO> child;
}
