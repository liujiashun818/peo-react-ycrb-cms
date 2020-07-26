import { parse } from 'qs'
import { query, update, onOff, deleteItem, getItem, create } from '../services/loadingImgs'

export default {
  namespace: 'loadingImgs',
  state: {
    list: [],
    loading: false,
    currentItem: {},
    showInput:false,
    checkVisible:false,
    selectType:'image',
    num:0,
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
        if (location.pathname === '/client/loadingImgs') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:location.query}
          }});
        }
        if (location.pathname === '/client/loadingImgs/loadingImgsEdit') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'edit',payload:location.query}
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
        let pagination = yield select( ({ loadingImgs }) => loadingImgs.pagination )
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
    *create({ payload }, { call, put, select }){
      const data = yield call(create,payload.data)
      if(data){
        if(!!payload.callback){
          payload.callback()
        }
      }
    },
    *update({ payload }, { call, put, select }){
      const data = yield call(update,payload.data)
      if(data){
        if(!!payload.callback){
          payload.callback()
        }
      }
    },
    *onOff({ payload }, { call, put, select }){
      const data = yield call(onOff,payload.id)
      if(data){
        if(!!payload.callback){
          payload.callback()
        }
      }
    },
    *deleteItem({ payload }, { call, put, select }){
      const data = yield call(deleteItem,payload.data.id)
      if(data){
        if(!!payload.callback){
          payload.callback()
        }
      }
    },
    *edit({ payload }, { call, put, select }){
      if(payload.type=='add'){
        payload.currentItem = {}
        payload.showInput = false,
        payload.selectType = 'image'
      }else{
          const itemData = yield call(getItem,payload.id)
          payload.currentItem = itemData.data;
          payload.selectType = itemData.data.type
          if(itemData.data.linkType == '1'){
            payload.showInput = true
          }else{
            payload.showInput = false
          }
      }
      yield put({
        type: 'querySuccess',
        payload:payload
      })
    }
  },
  reducers: {
    changeShowInput(state,action){
      return { ...state, ...action.payload, loading: true }
    },
    updateLinkType(state,{payload}){
        return {...state,...payload}
    },
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
    changeNum(state,{payload}){
        return { ...state, ...payload }
    },
    updateState(state,{payload}){
      return {...state,...payload}
    }
  }

}
