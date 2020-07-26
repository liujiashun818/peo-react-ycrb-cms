import {routerPath} from '../../../constants';
import {querySend} from  '../services/analysis'

export default {
    namespace: 'anls_sending',

    state: {
        loading: false,
        query: {
            beginTime:'',
            endTime:'',
            userName:''
        },
        list: [],
        pagination: false
    },

    subscriptions: {
        setup({dispatch, history}) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                const {pathname, query} = location;
                if(pathname === routerPath.ANLS_SENDING) {
                    dispatch({
                        type: 'app/firstLogined',
                        payload: {
                            fn: dispatch,
                            data: {
                                type: 'query',
                                payload: query
                            }
                        }
                    });
                }
            });
        }
    },

    effects: {
        *query({payload}, {call, put}) {
            yield put({ type: 'showLoading' })
            const data = yield call(querySend,payload);
            yield put({
                type: 'querySuccess',
                payload: {
                    list: data.data?data.data:[],
                      ...payload
                }
            });
        },

    },

    reducers: {
        querySuccess (state, action) {
            return { ...state, ...action.payload, loading: false }
        },
        showLoading (state) {
            return { ...state, loading: true }
        },
    }
};
