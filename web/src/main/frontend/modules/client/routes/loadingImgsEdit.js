import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'dva';
import { routerRedux } from 'dva/router'
import { routerPath } from '../../../constants'
import { Jt, Immutable } from '../../../utils'
import LoadingImgsEditForm from '../../../components/client/loadingImgsEditForm';

function LoadingImgsEdit({ location, dispatch, loadingImgs },context) {
	const {currentItem={},showInput=false,num,selectType,checkVisible} = loadingImgs
	const formProps = {
		currentItem,
        checkVisible,
        num,
        selectType,
        showInput,
		updateState(data){
			dispatch({
				type:'loadingImgs/updateState',
				payload:{
					...data
				},
		})
		},
		goBack(){
            dispatch({
                type:'loadingImgs/changeNum',
                payload:{
                    num:0
                },
            })
			context.router.goBack()
		},
		changeShowInput(param){
			dispatch({
				type:'loadingImgs/changeShowInput',
				payload:{
					showInput:param
				},
			})
		},
        updateLinkType(data){
            dispatch({
                type:'loadingImgs/updateLinkType',
                payload:{
                    currentItem:data,
                    showInput:data.showInput,
                    num:data.num,
                    checkVisible:data.checkVisible,
                },
            })
        },
		handleSubmit(data){
            dispatch({
                type:'loadingImgs/changeNum',
                payload:{
                    num:0
                },
            })
			if(location.query.type == 'update'){
				data.id = currentItem.id
				dispatch({
					type:'loadingImgs/update',
					payload:{
						data,
			            callback:() => {
			              context.router.goBack()
			            }
					},
				})
			}else{
				data.delFlag = 1
				dispatch({
					type:'loadingImgs/create',
					payload:{
						data,
			            callback:() => {
			              context.router.goBack()
			            }
					},
				})
			}
		}
	}
	  return (
          <div className='content-inner'>
	      <LoadingImgsEditForm key={new Date().getTime()} {...formProps}></LoadingImgsEditForm>
	    </div>
	  );
}
LoadingImgsEdit.contextTypes = {
    router: PropTypes.object.isRequired
}
function mapStateToProps ({ loadingImgs }) {
  return { loadingImgs }
}

export default connect(mapStateToProps)(LoadingImgsEdit)
