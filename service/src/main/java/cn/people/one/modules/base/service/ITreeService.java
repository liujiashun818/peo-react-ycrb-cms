package cn.people.one.modules.base.service;


import java.util.List;

/**
 * Created by lml on 2017/1/5.
 */
public interface ITreeService<T> extends IBaseService<T> {

    List<T> queryByParentId(Long parentId, Integer delFlag);

    List<T> queryByParentId(Boolean filterView, Long parentId);

    List<T> findByParentIdsLike(String parentIds);

    T changeOnlineStatus(Long id);

    int vDelete(Long id);

}
