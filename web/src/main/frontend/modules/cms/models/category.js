import { getDict } from '../../../services/app'
import { queryOffices } from '../../users/services/users'
import { createCatg, removeCatg, updateCatg, updateCatgs, queryCatgs, queryCatg, onOffCatg, queryCatgModels} from '../services/category'
import { parse } from 'qs'
import { Jt, Immutable } from '../../../utils'
import { routerPath } from '../../../constants'

export default {
    namespace: 'category',

    state: {
        // 组织机构列表
        offices: [],
        // 栏目模型
        catgModels: [],
        // 栏目展示类型
        catgViews: [],
        // 栏目列表
        catgs: [],
        // 当前栏目
        curCatg: {},
        // 是否正在加载中
        loading: false
    },

    subscriptions: {
        setup({ dispatch, history }) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                const pathname = location.pathname;
                if(pathname === routerPath.CATEGORY_LIST) {
                    // 获取栏目模型
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catgModels',payload:{ type: 'cms_model'}}
                    }});
                    // 获取栏目列表
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catgs',payload:{ location } }
                    }});
                }
                else if(pathname === routerPath.CATEGORY_EDIT) {
                    // 获取组织详情
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catg',payload:{ location } }
                    }});
                    // 获取组织机构
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:offices',payload:{ location } }
                    }});
                    // 获取展示类型
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:catgViews',payload:{type: 'view_type'}}
                    }});
                }
            })
        }
    },

    effects: {
        *'effect:query:offices'({ payload }, { call, put }) {
            const { code, data=[] } = yield call(queryOffices)
            if(code === 0) {
                yield put ({
                    type: 'reducer:update',
                    payload: {
                        offices: Jt.tree.format(data)
                    }
                })
            }
        },
        *'effect:query:catgs'({ payload }, { call, put, select }) {
            const { pathname, query } = payload.location || {}
            let catgs = yield select( ({ category }) => category.catgs )
            if(pathname === routerPath.CATEGORY_LIST) {
                yield put({ type: 'reducer:update', payload: { loading: true } })
                catgs = []
            }
            if(Jt.array.isEmpty(catgs)) {
                const { code, data } = yield call(queryCatgs)
                if(code === 0) {
                    yield put({
                        type: 'reducer:update:catgs',
                        payload: data || []
                    })

                }
            }
        },
        *'effect:query:catg'({ payload }, { call, put, select}) {
            const query = payload.location.query
            const type = query.type || 'addCatg'
            if(query.type === 'addCatg') {
                yield put({
                    type: 'reducer:update',
                    payload:{curCatg: {}}
                })
            }
            else if(query.type === 'updateCatg') {
                const { code, data={} } = yield call(queryCatg, { id: query.id })
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload:{curCatg: data}
                    })
                }
            }
            else if(type === 'addSubCatg') {
                yield put({
                    type: 'reducer:update',
                    payload: {curCatg: {parentId: query.id}}
                })
            }
        },
        *'effect:query:catgModels'({ payload }, { call, put }) {
            const {code, data} = yield call(queryCatgModels)
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: { catgModels: data }
                });
            }
        },
        *'effect:query:catgViews'({ payload }, { call, put }) {
            const data = yield call(getDict, parse(payload))
            if(!Jt.array.isEmpty(data)) {
                yield put({
                    type: 'reducer:update',
                    payload: { catgViews: data }
                })
            }
        },
        *'effect:delete:catg'({ payload }, { call, put }) {
            const { code } = yield call(removeCatg, payload.catg)
            if(code=== 0) {
                yield put({
                    type: 'effect:query:catgs',
                    payload
                })
            }
        },
        *'effect:update:catg'({ payload }, { call, put }) {
            const { code } = yield call(updateCatg, payload.catg)
            if(code === 0) {
                if(payload.success) {
                    payload.success()
                }
                else {
                    yield put({
                        type: 'effect:query:catgs',
                        payload
                    })
                }
            }
        },
        *'effect:update:catgs'({ payload }, { call, put }) {
            const { code } = yield call(updateCatgs, payload.catgs)
            if(code === 0) {
                yield put({
                    type: 'effect:query:catgs',
                    payload
                })
            }
        },
        *'effect:create:catg'({ payload }, { call, put }) {
            const { code } = yield call(createCatg, payload.catg)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:onOff:catg'({ payload }, { call, put }) {
            const { code } = yield call(onOffCatg, payload.catg)
            if(code === 0) {
                yield put({
                    type: 'effect:query:catgs',
                    payload
                })
            }
        }
    },

    reducers: {
        'reducer:update'(state, { payload }) {
            return { ...state, ...payload }
        },
        'reducer:update:catgs'(state, { payload }) {
            let catgs = payload || []
            function formatCatgs(data = []) {
                for(let i = 0, len = data.length; i < len; i++) {
                    data[i].key = data[i].value = data[i].id + ''
                    data[i].label = data[i].name
                    if(data[i].child) {
                        data[i].children = data[i].child
                        delete data[i].child
                        formatCatgs(data[i].children)
                    }
                }
            }
            if(catgs.length > 0) {
                formatCatgs(catgs)
            }
            return { ...state, catgs, loading: false }
        }
    }
}
