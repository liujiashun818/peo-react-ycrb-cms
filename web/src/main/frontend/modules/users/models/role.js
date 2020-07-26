import {getDict} from '../../../services/app';
import {query as queryMenus} from '../../sys/services/menus';
import {queryOffices, queryRoles, queryRole, createRole, updateRole, deleteRole} from '../services/users'
import {routerPath} from '../../../constants'
import {Jt} from '../../../utils'
export default {
	namespace: 'role',

	state: {
		// 是否正在加载中
		loading: false,
		// 机构列表
		offices: [],
		// 数据范围
		dataScopes: [],
		// 角色列表
		roles: [],
		// 菜单列表
		menus: [],
		// 当前角色
		role: {}
	},

	subscriptions: {
		setup({dispatch, history}) {
            window.Mydispatch = dispatch;
		    history.listen(location => {
				// 获取数据范围
		          dispatch({type: 'app/firstLogined', payload: {
		            fn:dispatch,
		            data:{type: 'effect:query:dict',payload:{attrName: 'dataScopes', type: 'sys_data_scope'}}
		          }});
				if(location.pathname === routerPath.ROLE_EDIT) {
					// 获取机构列表
			          dispatch({type: 'app/firstLogined', payload: {
			            fn:dispatch,
			            data:{type: 'effect:query:offices'}
			          }});
					// 获取菜单列表
			          dispatch({type: 'app/firstLogined', payload: {
			            fn:dispatch,
			            data:{type: 'effect:query:menus'}
			          }});
					// 查询当前角色
			          dispatch({type: 'app/firstLogined', payload: {
			            fn:dispatch,
			            data:{type: 'effect:query:role', payload: {location}}
			          }});
				}
				else if(location.pathname === routerPath.ROLE_LIST) {
			          dispatch({type: 'app/firstLogined', payload: {
			            fn:dispatch,
			            data:{type: 'effect:query:roles'}
			          }});
				}
			});
		}
	},

	effects: {
		*'effect:query:dict'({payload}, {call, put}) {
			const data = yield call(getDict, {type: payload.type});
			if(data && data.length > 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						[`${payload.attrName}`]: data
					}
				});
			}
		},
		*'effect:query:menus'({payload}, {call, put}) {
			const {code, data} = yield call(queryMenus);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						menus: Jt.tree.format(data)
					}
				});
			}
		},
		*'effect:query:offices'({payload}, {call, put}) {
			const {code, data} = yield call(queryOffices);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						offices: Jt.tree.format(data)
					}
				});
			}
		},
		*'effect:query:role'({payload}, {call, put}) {
			const query = payload.location.query;
			if(query.action === 'update') {
				const {code, data} = yield call(queryRole, {id: query.id});
				if(code === 0) {
					yield put({
						type: 'reducer:update',
						payload: {role: data}
					});
				}
			}
			else {
				yield put({
					type: 'reducer:update',
					payload: {role: {officeId: 1}}
				});
			}

		},
		*'effect:query:roles'({payload}, {call, put}) {
			yield put({type: 'reducer:update', payload: {loading: true}});
			const {code, data} = yield call(queryRoles);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						loading: false,
						roles: data
					}
				});
			}
		},
		*'effect:create:role'({payload}, {call, put}) {
			const {code} = yield call(createRole, payload.role);
			if(code === 0) {
				payload.success && payload.success();
			}
		},
		*'effect:update:role'({payload}, {call, put}) {
			const {code} = yield call(updateRole, payload.role);
			if(code === 0) {
				payload.success && payload.success();
			}
		},
		*'effect:delete:role'({payload}, {call, put}) {
			const {code} = yield call(deleteRole, {id: payload});
			if(code === 0) {
				yield put({
					type: 'effect:query:roles'
				});
			}
		}
	},

	reducers: {
		'reducer:update'(state, {payload}) {
			return {...state, ...payload}
		}
	}
}
