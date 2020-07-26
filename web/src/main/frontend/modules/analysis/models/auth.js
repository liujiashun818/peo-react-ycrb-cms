import {queryAnlsAuths} from '../services/analysis';
import {routerPath} from '../../../constants';
import {Jt} from '../../../utils';

export default {
	namespace: 'anls_auth',

	state: {
		loading: false,
		list: []
	},

	subscriptions: {
		setup({dispatch, history}) {
            window.Mydispatch = dispatch;
			history.listen(location => {
				const {pathname} = location;
				if(pathname === routerPath.ANLS_AUTH) {
		          	dispatch({type: 'app/firstLogined', payload: {
		            	fn:dispatch,
		            	data:{type: 'effect:query:list'}
		          	}});
				}
			})

		}
	},

	effects: {
		*'effect:query:list'({payload}, {call, put}) {
			yield put({type: 'reducer:update', payload: {loading: true}});
			const {code, data} = yield call(queryAnlsAuths, payload);
			if(code === 0) {
				yield put({
					type: 'reducer:update',
					payload: {
						loading: false,
						list: data || []
					}
				});
			}
		}
	},

	reducers: {
		'reducer:update'(state, {payload}) {
			return {...state, ...payload};
		}
	}
}
