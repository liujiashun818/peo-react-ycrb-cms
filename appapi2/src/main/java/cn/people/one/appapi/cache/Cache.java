package cn.people.one.appapi.cache;

import java.util.List;

/**
 * Created by wilson on 2018-11-05.
 */
public class Cache<T> {
    private CacheStatus status;
    private T object;
    private List<T> list;

    public Cache(CacheStatus status) {
        this.status = status;
    }

    public Cache(T object) {
        this.status = CacheStatus.CACHING;
        this.object = object;
    }

    public Cache(List<T> list) {
        this.status = CacheStatus.CACHING;
        this.list = list;
    }

    public CacheStatus getStatus() {
        return status;
    }

    public void setStatus(CacheStatus status) {
        this.status = status;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
