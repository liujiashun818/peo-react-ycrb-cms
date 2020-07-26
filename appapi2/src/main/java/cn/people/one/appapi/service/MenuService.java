package cn.people.one.appapi.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import cn.people.one.appapi.converter.MenuConverter;
import cn.people.one.appapi.vo.MenuVO;
import cn.people.one.appapi.vo.ResultVO4;
import cn.people.one.modules.base.entity.BaseEntity;
import cn.people.one.core.util.text.StringUtils;
import cn.people.one.modules.client.constant.EClientMenuPosition;
import cn.people.one.modules.client.model.ClientMenu;
import cn.people.one.modules.client.service.IClientMenuService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by wilson on 2018-10-09.
 */
@Slf4j
@Service("menuServiceV2")
public class MenuService {

    @Autowired
    private IClientMenuService clientMenuService;

    public ResultVO4<MenuVO> list(Integer tabType) {
    	List<ClientMenu>  tops = clientMenuService.queryMenuByParentId(1L, BaseEntity.STATUS_ONLINE,tabType);
        if (null == tops || tops.size() < 1) {
            return ResultVO4.result(-1, "服务器未配置菜单");
        }
        List<ClientMenu> list = new ArrayList<>();
        for (ClientMenu top : tops) {
        		List<ClientMenu> chs = clientMenuService.queryByParentId(top.getId(),BaseEntity.STATUS_ONLINE);
        		if (chs != null && chs.size() > 0) {
                     list.addAll(chs);
     			}
        }

        List<MenuVO> menus = new ArrayList<>(list.size());
        fetchChildren(list, menus);
        return ResultVO4.success(null,menus, menus.size(), menus.size(), 1);
    }

    public ResultVO4<MenuVO> get(Long id) {
        ClientMenu ori = clientMenuService.fetch(id);
        if (ori == null) {
            return ResultVO4.result(-1, "ID错误, 未找到对应的菜单");
        }

        List<ClientMenu> cmenus = clientMenuService.queryByParentId(ori.getId(),BaseEntity.STATUS_ONLINE);
        if (cmenus == null || cmenus.size() < 1) {
            return ResultVO4.success(MenuConverter.toVO(ori), Collections.emptyList(), 0, 0, 0);
        }

        List<MenuVO> menuVOs = new ArrayList<>(cmenus.size());
        fetchChildren(cmenus, menuVOs);
        return ResultVO4.success(MenuConverter.toVO(ori), menuVOs, menuVOs.size(), menuVOs.size(), 1);
    }

    private void fetchChildren(List<ClientMenu> sources, List<MenuVO> targets) {
        if (sources == null || sources.size() < 1) {
            return;
        }

        if (targets == null) {
            return;
        }

        for (ClientMenu menu : sources) {
            //跳过根节点和分组节点
            if (menu.getId() == 1 || menu.getParentId() == 1) {
                continue;
            }

            MenuVO menuVO = MenuConverter.toVO(menu);
            if (StringUtils.isBlank(menu.getLinks())) {
                menuVO.setLinks(null);
            } else {
                try {
                    menuVO.setLinks(JSON.parseObject(menu.getLinks(), Map.class));
                } catch (Exception e) {
                    log.error("Menu id: " + menu.getId() + " links result");
                }
            }

            if (menu.getParentId() > 1) {
                ClientMenu parent = clientMenuService.fetch(menu.getParentId());
                if (parent != null &&
                        (EClientMenuPosition.NORMAL.value().equalsIgnoreCase(parent.getPosition())
                                || EClientMenuPosition.ONE_TOP.value().equalsIgnoreCase(parent.getPosition()))) {
                    menuVO.setGroupName(parent.getName());
                    menuVO.setGroupCode(parent.getSlug());
                }
            }

            List<ClientMenu> cmenus = clientMenuService.queryByParentId(menu.getId(),BaseEntity.STATUS_ONLINE);
            if (cmenus != null && cmenus.size() > 0) {
                List<MenuVO> menus = new ArrayList<>(cmenus.size());
                fetchChildren(cmenus, menus);
                menuVO.setChildren(menus);
            }

            targets.add(menuVO);
        }
    }

}
