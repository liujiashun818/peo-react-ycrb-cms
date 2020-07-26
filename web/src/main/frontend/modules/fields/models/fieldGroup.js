import { queryFieldGroups, queryFieldGroup, saveFieldGroup, updateFieldGroup, deleteFieldGroup, queryCategoryModels, queryFields, batchDelete } from '../services/fields'
import { queryCatgs } from '../../cms/services/category';
import { parse } from 'qs';
import { Jt } from '../../../utils';
import { PAGE_SIZE, routerPath } from '../../../constants';

export default {
    namespace: 'fieldGroup',
    state: {
        list: [],
        curItem: {},
        catgsTree:[],
        catgModels:[],
        loading: false,
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: PAGE_SIZE,
            current: 1,
            total: null
        },
        fieldList:[],
        selectedRowKeys:[]
    },
    subscriptions: {
        setup ({ dispatch, history }) {
            window.Mydispatch = dispatch;
            //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
            history.listen(location => {
                if(location.pathname === routerPath.FIELD_GROUP_LIST) {
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:fieldGroups'}
                    }});
                }
                if(location.pathname === routerPath.FIELD_GROUP_EDIT) {
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:fieldGroup',payload: { location }}
                    }});

                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catgs',payload: { location }}
                    }});

                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catgModels',payload: { location }}
                    }});

                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:fields',payload: { location }}
                    }});
                }
            })
        }
    },
    effects: {
        *'effect:query:fieldGroups'({ payload }, { call, put, select }) {
            yield put({ type: 'reducer:showLoading' })
            let params = Object.assign({
                'pageNumber': 1,
                'pageSize': 10
            }, payload)
            const {pagination} = yield select(({fieldGroup}) => {return {pagination: fieldGroup.pagination};});
            const {code, data}  = yield call(queryFieldGroups,params);
            if (code === 0 && data && data.list) {
                yield put({
                    type: 'reducer:querySuccess',
                    payload: {
                        list: data.list || [],
                        pagination: {
                            ...pagination,
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        },
                        selectedRowKeys: []
                    }
                })
            }
        },
        *'effect:query:fieldGroup'({ payload }, { call, put, select}) {
            const query = payload.location.query;
            const type = query.type || 'create';
            if(type === 'create') {
                yield put({
                    type: 'reducer:update',
                    payload:{curItem: {}}
                });
            }
            else if(type === 'update') {
                const {code, data={}} = yield call(queryFieldGroup, {id: query.id});
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload:{curItem: data}
                    });
                }
            }
        },
        *'effect:save:fieldGroup'({ payload }, { call, put }) {
            const { code } = yield call(saveFieldGroup, payload.item);
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:update:fieldGroup'({ payload }, { call, put }) {
            const { code } = yield call(updateFieldGroup, payload.item)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:delete:fieldGroup'({ payload }, { call, put }) {
            const {code} = yield call(deleteFieldGroup, payload.item)
            if(code=== 0) {
                yield put({
                    type: 'effect:query:fieldGroups',
                    payload:payload.pager
                })
                payload.success && payload.success() //若在编辑页删除后返回列表页
            }
        },
        *'effect:delete:batchDelete'({ payload }, { call, put, select }){
            const data = yield call(batchDelete, payload)
            yield put({
                type: 'effect:query:fieldGroups',
            })
        },
        *'effect:query:catgs'({ payload }, { call, put, select }) {
            const { code, data } = yield call(queryCatgs);
            if(code === 0) {
                yield put({
                  type: 'reducer:update',
                  payload: {catgsTree: Jt.tree.format(data) || []}
                });
            }
        },
        *'effect:query:catgModels'({ payload }, { call, put, select }) {
            const { code, data } = yield call(queryCategoryModels);
            yield put({ type: 'reducer:showLoading' })
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {catgModels: data || []}
                })
            }
        },
        *'effect:query:fields'({ payload }, { call, put, select }) {
            const { code, data } = yield call(queryFields, {pageNumber: 1, pageSize: -1});
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {fieldList: data.list || []}
                });
            }
        },
    },
    reducers: {
        'reducer:showLoading'(state) {
            return { ...state, loading: true }
        },
        'reducer:querySuccess'(state, action) {
            return { ...state, ...action.payload, loading: false }
        },
        'reducer:update'(state, { payload }) {
            return { ...state, ...payload }
        }
    }
}
