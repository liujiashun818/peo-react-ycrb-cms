import { create, update, query,deleteItem,onOff, queryView, tree,clientTree} from '../services/floatMenu'
import {queryCatgs} from "../../cms/services/category";
import { parse } from 'qs'
import {Jt} from "../../../utils";

export default {
    namespace: 'floatMenu',
    state: {
        list: [],
        record:{},
        loading: false,
        submitLoading:false,
        visible: false,
        currentItem: {},
        showInput:false,
        clientTree:[],
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
            total: null
        }
    },
    subscriptions: {
        setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
            window.Mydispatch = dispatch;
            history.listen(location => {
                if (location.pathname === '/client/floatMenu') {
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'query',payload:location.query}
                        }});
                    dispatch({type: 'query:client:tree'});
                }

            })
        }
    },
    effects: {
        *query({ payload }, { call, put, select }){
            let pagination = yield select( ({ floatMenu }) => floatMenu.pagination )
            let params = Object.assign({
                'pageNumber': pagination.current,
                'pageSize': 10,
            }, payload)
            const data = yield call(query, parse(params))
            let list = [];
            if(data.data.list){
                list = data.data.list;
            }
            pagination.total = data.data.pager.recordCount
            yield put({
                type: 'updateState',
                payload: {
                    list: list,
                    pagination
                }
            })
        },
        *'query:client:tree'({ payload }, { call, put, select }){
            const {data}=yield call(clientTree);
            yield put({
                type:'reducer:query:client:success',
                payload:data
            })
        },
        *deleteItem({ payload }, { call, put, select }){
            const data = yield call(deleteItem,payload.id)
            if(data){
                if(!!payload.callback){
                    payload.callback()
                }
            }
        },
        *onOff({ payload }, { call, put, select }){
            const data = yield call(onOff,payload.id)
            if(data){
                if(!!payload.callback){
                    payload.callback()
                }
            }
        },
        *edit({ payload }, { call, put, select }){
            yield put({
                type: 'updateState',
                payload:{submitLoading:true}
            })
            let data = ''
            if(payload.actionType=='update'){
                data = yield call(update,parse(payload))
            }else if(payload.actionType=='add'){
                //默认禁用
                payload.delFlag = 1;
                data = yield call(create,parse(payload))
            }
            if(data.code==0){
                if(!!payload.callback){
                    payload.callback()
                }
                yield put({
                    type: 'updateState',
                    payload:{ submitLoading: false, visible: false }
                })

            }
        },
        *'pageChange'({ payload }, { call, put, select }){
            let params = Object.assign({
                'pageNumber': payload.current,
                'pageSize': 10,
            }, payload)
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
            /*yield put({
                type: 'updateState',
                payload: { pagination ,loading:true}
            });*/
            //const data = yield call(query, parse(params))

            /*yield put({
                type: 'querySuccess',
                payload: {list}
            });*/

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
            return { ...state, ...payload }
        },
        'reducer:pageChange'(state, { payload }){
            var pagination = {...payload}
            return {...state,pagination,loading:true}
        },
        'reducer:query:client:success'(state, {payload}) {
            const clientTree = Jt.tree.format(payload);
            return {...state, clientTree};
        }
    }

}
