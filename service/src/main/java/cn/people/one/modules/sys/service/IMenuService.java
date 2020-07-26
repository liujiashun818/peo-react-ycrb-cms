package cn.people.one.modules.sys.service;

import cn.people.one.modules.base.service.ITreeService;
import cn.people.one.modules.sys.model.Menu;
import cn.people.one.modules.sys.model.front.NavMenuVO;

import java.util.List;

/**
* 用户管理下菜单管理Service
* @author cuiyukun
*/
public interface IMenuService extends ITreeService<Menu> {

    List<NavMenuVO> getCurrentUserMenu(Long id,Long userId,Boolean filterView);

    int updateIgnoreNull(Menu menu);

    void batchUpdate(List<Menu> list);

    Menu queryCode(String code);

    List<NavMenuVO> getMenuTree(Long id, Long userId);

    List<NavMenuVO> getMenuTree(Long id, Boolean filterView,Long userId);

    int updateSort(Long[] ids , Integer[] sorts);

    void upadte(Menu menu);
}