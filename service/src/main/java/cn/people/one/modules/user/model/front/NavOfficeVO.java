package cn.people.one.modules.user.model.front;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * Created by cuiyukun on 2017/3/2.
 */
@Data
public class NavOfficeVO implements Serializable {
    private Long id;
    private String name;
    private String remark;
    private String slug;
    private Integer sort;
    private Integer delFlag;
    private Integer parentId;// 父级编号
    protected String parentIds; // 所有父级编号
    private List<NavOfficeVO> child;//子菜单
}

