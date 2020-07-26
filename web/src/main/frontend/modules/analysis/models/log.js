import {queryAnlsLogs, queryUsers} from '../services/analysis';
import {routerPath} from '../../../constants';
import {Jt} from '../../../utils';
import moment from 'moment';

export default {
    namespace: 'anls_log',

    state: {
        loading: false,
        query: {},
        users: [],
        list: [],
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: 10,
            current: 1,
            total: null
        }
    },

    subscriptions: {
        setup({dispatch, history}) {
            window.Mydispatch = dispatch;
            dispatch({
                type: 'app/firstLogined',
                payload: {
                    fn: dispatch,
                    data: {
                        type: 'effect:query:users'
                    }
                }
            });
            history.listen(location => {
                const {pathname, query} = location;
                if(pathname === routerPath.ANLS_LOG) {
                    dispatch({
                        type: 'app/firstLogined',
                        payload: {
                            fn: dispatch,
                            data: {
                                type: 'effect:query:list',
                                payload: query
                            }
                        }
                    });
                }
            });
        }
    },

    effects: {
        *'effect:query:users'({payload}, {call, put, select}) {
            const {code, data=[]} = yield call(queryUsers);
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        users: data
                    }
                });
            }
        },
        *'effect:query:list'({payload}, {call, put, select}) {
            yield put({type: 'reducer:update', payload: {loading: true}});
            let {query, pagination} = yield select(({anls_log}) => anls_log);
            if(Jt.object.isEmpty(payload)) {
                const now = new Date();
                const year = now.getFullYear();
                let beginM = now.getMonth() + 1;
                let endM = beginM === 12 ? 1 : (beginM + 1);
                const endY = endM < beginM ? (year + 1) : year;
                beginM = beginM < 10 ? ('0' + beginM) : beginM;
                endM = endM < 10 ? ('0' + endM) : endM;
                const beginTime = year + '-' + beginM + '-01';
                const endTime = endY + '-' + endM + '-01';

                query = {
                    pageNumber: 1,
                    pageSize: pagination.pageSize,
                    isException: 0,
                    beginTime,
                    endTime
                };
            }
            else {
                query = {
                    pageNumber: 1,
                    pageSize: pagination.pageSize,
                    ...payload
                };
            }
            const {code, data} = yield call(queryAnlsLogs, query);
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        loading: false,
                        query,
                        list: data.list,
                        pagination: {
                            ...pagination,
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize: data.pager.pageSize
                        }
                    }
                });
            }
        }
    },

    reducers: {
        'reducer:update'(state, {payload}) {
            return {...state, ...payload}
        }
    }
};
