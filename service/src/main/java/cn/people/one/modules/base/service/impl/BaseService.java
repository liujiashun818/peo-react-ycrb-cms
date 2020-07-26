package cn.people.one.modules.base.service.impl;

import cn.people.one.core.base.api.QueryResultVO;
import cn.people.one.core.base.dao.BaseDao;
import cn.people.one.modules.base.entity.BaseModel;
import cn.people.one.modules.base.service.IBaseService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.*;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author YX
 * @date 2019-02-28
 * @comment
 */
@Service
@Transactional( readOnly = true)
public abstract class BaseService<T extends BaseModel> implements IBaseService<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseService.class);
    protected static final Long DEFAULT_PAGE_NUMBER = 10L;
    protected Class<T> tClass = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    @Autowired
    protected BaseDao dao;

    public BaseService() {
    }
    @Override
    public int count(String tableName, Condition cnd) {
        return this.dao.count(tableName, cnd);
    }

    public T fetch(Long id) {
        return (T) this.dao.fetch(this.tClass, Cnd.where("id", "=", id));
    }

    public T fetch(String name) {
        return (T) this.dao.fetch(this.tClass, name);
    }

    @Override
    @Transactional
    public int delete(String name) {
        return this.dao.delete(this.tClass, name);
    }

    @Override
    @Transactional
    public Object save(T t) {
        if (t.getId()==null) {
            t.init();
            return this.dao.insert(t);
        } else {
            t.setUpdateAt(System.currentTimeMillis());
            return this.dao.updateIgnoreNull(t);
        }
    }

    @Override
    @Transactional
    public T insert(T t) {
        return (T) this.dao.insert(t);
    }

    @Transactional
    public int update(T t, String fieldName) {
        return Daos.ext(this.dao, FieldFilter.create(this.tClass, (String)null, fieldName, true)).update(t);
    }

    @Transactional
    public int updateIgnoreNull(T t) {
        return this.dao.updateIgnoreNull(t);
    }

    @Transactional
    public int delete(Long id) {
        return this.dao.delete(this.tClass, (long)id);
    }

    public int getMaxId() {
        return this.dao.getMaxId(this.tClass);
    }

    public int getMinId() {
        String tableName = this.dao.getEntity(this.tClass).getTableName();
        Sql sql = Sqls.create("SELECT MIN(id) FROM " + tableName);
        sql.setCallback((conn, rs, sl) -> {
            int minId = 0;
            if (null != rs && rs.next()) {
                minId = rs.getInt(1);
            }

            return minId;
        });
        return this.dao.execute(sql).getInt();
    }

    @Transactional
    public void delete(Long[] ids) {
        this.dao.clear(this.tClass, Cnd.where("id", "in", ids));
    }

    @Override
    @Transactional
    public int vDelete(Long id) {
        return this.dao.update(this.tClass, Chain.make("del_flag", 3), Cnd.where("id", "=", id));
    }

    @Transactional
    public int vDelete(Long[] ids) {
        return this.dao.update(this.tClass, Chain.make("del_flag", true), Cnd.where("id", "in", ids));
    }

    public List<T> query(String fieldName, Condition cnd) {
        return Daos.ext(this.dao, FieldFilter.create(this.tClass, fieldName)).query(this.tClass, cnd);
    }

    public int count(Sql sql) {
        sql.setCallback((conn, rs, s) -> {
            int count = 0;
            if (null != rs && rs.next()) {
                count = rs.getInt(1);
            }

            return count;
        });
        this.dao.execute(sql);
        return sql.getInt();
    }

    public List<T> list(Sql sql) {
        sql.setCallback(Sqls.callback.records());
        this.dao.execute(sql);
        return sql.getList(this.tClass);
    }

    public QueryResultVO<T> listPage(Integer pageNumber, Integer pageSize, Condition cnd) {
        return this.listPage(this.tClass, this.setPageNumber(pageNumber), this.setPageSize(pageSize), cnd);
    }

    public QueryResultVO<T> listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd) {
        Pager pager = this.dao.createPager(this.setPageNumber(pageNumber), this.setPageSize(pageSize));
        List<T> list = this.dao.query(clazz, cnd, pager);
        pager.setRecordCount(this.dao.count(clazz, cnd));
        return new QueryResultVO<T>(list, pager);
    }

    public QueryResultVO<T> listPage(Integer pageNumber, Integer pageSize, Condition cnd, String fieldName) {
        return this.listPage(this.tClass, this.setPageNumber(pageNumber), this.setPageSize(pageSize), cnd, fieldName);
    }

    public QueryResultVO<T> listPage(Class clazz, Integer pageNumber, Integer pageSize, Condition cnd, String fieldName) {
        Pager pager = this.dao.createPager(this.setPageNumber(pageNumber), this.setPageSize(pageSize));
        List list;
        if (StringUtils.isNotBlank(fieldName)) {
            list = Daos.ext(this.dao, FieldFilter.create(clazz, fieldName)).query(clazz, cnd, pager);
        } else {
            list = this.dao.query(clazz, cnd, pager);
        }

        pager.setRecordCount(this.dao.count(clazz, cnd));
        return new QueryResultVO<T>(list, pager);
    }

    public QueryResultVO<T> listPage(Integer pageNumber, Integer pageSize, Sql sql) {
        Pager pager = this.dao.createPager(this.setPageNumber(pageNumber), this.setPageSize(pageSize));
        pager.setRecordCount((int)Daos.queryCount(this.dao, sql.toString()));
        sql.setPager(pager);
        sql.setCallback(Sqls.callback.entities());
        sql.setEntity(this.dao.getEntity(this.tClass));
        this.dao.execute(sql);
        return new QueryResultVO<T>(sql.getList(this.tClass), pager);
    }

    public List<T> findByIds(String ids) {
        List<T> list = Lists.newArrayList();
        Arrays.stream(ids.split(",")).forEach((id) -> {
            T t = this.fetch(Long.parseLong(id));
            if (Objects.nonNull(t)) {
                list.add(t);
            }

        });
        return list;
    }

    public Cnd getDelFlag(Integer delFlag) {
        return null != delFlag ? Cnd.where("del_flag", " = ", delFlag) : Cnd.where("del_flag", "<", 3);
    }

    @Transactional
    public void batchUpdate(List<T> list) {
        if (!Lang.isEmpty(list)) {
            list.stream().forEach((t) -> {
                this.updateIgnoreNull(t);
            });
        }

    }

    private Integer setPageSize(Integer pageSize) {
        return null != pageSize && pageSize != 0 ? pageSize : 30;
    }

    private Integer setPageNumber(Integer pageNumber) {
        return null != pageNumber && pageNumber != 0 ? pageNumber : 1;
    }
}
