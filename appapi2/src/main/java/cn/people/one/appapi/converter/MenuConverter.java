package cn.people.one.appapi.converter;

import cn.people.one.appapi.vo.MenuVO;
import cn.people.one.modules.client.model.ClientMenu;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wilson on 2018-10-10.
 */
public class MenuConverter {

    public static MenuVO toVO(ClientMenu po) {
        if (po == null) {
            return null;
        }

        MenuVO vo = new MenuVO();
        BeanUtils.copyProperties(po, vo, "links", "groupCode", "groupName");
        return vo;
    }

    public static List<MenuVO> toVO(List<ClientMenu> pos) {
        if (pos == null || pos.size() < 1) {
            return Collections.emptyList();
        }

        List<MenuVO> vos = new ArrayList<>(pos.size());
        for (ClientMenu po : pos) {
            MenuVO vo = toVO(po);
            if (vo != null) {
                vos.add(vo);
            }
        }

        return vos;
    }

}
