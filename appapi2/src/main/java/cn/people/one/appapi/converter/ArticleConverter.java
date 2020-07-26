package cn.people.one.appapi.converter;

import cn.people.one.appapi.vo.ArticleMediaVO;
import cn.people.one.appapi.vo.MediaResourceVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wilson on 2018-10-16.
 */
public class ArticleConverter {

    public static MediaResourceVO toResourceVO(cn.people.one.modules.cms.model.front.MediaResourceVO po) {
        if (po == null) {
            return null;
        }

        MediaResourceVO vo = new MediaResourceVO();
        BeanUtils.copyProperties(po, vo);
        return vo;
    }

    public static List<MediaResourceVO> toResourceVO(List<cn.people.one.modules.cms.model.front.MediaResourceVO> pos) {
        if (pos == null || pos.size() < 1) {
            return Collections.emptyList();
        }

        List<MediaResourceVO> vos = new ArrayList<>(pos.size());
        for (cn.people.one.modules.cms.model.front.MediaResourceVO po : pos) {
            MediaResourceVO vo = toResourceVO(po);
            if (vo != null) {
                vos.add(vo);
            }
        }
        return vos;
    }

    public static ArticleMediaVO toMediaVO(cn.people.one.modules.cms.model.front.ArticleMediaVO po) {
        if (po == null) {
            return null;
        }

        ArticleMediaVO vo = new ArticleMediaVO();
        BeanUtils.copyProperties(po, vo, "resources");
        vo.setResources(toResourceVO(po.getResources()));
        return vo;
    }

    public static List<ArticleMediaVO> toMediaVO(List<cn.people.one.modules.cms.model.front.ArticleMediaVO> pos) {
        if (pos == null || pos.size() < 1) {
            return Collections.emptyList();
        }

        List<ArticleMediaVO> vos = new ArrayList<>(pos.size());
        for (cn.people.one.modules.cms.model.front.ArticleMediaVO po : pos) {
            ArticleMediaVO vo = toMediaVO(po);
            if (vo != null) {
                vos.add(vo);
            }
        }
        return vos;
    }

}
