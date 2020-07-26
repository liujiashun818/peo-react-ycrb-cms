import { parse } from 'qs'
import { query, deleteItem } from '../services/guestbook'

export default {
  namespace: 'guestbook',
  state: {
    list: [],
    loading: false,
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
    setup ({ dispatch, history }) {
        window.Mydispatch = dispatch;
      history.listen(location => {
        if (location.pathname === '/client/guestbook') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:location.query}
          }});
        }
      })
    }
  },
  effects: {
    *query({ payload }, { call, put, select }){
      let params = Object.assign({
            'pageNumber': 1,
            'pageSize': 10,
        }, payload)
      let pagination = yield select( ({ guestbook }) => guestbook.pagination )
      const data = yield call(query, parse(params))
      if (data) {
        pagination.total = data.data.pager.recordCount
        pagination.current = data.data.pager.pageNumber
        pagination.pageSize = parseInt(params.pageSize)
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data.list,
            pagination
          }
        })
      }
    },
    *deleteItem({ payload }, { call, put, select }){
      const data = yield call(deleteItem,payload.data.id)
      if(data){
        if(!!payload.callback){
          payload.callback()
        }
      }
    }
  },
  reducers: {
    showLoading (state) {
      return { ...state, loading: true }
    },
    querySuccess (state, action) {
      return { ...state, ...action.payload, loading: false }
    },
    showModal (state, action) {
      return { ...state, ...action.payload, modalVisible: true }
    },
    hideModal (state) {
      return { ...state, modalVisible: false }
    },
  }

}
