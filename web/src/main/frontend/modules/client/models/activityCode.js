import { parse } from 'qs'
import { query, createActivity, queryActivity } from '../services/activityCode'
import { Jt } from '../../../utils';

export default {
  namespace: 'activityCode',
  state: {
    list: [],
    loading: false,
    curActivity: {},
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
    setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
        window.Mydispatch = dispatch;
        history.listen(location => {
        if (location.pathname === '/client/activityCode') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:location.query}
          }});
        }
        if(location.pathname === '/client/activityCode/activityCodeEdit') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'queryActivity',payload:{location}}
          }});
        }
      })
    }
  },
  effects: {
    *query ({ payload }, { call, put ,select}) {
      let params = Object.assign({
        'pageNumber':1,
        'pageSize':10,
        'type':1
      },payload)
      let pagination = yield select ( ({activityCode}) => activityCode.pagination )
      const {code, data} = yield call(query,parse(params))
      if (code === 0) {
        pagination.total = data.pager.recordCount
        pagination.current = data.pager.pageNumber
        pagination.pageSize = parseInt(params.pageSize)
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.list || [],
            pagination
          }
        })
      }
    },
    *queryActivity({ payload }, { call, put, select}) {
      const query = payload.location.query;
      const type = query.type || 'create'
            if(type === 'create') {
              yield put({
                        type: 'reducer:update',
                        payload:{curActivity: {}}
                    })
            }
    },
    *createActivity ({ payload }, { call, put ,select}) {
      const { code } = yield call(createActivity, payload.code)
      if(code === 0) {
        payload.success && payload.success()
      }
      else {console.log(code)}
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
    'reducer:update'(state, { payload }) {
      return { ...state, ...payload }
    }
  }

}
