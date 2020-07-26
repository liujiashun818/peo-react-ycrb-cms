import {login, userInfo, logout, getMenu, getDict, queryCfg, getMediaList,currentPermission} from '../services/app'
import { routerRedux,getCurrentLocation} from 'dva/router'
import { PAGE_SIZE, DEV_UNLOGIN } from '../constants'
import {parse} from 'qs'
import {Jt} from '../utils';
import {message} from  'antd'
let logined = DEV_UNLOGIN;
let firstLoginedFns = new Set();
const doFirstLogined = (fns) => {
    for (let item of fns) {
        item.fn(item.data)
    }
}

export default {
    namespace: 'app',
    state: {
        expired: false,
        login: DEV_UNLOGIN,
        loading: false,
        location:{},
        user: {
            name: 'admin'
        },
        cfg: {},
        currentPermission:[],
        loginButtonLoading: false,
        menuPopoverVisible: false,
        siderFold: localStorage.getItem('antdAdminSiderFold') === 'true',
        darkTheme: localStorage.getItem('antdAdminDarkTheme') !== 'false',
        isNavbar: document.body.clientWidth < 769,
        menu:[],
        mediaList:[],
        mediaListPagination: {
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            current: 1,
            total: null,
            pageSize: PAGE_SIZE
        }
    },
    subscriptions: {
        setup ({history,dispatch},onError) {
            window.Mydispatch = dispatch;
            if(!DEV_UNLOGIN){
                dispatch({type: 'queryUser'}) //先获取当前登录用户的数据，如果获取成功则自动登录
            }
            window.onresize = function () {
                dispatch({type: 'changeNavbar'})
            }
            dispatch({type: 'firstLogined', payload: {
                fn:dispatch,
                data:{type: 'queryMenu'}
            }});
            dispatch({type: 'firstLogined', payload: {
                fn: dispatch,
                data: {type: 'effect:query:cfg'}
            }});
            history.listen(location => {
                dispatch({type:'saveLocation',payload:{
                    location
                }})
            });
        }
    },
    effects: {
        *login ({payload}, {call, put, select}) {
            yield put({type: 'showLoginButtonLoading'})


            try {
                const data = yield call(login, parse(payload.data))

                sessionStorage.userInfo = JSON.stringify(data)

                // const expired = yield select(({app}) => app.expired);
                // if(expired) {
                //     window.location.reload();
                //     return;
                // }
                if (data && (data.success || data.code === 0) ) {
                    yield put({
                        type: 'loginSuccess',
                        payload: {
                            user: {
                                name: data.data && data.data.username
                            },
                            currentPermission: data.data && data.data.permissions || []
                        }
                    });
                    logined = true;
                    doFirstLogined(firstLoginedFns)
                }
                else if(data && data.code === 13){
                    yield put({
                        type: 'loginFail',

                    });
                    message.error(data.msg)
                }
                else {
                    yield put({
                        type: 'loginFail'
                    });

                    if(payload.data.callback){
                        payload.data.callback()
                    }

                }
            }
            catch (e) {

                yield put({
                    type: 'loginFail'
                });
                message.error(e.message)

            }

        },
        *queryUser ({payload}, {call, put}) {
            yield put({type: 'showLoading'});
            logined = false;
            try {
                //const data = sessionStorage.userInfo?JSON.parse(sessionStorage.userInfo):yield call(userInfo, parse(payload));

                const data = yield call(userInfo, parse(payload))
                if (data && data.code === 0) {
                    yield put({
                        type: 'loginSuccess',
                        payload: {
                            expired: false,
                            user: {
                                name:  data.data && data.data.username
                            },
                            currentPermission: data.data && data.data.permissions || []
                        }
                    });
                    logined = true;
                    doFirstLogined(firstLoginedFns)
                }
                else if(data && data.code === 13){
                    yield put({
                        type: 'logoutSuccess',
                        payload: {
                            expired: false
                        }
                    });
                    yield put({type: 'hideLoading'});
                }
                else {
                    yield put({
                        type: 'logoutSuccess',
                        payload: {
                            expired: true
                        }
                    });
                    yield put({type: 'hideLoading'});
                }
            }catch(error) {
                console.log("err",error)
                yield put({
                    type: 'logoutSuccess',
                    payload: {
                        expired: true
                    }
                })
                yield put({type: 'hideLoading'})
            }
        },
        *logout ({payload}, {call, put}) {
            try {
                const data = yield call(logout, parse(payload))
                sessionStorage.removeItem("userInfo")
                sessionStorage.removeItem("menuData")
                if (data.code === 0) {
                    if(data.data && data.data.direct && data.data.direct != '') {
                        window.location.href = data.data.direct;
                    }
                    else {
                        yield put({
                            type: 'logoutSuccess'
                        });
                        logined = false;
                    }
                }
            }catch(error) {
                yield put({
                    type: 'logoutSuccess'
                })
                logined = false;
                console.log('退出失败')
            }
        },
        //请求api返回用户未登录状态的情况下，调用此函数
        *onLogout ({payload}, {call, put}) {
            debugger;
            yield put({
                type: 'logoutSuccess',
                payload: {
                    expired: true
                }
            })
            logined = false;
        },
        *firstLogined ({payload}, {call, put, select}) {
            if(logined){
                payload.fn(payload.data)
            }else{
                firstLoginedFns.add({...payload})
            }
        },
        *queryMenu ({payload}, {call, put}) {
            try {
                let menuData;
                if(!!sessionStorage.menuData){
                    menuData = JSON.parse(sessionStorage.menuData)
                }else{
                    menuData = yield call(getMenu, parse(payload))
                    sessionStorage.menuData = JSON.stringify(menuData)
                }
                let { code, data } = menuData
                if (code === 0) {
                    // 去除顶级菜单
                    if(data.length === 1 && data[0].parentId === 0) {
                        data = data[0].child || []
                    }
                    yield put({
                        type: 'updateMenu',
                        payload: {
                            menu:data
                        }
                    })
                }
            }catch(error) {
                console.log('获取导航菜单失败');
            }
        },
        *switchSider ({payload}, {put}) {
            yield put({
                type: 'handleSwitchSider'
            })
        },
        *changeTheme ({payload}, {put}) {
            yield put({
                type: 'handleChangeTheme'
            })
        },
        *changeNavbar ({payload}, {put}) {
            if (document.body.clientWidth < 769) {
                yield put({type: 'showNavbar'})
            } else {
                yield put({type: 'hideNavbar'})
            }
        },
        *switchMenuPopver ({payload}, {put}) {
            yield put({
                type: 'handleSwitchMenuPopver'
            })
        },
        *queryMediaList ({ payload }, { call, put }) {  //获取媒体列表
            const res = yield call(getMediaList, parse(payload))
            if (res && res.code == 0) {
                const data = res.data;
                yield put({
                    type: 'queryMediaListSuccess',
                    payload: {
                        mediaList: data.list,
                        mediaListPagination: {
                            total: data.pager.recordCount,
                            current: data.pager.pageNumber,
                            pageSize:data.pager.pageSize
                        }
                    }
                });
            }
        },
        *'effect:query:cfg' ({payload}, {call, put, select}) {
            const cfg = yield select(({app}) => app.cfg);
            if(Jt.object.isEmpty(cfg)) {
                const {code, data} = yield call(queryCfg);
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload: {cfg: data}
                    });
                }
            }
        }
    },
    reducers: {
        'reducer:update' (state, {payload}) {
            return {...state, ...payload};
        },
        loginSuccess (state, action) {
            return {
                ...state,
                ...action.payload,
                login: true,
                loginButtonLoading: false
            };
        },
        logoutSuccess (state, action) {
            return {
                ...state,
                ...action.payload,
                login: false,
                loading: false
            }
        },
        loginFail (state) {
            return {
                ...state,
                login: false,
                loginButtonLoading: false
            }
        },
        showLoginButtonLoading (state) {
            return {
                ...state,
                loginButtonLoading: true
            }
        },
        showLoading (state) {
            return {
                ...state,
                loading: true
            }
        },
        hideLoading (state) {
            return {
                ...state,
                loading: false
            }
        },
        handleSwitchSider (state) {
            localStorage.setItem('antdAdminSiderFold', !state.siderFold)
            return {
                ...state,
                siderFold: !state.siderFold
            }
        },
        handleChangeTheme (state) {
            localStorage.setItem('antdAdminDarkTheme', !state.darkTheme)
            return {
                ...state,
                darkTheme: !state.darkTheme
            }
        },
        showNavbar (state) {
            return {
                ...state,
                isNavbar: true
            }
        },
        hideNavbar (state) {
            return {
                ...state,
                isNavbar: false
            }
        },
        handleSwitchMenuPopver (state) {
            return {
                ...state,
                menuPopoverVisible: !state.menuPopoverVisible
            }
        },
        updateMenu (state, action) {
            return {
                ...state,
                ...action.payload
            }
        },
        queryMediaListSuccess (state, { payload }) {
            return {
                ...state,
                ...payload
            }
        },
        saveLocation(state,{payload}){
            return {
                ...state,
                ...payload
            }
        }
    }
}
