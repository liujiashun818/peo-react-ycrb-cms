import {query,queryTree,offline,online,settop,cancelTop,getDetail,queryRecommond,saveRecommond,save1,saveOrder,queryComment} from "../services/askServices";
import {parse} from "qs";

export default {
    namespace: 'askList',
    state:{
        list: [],
        loading: false,
        selectedRows:"",
        currentItem: {},
        domainList:[],
        typeList:[],
        recommendList:[],
        modalVisible: false,
        menus:[],
        query:{},
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            current: 1,
            pageSize:15,
            total: null
        }
    },

    subscriptions: {
        setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
            window.Mydispatch = dispatch;
            history.listen(location => {
                if (location.pathname === '/ask/asks') {
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'queryRecommond',payload:{}}
                        }});
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'queryTree',payload:{loadlist:true}}
                        }});
                }

                if (location.pathname === '/ask/edit') {
                    // console.log(location,'skjjshjah')
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'queryTree',payload:{}}
                        }});
                    dispatch({type: 'app/firstLogined', payload: {
                            fn:dispatch,
                            data:{type: 'getDetail',payload:location.query}
                        }});

                }
            });
        }
    },


    effects: {
        *query ({ payload }, { call, put }) {
            // console.log(pagload)
            yield put({ type: 'showLoading' })
            const data = yield call(query, parse(payload));
            delete payload.current;
            delete payload.pageSize;
            if (data) {
                yield put({
                    type: 'querySuccess',
                    payload: {
                        query:payload,
                        list: data.data.list,
                        pagination: {
                            showSizeChanger: true,
                            showQuickJumper: true,
                            showTotal: total => `共 ${total} 条`,
                            current: data.data.pager.pageNumber,
                            pageSize:data.data.pager.pageSize,
                            total: data.data.pager.recordCount
                        }


                    }
                })
            }
        },
        *queryTree ({ payload }, { call, put }) {

            yield put({ type: 'showLoading' })
            const data = yield call(queryTree, parse(payload))
            if (data) {
                yield put({
                    type: 'querySuccess',
                    payload: {
                        ...payload,
                        menus: data.data?data.data:[]

                    }
                })
               
              
                if(payload.loadlist){
                    let datalist = data.data?data.data:[];

                    if(datalist[0] && datalist[0].children &&  datalist[0].children.length){
                        yield put({
                            type: 'query',
                            payload: {
                                govId:datalist[0].children[0].id
        
                            }
                        })
                    }
                    
                }
            }
        },
        *getDetail({payload},{call,put}){
            let data = yield call(getDetail, payload.id)
            if (data) {
                yield put({
                    type: 'querySuccess',
                    payload: {
                        currentItem:data.data.detail,
                        domainList:data.data.domainList,
                        typeList:data.data.typeList,
                    }
                })
            }
        },
        *offline({payload},{call,put,select}){
            let data = yield call(offline, payload)
            if (data) {
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            }
        },

        *online({payload},{call,put,select}){
            let data = yield call(online, payload)
            if (data) {
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            }
        },
        *settop({payload},{call,put,select}){
            let data = yield call(settop, payload)
            if (data) {
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            }
        },
        *cancelTop({payload},{call,put,select}){
            let data = yield call(cancelTop, payload)
            if (data) {
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            }
        },
        *queryRecommond({payload},{call,put,select}){
            let data = yield call(queryRecommond, {});
            if (data) {
                yield put({
                    type: 'querySuccess',
                    payload: {
                        recommendList:data.data,

                    }
                })
            }
        },
        *recmmond({payload},{call,put,select}){
            let data = yield call(saveRecommond, payload);
            if (data.data) {
                if(payload.callback){
                    payload.callback(true)
                }
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            } else {
                if(payload.callback){
                    payload.callback(false)
                }
            }
        },
        *selectRow({payload},{call,put,select}){
            yield put({
                type: 'selectedRowsSuccess',
                payload: payload
            })
        },
        *save({payload},{call,put,select}){
            console.log("save p",payload)
            let data = yield call(save1, payload);
            if (data.code==0) {
                if(payload.callback){
                    payload.callback(true);
                }
                yield put({
                    type: 'querySuccess',
                    payload: {

                    }
                })
            } else {
                if(payload.callback){
                    payload.callback(false);
                }
            }
        },
        *saveOrder({payload},{call,put,select}){
            let data = yield call(saveOrder, payload);
            if (data.data) {
                if(payload.callback){
                    payload.callback(true)
                }
                const askList = yield select((state)=>state.askList )
                yield put({ type: 'query',payload: {...askList.query,current:askList.pagination.current,pageSize:askList.pagination.pageSize}})
            } else {
                if(payload.callback){
                    payload.callback(false)
                }
            }
        },

    },

    reducers: {
        showLoading (state) {
            return { ...state, loading: true }
        },
        selectedRowsSuccess(state,action){
            return { ...state, selectedRows: action.payload }
        },
        querySuccess (state, action) {
            return { ...state, ...action.payload, loading: false }
        },
        showModal (state, action) {
            return { ...state, ...action.payload, modalVisible: true }
        },
        hideModal (state) {
            return { ...state, modalVisible: false }
        },
        changeIcon (state,action){
            return {...state, ...action.payload}
        }

    }
}
