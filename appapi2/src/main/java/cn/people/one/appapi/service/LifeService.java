package cn.people.one.appapi.service;

import cn.people.one.appapi.vo.LifeItemVO;
import cn.people.one.appapi.vo.LifeServiceVO;
import cn.people.one.core.util.mapper.BeanMapper;
import cn.people.one.modules.cms.model.Article;
import cn.people.one.modules.cms.model.Category;
import cn.people.one.modules.cms.model.front.ArticleVO;
import cn.people.one.modules.cms.model.type.ArticleType;
import cn.people.one.modules.cms.service.IArticleService;
import cn.people.one.modules.cms.service.ICategoryService;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

/**
 * @author YX
 * @date 2018/10/17
 * @comment
 */
@Slf4j
@Service
public class LifeService {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IArticleService articleService;

    /**
     * 查询生活服务
     *
     * @return
     */
    public LifeServiceVO getList() {
        Condition condition = Cnd.where(Category.Constant.SLUG, "=", Category.Constant.SLUG_VALUE)
                .and(Category.Constant.DEL_FLAG, "=", 0)
                .limit(1);
        List<Category> categoryList = categoryService.query("id", condition);
        if (categoryList == null || categoryList.size() == 0) {
            return null;
        }
        Long categoryId = categoryList.get(0).getId();
        ArticleVO focusVO = new ArticleVO();
        focusVO.setCategoryId(categoryId);
        focusVO.setBlock(1);
        focusVO.setDelFlag(0);
        focusVO.setType(ArticleType.LINK.value());
        ArticleVO listVO = new ArticleVO();
        listVO.setBlock(2);
        listVO.setDelFlag(0);
        listVO.setType(ArticleType.LINK.value());
        listVO.setCategoryId(categoryId);
        List<Article> focus = articleService.findArticlePageByArticleVO(1, 4, focusVO);
        List<Article> lists = articleService.findArticlePageByArticleVO(1, 4, listVO);
        List<LifeItemVO> lifeFocus = Lists.newArrayList();
        List<LifeItemVO> lifeLists = Lists.newArrayList();
        if (null != focus) {
            focus.forEach((Article article) -> {
                LifeItemVO item = BeanMapper.map(article, LifeItemVO.class);
                lifeFocus.add(item);
            });
        }
        if (null != lists) {
            lists.forEach((Article article) -> {
                LifeItemVO item = BeanMapper.map(article, LifeItemVO.class);
                lifeLists.add(item);
            });
        }
        LifeServiceVO lifeServiceVO = new LifeServiceVO();
        lifeServiceVO.setLists(lifeLists);
        lifeServiceVO.setFocus(lifeFocus);
        return lifeServiceVO;
    }
}
