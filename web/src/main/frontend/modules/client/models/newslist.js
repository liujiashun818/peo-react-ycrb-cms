import { parse } from 'qs'
import { query, deleteItem, getItem, create, getItemById, getItemByName } from '../services/list'

export default {
  namespace: 'newslist',
  state: {
    list: [],
    loading: false,
    newslistDetail: {},
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
        if (location.pathname === '/client/newslist') {
          if(location.query.name && location.query.name.length > 0 ) {
            dispatch({type: 'app/firstLogined', payload: {
              fn:dispatch,
              data:{type: 'queryByName',payload:location.query}
            }});
          } else {
            dispatch({type: 'app/firstLogined', payload: {
              fn:dispatch,
              data:{type: 'query',payload:location.query}
            }});
          }
        }
        if (location.pathname === '/client/newslist/detail') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'queryById',payload:location.query}
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
        let pagination = yield select( ({ newslist }) => newslist.pagination )
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
    //
    *queryByName({ payload }, { call, put, select }){
      let params = Object.assign({
          'pageNumber': 1,
          'pageSize': 10,
      }, payload)
      let pagination = yield select( ({ newslist }) => newslist.pagination )
      const data = yield call(getItemByName, parse(params))
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
  *queryById({ payload }, { call, put, select }){
    let params = Object.assign({
        'pageNumber': 1,
        'pageSize': 10,
    }, payload)
    let pagination = yield select( ({ newslist }) => newslist.pagination )
    const data = yield call(getItemById, parse(params))
    if (data) {
      payload.newslistDetail = data.data;
      yield put({
        type: 'queryDetailSuccess',
        payload: {
          payload:payload,
          pagination
        }
      })
    }
},
  //
    *create({ payload }, { call, put, select }){
      const data = yield call(create,payload.data)
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
    queryDetailSuccess (state, action) {
        return { ...state, newslistDetail: action.payload.payload.newslistDetail, loading: false }
    },
    querySuccess (state, action) {
        return { ...state,  ...action.payload, loading: false }
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
