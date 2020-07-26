import { getUserInfo, updateUserInfo } from '../services/userInfo'
import { parse } from 'qs'

export default {
	namespace: 'userInfo',
    state: {
	    loading: false,
    },
    subscriptions: {
	    setup ({ dispatch, history }) {
          window.Mydispatch = dispatch;
	      history.listen(location => {
	        if (location.pathname === '/myCenter/userInfo') {
	           dispatch({type: 'app/firstLogined', payload: {
	            fn:dispatch,
	            data:{type: 'getUserInfo'}
	          }});
	        }
	      })
	    }
    },
    effects:{
    	*getUserInfo({ payload }, { call, put}){
    		const data = yield call(getUserInfo)
	        if (data) {
	          yield put({
	            type: 'querySuccess',
	            payload: {
	            	currentItem:data.data
	            }
	          })
	        }
	    },
	    *update({ payload }, { call, put}){
	    	const data = yield call(updateUserInfo,parse(payload.data))
	    	if(data){
	    	 	payload.callback(data)
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
