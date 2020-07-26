package cn.people.one.modules.client.service;

import cn.people.one.modules.base.service.ITreeService;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.model.front.ClientMenuVO;

import java.util.List;

/**
 * Created by lml on 17-2-14.
 */
public interface IClientMenuService extends ITreeService<ClientMenu> {

    List<ClientMenu> getMenuList();

    List<ClientMenuVO> getTree(Long id);

    List<ClientMenuVO> getTreeView(Long id, Integer delFlag);

    /**
     * 保存
     * @param clientMenu
     */
    void saveClientMenu(ClientMenu clientMenu);

	List<ClientMenu> queryMenuByParentId(Long parentId, Integer delFlag, Integer tabType);
}
