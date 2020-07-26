import { queryCatgs } from '../services/category';
import {
    queryLive, createLive, updateLive, createSpeak,
    getLiveUsers, getLiveUser, removeLiveUser, createLiveUser, updateLiveUser
} from '../services/live';
import { routerPath } from '../../../constants'
import { Jt } from '../../../utils'

export default {
    namespace: 'live',

    state: {
        //是否正在保存
        saveLoading:false,
        //栏目列表
        catgs: [],
        // 基本信息
        base: {},
        // 主持人列表
        hosts: [],
        // 嘉宾列表
        guests: []
    },

    subscriptions: {
        setup({dispatch, history}) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                const pathname = location.pathname;
                if(pathname === routerPath.LIVE_EDIT || pathname === routerPath.LIVE_ROOM) {
                    // 获取直播基本信息
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:live',payload:{location}}
                    }});
                }
                if(location.pathname === routerPath.LIVE_EDIT) {
                    // 获取主持人列表
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:hosts'}
                    }});
                    // 获取嘉宾列表
                    dispatch({type: 'app/firstLogined', payload: {
                        fn:dispatch,
                        data:{type: 'effect:query:guests'}
                    }});
                }
            });
        }
    },

    effects: {
        *'effect:query:hosts'({payload}, {call, put}) {
            const {code, data} = yield call(getLiveUsers, {role: 'host'})
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        hosts: data
                    }
                })
            }
        },
        *'effect:query:guests'({payload}, {call, put}) {
            const {code, data} = yield call(getLiveUsers, {role: 'guest'})
            if(code === 0) {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        guests: data
                    }
                })
            }
        },
        *'effect:create:liveUser'({payload}, {call, put}) {
            const user = payload.user;
            const {code, data} = yield call(createLiveUser, user);
            if(code === 0) {
                if(user.role === 'host') {
                    yield put({type: 'effect:query:hosts'});
                }
                else {
                    yield put({type: 'effect:query:guests'});
                }
                payload.success && payload.success();
            }
        },
        *'effect:update:liveUser'({payload}, {call, put}) {
            const user = payload.user;
            const {code, data} = yield call(updateLiveUser, user);
            if(code === 0) {
                if(user.role === 'host') {
                    yield put({type: 'effect:query:hosts'});
                }
                else {
                    yield put({type: 'effect:query:guests'});
                }
                payload.success && payload.success();
            }
        },
        *'effect:delete:liveUser'({ payload }, { call, put }) {
            const user = payload.user
            const {code, data} = yield call(removeLiveUser, user);
            if(code === 0) {
                if(user.role === 'host') {
                    yield put({type: 'effect:query:hosts'});
                }
                else {
                    yield put({type: 'effect:query:guests'});
                }
                payload.success && payload.success();
            }
        },
        *'effect:query:live'({payload}, {call, put, select}) {
            const res = yield call(queryCatgs);
            let catgs = [];
            let categoryId = yield select(({cms}) => cms.naq.categoryId);
            if(res.code === 0) {
                catgs = res.data;
                Jt.catg.format(catgs);
                if(!categoryId) {
                    categoryId = Jt.catg.getInitId(catgs);
                }
            }
            const publishDate = new Date().format('yyyy-MM-dd hh:mm:ss');
            const query = payload.location.query;
            if(query.action === 'edit') {
                const { code, data } = yield call(queryLive, { id: query.id });
                data.publishDate = data.publishDate || publishDate;
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload:{base: data || {}, catgs}
                    })
                }
            }
            else {
                yield put({
                    type: 'reducer:update',
                    payload: {
                        catgs,
                        base: {
                            categoryId: categoryId,
                            status: '1',
                            delFlag: '2',
                            showTitle: true,
                            block: '1',
                            viewType: 'banner',
                            liveType: '1',
                            publishDate
                        }
                    }
                });
            }
        },
        *'effect:create:live'({payload}, {call, put}) {
            const { code } = yield call(createLive, payload.live)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:update:live'({payload}, {call, put}) {
            const { code } = yield call(updateLive, payload.live)
            if(code === 0) {
                payload.success && payload.success()
            }
        },
        *'effect:create:speak'({payload}, {call, put}) {
            const {code} = yield call(createSpeak, payload.speak)
            if(code === 0) {
                payload.success && payload.success();
            }
        },
        *'effect:update:liveAttrs'({payload}, {call, put, select}) {
            const base = yield select(({live}) => live.base );
            yield put({
                type: 'reducer:update',
                payload: {
                    base: {...base, ...payload}
                }
            });
        }
    },

    reducers: {
        'reducer:update'(state, { payload }) {
            return { ...state, ...payload }
        }
    }
}
