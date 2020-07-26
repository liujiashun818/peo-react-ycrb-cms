import { parse } from 'qs'
import {query,batchOnOff,update,getSensData,updateSensData,saveSensData, addReply,batchOnOff2} from '../services/comment'
import { Jt, Immutable } from '../../../utils'
import { createReplyInfo } from '../../../services/clientReply'

function formatTreeData(data = []) {
  for(let i = 0; i < data.length; i++) {
    data[i].key = data[i].value = data[i].id + ''
    if(!Jt.array.isEmpty(data[i].child)) {
      data[i].children = data[i].child
      delete data[i].child
      formatTreeData(data[i].children);
    }
  }
  return [...data];
}

export default{
	namespace:'comment',
	state:{
		currentItem:{},
		list: [],
        replyModalVisible: false,
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
	subscriptions:{
		setup({dispatch, history}){
            window.Mydispatch = dispatch;
			history.listen(location => {
			
		        if (location.pathname === '/cms/comment') {
	    	          dispatch({type: 'app/firstLogined', payload: {
			            fn:dispatch,
			            data:{type: 'queryComment',payload:location.query}
			          }});
		        }
			})
		}
	},
	effects:{
		*queryComment({ payload }, { call, put, select }){
			yield put({type:'showLoading'})
            let params = Object.assign({
                'pageNumber': 1,
                'pageSize': 10,
                'delFlag':0
            }, payload)
	        let pagination = yield select( ({ comment }) => comment.pagination ) //对应namespace 的 comment 的 state
			const data = yield call(query, parse(params))
			console.log("comment.",data)
            if (data) {
            	pagination.total = data.data.pager.recordCount
            	pagination.current = data.data.pager.pageNumber
            	pagination.pageSize = parseInt(params.pageSize)
		        yield put({
		          type: 'querySuccess',
		          payload: {
		            list: data.data.list,
					pagination,
					delFlag:params.delFlag
		          }
		        })
      		}
		},
		*getSensWord({payload},{call,put}){
			const data = yield call(getSensData,1)
			if(data){
				if(data.data){
			        yield put({
			          type: 'putInState',
			          payload: {
			     		sensWord:data.data.sensitiveWord,
			     		shouldPostSensWord:false,
			     		...payload
			          }
			        })
				}else{
			        yield put({
			          type: 'putInState',
			          payload: {
			     		sensWord:'',
			     		shouldPostSensWord:true,
			     		...payload
			          }
			        })
				}

			}
		},
		*saveSensWord({payload},{call,put}) {
			const data = yield call(saveSensData,parse(payload))
			if(data){
		        yield put({
		          type: 'putInState',
		          payload: {
		     		visible: false
		          }
		        })
			}
		},
        *replyShow({payload},{call,put}){
            yield put({
              type: 'putInState',
              payload:{
                replyModalVisible:true,
                currentItem: payload
              }
            });

        },
        *replyAdmin({payload},{call,put,select}){
            const data = yield call(createReplyInfo,payload.replyInfo);
            if(data){
                yield put({
                  type: 'replyHide',
                })
            let pagination = yield select( ({ comment }) => comment.pagination )
            let params = Object.assign({
                'pageNumber': pagination.current,
                'pageSize': pagination.pageSize,
                'delFlag':0
            })
            const datas = yield call(query, parse(params))
            yield put({
                type: 'querySuccess',
                payload:{
                    list: datas.data.list,
                    pagination
                }
            })
            }
        },
        *replyHide({payload},{call,put,select}){
            yield put({
              type: 'putInState',
              payload:{
                replyModalVisible:false,
                currentItem:{}
              }
            })

        },
		*updateSensWord({payload},{call,put}){
			const data = yield call(updateSensData,parse(payload))
			if(data){
		        yield put({
		          type: 'putInState',
		          payload: {
		     		visible:false
		          }
		        })
			}
		},
		*updateProps({payload},{call,put,select}){
			let params = payload.params
			const data = yield call(update, parse(params)) //改
			let query = payload.query
            yield put({
                type: 'queryComment',
                payload:{
                	...query
                }
            })
		},
		*batchOnOff({payload},{call,put,select}){
			yield put({type: 'update:state', payload: {loading: true}});
			let delFlag = yield select( ({ comment }) => comment.delFlag )
			payload.delFlag = delFlag
			const data = yield call(batchOnOff, parse(payload))
			delete payload.commentIds
            yield put({
                type: 'queryComment',
                payload
            })
		},
        *batchOnOff2({payload},{call,put,select}){
            yield put({type: 'update:state', payload: {loading: true}});
            let delFlag = yield select( ({ comment }) => comment.delFlag )
            payload.delFlag = delFlag
            const data = yield call(batchOnOff2, parse(payload))
            delete payload.commentIds
            yield put({
                type: 'queryComment',
                payload
            })
        },
		*addReply({payload},{call,put,select}) {
			let params = payload.params;
			const data = yield call(addReply, parse(params)); //改
			let query = payload.query;
            yield put({
                type: 'queryComment',
                payload: {
                	...query
                }
            });
		}
	},
	reducers:{
		'putInState'(state, {payload}){
			return {...state,...payload}
		},
		'update:state'(state,{payload}) {
			return {...state, ...payload}
		},
	    showLoading (state) {
	      return { ...state, loading: true }
	    },
		'querySuccess'(state,{payload}){
			if(!!payload.list && payload.list.length>0){
				formatTreeData(payload.list)
			}else if(!payload.list){
				payload.list = []
			}
			console.log("xaaadxxx",payload)
			return {...state,...payload,loading: false}
		}
	}
}
