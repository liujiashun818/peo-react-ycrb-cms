import { create, update, query, queryView, tree, categoryTree} from '../services/clientMenu'
import { queryPush,deletePush } from '../services/clientPush'
import { parse } from 'qs'

function getFormItem(data,id){
  var item = {};
  function closure(data,id){
    for (var i in data) {
      if (data[i].id == id) {
        item = data[i];
        break;
      }else {
        closure(data[i].child,id);
      }
    }
  }
  closure(data,id)
  return item
}
export default {
  namespace: 'client',
  state: {
    list: [],
    pushList:[],
    loading: false,
    currentItem: {},
    modalVisible: false,
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
        if (location.pathname === '/client/clientMenu') {
           dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'query',payload:location.query}
          }});
        }
        if(location.pathname === '/client/clientMenu/clientMenuEdit') {
           dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'clientMenuEdit',payload:location.query}
          }});
        }
        if(location.pathname === '/client/clientPush') {
           dispatch({type: 'app/firstLogined', payload: {
            fn:dispatch,
            data:{type: 'queryPush',payload:location.query}
          }});
        }
      })
    }
  },
  effects: {
    *queryPush({ payload }, { call, put, select }){
        let params = Object.assign({
            'pageNumber': 1,
            'pageSize': 10
        }, payload)
        let pagination = yield select( ({ client }) => client.pagination )
        const data = yield call(queryPush, parse(params))
        if (data) {
          pagination.total = data.data.pager.recordCount
          pagination.current = data.data.pager.pageNumber
          yield put({
            type: 'querySuccess',
            payload: {
              pushList: data.data.list,
              pagination,
            }
          })
        }
    },
    *deletePush({ payload }, { call, put, select }){
        yield call(deletePush,payload.params)
        yield put({
          type:'queryPush',
          payload:payload.pager
        })
    },
    *query ({ payload }, { call, put }) {
      yield put({ type: 'showLoading' })
      const data = yield call(query, parse(payload))
      if (data) {
        yield put({
          type: 'querySuccess',
          payload: {
            list: data.data,
            ...payload
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
        if(!!payload.callback){
          payload.callback()
        }
      }
    },
    *'clientMenuEdit'({ payload },{ call, put }){
        const data = yield call(query)
        //const listViewData = yield call(queryView)
        const sysTreeData = yield call(tree,{type:'cms_mobile_menu_systype'})
        const viewTreeData = yield call(tree,{type:'cms_mobile_menu_showtype'})
        const categoryTreeData = yield call(categoryTree)
        //payload.listView = listViewData.data
        payload.list = data.data
        payload.sysTreeData = sysTreeData
        payload.viewTreeData = viewTreeData
        payload.categoryTreeData = categoryTreeData.data
        if(!payload.type || payload.type=='add'){
          payload.type = 'add'
          payload.currentItem = {}
        }else{
          payload.currentItem = getFormItem(payload.list,payload.id)
        }
        yield put({
          type: 'querySuccess',
          payload:payload
        })
    },
    *'clearCurrentItem'({ payload },{ call, put }){
      yield put({
        type: 'querySuccess',
        payload:{
          currentItem:{}
        }
      })
    },
    *'menusEditId'({ payload },{ call, put }){
      yield put({
        type:'showLoading'
      })
      const data = yield call(query)
      if (data) {
        let payloadData = {}
        if(!!payload.id && !!payload.parentId){
         payloadData = {
            list: data.data,
            ...payload
          }
        }else{
          payloadData = {
            list:data.data,
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
