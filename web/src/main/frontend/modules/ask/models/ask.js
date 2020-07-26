import {query} from "../services/askServices";
import {parse} from "qs";

export default {
    namespace: 'ask',
    state:{
        list: [],
        loading: false,
        currentItem: {},
        modalVisible: false,
        searchForm:{

        },
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            current: 1,
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
                            data:{type: 'query',payload:{pageSize:15,current:1}}
                        }});
                }
            });
        }
    },


    effects: {
        *query ({ payload }, { call, put }) {
            yield put({ type: 'showLoading' })
            const data = yield call(query, parse(payload))
            if (data) {
                yield put({
                    type: 'querySuccess',
                    payload: {
                        list: data.data,
                        ...payload
                    }
                })
            }
        },
    },

    reducers: {
        showLoading (state) {
            return { ...state, loading: true }
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
