import { parse } from 'qs'
import { getKeywords, addKeywords, deleteKeywords } from '../services/searchKeyWords';

export default {
  namespace: 'searchKeywords',
  state: {
    keywordsList: [],
    showModal: false
  },
  subscriptions: {
    setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
        window.Mydispatch = dispatch;
        history.listen(location => {
        if (location.pathname === '/client/searchKeywords') {
          dispatch({type: 'app/firstLogined', payload: {
            fn: dispatch,
            data: {type: 'effects:getKeywords', payload: {}}
          }});
        }
      })
    }
  },
  effects: {
    *'effects:getKeywords'({ payload = {} }, { call, put, select }){
        payload.pageNumber = 1;
        payload.pageSize = 10;
        payload.title = '';
        const data = yield call(getKeywords, parse(payload));
        if (data) {
          yield put({
            type: 'updateState',
            payload: {
              keywordsList: data.data.list || [],
              showModal: false
            }
          })
        }
    },
    *'effects:addKeywords'({ payload }, { call, put, select }) {
        const data = yield call(addKeywords, payload);
        if (data) {
          yield put({
            type: 'effects:getKeywords'
          })
        }
    },
    *'effects:deleteKeywords'({ payload }, { call, put, select }) {
        const data = yield call(deleteKeywords, payload);
        if (data) {
          yield put({
            type: 'effects:getKeywords'
          })
        }
    }
  },
  reducers: {
    updateState(state, {payload}) {
      return {...state,...payload}
    }
  }

}
