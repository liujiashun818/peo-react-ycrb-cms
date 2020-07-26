import {
    userList,
    getUserById,
    deleteItem,
    getRoleListAll,
    getOfficeTree,
    createUser,
    updateUser
} from '../services/users'
import {parse} from 'qs'

export default {
    namespace : 'user',
    state : {
        list: [],
        loading: false,
        currentItem: {},
        roleListAll: [],
        officeTree: [],
        pagination: {
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            current: 1,
            total: null
        }
    },

    subscriptions : {
        setup({dispatch, history}) {
            window.Mydispatch = dispatch;
            history.listen(location => {
                if (location.pathname === '/users/user') {
                    dispatch({
                        type: 'app/firstLogined',
                        payload: {
                            fn: dispatch,
                            data: {
                                type: 'userList',
                                payload: location.query
                            }
                        }
                    });
                }
                if (location.pathname === '/users/user/userEdit') {
                    dispatch({
                        type: 'app/firstLogined',
                        payload: {
                            fn: dispatch,
                            data: {
                                type: 'userListEdit',
                                payload: location.query
                            }
                        }
                    });
                }
            })
        }
    },

    effects : {
        *create({
            payload
        }, {call, put, select}) {
            const data = yield call(createUser, payload.data)
            if (data) {
                payload.callback()
            }
        },
        *update({
            payload
        }, {call, put, select}) {
            const data = yield call(updateUser, payload.data)
            if (data) {
                payload.callback()
            }
        },
        *userList({
            payload
        }, {call, put, select}) {
            let params = Object.assign({
                'pageNumber': 1,
                'pageSize': 10
            }, payload)
            let pagination = yield select(({user}) => user.pagination)
            const data = yield call(userList, parse(params))
            if (data) {
                pagination.total = data.data.pager.recordCount
                pagination.current = data.data.pager.pageNumber
                yield put({
                    type: 'querySuccess',
                    payload: {
                        list: data.data.list,
                        pagination
                    }
                })
            }
        },
        *userListEdit({
            payload
        }, {call, put, select}) {
            const roleListAll = yield call(getRoleListAll)
            const officeTree = yield call(getOfficeTree)
            if (payload.type == 'add') {
                payload.currentItem = {}
                payload.roleListAll = roleListAll.data
                payload.officeTree = officeTree.data
            } else if (payload.type == 'update') {
                const data = yield call(getUserById, payload.id)
                payload.currentItem = data.data
                payload.roleListAll = roleListAll.data
                payload.officeTree = officeTree.data
            }
            yield put({type: 'querySuccess', payload: payload})
        },
        *userDelete({
            payload
        }, {call, put, select}) {
            yield call(deleteItem, payload.id)
            yield put({type: 'userList', payload: payload.pager})
        }
    },

    reducers : {
        showLoading(state) {
            return {
                ...state,
                loading: true
            }
        },
        querySuccess(state, action) {
            return {
                ...state,
                ...action.payload,
                loading: false
            }
        },
        showModal(state, action) {
            return {
                ...state,
                ...action.payload,
                modalVisible: true
            }
        },
        hideModal(state) {
            return {
                ...state,
                modalVisible: false
            }
        }
    }

}
