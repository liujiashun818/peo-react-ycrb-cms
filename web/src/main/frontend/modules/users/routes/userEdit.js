import React from 'react'
import PropTypes from 'prop-types'
import { routerRedux } from 'dva/router'
import { connect } from 'dva'
import UserForm from '../../../components/users/user/userEditForm'

function UserEdit ({ location, dispatch, user },context) {
  const formProps = {
  	currentItem : user.currentItem,
  	roleListAll:user.roleListAll,
  	officeTree:user.officeTree,
  	type:location.query.type || 'add',
  	handleSubmit(data){
  		if(location.query.type == 'update'){
  			data.id = location.query.id
	        dispatch({
	          type:'user/update',
	          payload:{
	            data:data,
	            callback:() => {
	              context.router.goBack()
	            }
	          }
	        })   			
  		}else if(location.query.type == 'add'){
	        dispatch({
	          type:'user/create',
	          payload:{
	            data:data,
	            callback:() => {
	              context.router.goBack()
	            }
	          }
	        }) 
  		}
  	},
	goBack(){
		context.router.goBack()
	},
  }
  return (
    <div className='content-inner'>
      <UserForm {...formProps}></UserForm>
    </div>
  )
}

function mapStateToProps ({ user }) {
  return { user }
}
UserEdit.contextTypes = {
    router: PropTypes.object.isRequired
}
export default connect(mapStateToProps)(UserEdit)