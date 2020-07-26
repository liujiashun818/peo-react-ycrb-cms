import { create, remove, update, query, queryView} from '../services/menus'
import { parse } from 'qs'

export default {
  namespace: 'menus',
  state: {
    list: [],
    loading: false,
    currentItem: {},
    modalVisible: false,
    modalType: 'create',
    pagination: {
      showSizeChanger: true,
      showQuickJumper: true,
      showTotal: total => `共 ${total} 条`,
      current: 1,
      total: null
    }
  },
  subscriptions: {
    setup ({ dispatch, history }) { //dispatch,history是setup带进来的 不一定命名为setup subscription里面的任何属性都会执行
        window.Mydispatch = dispatch;
        history.listen(location => {
        if (location.pathname === '/sys/menu') {
          dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:location.query}
          }});
        }
        if(location.pathname === '/sys/menu/menusEdit') {
           dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'menusEditId',payload:location.query}
          }});
        }
      })
    }
  },
  effects: {
    *query ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      const data = yield call(query, parse(payload))
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
            ...payload
            // pagination: {
            //   total: data.page.total,
            //   current: data.page.current
            // }
          }
        })
      }
    },
    *'delete' ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      yield call(remove, { id: payload })
      const data = yield call(query, parse(payload))
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
            // pagination: {
            //   total: data.page.total,
            //   current: data.page.current
            // }
          }
        })
      }
    },
    *create ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      yield call(create,payload.data)
      const data = yield call(query)
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
          }
        })
        payload.callback()
      }
    },
    *update ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      yield call(update,payload.data)
      const data = yield call(query, parse(payload))
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
          }
        })
        payload.callback()
      }
    },
    *'menusEditId'({ payload },{ call, put }){
      yield put({
        type:'showLoading'
      })
      const data = yield call(query)
      //const dataView = yield call(queryView)
      if (data) {
        let payloadData = {}
        if(!!payload.id && !!payload.parentId){
          let item = {}
          function getArray(data,parentId,id){
            for (var i in data) {
              if(parentId==0){
                if(data[i].id==id){
                  item = data[i]
                  break;
                }
              }else{
                if (data[i].id == parentId) {
                    for(var j in data[i].child){
                      if(data[i].child[j].id == id){
                        item = data[i].child[j];
                        break;
                      }
                    }
                    break;
                }else{
                    getArray(data[i].child, parentId, id);
                }
              }
            }
          }
          getArray(data.data,payload.parentId,payload.id)
         payloadData = {
            list: data.data,
            //listView:dataView.data,
            ...payload
          }
          if(!!item.icon && payload.type == 'update'){
            payloadData.submitIcon = item.icon
            payloadData.selectedIcon = item.icon
          }else{
            payloadData.selectedIcon = null
            payloadData.submitIcon = null
          }
        }else{
          payloadData = {
            list:data.data,
            submitIcon:null,
            selectedIcon:null,
            id:null,
            parentId:null
          }
        }
        yield put({
          type: 'querySuccess',
          payload:payloadData
          // {
          //   list: data.data,
          //   ...payload
          //   // pagination: {
          //   //   total: data.page.total,
          //   //   current: data.page.current
          //   // }
          // }
        })
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
    changeIcon (state,action){
      return {...state, ...action.payload}
    }
  }

}
