import {queryFields, deleteField, deleteFields, updateField} from '../services/fields';
import {Jt} from '../../../utils';
import {PAGE_SIZE, routerPath} from '../../../constants';

export default {
	namespace: 'field',
	state: {
		loading: false,
		list: [],
		pagination: {
			showSizeChanger: true,
            showQuickJumper: true,
            showTotal: total => `共 ${total} 条`,
            pageSize: PAGE_SIZE,
            current: 1,
            total: null
		},
		curField: {},
		selectedIds: [],
		femv: false
	},
	subscriptions: {
		setup({dispatch, history}) {
            window.Mydispatch = dispatch;
			history.listen(location => {
				if(location.pathname === routerPath.FIELD_LIST) {
					dispatch({type: 'app/firstLogined', payload: {
						fn: dispatch,
						data: {type: 'effect:query:fields'}
					}});
				}
			});
		}
	},
	effects: {
		*'effect:query:fields'({payload}, {call, put, select}) {
			yield put({type: 'reducer:update', payload: {loading: true}});
			const {pagination} = yield select(({field}) => {return {pagination: field.pagination};});
			const params = {
				pageNumber: pagination.current,
				pageSize: pagination.pageSize,
				...payload
			};
			const {code, data} = yield call(queryFields, params);
			if(code === 0 && data) {
				yield put({
					type: 'reducer:update',
					payload: {
						loading: false,
						list: data.list || [],
						pagination: {
							...pagination,
							total: data.pager.recordCount,
							current: data.pager.pageNumber,
							pageSize: data.pager.pageSize
						},
						curField: {},
						selectedIds: []
					}
				});
			}
		},
		*'effect:delete:field'({payload}, {call, put}) {
			const {code} = yield call(deleteField, {id: payload.id});
			if(code === 0) {
				yield put({
					type: 'effect:query:fields'
				});
			}
		},
		*'effect:delete:fields'({payload}, {call, put}) {
			const {code} = yield call(deleteFields, {ids: payload.ids});
			if(code === 0) {
				yield put({
					type: 'effect:query:fields'
				});
			}
		},
		*'effect:update:field'({payload}, {call, put}) {
			const {code} = yield call(updateField, payload);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						femv: false
					}
				});
				yield put({
					type: 'effect:query:fields'
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
