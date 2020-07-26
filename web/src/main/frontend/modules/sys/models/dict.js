import {query,getItem,deleteItem,create,update,typeList} from '../services/dict'
import { parse } from 'qs'

export default {
	namespace: 'dict',
    state: {
	    loading: false,
	    currentItem:{},
	    typeData:[],
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
            window.Mydispatch = dispatch;
	      history.listen(location => {
	        if (location.pathname === '/sys/dict') {
              dispatch({type: 'app/firstLogined', payload: {
                fn:dispatch,
                data:{type: 'query',payload:location.query}
              }});
	        }
	        if (location.pathname === '/sys/dict/dictEdit') {
              dispatch({type: 'app/firstLogined', payload: {
                fn:dispatch,
                data:{type: 'dictEditId',payload:location.query}
              }});
	        }
	      })
	    }
    },
    effects:{
    	*query({payload},{call,put,select}){
    		let typeData = yield call(typeList)
            let params = Object.assign({
                'pageNumber': 1,
                'pageSize': 10
            }, payload)
	        let pagination = yield select( ({ dict }) => dict.pagination )
	    	const data = yield call(query, parse(params))
		    if (data) {
	           	pagination.total = data.data.pager.recordCount
	        	pagination.current = data.data.pager.pageNumber
		        yield put({
		          type: 'querySuccess',
		          payload: {
		            list: data.data.list,
					pagination,
					typeData:typeData.data
		          }
		        })
		    }
    	},
    	*dictDeleteId({payload},{call,put,select}){
    		yield call(deleteItem,payload.id)
    		yield put({
    			type:'query',
    			payload:payload.pager
    		})
    	},
    	*dictEditId({payload},{call,put,select}){
    		if(payload.type == 'update'){
    			const id = payload.id
    			const data = yield call(getItem,id)
    			if(!!data){
			        yield put({
			          type: 'querySuccess',
			          payload: {
			            currentItem: data,
			          }
			        })
    			}
    		}
    		else{
		        yield put({
		          type: 'querySuccess',
		          payload: {
		            currentItem: {},
		          }
		        })
    		}
    	},
    	*create({payload},{call,put,select}){
    		const data = yield call(create,payload.data)
    		if(data){
    			payload.callback()
    		}
    	},
    	*update({payload},{call,put,select}){
    		const data = yield call(update,payload.data)
    		if(data){
    			payload.callback()
    		}
    	}
    },
  	reducers: {
	    showLoading (state) {
	      return { ...state, loading: true }
	    },
	    querySuccess (state, action) {
	      return { ...state, ...action.payload, loading: false }
	    }
  	}
}
