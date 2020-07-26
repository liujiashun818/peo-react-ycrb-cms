package cn.people.one.modules.base.service;

import cn.people.one.core.base.api.QueryResultVO;
import org.nutz.dao.Cnd;
import org.nutz.dao.Condition;
import org.nutz.dao.QueryResult;
import org.nutz.dao.sql.Sql;

import java.util.List;

/**
 * @author YX
 * @date 2019-02-28
 * @comment
 */
public interface IBaseService<T> {
    int count(String var1, Condition var2);

    int count(Sql var1);

    T fetch(Long var1);

    T fetch(String var1);

    int delete(String var1);

    Object save(T var1);

    T insert(T var1);

    int update(T var1, String var2);

    int updateIgnoreNull(T var1);

    int delete(Long var1);

    int getMaxId();

    int getMinId();

    void delete(Long[] var1);

    int vDelete(Long var1);

    int vDelete(Long[] var1);

    List<T> query(String var1, Condition var2);

    List<T> list(Sql var1);

    QueryResultVO<T> listPage(Integer var1, Integer var2, Condition var3);

    QueryResultVO<T> listPage(Class var1, Integer var2, Integer var3, Condition var4);

    QueryResultVO<T> listPage(Integer var1, Integer var2, Condition var3, String var4);

    QueryResultVO<T> listPage(Class var1, Integer var2, Integer var3, Condition var4, String var5);

    QueryResultVO<T> listPage(Integer var1, Integer var2, Sql var3);

    List<T> findByIds(String var1);

    Cnd getDelFlag(Integer var1);

    void batchUpdate(List<T> var1);
}
