import { queryOffices, createOffice, updateOffice, deleteOffice, queryOffice } from '../services/users'
import { parse } from 'qs';
import { Jt } from '../../../utils';

export default {
  namespace: 'office',
  state: {
    tree: [],
    curOffice: {},
    loading: false,
  },
  subscriptions: {
    setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
        window.Mydispatch = dispatch;
      history.listen(location => {
        if (location.pathname === '/users/office') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'queryOffices'}
          }});
        }
        if(location.pathname === '/users/office/officeEdit') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'queryOffice',payload: { location }}
          }});
        }
      })
    }
  },
  effects: {
    *queryOffices ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      const data = yield call(queryOffices)
      if (data) {
        const treeData = Jt.tree.format(data.data);
        yield put({
          type: 'querySuccess',
          payload: {
            tree: treeData
          }
        })
      }
    },
    *queryOffice({ payload }, { call, put, select}) {
      const query = payload.location.query;
      const type = query.type || 'create'
            if(type === 'create') {
              yield put({
                        type: 'reducer:update',
                        payload:{curOffice: {}}
                    })
            }
            else if(type === 'update') {
                const { code, data={} } = yield call(queryOffice, { id: query.id })
                if(code === 0) {
                    yield put({
                        type: 'reducer:update',
                        payload:{curOffice: data}
                    })
                }
            }
            else if(type === 'createSub') {
                yield put({
                    type: 'reducer:update',
                    payload: {curOffice: {parentId: query.parentId || '1'}}
                })
            }
    },
    *create ({ payload }, { call, put }) {
      const { code } = yield call(createOffice, payload.office)
      if(code === 0) {
        payload.success && payload.success()
      }
    },
    *update ({ payload }, { call, put }) {
      const { code } = yield call(updateOffice, payload.office)
      if(code === 0) {
        payload.success && payload.success()
      }
    },
    *delete ({ payload }, { call, put }) {
      const { code } = yield call(deleteOffice, payload.office)
      if(code=== 0) {
        yield put({
          type: 'queryOffices'
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
    'reducer:update'(state, { payload }) {
      return { ...state, ...payload }
    }
  }

}
