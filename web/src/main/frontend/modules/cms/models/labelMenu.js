import { create, update, query, queryView, tree, categoryTree,deleteItem} from '../services/labelMenu'
import {queryCatgs} from "../../cms/services/category";
import { parse } from 'qs'
import {Jt} from "../../../utils";

export default {
    namespace: 'labelMenu',
    state: {
        list: [],
        record:{},
        loading: false,
        visible: false,
        currentItem: {},
        showInput:false,
        // 栏目列表
        catgs: [],
        // 初始化栏目ID
        catgId: '',
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            current: 1,
            pageSize: 10,
            total: 20
        }
    },
    subscriptions: {
        setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
            window.Mydispatch = dispatch;
            history.listen(location => {
                if (location.pathname === '/cms/labelMenu') {
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'query',payload:location.query}
                        }});
                }
            })
        }
    },
    effects: {
        *query({ payload }, { call, put, select }){
            let pagination = yield select( ({ labelMenu }) => labelMenu.pagination )
            let params = Object.assign({
                'pageNumber': pagination.current,
                'pageSize': 10,
            }, payload)
            yield put({
                type: 'updateState',
                payload: {
                    loading:true
                }
            })
            const data = yield call(query, parse(params))
            let list =[]
            if(data.data.list){
                list = data.data.list;
            }
            pagination.total = data.data.pager.recordCount
            yield put({
                type: 'updateState',
                payload: {
                    list: list,
                    pagination,
                    loading:false
                }
            })
        },
        *deleteItem({ payload }, { call, put, select }){
            const data = yield call(deleteItem,payload.id)
            if(data.code ==0){
                if(!!payload.callback){
                    payload.callback()
                }
            }
        },
        *edit({ payload }, { call, put, select }){
            let data = ''
            if(payload.actionType=='update'){
                data = yield call(update,parse(payload))
            }else if(payload.actionType=='add'){
                data = yield call(create,parse(payload))
            }
            if(data.code==0){
                if(!!payload.callback){
                    payload.callback()
                }
                yield put({
                    type: 'updateState',
                    payload:{ visible: false }
                })

            }
        },
        *'pageChange'({ payload }, { call, put, select }){
            // let params = Object.assign({
            //     'pageNumber': payload.current,
            //     'pageSize': 10,
            // }, payload)
            let params = {
                'pageNumber': payload.current,
                'pageSize': 10,
            }

            const data = yield call(query, parse(params))
            if(data.data.list){
                payload.total = data.data.pager.recordCount
                const pagination = payload
                yield put({
                    type: 'updateState',
                    payload: {
                        list: data.data.list,
                        pagination
                    }
                })
            }
        },
        *'effect:query:catgs'({payload}, {call, put, select}) {
            const {code, data} = yield call(queryCatgs);
            if(code === 0) {
                yield put({
                    type: 'reducer:query:catgs:success',
                    payload: data
                });
            }
        },
    },

    reducers: {
        showLoading (state) {
            return { ...state, loading: true }
        },
        querySuccess (state, { payload }) {
            const list = payload.list;
            return ({ ...state, list, loading: false })
        },
        'updateState' (state,{payload}) {
            return { ...state, ...payload}
        },
        'reducer:pageChange'(state, { payload }){
            var pagination = {...payload}
            return {...state,pagination,loading:true}
        },
        'reducer:query:catgs:success'(state, {payload}) {
            const catgs = Jt.tree.format(payload);
            const catgId = Jt.catg.getInitId(catgs);
            return {...state, catgs, catgId};
        }
    }

}
