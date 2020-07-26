import { query } from '../services/exa_1'
import { parse } from 'qs'

export default {

  namespace: 'exa_1',

  state: {
    list: [],
    pagination: {
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: total => `共 ${total} 条`,
      current: 1,
      total: null
    }
  },

  subscriptions: {
    setup ({ dispatch, history }) {
      history.listen(location => {
          const params = Object.assign({
              'pageNumber':1,
              'pageSize':20,
              'categoryId':1,
              'delFlag':0
            }, location.query)
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:params}
          }});
        })
      }
  },

  effects: {
    *query ({ payload }, { call, put }) {
      const data = yield call(query, parse(payload))
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data.list,
            pagination: {
              total: data.data.pager.recordCount,
              current: data.data.pager.pageNumber
            }
          }
        })
      }
    }
  },
  reducers: {
    querySuccess (state, action) {
      return { ...state, ...action.payload }
    }
  }

}
