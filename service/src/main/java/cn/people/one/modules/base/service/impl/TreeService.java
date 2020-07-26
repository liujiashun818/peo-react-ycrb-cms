package cn.people.one.modules.base.service.impl;

import cn.people.one.modules.base.entity.BaseModel;
import cn.people.one.modules.base.entity.TreeEntity;
import cn.people.one.modules.base.service.ITreeService;
import cn.people.one.modules.cms.model.Category;
import lombok.extern.slf4j.Slf4j;
import org.nutz.lang.Strings;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service基类
 */
@Slf4j
@Transactional(readOnly = true)
public abstract class TreeService<T extends TreeEntity<T>> extends BaseService<T> implements ITreeService<T> {

    private static Long rootId = 1L;

    @Override
    @Transactional(readOnly = false)
    public T save(T entity) {
        Class<T> entityClass = tClass;

        // 如果没有设置父节点，则代表为跟节点，有则获取父节点实体
        if (null == entity.getParentId()) {
            entity.setParent(fetch(rootId));
        } else {
            T parent = super.fetch(entity.getParentId());
            if (null == parent) {
                // 如果传入的父节点不存在，则父节点为根节点
                entity.setParentId(rootId);
                entity.setParent(fetch(rootId));
            } else {
                entity.setParent(parent);
            }
        }

        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = entity.getParentIds();

        // 设置新的父节点串
        entity.setParentIds(Strings.sNull(entity.getParent().getParentIds()) + entity.getParent().getId() + ",");

        // 保存或更新实体
        super.save(entity);

        // 更新子节点 parentIds
        T o = null;
        try {
            o = entityClass.newInstance();
        } catch (Exception e) {
            log.error("初始化实例错误", e);
            return entity;
        }
        o.setParentIds("%," + entity.getId() + ",%");
        List<T> list = findByParentIdsLike(o.getParentIds());
        for (T e : list) {
            if (e.getParentIds() != null && oldParentIds != null) {
                e.setParentIds(e.getParentIds().replace(oldParentIds, entity.getParentIds()));
                save(e);
            }
        }
        return entity;
    }

    @Override
    public List<T> queryByParentId(Long parentId, Integer delFlag) {
        return dao.query(tClass, getDelFlag(delFlag).and("parent_id", "=", parentId).desc("sort").desc("create_at"));
    }

    public List<T> queryByParentId(Boolean filterView, Long parentId) {
        List<T> list;
        if (filterView != null) {
            list = dao.query(tClass, getDelFlag(null).and("parent_id", "=", parentId).and("is_show", "=", filterView).desc("sort"));
        } else {
            list = dao.query(tClass, getDelFlag(null).and("parent_id", "=", parentId).desc("sort"));
        }
        return list;
    }

    @Override
    public List<T> findByParentIdsLike(String parentIds) {
        return dao.query(tClass, getDelFlag(Category.STATUS_ONLINE).and("parent_ids", "like", "%" + parentIds + "%"));
    }

    /*
    @Override
    public QueryResult listPage(Integer pageNo, Integer pageSize) {
        //待补充条件
        return listPage(pageNo,pageSize,getBaseCnd().and("id",">",1).desc("sort"));
    }
    */

    /**
     * 上下线状态反转
     *
     * @param id
     */
    @Override
    @Transactional(readOnly = false)
    public T changeOnlineStatus(Long id) {
        T t = dao.fetch(tClass, id);
        if (null != t) {
            if (BaseModel.STATUS_ONLINE == t.getDelFlag()) {
                t.setDelFlag(BaseModel.STATUS_OFFLINE);
            } else {
                t.setDelFlag(BaseModel.STATUS_ONLINE);
            }
        }
        return t;
    }

    @Override
    @Transactional(readOnly = false)
    public int vDelete(Long id) {
        if (id == 1) {
            //根节点不允许删除
            return 0;
        } else {
            return super.vDelete(id);
        }
    }
}
