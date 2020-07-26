package cn.people.one.modules.sys.model.front;

import cn.people.one.modules.sys.model.Menu;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lml on 17-2-8.
 */
@Data
public class MenuVO extends Menu implements Serializable {
    private List<MenuVO> child;//子菜单
}
