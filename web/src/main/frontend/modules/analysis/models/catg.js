import {queryCatgs} from '../../cms/services/category';
import {queryOffices} from '../../users/services/users';
import {queryAnlsCatgs} from '../services/analysis';
import {routerPath} from '../../../constants';
import {Jt} from '../../../utils';

export default {
	namespace: 'anls_catg',

	state: {
		loading: false,
		catgs: [],
		offcs: [],
		list: []
	},

	subscriptions: {
		setup({dispatch, history}) {
            window.Mydispatch = dispatch;
			dispatch({
				type: 'app/firstLogined',
				payload: {
					fn: dispatch,
					data: {type: 'effects:query:catgs'}
				}
			});
			dispatch({
				type: 'app/firstLogined',
				payload: {
					fn: dispatch,
					data: {type: 'effect:query:offcs'}
				}
			});
			history.listen(location => {
				const {pathname} = location;
				if(pathname === routerPath.ANLS_CATG) {
					dispatch({
						type: 'app/firstLogined',
						payload: {
							fn: dispatch,
							data: {type: 'effect:query:list'}
						}
					});
				}
			});
		}
	},

	effects: {
		*'effects:query:catgs'({payload}, {call, put}) {
			const {code, data=[]} = yield call(queryCatgs);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						catgs: Jt.tree.format(data)
					}
				});
			}
		},
		*'effect:query:offcs'({payload}, {call, put}) {
			const {code, data=[]} = yield call(queryOffices);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						offcs: Jt.tree.format(data)
					}
				});
			}
		},
		*'effect:query:list'({payload}, {call, put}) {
			yield put({type: 'reducer:update', payload: {loading: true}});
			const {code, data=[]} = yield call(queryAnlsCatgs,payload);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						loading: false,
						list: data
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
